package cohen.equivalenceInstr;

import java.util.Random;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;

/**
*
* @author Thomas
* This class implements a Equivalence which transform Character to an equivalent instruction
* For example "'a'" is transformed to "(100 - 3)"
*
*/
public class CharEquivalence implements Equivalence {

	/**
	 * This method determinate an equivalent instruction for a character expression
	 *
	 * @param origin A character expression
	 *
	 * @return Return a string correspond to an equivalent instruction for a character expression
	 */
	public String getEquivalence(CtExpression origin){
		Random rand=new Random();
		char val=(Character)((CtLiteral) origin).getValue();
		return determinEquivalence(val,rand.nextInt(2)+1);
	}

	/**
	 * This method determinate an equivalent instruction for a character expression
	 *
	 * @param o_val A character value
	 * @param iter Number of parts contains in the equivalent instruction
	 *
	 * @return Return a string correspond to an equivalent instruction for a character expression
	 */
	public String determinEquivalence(Object o_val,int iter){
		char val=(char)o_val;
		return "(char)("+(new DoubleEquivalence()).determinEquivalence(((Integer)(int)val).doubleValue(), iter)+")";
	}

}
