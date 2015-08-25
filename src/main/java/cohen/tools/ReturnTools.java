package cohen.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;

public class ReturnTools {

	public Set<CtVariableReference> getAccessVariable(CtReturn cr) {
		// TODO Auto-generated method stub
		Set<CtVariableReference> sva=new HashSet();
		ExpressionTools et=new ExpressionTools();
		sva.addAll(et.getAccessVariable(cr.getReturnedExpression()));
		return sva;
	}

	public CtTypeReference getTypeReturn(CtReturn s) {
		// TODO Auto-generated method stub
		CtMethod cm=s.getParent(CtMethod.class);
		return cm.getType();
	}

}
