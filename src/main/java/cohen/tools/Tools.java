package cohen.tools;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtWhile;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.support.reflect.code.CtVariableAccessImpl;
import spoon.support.reflect.reference.CtVariableReferenceImpl;

public class Tools {
	public Set<CtVariableReference> getVariableAccess(CtStatement s){
		if(s instanceof CtBlock){
			CtBlock cb=(CtBlock)s;
			List<CtStatement> ls=cb.getStatements();
			Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
			for(CtStatement sTmp:ls){
				sva.addAll(traitementStatementVAccess(sTmp));
			}
			return sva;
		}
		return traitementStatementVAccess(s);
	}

	protected Set<CtVariableReference> traitementStatementVAccess(CtStatement s){
		if(s!=null){
			Class c=s.getClass();
			if(c!=null){
				String className=c.getSimpleName();
				if(className.equals("CtInvocationImpl")){
					return (new InvocationTools()).getAccessVariable((CtInvocation)s);
				}
				else if(className.equals("CtLocalVariableImpl")){
					return (new LocalVariableTools()).getAccessVariable((CtLocalVariable)s);
				}
				else if(className.equals("CtUnaryOperatorImpl")){
					return (new UnaryOperatorTools()).getAccessVariable((CtUnaryOperator)s);
				}
				else if(className.equals("CtIfImpl")){
					return (new IfTools()).getAccessVariable((CtIf)s);
				}
				else if(className.equals("CtSwitchImpl")){
					return (new SwitchTools()).getAccessVariable((CtSwitch)s);
				}
				else if(className.equals("CtForImpl")){
					return (new ForTools()).getAccessVariable((CtFor)s);
				}
				else if(className.equals("CtDoImpl")){
					return (new DoTools()).getAccessVariable((CtDo)s);
				}
				else if(className.equals("CtWhileImpl")){
					return (new WhileTools()).getAccessVariable((CtWhile)s);
				}
				else if(className.equals("CtTryImpl")){
					return (new TryTools()).getAccessVariable((CtTry)s);
				}
				else if(className.equals("CtReturnImpl")){
					return (new ReturnTools()).getAccessVariable((CtReturn)s);
				}
			}
		}
		return new HashSet<CtVariableReference>();
	}

	public Set<CtVariableReference> getVariableUpdate(CtStatement s){
		if(s instanceof CtBlock){
			CtBlock cb=(CtBlock)s;
			List<CtStatement> ls=cb.getStatements();
			Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
			for(CtStatement sTmp:ls){
				sva.addAll(traitementStatementVUpdate(sTmp));
			}
			return sva;
		}
		return traitementStatementVUpdate(s);
	}

	protected Set<CtVariableReference> traitementStatementVUpdate(CtStatement s) {
		// TODO Auto-generated method stub
		if(s!=null){
			Class c=s.getClass();
			if(c!=null){
				String className=c.getSimpleName();
				if(className.equals("CtAssignmentImpl")){
					return (new AssignementTools()).getUpdateVariable((CtAssignment)s);
				}
				if(className.equals("CtInvocationImpl")){
					return (new InvocationTools()).getUpdateVariable((CtInvocation)s);
				}
				else if(className.equals("CtLocalVariableImpl")){
					return (new LocalVariableTools()).getUpdateVariable((CtLocalVariable)s);
				}
				else if(className.equals("CtUnaryOperatorImpl")){
					return (new UnaryOperatorTools()).getUpdateVariable((CtUnaryOperator)s);
				}
				else if(className.equals("CtIfImpl")){
					return (new IfTools()).getUpdateVariable((CtIf)s);
				}
				else if(className.equals("CtSwitchImpl")){
					return (new SwitchTools()).getUpdateVariable((CtSwitch)s);
				}
				else if(className.equals("CtFor")){
					return (new ForTools()).getUpdateVariable((CtFor)s);
				}
				else if(className.equals("CtDo")){
					return (new DoTools()).getUpdateVariable((CtDo)s);
				}
				else if(className.equals("CtWhile")){
					return (new WhileTools()).getUpdateVariable((CtWhile)s);
				}
				else if(className.equals("CtTry")){
					return (new TryTools()).getUpdateVariable((CtTry)s);
				}
			}
		}
		return new HashSet<CtVariableReference>();
	}

	public Set<CtVariableReference> getLocalVariable(CtStatement s) {
		// TODO Auto-generated method stub
		if(s instanceof CtBlock){
			CtBlock cb=(CtBlock)s;
			List<CtStatement> ls=cb.getStatements();
			Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
			for(CtStatement sTmp:ls){
				sva.addAll(traitementStatementVLocal(sTmp));
			}
			return sva;
		}
		return traitementStatementVLocal(s);
	}

	protected Set<CtVariableReference> traitementStatementVLocal(CtStatement s) {
		// TODO Auto-generated method stub
		if(s!=null){
			Class c=s.getClass();
			if(c!=null){
				String className=c.getSimpleName();
				if(className.equals("CtLocalVariableImpl")){
					Set<CtVariableReference> cva=new HashSet<CtVariableReference>();
					cva.add(((CtLocalVariable)s).getReference());
					return cva;
				}
			}
		}
		return new HashSet<CtVariableReference>();
	}

	public CtTypeReference getTypeReturn(CtStatement s){
		if(s instanceof CtBlock){
			CtBlock cb=(CtBlock)s;
			List<CtStatement> ls=cb.getStatements();
			CtTypeReference type;
			for(CtStatement sTmp:ls){
				type=traitementStatementReturn(sTmp);
				if(type!=null)
					return type;
			}
			return null;
		}
		return traitementStatementReturn(s);
	}

	protected CtTypeReference traitementStatementReturn(CtStatement s){
		if(s!=null){
			Class c=s.getClass();
			if(c!=null){
				String className=c.getSimpleName();
				if(className.equals("CtReturnImpl")){
					return (new ReturnTools()).getTypeReturn((CtReturn)s);
				}
				else if(className.equals("CtIfImpl")){
					return (new IfTools()).getTypeReturn((CtIf)s);
				}
				else if(className.equals("CtSwitchImpl")){
					return (new SwitchTools()).getTypeReturn((CtSwitch)s);
				}
				else if(className.equals("CtFor")){
					return (new ForTools()).getTypeReturn((CtFor)s);
				}
				else if(className.equals("CtDo")){
					return (new DoTools()).getTypeReturn((CtDo)s);
				}
				else if(className.equals("CtWhile")){
					return (new WhileTools()).getTypeReturn((CtWhile)s);
				}
				else if(className.equals("CtTry")){
					return (new TryTools()).getTypeReturn((CtTry)s);
				}
			}
		}
		return null;
	}
}
