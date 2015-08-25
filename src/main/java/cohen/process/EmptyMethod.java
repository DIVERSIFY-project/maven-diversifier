package cohen.process;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.code.CtBreakImpl;
import spoon.support.reflect.code.CtContinueImpl;
import spoon.support.reflect.code.CtDoImpl;
import spoon.support.reflect.code.CtForImpl;
import spoon.support.reflect.code.CtIfImpl;
import spoon.support.reflect.code.CtReturnImpl;
import spoon.support.reflect.code.CtThrowImpl;
import spoon.support.reflect.code.CtTryImpl;
import spoon.support.reflect.code.CtWhileImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author Thomas
 *         This class implements a processor which add empty method and unused object
 */
public class EmptyMethod extends AbstractCohenProcessor<CtMethod> {

	@Override
	/**
	 *
	 * @param m CtMethod to process
	 */
	public void process(CtMethod m) {
		try {
			// Determine if method is static or not
			boolean staticMethod = isStatic(m);

			// Retrieve its body
			CtBlock cb = m.getBody();

			// If block is not empty, process it
			if (cb != null) {
				// Retrieve statements
				List<CtStatement> statements = cb.getStatements();
				statements = getAllStatements(statements);

				// Add empty method and unused object to theses statements
				addCallEmptyMethod(m.getParent(CtClass.class), statements, staticMethod);
				addCallUnusedObject(m.getParent(CtClass.class), statements);
			}
		} catch (ClassCastException ex) {
			System.out.println(ex.toString());
		}
	}

	/**
	 * @param statements Statements from which retrieve statements
	 * @return Statements retrieved
	 */
	protected List<CtStatement> getAllStatements(List<CtStatement> statements) {
		List<CtStatement> lcs = new ArrayList<CtStatement>();
		for (CtStatement s : statements) {
			lcs.addAll(getAllStatements(s));
		}
		return lcs;
	}

	/**
	 * @param statement Statement to add
	 * @return Statements retrieved
	 */
	protected List<CtStatement> getAllStatements(CtStatement s) {
		List<CtStatement> lcs = new ArrayList<CtStatement>();
		Class cs = s.getClass();

		// If s is a block, add the block to the list
		if (cs.equals(CtBlockImpl.class)) {
			CtBlock cb = (CtBlock) s;
			lcs.addAll(cb.getStatements());
		} else {
			// If there is a while, add it and its body to list
			if (cs.equals(CtWhileImpl.class)) {
				lcs.add(s);
				CtBlock cb = (CtBlock) ((CtWhile) s).getBody();
				lcs.addAll(getAllStatements(cb.getStatements()));
			}
			// If do...while, add it and its body to list
			else if (cs.equals(CtDoImpl.class)) {
				lcs.add(s);
				CtBlock cb = (CtBlock) ((CtDo) s).getBody();
				lcs.addAll(getAllStatements(cb.getStatements()));
			}
			// If for ..., add it and its body to list
			else if (cs.equals(CtForImpl.class)) {
				lcs.add(s);
				CtBlock cb = (CtBlock) ((CtFor) s).getBody();
				lcs.addAll(getAllStatements(cb.getStatements()));
			}
			// If if...else ..., add its entire block (if/then/else) to the list
			else if (cs.equals(CtIfImpl.class)) {
				CtIf ci = (CtIf) s;
				CtStatement cbt = ci.getThenStatement();
				CtStatement cbe = ci.getElseStatement();
				if (cbt != null) {
					lcs.addAll(getAllStatements(cbt));
				}
				if (cbe != null) {
					lcs.addAll(getAllStatements(cbe));
				}
			}
			// Else just add it
			else {
				lcs.add(s);
			}
		}
		return lcs;
	}

	/**
	 * @param m Method to scan
	 * @return True if method is true, else false
	 */
	protected boolean isStatic(CtMethod m) {
		Set<ModifierKind> modifier = m.getModifiers();
		return modifier.contains(ModifierKind.STATIC);
	}

	/**
	 * @param c          Class concerned by the useless adds
	 * @param statements Statements in which the method have to add useless statements
	 */
	private void addCallUnusedObject(CtClass c, List<CtStatement> statements) {
		// Determine ramdomly where to add useless adds
		Random rand = new Random();
		int nbCall = rand.nextInt((statements.size() / 2) + 1);
		for (int i = 0; i < nbCall; i++) {
			int posStatements = rand.nextInt(statements.size());
			CtStatement cs = statements.get(posStatements);
			Class classe = cs.getClass();
			addCallUnusedObjectAt(c, cs);
		}
	}

