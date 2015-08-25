package fr.inria.diversify

import spoon.processing.AbstractProcessor
import spoon.reflect.code.CtIf
import spoon.reflect.code.CtStatement
import spoon.reflect.code.UnaryOperatorKind
import spoon.support.reflect.code.CtBlockImpl
import spoon.support.reflect.code.CtUnaryOperatorImpl

/**
 * Created by nicolas on 21/08/2015.
 */
public class InvertIfProcessor extends AbstractProcessor<CtIf> {
    @Override
    public void process(CtIf ctIf) {
        // inverse conditions
        ctIf.setCondition(new CtUnaryOperatorImpl<Boolean>(kind: UnaryOperatorKind.NOT, operand: ctIf.getCondition()));

        // invert then and else
        CtStatement th = ctIf.getThenStatement();
        CtStatement el = ctIf.getElseStatement();

        ctIf.setThenStatement(el != null ? el : new CtBlockImpl<>());
        ctIf.setElseStatement(th);

        new ParentUpdater().scan(ctIf);
    }
}
