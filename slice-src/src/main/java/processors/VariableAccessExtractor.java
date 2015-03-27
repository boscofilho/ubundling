package processors;

import spoon.reflect.code.*;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;


/**
 * Class to extract package dependencies of a given POM object
 * <p/>
 * Created by marodrig on 05/03/2015.
 */
public class VariableAccessExtractor extends ExtractorProcessor<CtVariableAccess> {

    /**
     * Empty constructor to override strange default behavior
     */
    public VariableAccessExtractor() {
        clearProcessedElementType();
        addProcessedElementType(CtVariableAccess.class);
    }

    @Override
    protected ArrayList<CtTypeReference> dependencies(CtVariableAccess variableAccess) {

        ArrayList<CtTypeReference> result = new ArrayList<CtTypeReference>();
        result.addAll(super.dependencies(variableAccess));
        if ( variableAccess.getType() != null && variableAccess.getType().getQualifiedName() != null ) {
            result.add(variableAccess.getType());
        }
        return result;
    }
}
