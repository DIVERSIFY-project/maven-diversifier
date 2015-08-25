package cohen.tools;

import java.util.HashSet;
import java.util.Set;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.reference.CtVariableReference;

public class AssignementTools {

	public Set<CtVariableReference> getUpdateVariable(CtAssignment s) {
		Set<CtVariableReference> sva=new HashSet<CtVariableReference>();
		if(s.getAssigned() instanceof CtVariableAccess){
			sva.add(((CtVariableAccess)s.getAssigned()).getVariable());
		}
		return sva;
	}

}
