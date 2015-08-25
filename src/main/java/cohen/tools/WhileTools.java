package cohen.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtWhile;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;

public class WhileTools {
	public Set<CtVariableReference> getAccessVariable(CtWhile cw) {
		// TODO Auto-generated method stub
		ExpressionTools et=new ExpressionTools();
		Tools t=new Tools();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		sva.addAll(et.getAccessVariable(cw.getLoopingExpression()));
		sva.addAll(t.getVariableAccess(cw.getBody()));
		return sva;
	}

	public Set<CtVariableReference> getUpdateVariable(CtWhile cw) {
		// TODO Auto-generated method stub
		ExpressionTools et=new ExpressionTools();
		Tools t=new Tools();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		sva.addAll(et.getUpdateVariable(cw.getLoopingExpression()));
		sva.addAll(t.getVariableUpdate(cw.getBody()));
		return sva;
	}

	public CtTypeReference getTypeReturn(CtWhile cw) {
		// TODO Auto-generated method stub
		Tools t=new Tools();
		return t.getTypeReturn(cw.getBody());
	}
}
