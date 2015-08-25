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
* This class implements a Equivalence which transform Float to an equivalent instruction
* For example "5f" is transformed to "(1.5f + 3.5f)"
*
*/
public class FloatEquivalence implements Equivalence{

	/**
	 * This method determinate an equivalent instruction for a float expression
	 *
	 * @param origin A float expression
	 *
	 * @return Return a string correspond to an equivalent instruction for a float expression
	 */
	public String getEquivalence(CtExpression origin){
		Random rand=new Random();
		float val=(float)((CtLiteral) origin).getValue();
		return determinEquivalence(val,rand.nextInt(2)+1);
	}

	/**
	 * This method determinate an equivalent instruction for a float expression
	 *
	 * @param o_val A float value
	 * @param iter Number of parts contains in the equivalent instruction
	 *
	 * @return Return a string correspond to an equivalent instruction for a float expression
	 */
	public String determinEquivalence(Object o_val,int iter){
		Float val=(float)o_val;
		return "(float)("+(new DoubleEquivalence()).determinEquivalence(val.doubleValue(), iter)+")";
	}

}