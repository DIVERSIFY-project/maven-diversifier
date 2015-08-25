package cohen.tools;

import java.util.Set;

import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.reference.CtVariableReference;

public class UnaryOperatorTools {
	public Set<CtVariableReference> getAccessVariable(CtUnaryOperator cuo){
		ExpressionTools et=new ExpressionTools();
		return et.getAccessVariable(cuo.getOperand());
	}

	public Set<CtVariableReference> getUpdateVariable(CtUnaryOperator cuo) {
		// TODO Auto-generated method stub
		ExpressionTools et=new ExpressionTools();
		return et.getUpdateVariable(cuo.getOperand());
	}
}
