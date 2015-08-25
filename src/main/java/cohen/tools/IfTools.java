package cohen.tools;

import java.util.HashSet;
import java.util.Set;

import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;

public class IfTools {
	public Set<CtVariableReference> getAccessVariable(CtIf ci){
		ExpressionTools et=new ExpressionTools();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		sva.addAll(et.getAccessVariable(ci.getCondition()));
		Set<CtVariableReference> svaThen=(new Tools()).getVariableAccess(ci.getThenStatement());
		Set<CtVariableReference> svaElse=(new Tools()).getVariableAccess(ci.getElseStatement());
		sva.addAll(svaThen);
		sva.addAll(svaElse);
		return sva;
	}

	public Set<CtVariableReference> getUpdateVariable(CtIf ci) {
		// TODO Auto-generated method stub
		ExpressionTools et=new ExpressionTools();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		sva.addAll(et.getUpdateVariable(ci.getCondition()));
		Set<CtVariableReference> svaThen=(new Tools()).getVariableUpdate(ci.getThenStatement());
		Set<CtVariableReference> svaElse=(new Tools()).getVariableUpdate(ci.getElseStatement());
		sva.addAll(svaThen);
		sva.addAll(svaElse);
		return sva;
	}

	public CtTypeReference getTypeReturn(CtIf ci) {
		// TODO Auto-generated method stub
		CtTypeReference ref=(new Tools()).getTypeReturn(ci.getThenStatement());
		if(ref!=null){
			return ref;
		}
		return (new Tools()).getTypeReturn(ci.getElseStatement());
	}
}
