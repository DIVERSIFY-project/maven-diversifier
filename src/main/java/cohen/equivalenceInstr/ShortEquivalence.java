package cohen.equivalenceInstr;

import java.util.Random;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;

/**
*
* @author Thomas
* This class implements a Equivalence which transform short to an equivalent instruction
* For example "5" is transformed to "(10 - 5)"
*
*/
public class ShortEquivalence implements Equivalence {

	/**
	 * This method determinate an equivalent instruction for a short expression
	 *
	 * @param origin A short expression
	 *
	 * @return Return a string correspond to an equivalent instruction for a short expression
	 */
	public String getEquivalence(CtExpression origin){
		Random rand=new Random();
		short val=(Short)((CtLiteral) origin).getValue();
		return determinEquivalence(val,rand.nextInt(2)+1);
	}

	/**
	 * This method determinate an equivalent instruction for a short expression
	 *
	 * @param o_val A short value
	 * @param iter Number of parts contains in the equivalent instruction
	 *
	 * @return Return a string correspond to an equivalent instruction for a short expression
	 */
	public String determinEquivalence(Object o_val,int iter){
		Short val=(short)o_val;
		return "(short)("+(new DoubleEquivalence()).determinEquivalence(val.doubleValue(), iter)+")";
	}

}
