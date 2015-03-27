package processors;

import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;


/**
 * Class to extract package dependencies of a given POM object
 * <p/>
 * Created by marodrig on 05/03/2015.
 */
public class FieldExtractor extends ExtractorProcessor<CtField> {

    /**
     * Empty constructor to override strange default behavior
     */
    public FieldExtractor() {
        clearProcessedElementType();
        addProcessedElementType(CtField.class);
    }

    @Override
    protected ArrayList<CtTypeReference> dependencies(CtField field) {
        ArrayList<CtTypeReference> result = new ArrayList<CtTypeReference>();
        result.addAll(super.dependencies(field));
        result.add(field.getType());
        return result;
    }
}
