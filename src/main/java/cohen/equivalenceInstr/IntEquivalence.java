package cohen.equivalenceInstr;

import java.util.Random;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;

/**
*
* @author Thomas
* This class implements a Equivalence which transform Integer to an equivalent instruction
* For example "5" is transformed to "(100 - 95)"
*
*/
public class IntEquivalence implements Equivalence{

	/**
	 * This method determinate an equivalent instruction for a integer expression
	 *
	 * @param origin A integer expression
	 *
	 * @return Return a string correspond to an equivalent instruction for a integer expression
	 */
	public String getEquivalence(CtExpression origin){
		Random rand=new Random();
		int val=(Integer)((CtLiteral) origin).getValue();
		return determinEquivalence(val,rand.nextInt(2)+1);
	}

	/**
	 * This method determinate an equivalent instruction for a integer expression
	 *
	 * @param o_val A integer value
	 * @param iter Number of parts contains in the equivalent instruction
	 *
	 * @return Return a string correspond to an equivalent instruction for a integer expression
	 */
	public String determinEquivalence(Object o_val,int iter){
		Integer val=(int)o_val;
		return "(int)("+(new DoubleEquivalence()).determinEquivalence(val.doubleValue(), iter)+")";
	}

}
