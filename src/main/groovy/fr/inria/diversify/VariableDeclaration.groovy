package fr.inria.diversify

import spoon.processing.AbstractProcessor
import spoon.reflect.code.CtAssignment
import spoon.reflect.code.CtBlock
import spoon.reflect.code.CtLocalVariable
import spoon.reflect.code.CtStatement
import spoon.reflect.declaration.CtVariable
import spoon.reflect.declaration.ModifierKind

/**
 * Created by nicolas on 24/08/2015.
 */
class VariableDeclaration extends AbstractProcessor<CtBlock<?>> {

    @Override
    public void process(CtBlock<?> ctBlock) {
        for (CtStatement stmt : new ArrayList<CtStatement>(ctBlock.getStatements())) {
            if (stmt instanceof CtVariable) {
                CtLocalVariable variable = (CtLocalVariable) stmt;

                if (variable.getDefaultExpression() != null) {
                    CtAssignment assignment = getFactory().Code()
                            .createVariableAssignment(variable.getReference(), false,
                            variable.getDefaultExpression());
                    variable.insertAfter(assignment);

                    if (variable.hasModifier(ModifierKind.FINAL) || variable.getType().isPrimitive()) {
                        variable.setDefaultExpression(null);
                    } else {
                        variable.setDefaultExpression(getFactory().Code().createLiteral(null));
                    }
                }
            }
        }

        new ParentUpdater().scan(ctBlock);
    }
}
