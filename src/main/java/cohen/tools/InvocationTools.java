package cohen.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.reference.CtVariableReference;

public class InvocationTools {

	public Set<CtVariableReference> getAccessVariable(CtInvocation ci){
		CtExpression ceInv=ci.getTarget();
		List<CtExpression> lce=ci.getArguments();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		ExpressionTools et=new ExpressionTools();
		sva.addAll(et.getAccessVariable(ceInv));
		for(CtExpression ce:lce){
			Set<CtVariableReference> svaTmp=et.getAccessVariable(ce);
			sva.addAll(svaTmp);
		}
		return sva;
	}

	public Set<CtVariableReference> getUpdateVariable(CtInvocation ci) {
		// TODO Auto-generated method stub
		List<CtExpression> lce=ci.getArguments();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		ExpressionTools et=new ExpressionTools();
		for(CtExpression ce:lce){
			Set<CtVariableReference> svaTmp=et.getUpdateVariable(ce);
			sva.addAll(svaTmp);
		}
		return sva;
	}
}
