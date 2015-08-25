package cohen.equivalenceInstr;

import java.util.Random;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;

public interface Equivalence {
	public String getEquivalence(CtExpression origin);

	public String determinEquivalence(Object val,int iter);
}
