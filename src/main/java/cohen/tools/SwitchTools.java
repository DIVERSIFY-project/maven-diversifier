package cohen.tools;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;

public class SwitchTools {
	public Set<CtVariableReference> getAccessVariable(CtSwitch cs){
		ExpressionTools et=new ExpressionTools();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		sva.addAll(et.getAccessVariable(cs.getSelector()));
		List<CtCase> lcc=cs.getCases();
		for(CtCase cc:lcc){
			sva.addAll((new CaseTools()).getAccessVariable(cc));
		}
		return sva;
	}

	public Set<CtVariableReference> getUpdateVariable(CtSwitch cs) {
		// TODO Auto-generated method stub
		ExpressionTools et=new ExpressionTools();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		sva.addAll(et.getUpdateVariable(cs.getSelector()));
		List<CtCase> lcc=cs.getCases();
		for(CtCase cc:lcc){
			sva.addAll((new CaseTools()).getUpdateVariable(cc));
		}
		return sva;
	}

	public CtTypeReference getTypeReturn(CtSwitch cs) {
		// TODO Auto-generated method stub
		CtTypeReference type;
		List<CtCase> lcc=cs.getCases();
		for(CtCase cc:lcc){
			type=(new CaseTools()).getTypeReturn(cc);
			if(type!=null)
				return type;
		}
		return null;
	}
}
