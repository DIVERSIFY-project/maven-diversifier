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
* This class implements a Equivalence which transform Double to an equivalent instruction
* For example "5.0" is transformed to "(1.5 + 3.5)"
*
*/
public class DoubleEquivalence implements Equivalence{

	/**
	 * This method determinate an equivalent instruction for a double expression
	 *
	 * @param origin A double expression
	 *
	 * @return Return a string correspond to an equivalent instruction for a double expression
	 */
	public String getEquivalence(CtExpression origin){
		Random rand=new Random();
		double val=(Double)((CtLiteral) origin).getValue();
		return determinEquivalence(val,rand.nextInt(2)+1);
	}

	/**
	 * This method determinate an equivalent instruction for a double expression
	 *
	 * @param o_val A double value
	 * @param iter Number of parts contains in the equivalent instruction
	 *
	 * @return Return a string correspond to an equivalent instruction for a double expression
	 */
	public String determinEquivalence(Object o_val,int iter){
		//Get double value
		double val=(double)o_val;

		if(iter==0){
			return Double.toString(val);
		}

		Random rand=new Random();

		//Get random first operand
		double op1=rand.nextDouble();

		//Get random sign
		int sign=rand.nextInt(3);

		//if the sign is division and the first operand is equals to 0, the first operand change to 1
		if(sign==2 && op1==0) op1=1;

		//Determinate second operand
		double op2=getOperand(val,op1,sign);

		//Get character represent the sign
		char op=getOperateur(sign);

		//Determinate the third operand
		double op3=calcOperand3(val, op1, op, op2);

		//Determinate the first operand with subdivise it to many operation
		String s_op1=determinEquivalence(op1, iter-1);

		//Determinate the second operand with subdivise it to many operation
	    String s_op2=determinEquivalence(op2, iter-1);

	    if(iter==1){
	    	s_op1="Double.parseDouble(\""+s_op1+"\")";
	    }

	  //Return double equivalence
		return getBinOp(s_op1,op,s_op2,op3);
	}

	/**
	 * Determinate third operand of the equivalence
	 *
	 * @param val Double value
	 * @param op1 The first operand
	 * @param op The sign
	 * @param op2 The second operand
	 *
	 * @return Return the third operand
	 */
	protected double calcOperand3(double val,double op1,char op,double op2){
		switch(op){
			case '+':
				if(op1==Double.POSITIVE_INFINITY || op1==Double.NEGATIVE_INFINITY || op2==Double.POSITIVE_INFINITY || op2==Double.NEGATIVE_INFINITY)
					return 0;
				return val-(op1+op2);
			case '*':
				if(op1==Double.POSITIVE_INFINITY || op1==Double.NEGATIVE_INFINITY || op2==Double.POSITIVE_INFINITY || op2==Double.NEGATIVE_INFINITY)
					return 0;
				return val-(op1*op2);
			case '-':
				if(op1==Double.POSITIVE_INFINITY || op1==Double.NEGATIVE_INFINITY || op2==Double.POSITIVE_INFINITY || op2==Double.NEGATIVE_INFINITY)
					return 0;
				return val-(op1-op2);
		}
		return 0;
	}

	/**
	 * Get the sign representation
	 *
	 * @param sign The sign
	 *
	 * @return Return the sign representation
	 */
	protected char getOperateur(int sign){
		switch(sign){
		case 0:
			return '+';
		case 1:
			return '*';
		case 2:
			return '-';
		}
		return ' ';
	}

	/**
	 * Generate the Double equivalence with 3 operands and a sign
	 *
	 * @param op1 The first operand
	 * @param sign The sign
	 * @param op2 The second operand
	 * @param op3 The third operand
	 *
	 * @return Return the Double equivalence
	 */
	protected String getBinOp(String op1,char sign,String op2,double op3){
		if(op3!=0){
			return "(("+op1+")"+sign+"("+op2+"))+"+op3;
		}
		return "("+op1+")"+sign+"("+op2+")";
	}

	/**
	 * Determinate second operand of the equivalence
	 *
	 * @param val Double value
	 * @param op1 The first operand
	 * @param sign The sign
	 *
	 * @return Return the second operand
	 */
	protected double getOperand(double val, double op1, int sign) {
		// TODO Auto-generated method stub
		//Init second operand
		double op2=0;

		switch(sign){
			//Plus case
			case 0:
				op2=val-op1;
				break;
			//Multiply case
			case 1:
				op2=val/op1;
				break;
			//Minus case
			case 2:
				op2=op1-val;
				break;
		}
		return op2;
	}
}