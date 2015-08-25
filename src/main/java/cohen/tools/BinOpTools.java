package cohen.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.reference.CtVariableReference;

public class BinOpTools {
	public Set<CtVariableReference> getAccessVariable(CtBinaryOperator cbo){
		ExpressionTools et=new ExpressionTools();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		sva.addAll(et.getAccessVariable(cbo.getLeftHandOperand()));
		sva.addAll(et.getAccessVariable(cbo.getRightHandOperand()));
		return sva;
	}

	public Set<CtVariableReference> getUpdateVariable(CtBinaryOperator cbo) {
		// TODO Auto-generated method stub
		ExpressionTools et=new ExpressionTools();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		sva.addAll(et.getUpdateVariable(cbo.getLeftHandOperand()));
		sva.addAll(et.getUpdateVariable(cbo.getRightHandOperand()));
		return sva;
	}
}
