package processors;

import extractors.DependencyExtractor;
import org.apache.log4j.Logger;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;

import java.util.*;


/**
 * Class to extract package dependencies of a given POM object
 * <p/>
 * Created by marodrig on 05/03/2015.
 */
public abstract class ExtractorProcessor<E extends CtElement> extends AbstractProcessor<E> {

    final static Logger logger = Logger.getLogger(DependencyExtractor.class);

    public DependencyExtractor getExtractor() {
        return extractor;
    }

    public void setExtractor(DependencyExtractor extractor) {
        this.extractor = extractor;
    }

    DependencyExtractor extractor;

    /**
     * Empty constructor to override strange default behavior
     */
    public ExtractorProcessor() {
        clearProcessedElementType();
        //addProcessedElementType(E.class);
    }

    @Override
    public void process(E e) {
        try {
            extractor.extract(dependencies(e));
        } catch (Exception ex) {
            logger.warn("Exception", ex);
        }
    }

    /**
     * Returns the types fully qualified names that this elements extract on
     */
    protected ArrayList<CtTypeReference> dependencies(E e) {
        ArrayList<CtTypeReference> result = new ArrayList<CtTypeReference>();
        for (CtAnnotation a : e.getAnnotations()) result.add(a.getAnnotationType());
        result.add(e.getPosition().getCompilationUnit().getMainType().getReference());
        return result;
    }


}
