import fr.inria.diversify.buildSystem.maven.MavenBuilder;
import spoon.reflect.factory.Factory;

import java.io.IOException;

/**
 * Created by marodrig on 05/03/2015.
 */
public class Unbundling {

    /**
     * entry of the un-bundling program
     * @param args
     */
    public static void main(String[] args) throws IllegalAccessException, InstantiationException, ClassNotFoundException, IOException, InterruptedException {
        String s =  "\\guava\\src";
        String tpath = "\\guava-testlib";
        String pp = "C:\\MarcelStuff\\DATA\\DIVERSE\\input_programs\\guava-master";
        String up = "C:\\MarcelStuff\\DATA\\DIVERSE\\output_programs\\guava-master";
        Factory f = new SpoonMetaFactory().buildNewFactory(pp + s, 7);


        /*
        f.CompilationUnit().getMap();
        for (Map.Entry<String, CompilationUnit> m : f.CompilationUnit().getMap().entrySet()) {
            try {
                System.out.println(m.getKey() + " " + m.getValue().getMainType().getQualifiedName());
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }
        }*/



        SlicerProcessor d = new SlicerProcessor(f);
        d.setSrcPath(s);
        d.setProjectPath(pp);
        d.setUnbundledPath(up);
        d.setTestPath(tpath);
        d.setPackageName("com.google");

        d.getInputClasses().add("com.google.common.collect.BiMap");
        /*
        d.getInputClasses().add("com.google.common.collect.Iterables");
        d.getInputClasses().add("com.google.common.collect.HashBiMap");
        d.getInputClasses().add("com.google.common.collect.ReferenceMap");
        d.getInputClasses().add("com.google.common.collect.ForwardingMap");
        d.getInputClasses().add("com.google.common.collect.Multiset");
        d.getInputClasses().add("com.google.common.base.Objects");
        d.getInputClasses().add("com.google.common.collect.ImmutableMap");
        d.getInputClasses().add("com.google.common.collect.ImmutableList");
        d.getInputClasses().add("com.google.common.collect.PrimitiveArrays");
        d.getInputClasses().add("com.google.common.collect.ForwardingList");
        */

//        d.getInputClasses().add("com.google.common.collect.ImmutableMap");
/*
        d.getInputClasses().add("com.google.common.base.Optional");
        d.getInputClasses().add("com.google.common.io.LittleEndianDataInputStream");
        d.getInputClasses().add("com.google.common.primitives.Doubles");
        d.getInputClasses().add("com.google.common.hash.HashCode");
        d.getInputClasses().add("com.google.common.base.CharMatcher");
        d.getInputClasses().add("com.google.common.primitives.Ints");
        d.getInputClasses().add("com.google.common.collect.DiscreteDomain");
        d.getInputClasses().add("com.google.common.eventbus.EventBus");
        d.getInputClasses().add("com.google.common.base.Function");
        d.getInputClasses().add("com.google.common.cache.LocalCache");
*/
        d.unbundle();

        System.out.println();
        System.out.println("BUNDLE:");
        for ( String cName : d.getBundle() ) {
            System.out.println(cName);
        }

        int k = 0;
        for ( String className : d.getBundle() ) {
            if ( !className.contains("$") ) k++; //Inner or nested class are not counted
        }
        float pc = (float)k / (float)f.CompilationUnit().getMap().size() * 100;
        System.out.println("----------------------------------");
        System.out.println("This bundle uses the " + pc + "% of the library");



        System.out.println("----------------------------------");
        System.out.println("COMPILING BUNDLE... ");

        MavenBuilder rb = new MavenBuilder(up + "\\guava", up + "\\guava" + s);
        rb.setPhase(new String[]{"clean", "compile"});
        rb.setTimeOut(0);
        //rb.initPom(getOutputDir() + "/pom.xml");
        rb.runBuilder();

        System.out.println("----------------------------------");
        System.out.print("COMPILATION RESULT: ");
        if ( rb.getStatus() == -2 ) {
            System.out.println("The source failed to compile");
        } else if ( rb.getStatus() == -1 ) {
            System.out.println("The source failed to pass al tests");
        } else if ( rb.getStatus() == 0 ){
            System.out.println("Compilation successful");
        }
        System.out.println("----------------------------------");



    }



}
