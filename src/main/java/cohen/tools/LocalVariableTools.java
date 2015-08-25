package cohen.tools;

import java.util.HashSet;
import java.util.Set;

import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.reference.CtVariableReference;

public class LocalVariableTools {
	public Set<CtVariableReference> getAccessVariable(CtLocalVariable clv){
		ExpressionTools et=new ExpressionTools();
		return et.getAccessVariable(clv.getDefaultExpression());
	}

	public Set<CtVariableReference> getUpdateVariable(CtLocalVariable clv) {
		// TODO Auto-generated method stub
		ExpressionTools et=new ExpressionTools();
		return et.getUpdateVariable(clv.getDefaultExpression());
	}
}
