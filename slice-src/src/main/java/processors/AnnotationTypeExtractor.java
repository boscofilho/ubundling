package processors;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;


/**
 * Class to extract package dependencies of a given POM object
 * <p/>
 * Created by marodrig on 05/03/2015.
 */
public class AnnotationTypeExtractor extends ExtractorProcessor<CtAnnotationType> {

    /**
     * Empty constructor to override strange default behavior
     */
    public AnnotationTypeExtractor() {
        clearProcessedElementType();
        addProcessedElementType(CtAnnotationType.class);
    }

    @Override
    protected ArrayList<CtTypeReference> dependencies(CtAnnotationType field) {
        ArrayList<CtTypeReference> result = new ArrayList<CtTypeReference>();
        result.addAll(super.dependencies(field));
        return result;
    }
}
