package cohen.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;

public class DoTools {
	public Set<CtVariableReference> getAccessVariable(CtDo cd) {
		// TODO Auto-generated method stub
		ExpressionTools et=new ExpressionTools();
		Tools t=new Tools();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		sva.addAll(et.getAccessVariable(cd.getLoopingExpression()));
		sva.addAll(t.getVariableAccess(cd.getBody()));
		return sva;
	}

	public Set<CtVariableReference> getUpdateVariable(CtDo cd) {
		// TODO Auto-generated method stub
		ExpressionTools et=new ExpressionTools();
		Tools t=new Tools();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		sva.addAll(et.getUpdateVariable(cd.getLoopingExpression()));
		sva.addAll(t.getVariableUpdate(cd.getBody()));
		return sva;
	}

	public CtTypeReference getTypeReturn(CtDo cd) {
		// TODO Auto-generated method stub
		Tools t=new Tools();
		return t.getTypeReturn(cd.getBody());
	}
}
