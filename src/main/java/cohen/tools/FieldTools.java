package cohen.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.reference.CtVariableReference;

public class FieldTools {

	public Set<CtVariableReference> getAccessVariable(CtFieldAccess cf) {
		// TODO Auto-generated method stub
		CtExpression ceInv=cf.getTarget();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		ExpressionTools et=new ExpressionTools();
		sva.addAll(et.getAccessVariable(ceInv));
		return sva;
	}

}
