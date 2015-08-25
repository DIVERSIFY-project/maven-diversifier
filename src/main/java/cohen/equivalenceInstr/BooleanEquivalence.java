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
 * This class implements a Equivalence which transform Boolean to an equivalent instruction
 * For example "true" is transformed to "(5-2)==3"
 *
 */
public class BooleanEquivalence implements Equivalence {

	/**
	 * This method determinate an equivalent instruction for a boolean expression
	 *
	 * @param origin A boolean expression
	 *
	 * @return Return a string correspond to an equivalent instruction for a boolean expression
	 */
	public String getEquivalence(CtExpression origin){
		Random rand=new Random();
		boolean val=(Boolean)((CtLiteral) origin).getValue();
		return determinEquivalence(val,rand.nextInt(2)+ 1);
	}

	/**
	 * This method determinate an equivalent instruction for a boolean expression
	 *
	 * @param o_val A boolean value
	 * @param iter Number of parts contains in the equivalent instruction
	 *
	 * @return Return a string correspond to an equivalent instruction for a boolean expression
	 */
	@Override
	public String determinEquivalence(Object o_val, int iter) {
		// TODO Auto-generated method stub
		Random rand=new Random();

		// Get boolean value
		boolean val=(boolean)o_val;

		//Get random first operand
		Integer op1=rand.nextInt();

		//Get random sign
		int sign=rand.nextInt(4);

		//Determinate second operand
		Integer op2=getOperand(val,op1,sign);

		//Determinate the first operand with subdivise it to many operation
		String s_op1=(new DoubleEquivalence()).determinEquivalence(op1.doubleValue(), iter);

		//Determinate the second operand with subdivise it to many operation
		String s_op2=(new DoubleEquivalence()).determinEquivalence(op2.doubleValue(), iter);

		//Return boolean equivalence
		return getBinOp(s_op1, sign, s_op2);
	}

	/**
	 * Generate the Boolean equivalence with 2 operands and a sign
	 *
	 * @param op1 The first operand
	 * @param sign The sign
	 * @param op2 The second operand
	 *
	 * @return Return the Boolean equivalence
	 */
	protected String getBinOp(String op1,int sign,String op2){
		switch(sign){
		//Equals case
		case 0: return "(("+op1+")==("+op2+"))";
		//Not equals case
		case 1: return "(("+op1+")!=("+op2+"))";
		//Greater case
		case 2: return "(("+op1+")>("+op2+"))";
		//Less case
		case 3: return "(("+op1+")<("+op2+"))";
		}
		return "";
	}

	/**
	 * Determinate second operand of the equivalence
	 *
	 * @param val Boolean value
	 * @param op1 The first operand
	 * @param sign The sign
	 *
	 * @return Return the second operand
	 */
	protected int getOperand(boolean val, int op1, int sign) {
		// TODO Auto-generated method stub
		if(val){
			//Get second operand if value is true
			return getOperandTrue(op1, sign);
		}
		//Get second operand if value is false
		return getOperandFalse(op1, sign);
	}

	/**
	 * Determinate second operand of the equivalence if boolean value is true
	 *
	 * @param op1 The first operand
	 * @param sign The sign
	 *
	 * @return Return the second operand
	 */
	protected int getOperandTrue(int op1,int sign){
		//Init second operand
		int op2=0;

		switch(sign){
		//Equals case
		case 0:
			// The second operand equals to the first operand
			op2=op1;
			break;
		//Not equals case
		case 1:
			// The second operand not equals to the first operand
			op2=op1 + 1;
			break;
		//Greater case
		case 2:
			// The second operand less than the first operand
			op2=op1-1;
			break;
		//Less case
		case 3:
			// The second operand greater than the first operand
			op2=op1+1;
			break;
		}
		return op2;
	}

	/**
	 * Determinate second operand of the equivalence if boolean value is false
	 *
	 * @param op1 The first operand
	 * @param sign The sign
	 *
	 * @return Return the second operand
	 */
	protected int getOperandFalse(int op1,int sign){
		//Init second operand
		int op2=0;

		switch(sign){
		//Equals case
		case 0:
			// The second operand not equals to the first operand
			op2=op1+1;
			break;
		//Not equals case
		case 1:
			// The second operand equals to the first operand
			op2=op1;
			break;
		//Greater case
		case 2:
			// The second operand greater than the first operand
			op2=op1+1;
			break;
		//Less case
		case 3:
			// The second operand less than the first operand
			op2=op1-1;
			break;
		}
		return op2;
	}
}
