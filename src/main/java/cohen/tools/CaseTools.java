package cohen.tools;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;

public class CaseTools {
	public Set<CtVariableReference> getAccessVariable(CtCase cc){
		ExpressionTools et=new ExpressionTools();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		sva.addAll(et.getAccessVariable(cc.getCaseExpression()));
		List<CtStatement> ls=cc.getStatements();
		Tools t=new Tools();
		for(CtStatement cs:ls){
			sva.addAll(t.getVariableAccess(cs));
		}
		return sva;
	}

	public Set<CtVariableReference> getUpdateVariable(CtCase cc) {
		// TODO Auto-generated method stub
		ExpressionTools et=new ExpressionTools();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		sva.addAll(et.getUpdateVariable(cc.getCaseExpression()));
		List<CtStatement> ls=cc.getStatements();
		Tools t=new Tools();
		for(CtStatement cs:ls){
			sva.addAll(t.getVariableUpdate(cs));
		}
		return sva;
	}

	public CtTypeReference getTypeReturn(CtCase cc) {
		// TODO Auto-generated method stub
		CtTypeReference type;
		List<CtStatement> ls=cc.getStatements();
		Tools t=new Tools();
		for(CtStatement cs:ls){
			type=t.getTypeReturn(cs);
			if(type!=null)
				return type;
		}
		return null;
	}
}
