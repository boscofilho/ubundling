package processors;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;


/**
 * Class to extract package dependencies of a given POM object.
 *
 * Is so specific because of a ¿bug? of Spoon not being able to detect a dependency in a class
 *
 * <p/>
 * Created by marodrig on 05/03/2015.
 */
public class InstanceOfExtractor extends ExtractorProcessor<CtBinaryOperator> {

    /**
     * Empty constructor to override strange default behavior
     */
    public InstanceOfExtractor() {
        clearProcessedElementType();
        addProcessedElementType(CtBinaryOperator.class);
    }

    @Override
    protected ArrayList<CtTypeReference> dependencies(CtBinaryOperator operator) {
        if ( operator.getKind().equals(BinaryOperatorKind.INSTANCEOF ) ) {
            ArrayList<CtTypeReference> result = new ArrayList<CtTypeReference>();
            result.addAll(super.dependencies(operator));
            result.add((CtTypeReference)((CtLiteral)operator.getRightHandOperand()).getValue());
            return result;
        }
        return new ArrayList<CtTypeReference>();
    }
}