	/**
	 * @param c  Class concerned by the useless adds
	 * @param cs Statement to which we determine randomly if we had an useless object or not
	 */
	private void addCallUnusedObjectAt(CtClass c, CtStatement cs) {
		CtCodeSnippetStatement snippet = getFactory().Core().createCodeSnippetStatement();
		Random rand = new Random();
		snippet.setValue("int " + nameConstant() + " = " + rand.nextInt());

		snippet.setParent(cs.getParent());

		// Determine if cs is the last call of a method (for example return)
		// if it is, insert useless statement before it
		// else, insert useless statement after it
		if (unreachable(cs)) {
			cs.insertBefore(snippet);
		} else {
			cs.insertAfter(snippet);
		}
	}

	/**
	 * @param Class        concerned by the useless adds
	 * @param statements   Statements concerned by the useless adds
	 * @param staticMethod Determine if we create a static or non static method
	 */
	protected void addCallEmptyMethod(CtClass c, List<CtStatement> statements, boolean staticMethod) {
		Random rand = new Random();
		int nbCall = rand.nextInt((statements.size() / 2) + 1);

		// Randomly choose where to add useless statement
		for (int i = 0; i < nbCall; i++) {
			int posStatements = rand.nextInt(statements.size());
			CtStatement cs = statements.get(posStatements);
			Class classe = cs.getClass();
			addCallEmptyMethodAt(c, cs, staticMethod);
		}
	}

	/**
	 * @param Class        concerned by the useless adds
	 * @param statements   Statements concerned by the useless adds
	 * @param staticMethod Determine if we create a static or non static method
	 */
	protected void addCallEmptyMethodAt(CtClass c, CtStatement cs, boolean staticMethod) {
		// Create an empty method
		CtMethod cm = createEmptyMethod(c, staticMethod);
		CtCodeSnippetStatement snippet = getFactory().Core().createCodeSnippetStatement();

		// Determine its call
		if (staticMethod) {
			snippet.setValue(c.getSimpleName() + "." + cm.getSimpleName() + "()");
		} else {
			snippet.setValue(cm.getSimpleName() + "()");
		}

		// Set parent
		snippet.setParent(cs.getParent());
		// If the statement is the last statement of method, insert before (for not having an "unreachable statement" by adding something after)
		// Else insert after this statement
		if (unreachable(cs)) {
			cs.insertBefore(snippet);
		} else {
			cs.insertAfter(snippet);
		}
	}

	/**
	 * @param cs Statement concerned by the analyse
	 * @return If the statement is the last of its method or not
	 */
	protected boolean unreachable(CtStatement cs) {
		Class c = cs.getClass();

		// Processing data depending on which type is cs from

		// Loops : scan if the loop in an infinite loop
		if (c.equals(CtWhileImpl.class)) {
			CtWhile cw = (CtWhile) cs;
			if (cw.getLoopingExpression().toString().equals("true")) {
				return true;
			}
			return unreachable(cw.getBody());
		} else if (c.equals(CtDoImpl.class)) {
			CtDo cd = (CtDo) cs;
			if (cd.getLoopingExpression().toString().equals("true")) {
				return true;
			}
			return unreachable(cd.getBody());
		}
		// If/else : check if we can be after this block or not
		else if (c.equals(CtIfImpl.class)) {
			CtIf ci = (CtIf) cs;
			CtStatement ct = ci.getThenStatement();
			CtStatement ce = ci.getElseStatement();
			if (ct != null && unreachable(ct)) {
				return ce == null || unreachable(ce);
			}
			return false;
		}
		// If a block (or a block from try/catch), check if we can reach its end
		else if (c.equals(CtBlockImpl.class)) {
			List<CtStatement> lcs = ((CtBlock) cs).getStatements();
			for (CtStatement csTmp : lcs) {
				if (unreachable(csTmp)) {
					return true;
				}
			}
			return false;
		} else if (c.equals(CtTryImpl.class)) {
			CtTry ct = (CtTry) cs;
			if (unreachable(ct.getBody())) {
				List<CtCatch> lcc = ct.getCatchers();
				for (CtCatch cc : lcc) {
					if (unreachable(cc.getBody())) {
						return true;
					}
				}
			}
			if (ct.getFinalizer() != null) {
				return unreachable(ct.getFinalizer());
			}
			return false;
		}
		// Check if the statement is an ending statement
		return (c.equals(CtReturnImpl.class) || c.equals(CtThrowImpl.class) || c.equals(CtBreakImpl.class) || c
				.equals(CtContinueImpl.class));
	}

	/**
	 * @param c            Class concerned by the useless add
	 * @param staticMethod True if the new method has to be static, else false
	 * @return
	 */
	protected CtMethod createEmptyMethod(CtClass c, boolean staticMethod) {
		CtMethod m = getFactory().Core().createMethod();
		m.setSimpleName(nameMethod());
		m.setBody(getFactory().Core().createBlock());
		CtTypeReference typeRet = getFactory().Core().createTypeReference();
		typeRet.setSimpleName("void");
		m.setType(typeRet);
		m.addModifier(ModifierKind.PUBLIC);
		if (staticMethod) {
			m.addModifier(ModifierKind.STATIC);
		}
		c.addMethod(m);
		return m;
	}

}
