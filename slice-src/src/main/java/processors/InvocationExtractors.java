package processors;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;


/**
 * Class to extract package dependencies of a given POM object
 * <p/>
 * Created by marodrig on 05/03/2015.
 */
public class InvocationExtractors extends ExtractorProcessor<CtInvocation> {

    /**
     * Empty constructor to override strange default behavior
     */
    public InvocationExtractors() {
        clearProcessedElementType();
        addProcessedElementType(CtInvocation.class);
    }

    @Override
    protected ArrayList<CtTypeReference> dependencies(CtInvocation ctInvocation) {

        ArrayList<CtTypeReference> result = new ArrayList<CtTypeReference>();
        result.addAll(super.dependencies(ctInvocation));
        result.add(ctInvocation.getExecutable().getDeclaringType());

        for (Object e : ctInvocation.getArguments()) {
            CtExpression ex = (CtExpression)e;
            if ( !result.contains(ex.getType()) ) result.add(ex.getType());

        }
        CtTypeReference s = ctInvocation.getType();
        if ( !result.contains(s) ) result.add(s);
        return result;
    }
}
