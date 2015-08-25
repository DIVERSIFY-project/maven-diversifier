package cohen.tools;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;

public class ForTools {

	public Set<CtVariableReference> getAccessVariable(CtFor cf) {
		// TODO Auto-generated method stub
		ExpressionTools et=new ExpressionTools();
		Tools t=new Tools();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		sva.addAll(et.getAccessVariable(cf.getExpression()));
		sva.addAll(getAccessVariableListStatement(cf.getForInit()));
		sva.addAll(getAccessVariableListStatement(cf.getForUpdate()));
		sva.addAll(t.getVariableAccess(cf.getBody()));
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

	public Set<CtVariableReference> getUpdateVariable(CtFor cf) {
		// TODO Auto-generated method stub
		ExpressionTools et=new ExpressionTools();
		Tools t=new Tools();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		sva.addAll(et.getUpdateVariable(cf.getExpression()));
		sva.addAll(getUpdateVariableListStatement(cf.getForInit()));
		sva.addAll(getUpdateVariableListStatement(cf.getForUpdate()));
		sva.addAll(t.getVariableUpdate(cf.getBody()));
		return sva;
	}

	private Set<CtVariableReference> getUpdateVariableListStatement(List<CtStatement> ls) {
		// TODO Auto-generated method stub
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		Tools t=new Tools();
		for(CtStatement cs:ls){
			sva.addAll(t.getVariableUpdate(cs));
		}
		return sva;
	}

	public CtTypeReference getTypeReturn(CtFor cf) {
		// TODO Auto-generated method stub
		Tools t=new Tools();
		return t.getTypeReturn(cf.getBody());
	}

}
