package extractors;

import org.apache.log4j.Logger;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by marodrig on 25/03/2015.
 */
public abstract class DependencyExtractor {

    public abstract void extract(Collection<CtTypeReference> dependency);
}
