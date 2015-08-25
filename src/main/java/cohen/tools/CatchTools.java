package cohen.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;

public class CatchTools {
	public Set<CtVariableReference> getAccessVariable(CtCatch cc) {
		// TODO Auto-generated method stub
		ExpressionTools et=new ExpressionTools();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		sva.addAll(getAccessVariableListStatement(((CtBlock)cc.getBody()).getStatements()));
		return sva;
	}

	protected Set<CtVariableReference> getAccessVariableListStatement(List<CtStatement> ls){
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		Tools t=new Tools();
		for(CtStatement cs:ls){
			sva.addAll(t.getVariableAccess(cs));
		}
		return sva;
	}

	public CtTypeReference getTypeReturn(CtCatch cc) {
		// TODO Auto-generated method stub
		Tools t=new Tools();
		return t.getTypeReturn(cc.getBody());
	}
}
