package cohen.process;

import cohen.equivalenceInstr.CharEquivalence;
import cohen.equivalenceInstr.DoubleEquivalence;
import cohen.equivalenceInstr.FloatEquivalence;
import cohen.equivalenceInstr.IntEquivalence;
import cohen.equivalenceInstr.LongEquivalence;
import cohen.equivalenceInstr.ShortEquivalence;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtAnnotationType;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtAssignmentImpl;
import spoon.support.reflect.code.CtLocalVariableImpl;
import spoon.support.reflect.code.CtReturnImpl;
import spoon.support.reflect.declaration.CtFieldImpl;

import java.util.Random;
import java.util.Set;

/**
 * @author Thomas
 *         This class implements a processor which transform instruction to an equivalence
 */
public class EquivalenceInstr extends AbstractCohenProcessor<CtLiteral> {

	@Override
	public boolean isToBeProcessed(CtLiteral candidate) {
		// Get Case parent
		CtElement elt = candidate.getParent(CtCase.class);

		//Get field parent
		CtField cf = ((CtField) (candidate.getParent(CtFieldImpl.class)));

		CtAnnotationType annotation = candidate.getParent(CtAnnotationType.class);

		Random rand = new Random();

		//Get modifier field
		Set<ModifierKind> mk = null;
		if (cf != null) {
			mk = cf.getModifiers();
		}

		return annotation == null && elt == null && (mk == null || !mk.contains(ModifierKind.STATIC)) && rand.nextInt(2) == 0;
	}

	;

	@Override
	public void process(CtLiteral origin) {
		//Get the type
		CtTypeReference type = origin.getType();

		//if literal have type and value
		if (type != null && origin.getValue() != null) {

			String exp = null;

			//if type is primitive
			if (type.isPrimitive()) {

				//Get equivalence
				exp = getExpressionPrimitiveType(type, origin);
			}

			//if have equivalence
			if (exp != null) {

				//cast type for return parent
				exp = castWithReturn(exp, type.getSimpleName(), origin);

				//isolate expression out of while, do and for block
				exp = isolateExp(origin, exp, type.getSimpleName());

				//Replace expression
				replaceExpression(origin, exp);
			}
		}
	}

	/**
	 * This method isolate expression out of while, do and for block
	 *
	 * @param origin The literal
	 * @param exp    The equivalence expression
	 * @param type   Type of literal
	 * @return Return a new expression
	 */
	protected String isolateExp(CtLiteral origin, String exp, String type) {
		// TODO Auto-generated method stub
		//Get method parent
		CtMethod cm = ((CtMethod) origin.getParent(CtMethod.class));

		//Get while parent
		CtWhile cw = ((CtWhile) origin.getParent(CtWhile.class));

		//literal have method parent and while parent
		if (cm != null && cw != null) {

			//Get the type
			CtTypeReference typeRet = getBasiqueType(type, origin);

			String s_type;
			if (typeRet != null) {
				s_type = typeRet.getSimpleName();
			} else {
				s_type = type;
			}

			//Get constant name
			String nameConst = nameConstant();

			//insert code isolation
			CtCodeSnippetStatement snippet = getFactory().Core().createCodeSnippetStatement();
			snippet.setValue("final " + s_type + " " + nameConst + "=" + exp);
			cm.getBody().insertBegin(snippet);
			return nameConst;
		}
		return exp;
	}

	/**
	 * Get equivalence for literal primitive type
	 *
	 * @param type   Type of literal
	 * @param origin The literal
	 * @return Return equivalence for the literal
	 */
	protected String getExpressionPrimitiveType(CtTypeReference type, CtExpression origin) {
		switch (type.getSimpleName()) {
		case "int":
			return (new IntEquivalence()).getEquivalence(origin);
		case "float":
			return (new FloatEquivalence()).getEquivalence(origin);
		case "short":
			return (new ShortEquivalence()).getEquivalence(origin);
		case "long":
			return (new LongEquivalence()).getEquivalence(origin);
		case "double":
			return (new DoubleEquivalence()).getEquivalence(origin);
		case "char":
			return (new CharEquivalence()).getEquivalence(origin);
		}
		return null;
	}

	/**
	 * Get type of the parent
	 *
	 * @param type   Literal type
	 * @param origin The literal
	 * @return Return the type of the parent
	 */
	protected CtTypeReference getBasiqueType(String type, CtExpression origin) {
		//Get the parent of literal
		CtElement elt = origin.getParent();

		//Get class of parent
		Class eltClass = elt.getClass();

		CtTypeReference typeRet = null;

		if (eltClass.equals(CtReturnImpl.class)) {
			typeRet = ((CtMethod) origin.getParent(CtMethod.class)).getType();
		} else if (eltClass.equals(CtFieldImpl.class)) {
			CtField cf = ((CtField) (origin.getParent(eltClass)));
			typeRet = cf.getType();
		} else if (eltClass.equals(CtLocalVariableImpl.class)) {
			typeRet = ((CtLocalVariable) (origin.getParent(eltClass))).getType();
		} else if (eltClass.equals(CtAssignmentImpl.class)) {
			typeRet = ((CtAssignment) (origin.getParent(eltClass))).getType();
		}
		return typeRet;
	}

	/**
	 * Get equivalence expression with the cast for the return
	 *
	 * @param exp    Equivalent expression
	 * @param type   Literal type
	 * @param origin The literal
	 * @return Return cast expression
	 */
	protected String castWithReturn(String exp, String type, CtExpression origin) {

		CtTypeReference typeRet = getBasiqueType(type, origin);

		if (typeRet != null) {
			String s_typeRet = typeRet.getSimpleName();

			if (typeRet.isPrimitive() && type != s_typeRet) {
				return "(" + s_typeRet + ")(" + exp + ")";
			}
		}
		return exp;
	}

	/**
	 * Replace literal by the equivalent expression
	 *
	 * @param origin The literal
	 * @param exp    Equivalent expression
	 */
	protected void replaceExpression(CtLiteral origin, String exp) {
		CtCodeSnippetExpression<CtBinaryOperator> snippet = getFactory().Core().createCodeSnippetExpression();
		snippet.setValue(exp);
		snippet.setParent(origin.getParent());
		origin.replace(snippet);
	}

	protected String getTypeName(CtLiteral exp) {
		return exp.getClass().getSimpleName();
	}

}
