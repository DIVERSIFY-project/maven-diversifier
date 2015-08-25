package fr.inria.diversify;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;

import java.util.Stack;

/**
 * Created by nicolas on 21/08/2015.
 */
public class ParentUpdater extends CtScanner {

	private Stack<CtElement> elements = new Stack<CtElement>();

	@Override
	protected void enter(CtElement e) {
		if (!elements.isEmpty()) {
			e.setParent(elements.peek());
		}
		elements.push(e);
		super.enter(e);
	}

	@Override
	protected void exit(CtElement e) {
		elements.pop();

		super.exit(e);
	}
}
