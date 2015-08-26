package encryptTest;

import fr.inria.diversify.EncryptLiteralProcessor;
import junit.framework.Assert;
import spoon.Launcher;
import spoon.OutputType;
import spoon.compiler.SpoonCompiler;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.factory.Factory;

import java.io.File;
import java.util.Arrays;

/**
 * Created by nicolas on 26/08/2015.
 */
public class Test {

	@org.junit.Test
	public void testSample() throws Exception {
		Launcher spoon = new Launcher();
		Factory factory = spoon.createFactory();
		factory.getEnvironment().setNoClasspath(true);

		SpoonCompiler compiler = spoon.createCompiler(
				factory,
				SpoonResourceHelper.resources("src/test/java/encryptTest/sample/"));

		Assert.assertTrue(compiler.build());
		compiler.process(Arrays.asList(EncryptLiteralProcessor.class.getName()));

		compiler.setOutputDirectory(new File("target/spooned"));
		compiler.generateProcessedSourceFiles(OutputType.COMPILATION_UNITS);

		compiler.setDestinationDirectory(new File("target/spooned-classes"));
		Assert.assertTrue(compiler.compile());
	}

}
