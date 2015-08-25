package cohen.process;

import java.util.UUID;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtElement;

/**
*
* @author Thomas
* This abstract class implements an AbstractProcessor for Cohen transformation
*
*/
public abstract class AbstractCohenProcessor<E extends CtElement> extends AbstractProcessor<E> {

	/**
	 * Generate an uniq name of method
	 *
	 * @return Return a uniq name of method
	 */
	protected String nameMethod(){
		String uuid=UUID.randomUUID().toString();
		uuid=uuid.replace('-', '_');
		return "m"+uuid;
	}

	/**
	 * Generate an uniq name of constant
	 *
	 * @return Return a uniq name of constant
	 */
	protected String nameConstant(){
		String uuid=UUID.randomUUID().toString();
		uuid=uuid.replace('-', '_');
		return "c"+uuid;
	}
}