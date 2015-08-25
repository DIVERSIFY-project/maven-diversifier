package cohen.process;

import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;

/**
 * 
 * @author Thomas
 * 
 * This class is reversing
 */
public class ReverseIf extends AbstractCohenProcessor<CtIf> {

	@Override
	/**
	 * 
	 * @param arg0 CtIf to process
	 */
	public void process(CtIf arg0) {
		// Create an if statement from factory
		CtIf ci=getFactory().Core().createIf();
		CtExpression old=arg0.getCondition();
		
		// Set conditional of if statement
		CtCodeSnippetExpression ce=getFactory().Core().createCodeSnippetExpression();
		ce.setValue("!("+old.toString()+")");
		ci.setCondition(ce);
		
		// Set then and else blocks
		ci.setThenStatement(arg0.getElseStatement());
		ci.setElseStatement(arg0.getThenStatement());
		
		// Replace the right block
		arg0.replace(ci);
	}

}
