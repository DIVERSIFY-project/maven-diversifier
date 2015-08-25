package cohen.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.reference.CtVariableReference;

public class ExpressionTools {

	public Set<CtVariableReference> getAccessVariable(CtExpression ce){
		if(ce!=null){
			Class c=ce.getClass();
			if(c!=null){
				String className=ce.getClass().getSimpleName();
				if(className.equals("CtVariableAccessImpl")){
					HashSet<CtVariableReference> s=new HashSet<CtVariableReference>();
					s.add(((CtVariableAccess)ce).getVariable());
					return s;
				}
				else if(className.equals("CtBinaryOperatorImpl")){
					return (new BinOpTools()).getAccessVariable((CtBinaryOperator)ce);
				}
				else if(className.equals("CtInvocationImpl")){
					return (new InvocationTools()).getAccessVariable((CtInvocation)ce);
				}
				else if(className.equals("CtFieldAccessImpl")){
					return (new FieldTools()).getAccessVariable((CtFieldAccess)ce);
				}
			}
		}
		return new HashSet<CtVariableReference>();
	}

	public Set<CtVariableReference> getUpdateVariable(CtExpression ce) {
		// TODO Auto-generated method stub
		if(ce!=null){
			Class c=ce.getClass();
			if(c!=null){
				String className=c.getSimpleName();
				if(className.equals("CtBinaryOperatorImpl")){
					return (new BinOpTools()).getUpdateVariable((CtBinaryOperator)ce);
				}
				else if(className.equals("CtVariableAccessImpl")){
					HashSet<CtVariableReference> s=new HashSet<CtVariableReference>();
					s.add(((CtVariableAccess)ce).getVariable());
					return s;
				}
			}
		}
		return new HashSet<CtVariableReference>();
	}
}
