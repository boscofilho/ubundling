package processors;

import org.kevoree.log.Log;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;

import java.util.ArrayList;


/**
 * Class to extract package dependencies of a given POM object
 * <p/>
 * Created by marodrig on 05/03/2015.
 */
public class ExecutableExtractors extends ExtractorProcessor<CtExecutable> {

    /**
     * Empty constructor to override strange default behavior
     */
    public ExecutableExtractors() {
        clearProcessedElementType();
        addProcessedElementType(CtExecutable.class);
    }

    @Override
    protected ArrayList<CtTypeReference> dependencies(CtExecutable ctExecutable) {

        ArrayList<CtTypeReference> result = new ArrayList<CtTypeReference>();
        result.addAll(super.dependencies(ctExecutable));

        for (Object e : ctExecutable.getParameters()) {
            try {
                CtParameter ex = (CtParameter) e;
                CtTypeReference s = ex.getType();
                if (!result.contains(s)) result.add(s);
            } catch (NullPointerException ex) {
                Log.error("Unable to find type of: " + ex.toString());
            }
        }
        try {
            result.add(ctExecutable.getDeclaringType().getReference());
            if (!(ctExecutable instanceof CtConstructor)) {
                CtTypeReference s = ctExecutable.getType();
                if (!result.contains(s)) result.add(s);
            }
            return result;
        } catch (NullPointerException ex) {
            Log.error(ex.toString());
            throw ex;
        }
    }
}
