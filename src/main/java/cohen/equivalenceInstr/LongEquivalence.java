package cohen.equivalenceInstr;

import java.util.Random;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;

/**
*
* @author Thomas
* This class implements a Equivalence which transform Long to an equivalent instruction
* For example "5l" is transformed to "(100l - 95l)"
*
*/
public class LongEquivalence implements Equivalence {

	/**
	 * This method determinate an equivalent instruction for a long expression
	 *
	 * @param origin A long expression
	 *
	 * @return Return a string correspond to an equivalent instruction for a long expression
	 */
	public String getEquivalence(CtExpression origin){
		Random rand=new Random();
		long val=(Long)((CtLiteral) origin).getValue();
		return determinEquivalence(val,rand.nextInt(2)+1);
	}

	/**
	 * This method determinate an equivalent instruction for a long expression
	 *
	 * @param o_val A long value
	 * @param iter Number of parts contains in the equivalent instruction
	 *
	 * @return Return a string correspond to an equivalent instruction for a long expression
	 */
	public String determinEquivalence(Object o_val,int iter){
		Long val=(long)o_val;
		return "(long)("+(new DoubleEquivalence()).determinEquivalence(val.doubleValue(), iter)+")";
	}

}
