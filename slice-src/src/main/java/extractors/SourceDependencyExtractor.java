package extractors;

import org.apache.log4j.Logger;
import spoon.reflect.reference.CtTypeReference;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by marodrig on 25/03/2015.
 */
public class SourceDependencyExtractor extends DependencyExtractor {

    final static Logger logger = Logger.getLogger(DependencyExtractor.class);

    /**
     * List of classes that are in the resulting bundle
     */
    private HashSet<String> bundle;

    /**
     * Name of the package being unbundled
     */
    private String packageName;

    /**
     * Fully qualified name of the class for which dependencies are being analyzed
     */
    String analyzing;

    /**
     * list of packages about to be analyzed in the next step
     */
    HashSet<String> newlyFound;

    /**
     * Extract all dependencies of the  classes given as parameters
     *
     * @param classes Classes type references
     */
    @Override
    public void extract(Collection<CtTypeReference> classes) {

        if (classes == null) return;

        for (CtTypeReference ref : classes) {
            while (ref != null) {
                String s = ref.getQualifiedName();
                if (s.startsWith(packageName) && !bundle.contains(s)) {
                    newlyFound.add(s);
                    bundle.add(s);
                    try {
                        extract(ref.getSuperInterfaces()); //Search all super interfaces
                    } catch (spoon.SpoonException e) {
                        logger.warn("could not load interfaces for " + ref.getQualifiedName());
                    }

                    //Only analyze inheritance if the class is not in the bundle, otherwise it means we already did
                    try {
                        ref = ref.getSuperclass(); //include super class as well
                    } catch (spoon.SpoonException e) {
                        logger.warn("could not load super class for " + ref.getQualifiedName());
                    }
                } else break;
            }
        }
    }



    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAnalyzing() {
        return analyzing;
    }

    public void setAnalyzing(String analyzing) {
        this.analyzing = analyzing;
    }

    public void setNewlyFound(HashSet<String> newlyFound) {
        this.newlyFound = newlyFound;
    }

    public HashSet<String> getNewlyFound() {
        if (newlyFound == null) newlyFound = new HashSet<String>();
        return newlyFound;
    }

    public void setBundle(HashSet<String> bundle) {
        this.bundle = bundle;
    }

    public HashSet<String> getBundle() {
        return bundle;
    }

}
