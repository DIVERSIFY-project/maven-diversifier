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

public class TryTools {
	public Set<CtVariableReference> getAccessVariable(CtTry ct) {
		// TODO Auto-generated method stub
		ExpressionTools et=new ExpressionTools();
		Tools t=new Tools();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		sva.addAll(t.getVariableAccess(ct.getBody()));
		List<CtCatch> lc=ct.getCatchers();
		CatchTools catchTools=new CatchTools();
		for(CtCatch cc:lc){
			sva.addAll(catchTools.getAccessVariable(cc));
		}
		return sva;
	}

	public Set<CtVariableReference> getUpdateVariable(CtTry ct) {
		// TODO Auto-generated method stub
		ExpressionTools et=new ExpressionTools();
		Tools t=new Tools();
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		sva.addAll(t.getVariableUpdate(ct.getBody()));
		List<CtCatch> lc=ct.getCatchers();
		CatchTools catchTools=new CatchTools();
		for(CtCatch cc:lc){
			sva.addAll(catchTools.getAccessVariable(cc));
		}
		return sva;
	}

	public CtTypeReference getTypeReturn(CtTry ct) {
		// TODO Auto-generated method stub
		ExpressionTools et=new ExpressionTools();
		Tools t=new Tools();
		CtTypeReference type=t.getTypeReturn(ct.getBody());
		if(type!=null)
			return type;
		List<CtCatch> lc=ct.getCatchers();
		CatchTools catchTools=new CatchTools();
		for(CtCatch cc:lc){
			type=catchTools.getTypeReturn(cc);
			if(type!=null)
				return type;
		}
		return null;
	}
}
