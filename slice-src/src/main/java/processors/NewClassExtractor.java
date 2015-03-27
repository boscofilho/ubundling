package processors;

import spoon.reflect.code.CtNewClass;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;


/**
 * Class to extract package dependencies of a given POM object
 * <p/>
 * Created by marodrig on 05/03/2015.
 */
public class NewClassExtractor extends ExtractorProcessor<CtNewClass> {

    /**
     * Empty constructor to override strange default behavior
     */
    public NewClassExtractor() {
        clearProcessedElementType();
        addProcessedElementType(CtNewClass.class);
    }

    @Override
    protected ArrayList<CtTypeReference> dependencies(CtNewClass c) {
        ArrayList<CtTypeReference> result = new ArrayList<CtTypeReference>();
        result.addAll(super.dependencies(c));
        result.add(c.getType());
        return result;
    }
}
