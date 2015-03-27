package processors;

import spoon.reflect.declaration.CtInterface;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;

/**
 * Class to extract package dependencies of a given POM object
 * <p/>
 * Created by marodrig on 05/03/2015.
 */
public class InterfaceExtractor extends ExtractorProcessor<CtInterface> {

    /**
     * Empty constructor to override strange default behavior
     */
    public InterfaceExtractor() {
        clearProcessedElementType();
        addProcessedElementType(CtInterface.class);
    }

    @Override
    protected ArrayList<CtTypeReference> dependencies(CtInterface c) {
        ArrayList<CtTypeReference> result = new ArrayList<CtTypeReference>();
        result.addAll(super.dependencies(c));
        return result;
    }
}

