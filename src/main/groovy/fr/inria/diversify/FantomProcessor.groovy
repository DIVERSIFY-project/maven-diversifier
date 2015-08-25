package fr.inria.diversify

import spoon.processing.AbstractProcessor
import spoon.reflect.declaration.CtClass
import spoon.reflect.declaration.CtMethod
import spoon.reflect.declaration.ModifierKind
import spoon.support.reflect.code.CtBlockImpl
import spoon.support.reflect.code.CtCodeSnippetStatementImpl
import spoon.support.reflect.code.CtInvocationImpl
import spoon.support.reflect.code.CtLocalVariableImpl
import spoon.support.reflect.declaration.CtMethodImpl

import java.lang.reflect.Method

import static fr.inria.diversify.utils.Looper.loop

/**
 * Created by nicolas on 25/08/2015.
 */
public class FantomProcessor extends AbstractProcessor<CtClass<?>> {

    public static final int FAKE_METHODS = 10;
    public static final int RANDOM_CALLS = 10;
    private Random random = new Random();
    def actionDictionary = ["do", "get", "add", "generate", "process", "create", "is", "to"]
    def objectDictionary = ["Method", "Value", "A", "Content", "Aray", "Hash"]
    def fantoms = [];

    @Override
    void process(CtClass<?> element) {
        fantoms.clear();

        Collection<Method> previous = new ArrayList<>(element.getMethods());

        // generate some fantom methods
        generateFantomMethod(element);

        // call fantom methods in random places
        addRandomCalls(element, previous);
        
        new ParentUpdater().scan(element);
    }

    private void generateFantomMethod(CtClass<?> element) {
        FAKE_METHODS.times {
            def name;
            loop {
                name = generateName();
            } until { element.getMethod(name) == null && !fantoms.contains(name) }

            CtMethod<?> method = new CtMethodImpl<>(
                    factory: getFactory(),
                    parent: element,
                    simpleName: name,
                    body: new CtBlockImpl(statements: [new CtCodeSnippetStatementImpl(value: "return \"\"")]),
                    type: factory.Type().createReference(Object.class)
            );
            fantoms.add(method.getSimpleName());
            element.addMethod(method);
        }
    }

    private void addRandomCalls(CtClass<?> element, List<CtMethod<?>> methods) {
        if (methods.isEmpty()) return;

        def names = [];
        RANDOM_CALLS.times {
            def methodName = fantoms.get(random.nextInt(fantoms.size()));
            def method = element.getMethod(methodName);

            // generate a variable name
            def name;
            loop {
                name = generateName();
            } until { !names.contains(name) }
            names.add(name);

            def localVariable = new CtLocalVariableImpl(
                    type: factory.Type().createReference(Object.class),
                    name: name,
                    defaultExpression: new CtInvocationImpl(executable: method.getReference()));

            // insert at a random point
            CtMethod insertMethod = methods[random.nextInt(methods.size())];

            if (!insertMethod.hasModifier(ModifierKind.STATIC) && insertMethod.getBody() != null && !insertMethod.getBody().statements.isEmpty())
                insertMethod.getBody().statements.add(random.nextInt(insertMethod.getBody().statements.size()), localVariable);
        }
    }

    private String generateName() {
        actionDictionary.get(random.nextInt(actionDictionary.size())) +
                objectDictionary.get(random.nextInt(objectDictionary.size()));
    }
}
