package cohen.process;

import java.util.ArrayList;
import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.code.CtLiteralImpl;
import spoon.support.reflect.code.CtLocalVariableImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;

/**
 * 
 * @author Mathieu
 * This class implements a processor which transforms every Object declaration
 * It changes every "Obj o = something;" by "Obj o = null;" and "o = something;" with Obj an non primitive type
 * 
 */
public class ReplaceVarDeclarationsProcessor extends AbstractProcessor<CtMethod<?>>
{	
	/**
	 * @param element Method to change
	 */
	public void process(CtMethod<?> element)
	{
		List<CtStatement> statements = element.getElements(new TypeFilter<CtStatement>(CtStatement.class));
		
		// If statements size is >0, first is like { ... }, with ... meaning all the others statements
		// Delete it for avoiding duplications
		// Else stop because this is an empty function, no modification needed
		if(statements.size()>0)
		{
			statements.remove(0);
		}
		else
		{
			return;
		}
		
		
		// Declarations
		List<CtStatement> variables = new ArrayList<CtStatement>();
		List<CtStatement> otherStatements = new ArrayList<CtStatement>();		
		List<CtStatement> content, contentVar;
		CtLocalVariable st;
		CodeFactory cf;
		CtLiteral cli;
		CtTypeReference ctri;
		boolean toContinue=false;
		CtStatement statement;
		
		// For each statement
		for(int i=0;i<statements.size();i++)
		{
			statement = statements.get(i);
			content = statement.getElements(new TypeFilter<CtStatement>(CtStatement.class));
			
			// Change i for avoiding duplications
			// For example : for "int i = method();", two statements will be in statements : "int i = method();" and "method();"
			// We ignore the second statement in this case
			i+=content.size()-1;
			
			// If statement is a variable
			if(statement instanceof CtLocalVariableImpl)
			{
				// Get the statement of correct class
				st = (CtLocalVariableImpl) statement;
				
				// Get its content
				contentVar = st.getElements(new TypeFilter<CtStatement>(CtStatement.class));

				// If st has primitive type, stop here (cannot replace int i = 0 by int i=null and i=0)
				// If size > 1, it means we have Obj o = ...; else we have only Obj o, so nothing to do in this case
				if(!st.getType().isPrimitive() && contentVar.size()>1)
				{
					// If final modifier, set toContinue to true
					for(ModifierKind s : st.getModifiers())
					{
						if(s.name().equalsIgnoreCase(s.FINAL.toString()))
						{
							toContinue=true;
							break;
						}
					}

					// If toContinue is set, it means final modifier is set
					// Cannot do "final Obj o = null; o=something;" so ignore this variable and don't modify it
					if(toContinue)
					{
						toContinue=false;
						otherStatements.add(st);
						continue;
					}
					
					// Create a litteral NULL
					cli = new CtLiteralImpl();
					ctri = new CtTypeReferenceImpl();
					ctri.setSimpleName(CtTypeReference.NULL_TYPE_NAME);
					cli.setType(ctri);
					
					// Create variable reference and sets it type to the correct type
					CtVariableReference ref = ToolsThreadVar.createFactory().Core().createLocalVariableReference();
					ref.setType(st.getType());
					ref.setSimpleName(st.getSimpleName());

					// Create variable assignment for setting it
					CtAssignment va = ToolsThreadVar.createFactory().Core().createAssignment();
					CtExpression expression = st.getDefaultExpression();
			        va.setAssignment(expression);
			        expression.setParent(va);

			        // Create variable access
			        CtVariableAccess vaccess = ToolsThreadVar.createFactory().Core().createVariableAccess();
			        vaccess.setVariable(ref);
			        vaccess.setType(ref.getType()); 
			        va.setAssigned(vaccess);
			        vaccess.setParent(va);

			        // Set variable declaration to "NULL"
					st.setDefaultExpression(cli);

			        // Add variable declaration to variables, and assignment to other statements
					// If base statement is "Obj o1 = o2", st will be "Obj o = null" and va will be "o = o2"
					variables.add(st);
					otherStatements.add(va);
				}
				else
				{
					// Else add the statement
					otherStatements.add(st);
				}
			}
			else
			{
				// Else add the statement
				otherStatements.add(statement);
			}
		}		

		// Replace the method block code by the new one
		CtBlock replaceBlock = new CtBlockImpl();
		List<CtStatement> toAdd = variables;
		toAdd.addAll(otherStatements);
		
		replaceBlock.setStatements(toAdd);
		element.setBody(replaceBlock);
	}	
}
