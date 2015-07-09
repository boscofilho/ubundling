import extractors.JuncoProcessor;
import extractors.SourceDependencyExtractor;
import processors.ExtractorProcessor;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
//import org.kevoree.log.Log;
import processors.*;
import spoon.processing.Processor;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Manager to slice the sources and tests
 * <p/>
 * <p/>
 * Created by marodrig on 05/03/2015.
 */
public class SlicerProcessor extends QueueProcessingManager {

    final static Logger logger = Logger.getLogger(SlicerProcessor.class);

    /**
     * Path to the test classes of the project
     */
    private String testPath;

    /**
     * Path to the coverage file
     */
    private String coveragePath;

    /**
     * Path to the built classes
     */
    private String builtClassesPath;

    /**
     * Path to the source of the project
     */
    private String srcPath;

    /**
     * Path of the unbundled code
     */
    private String unbundledPath;

    /**
     * Class fully qualified name of inputClasses that is going to be processed
     */
    private List<String> inputClasses;

    /**
     * Resulting bundle
     */
    private List<String> bundle;

    /**
     * Package that will be processed
     */
    private String packagePath;


    /**
     * Parent path of the complete project containing the sources
     * <p/>
     * Example if srcPath is c:/myproject/src/main/java
     * <p/>
     * projectPath is c:/myproject
     */
    private String projectPath;

    public SlicerProcessor(Factory factory) {
        super(factory);
        addProcessor(new VariableAccessExtractor());
        addProcessor(new ExecutableExtractors());
        addProcessor(new FieldExtractor());
        addProcessor(new InvocationExtractors());
        addProcessor(new InstanceOfExtractor());
        addProcessor(new AnnotationTypeExtractor());
        addProcessor(new NewClassExtractor());
        addProcessor(new InterfaceExtractor());
    }

    /**
     * Returns the path of the file of a class
     *
     * @param parentPath Parent path where the class is
     * @param qName      Qualified name of the class
     * @return A string with the qualified name of the class
     */
    private String getClassFile(String parentPath, String qName) {
        String s = parentPath + File.separator + qName.replace(".", File.separator);
        while (s.contains("$")) s = s.substring(0, s.lastIndexOf("$"));
        s += ".java";

        return s;
    }

    /**
     * Gets a CtClass element out of the name of the class
     *
     * @param name Name of the class
     * @return A CtClass element
     */
    protected CtElement getClass(String name) {
        String s = getClassFile(projectPath + srcPath, name);
        try {
            return getFactory().CompilationUnit().getMap().get(s).getMainType();
        } catch (NullPointerException ex) {
            logger.error("Unable to find class " + s);
            return null;
        }
    }


    /**
     * Process the bundle and find all classes that the bundle depends on
     * <p/>
     * Also extract the test that covering these clases
     */
    public void process() {
        //augmentBundleWithTest();
        processBundle();
    }

    /**
     * Augment the given bundle with its inheritors found in tests
     */
    protected void augmentBundleWithTest() {
        JuncoProcessor processor = new JuncoProcessor(bundle, coveragePath, builtClassesPath);
        try {
            for (String k : processor.process().keySet()) {
                bundle.add(k);
            }
        } catch (IOException e) {
            logger.error("Cannot process test dependencies");
        }
    }

    /**
     * Find al dependencies of the given classes
     */
    public void processBundle() {
        //Resulting bundle
        bundle = new ArrayList<String>(inputClasses);

        //Classes to analyze in the next step
        HashSet<String> newlyFound = new HashSet<String>();
        HashSet<String> globalBundle = new HashSet<String>();

        //Index of the next class in the bundle that is going to be analyzed
        int index = 0;

        SourceDependencyExtractor dependencyExtractor = new SourceDependencyExtractor();
        dependencyExtractor.setPackageName(getPackageName());
        dependencyExtractor.setNewlyFound(newlyFound);
        dependencyExtractor.setBundle(globalBundle);

        //Init all processors with a common set of classes to analyze
        //this prevents that two processors reports twice the same class
        for (Processor<?> p : getProcessors()) {
            ExtractorProcessor d = ((ExtractorProcessor) p);
            d.setExtractor(dependencyExtractor);
        }

        //Slice dependencies
        do {
            logger.info("Processing " + bundle.get(index));
            CtElement e = getClass(bundle.get(index));
            //CtType c = e instanceof CtClass ? (CtClass) e : null;
            //c = e instanceof CtInterface ? (CtInterface)e : c;
            //c = e instanceof CtAnnotationType ? (CtAnnotationType)e : c;
            if (e == null) {
                logger.warn("Unable to find " + bundle.get(index));
                bundle.remove(index);
            } else {
                for (Processor<?> p : getProcessors()) {
                    ExtractorProcessor d = ((ExtractorProcessor) p);
                    d.init();
                    d.process();
                    process(e, d);
                    d.processingDone();
                }
                bundle.addAll(newlyFound);
                newlyFound.clear();
                index++;
            }

        } while (bundle.size() > index);
    }

    /**
     * Performs the unbundling of the classes
     *
     * @param classes Classes that must remain in the bundle
     */
    public void unbundle(List<String> classes) {
        File source = new File(projectPath);
        File dest = new File(unbundledPath);

        logger.info("Copy results to output directory... please by patient");
        try {
            FileUtils.copyDirectory(source, dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Copy results to output directory. Done");

        final Path start = Paths.get(unbundledPath + srcPath);
        try {
            Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    //logger.info(file.toString());
                    String s = start.relativize(file).toString().replace(File.separator, ".");
                    if (s.endsWith(".java")) {
                        s = s.substring(0, s.lastIndexOf(".java"));
                        if (!bundle.contains(s))
                            Files.delete(file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException e)
                        throws IOException {
                    if (e == null) {
                        if (dir.toFile().listFiles().length <= 0)
                            Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    } else {
                        // directory iteration failed
                        throw e;
                    }
                }
            });
        } catch (IOException e) {
            logger.error("Unable to unbundle");
        }
    }

    /**
     * Process and unbundle the project
     */
    public void unbundle() {
        process();
        unbundle(getBundle());
    }

    public String getPackageName() {
        return packagePath.replace("/", ".");
    }

    public void setPackageName(String packageName) {
        this.packagePath = packageName.replace(".", "/");
    }


    public List<String> getInputClasses() {
        if (inputClasses == null) inputClasses = new ArrayList<String>();
        return inputClasses;
    }

    public void setInputClasses(List<String> inputClasses) {
        this.inputClasses = inputClasses;
    }

    public void setBundle(List<String> bundle) {
        this.bundle = bundle;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public List<String> getBundle() {
        return bundle;
    }

    public String getUnbundledPath() {
        return unbundledPath;
    }

    public void setUnbundledPath(String unbundledPath) {
        this.unbundledPath = unbundledPath;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getTestPath() {
        return testPath;
    }

    public void setTestPath(String testPath) {
        this.testPath = testPath;
    }

    public String getCoveragePath() {
        return coveragePath;
    }

    public void setCoveragePath(String coveragePath) {
        this.coveragePath = coveragePath;
    }

    public String getBuiltClassesPath() {
        return builtClassesPath;
    }

    public void setBuiltClassesPath(String builtClassesPath) {
        this.builtClassesPath = builtClassesPath;
    }
}
