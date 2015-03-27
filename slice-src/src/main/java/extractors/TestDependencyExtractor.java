package extractors;

import spoon.reflect.reference.CtTypeReference;

import java.util.Collection;

/**
 * Uses Junco coverage to extract test depending on the bundle
 * <p/>
 * Junco returns a Jacoco coverage file separated per test
 * <p/>
 * Created by marodrig on 25/03/2015.
 */
public class TestDependencyExtractor extends DependencyExtractor {



    @Override
    public void extract(Collection<CtTypeReference> dependency) {

    }

}
