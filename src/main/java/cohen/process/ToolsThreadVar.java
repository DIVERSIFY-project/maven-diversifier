package cohen.process;

import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtCatchVariable;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;

import java.util.List;

/**
 * @author Mathieu
 *         Tools for ReplaceVarDeclarationsProcessor and ThreadCreationProcessor
 */
public class ToolsThreadVar {
	// Int cpt for creating thread name
	static int cpt = 0;

	// Factory to create
	static Factory factory;

	/**
	 * @param packageName Package Name of the class to build
	 * @param className   Class name of the class to build
	 * @return
	 * @throws Exception
	 */
	public static <T extends CtType<?>> T build(String packageName, String className) throws Exception {
		SpoonCompiler comp = new Launcher().createCompiler();
		comp.addInputSources(SpoonResourceHelper
				.resources("./src/main/java/" + packageName.replace('.', '/') + "/" + className + ".java"));
		comp.build();
		return comp.getFactory().Package().get(packageName).getType(className);
	}

	/**
	 * @return Factory created
	 */
	public static Factory getFactory() {
		// Works as the instance of a singleton
		if (factory == null) {
			factory = createFactory();
		}
		return factory;
	}

	/**
	 * @return factory created
	 */
	public static Factory createFactory() {
		return new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
	}

	/**
	 * @param line to get as a code line
	 * @return CtCodeSnippetStatement corresponding to the code line
	 */
	public static CtCodeSnippetStatement createCodeLine(String line) {
		CtCodeSnippetStatement ret = ToolsThreadVar.createFactory().Core().createCodeSnippetStatement();
		ret.setValue(line);
		return ret;
	}

	/**
	 * @return a random thread name
	 */
	public static String getThreadRandomName() {
		cpt++;
		return "Thread" + cpt;
	}

	/**
	 * @param statements to add to the CtBlock
	 * @return CtBlock which contains every statements
	 */
	public static CtBlock getCtBlock(List<CtStatement> statements) {
		CtBlock block = new CtBlockImpl();

		for (CtStatement ct : statements) {
			block.addStatement(ct);
		}
		return block;
	}

	/**
	 * @param statementsTry   Statements to put in the try part
	 * @param exceptionType   Exception to catch
	 * @param statementsCatch Statements to put in the catch part
	 * @return CtTry which contains everything
	 */
	public static CtTry getTry(List<CtStatement> statementsTry, String exceptionType,
			List<CtStatement> statementsCatch) {
		// Create a try block with every statements
		CtTry ret = ToolsThreadVar.getFactory().Core().createTry();
		CtBlock bodyTry = new CtBlockImpl();

		for (CtStatement ct : statementsTry) {
			bodyTry.addStatement(ct);
		}

		ret.setBody(bodyTry);

		// Create a catch block with right exception and statements
		CtCatch catchTry = ToolsThreadVar.getFactory().Core().createCatch();
		CtBlock bodyCatch = new CtBlockImpl();

		for (CtStatement catchSt : statementsCatch) {
			bodyCatch.addStatement(catchSt);
		}
		catchTry.setBody(bodyCatch);

		CtCatchVariable<? extends Throwable> var = ToolsThreadVar.getFactory().Core().createCatchVariable();
		CtTypeReference typeException = new CtTypeReferenceImpl();
		typeException.setSimpleName(exceptionType);
		var.setType(typeException);
		var.setSimpleName("e");

		catchTry.setParameter(var);

		ret.addCatcher(catchTry);
		return ret;
	}

	/**
	 * @param ct CtElement of which we have to get the name
	 * @return The simple name of ct
	 */
	public static String getTextOf(CtElement ct) {
		if (ct instanceof CtVariableAccess) {
			return ((CtVariableAccess) ct).getVariable().getSimpleName();
		}
		if (ct instanceof CtArrayAccess) {
			return "toto" + ((CtArrayAccess) ct).getIndexExpression().getSignature();
		}
		return ct.getSignature();
	}
}
