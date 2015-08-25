package cohen.process;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtAssignmentImpl;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.code.CtInvocationImpl;
import spoon.support.reflect.code.CtVariableAccessImpl;
import spoon.support.reflect.declaration.CtFieldImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.support.reflect.declaration.CtParameterImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;

/**
 * 
 * @author Mathieu
 * 
 * This class changes every "Obj o = something" or "o = something;" by a thread creation and its start/join
 * and the value recuperation for the right variable
 * 
 */
@SuppressWarnings("rawtypes")
public class ThreadCreationProcessor extends AbstractProcessor<CtMethod<?>>
{
	// List with every thread classes created
	private List<CtClass> threadClasses;
	
	// List of list, every list contains a list of statements to replace
	private List<List<CtStatement>> toReplace;
	
	// List of list, every list contains a list of statements to put
	// toPut(i) corresponds to toReplace(i) indexes
	private List<List<CtStatement>> toPut;
	
	// List of every assigned variables names
	private List<String> variablesAssignees;
	
	// Statements and methodStatements
	private List<CtStatement> statements, methodStatements;

	/**
	 * arg0 Method to process 
	 */
	public void process(CtMethod<?> arg0)
	{
		threadClasses = new ArrayList<CtClass>();
		toReplace = new ArrayList<List<CtStatement>>();
		toPut = new ArrayList<List<CtStatement>>();
		variablesAssignees = new ArrayList<String>();
		statements = new ArrayList<CtStatement>();
		methodStatements = arg0.getElements(new TypeFilter<CtStatement>(CtStatement.class));
		
		// If statements size is >0, first is like { ... }, with ... meaning all the others statements
		// Delete it for avoiding duplications
		// Else stop because this is an empty function, no modification needed
		if(methodStatements.size()>0)
		{
			methodStatements.remove(0);
		}
		else
		{
			return;
		}
		
		// Change i for avoiding duplications
		// For example : for "int i = method();", two statements will be in statements : "int i = method();" and "method();"
		// We ignore the second statement in this case
		for(int i=0;i<methodStatements.size();i++)
		{
			CtStatement tmp = methodStatements.get(i);
			statements.add(tmp);
			int size = tmp.getElements(new TypeFilter<CtStatement>(CtStatement.class)).size();
			i+=size-1;
		}
		
		// Declarations
		CtStatement tmp=null;
		List<CtStatement> assignments;
		boolean putTmpToNull=false;
		
		// For each statement
		for(CtStatement statement : statements)
		{
			// tmp represents previous line
			// If there is a previous line
			if(tmp!=null)
			{
				/* statement par statement */
//				if(tmp instanceof CtAssignmentImpl)
//				{
//					List<CtStatement> assignments = new ArrayList<CtStatement>();
//					assignments.add(tmp);
//					processAssignments(arg0, assignments);
//				}

				/* deux statements par deux */
				// If statements are independents
				if(areIndependants(tmp, statement))
				{
					// If line and previous line are assignments
					if(tmp instanceof CtAssignmentImpl && statement instanceof CtAssignmentImpl)
					{
						// Create a list of assignments to process
						assignments = new ArrayList<CtStatement>();
						assignments.add(tmp);
						assignments.add(statement);
						processAssignments(arg0, assignments);
						
						// Have to set tmp to null for not processing two times same line
						putTmpToNull=true;
					}
				}
			}

			// Before replacing tmp, remember its name 
			if(tmp!=null && tmp instanceof CtAssignment)
			{
				variablesAssignees.add(((CtAssignment)tmp).getAssigned().toString());
			}
			
			// If we have to set tmp to null, set it to null
			if(putTmpToNull)
			{
				tmp=null;
				putTmpToNull=false;
			}
			else
			{
				// Set tmp to current line
				tmp=statement;
			}
		}

		// Manage the new block of method
		manageNewBlock(arg0);
	}

	@SuppressWarnings("unchecked")
	/**
	 * 
	 * @param arg0 CtMethod to manage with class fields
	 */
	private void manageNewBlock(CtMethod<?> arg0)
	{
		List<CtStatement> toAdd = new ArrayList<CtStatement>();
		toAdd.addAll(statements);
		List<CtStatement> toRep, toPutNow;
		
		// Process replaces
		for(int i=0; i<toReplace.size();i++)
		{
			toRep = toReplace.get(i);
			toPutNow = toPut.get(i);
						
			int index=-1;
			// Remove elements to replace
			while(toRep.size()>0)
			{
				index = toAdd.indexOf(toRep.get(0));
				toAdd.remove(toRep.get(0));
				toRep.remove(0);
			}
			
			if(index!=-1)
			{
				// Put elements to the right index
				for(int j=0;j<toPutNow.size();j++)
				{
					CtStatement ct = toPutNow.get(j);
					toAdd.add((index+j), ct);
				}
			}
		}

		// Add threadClasses to the beginning of method
		for(CtClass classToAdd : threadClasses)
		{
			toAdd.add(0,classToAdd);
		}

		// Set the body method to the new body
		CtBlock block = ToolsThreadVar.getCtBlock(toAdd);
		arg0.setBody(block);
	}

	/**
	 * 
	 * @param arg0 Method processed
	 * @param elements Elements to process
	 */
	private void processAssignments(CtMethod arg0, List<CtStatement> elements)
	{
		// If no element to process, then return
		int size = elements.size();
		if(size==0)	return;

		// Elements which will be to replace
		List<CtStatement> toReplaceList = new ArrayList<CtStatement>();
		
		// Elements to add before future try/catch (of threads join)
		List<CtStatement> statementsToAdd = new ArrayList<CtStatement>();

		// Elements to add after future try/catch
		List<CtStatement> statementsToAddEnd = new ArrayList<CtStatement>();		
		
		// Try for thread joins
		List<CtStatement> statementsTry = new ArrayList<CtStatement>();
		
		// Catch of this try, just printing stacktrace
		List<CtStatement> statementsCatch = new ArrayList<CtStatement>();
		String exceptionType=("java.lang.InterruptedException");
		statementsCatch.add(ToolsThreadVar.createCodeLine("e.printStackTrace()"));

		// Futures args of thread class
		List<CtExpression> args;

		// Declarations
		CtAssignmentImpl impl1;
		List<CtElement> elements1;
		String name1;
		CtExpression argToAdd;
		CtClass thread1;
		boolean toBreak, alreadyAssigned1, previous;
		CtCodeSnippetStatement statementThreadCreate1, statementThreadStart1;
		String nameVar1, nameVar;
		StringBuilder codeLine;

		List<CtElement> content;
		
		// For each element
		for(CtElement ct : elements)
		{
			// If array or a line like "obj.field = something", continue (too hard to manage for the moment)
			if(ct instanceof CtArrayAccess)
			{
				continue;
			}
			content = ct.getElements(new TypeFilter<CtElement>(CtElement.class));
			if(content.size()>1)
			{
				if(content.get(1).getElements(new TypeFilter<CtElement>(CtElement.class)).size()>1)
				{
					continue;
				}
			}
			
			args = new ArrayList<CtExpression>();
			// Get assignment
			impl1 = (CtAssignmentImpl) ct;
			
			// Get random name for thread class
			name1 = ToolsThreadVar.getThreadRandomName();

			// elements of assignment
			elements1 = impl1.getElements(new TypeFilter<CtElement>(CtElement.class));
			toBreak=false;
			
			// If size >2, this is a real assignement (like int i = something;), by starting to index 2, avoid to taking care of "i"
			
			CtTypeReference typeOfIncomingObject = null;
			CtElement elemVarAssignment;
			
			// Index 0 is the content of line, and  index 1 the variable name
			for(int i=2;i<elements1.size();i++)
			{
				elemVarAssignment = elements1.get(i);
				// If method invocation, don't want to put it in a thread - same for array (difficult to manage)
				// So put toBreak to true to break before processing the statement
				if(elemVarAssignment instanceof CtInvocationImpl || elemVarAssignment instanceof CtArrayAccess)
				{
					toBreak=true;
					break;
				}
				else 
				{
					// If variable
					if(elemVarAssignment instanceof CtVariableAccessImpl)
					{
						// If only one element (not like o.myValue with o a value), put it in args to add in thread, else stop !
						if(elemVarAssignment.getElements(new TypeFilter<CtElement>(CtElement.class)).size()>1)
						{
							toBreak=true;
						}
						else
						{
							// Set argument to add if it isn't already added
							argToAdd = (CtExpression) elements1.get(i);
							if(typeOfIncomingObject!=null)
							{
								argToAdd.setType(typeOfIncomingObject);
								typeOfIncomingObject=null;
							}
							if(!args.contains(argToAdd))
							{
								args.add(argToAdd);
							}
						}
					}
				}
			}
			
			// If has to break, break !
			if(toBreak) break;
			
			// Consults if variable is already assigned
			// If not, don't have to put it as a param of the thread !
			alreadyAssigned1 = false;
			if(variablesAssignees.contains(impl1.getAssigned().toString()))
			{
				alreadyAssigned1=true;
			}
			
			// Create thread from assignment and right params
			thread1 = getThreadClassOfAssignment(arg0, elements1, impl1, name1, alreadyAssigned1, args);
			if(thread1==null)
			{
				continue;
			}
			threadClasses.add(thread1);
			
			// Add assignment as line to replace
			toReplaceList.add(impl1);
			
			// Create thread assignment
			nameVar1 = "t"+name1;
			codeLine = new StringBuilder("");
			previous=true;
			
			String nameParam=ToolsThreadVar.getTextOf(ct);
			
			if(alreadyAssigned1)
			{
				codeLine.append(name1+" "+nameVar1+" = new "+name1+"("+nameParam);
			}
			else
			{
				previous=false;
				codeLine.append(name1+" "+nameVar1+" = new "+name1+"(");
			}
			String nameArg;

			for(CtElement arg : args)
			{
				nameArg = ToolsThreadVar.getTextOf(arg);
				if(!previous)
				{
					previous=true;
					codeLine.append(nameArg);
				}
				else
				{
					codeLine.append(","+nameArg);
				}
			}
			codeLine.append(")");
			statementThreadCreate1=ToolsThreadVar.createCodeLine(codeLine.toString());

			// Start le thread
			statementThreadStart1=ToolsThreadVar.createCodeLine(nameVar1+".start()");
			statementsToAdd.add(statementThreadCreate1);
			statementsToAdd.add(statementThreadStart1);
			
			// Add join statements to the try body
			statementsTry.add(ToolsThreadVar.createCodeLine(nameVar1+".join()"));
			
			// Add lines to retrieve values from thread
//			nameVar = elements1.get(1).getSignature();
			nameVar = ((CtAssignment)ct).getAssigned().toString();
			CtTypeReference typeVar = ((CtAssignment)ct).getType(); 
			// If assignment is not primitive and doesn't contain a "." (means it has a generic type), so set it !
			if(!typeVar.isPrimitive() && !typeVar.toString().contains("."))
			{
				statementsToAddEnd.add(ToolsThreadVar.createCodeLine(nameVar+"=("+typeVar+")"+nameVar1+"."+nameVar));
			}
			else
			{
				statementsToAddEnd.add(ToolsThreadVar.createCodeLine(nameVar+"="+nameVar1+"."+nameVar));
			}
		}

		// Add every statements to right lists
		statementsToAdd.add(ToolsThreadVar.getTry(statementsTry, exceptionType, statementsCatch));	
		statementsToAdd.addAll(statementsToAddEnd);
		toReplace.add(toReplaceList);
		toPut.add(statementsToAdd);
	}
	
	/**
	 * 
	 * @param arg0 Method to process
	 * @param elements Elements of assignment (for thread constructor)
	 * @param assignment Assignment replaced in thread (for thread run method)
	 * @param nameThread Thread name
	 * @param alreadyAssigned If the 
	 * @param args Args to add to this thread
	 * @return CtClass of thread created
	 */
	@SuppressWarnings("unchecked")
	public CtClass getThreadClassOfAssignment(CtMethod arg0, List<CtElement> elements, CtAssignment assignment, String nameThread, boolean alreadyAssigned, List<CtExpression> args)
	{
		Factory factory = ToolsThreadVar.getFactory();

		// Create a private thread class which extends java.lang.Thread
		CtClass threadClass1 = factory.Core().createClass();
		CtTypeReferenceImpl threadSuperclass = new CtTypeReferenceImpl();
		threadSuperclass.setSimpleName("java.lang.Thread");
		threadClass1.setSuperclass(threadSuperclass);
		threadClass1.setFactory(ToolsThreadVar.getFactory());
		threadClass1.setParent(arg0.getParent());
		threadClass1.setSimpleName(nameThread);
		
		// If assignment is not primitive and doesn't contain a "." (means it has a generic type), so set it !
		if(!assignment.getType().isPrimitive() && !assignment.getType().toString().contains("."))
		{
			List<CtTypeReference<?>> tmpList = new ArrayList<CtTypeReference<?>>();
			tmpList.add(assignment.getType());
			threadClass1.setFormalTypeParameters(tmpList);
		}
		
		// Create field and param for the variable set as param
		List<CtVariableAccess> elementsClass1 = assignment.getElements(new TypeFilter<CtVariableAccess>(CtVariableAccess.class));
		List<CtParameter> parameters = new ArrayList<CtParameter>();
				
		CtExpression tmpVarAcc = (CtExpression) elements.get(1);
		String nomChamp;
		
		if(tmpVarAcc instanceof CtArrayAccess)
		{
			return null;
		}
		nomChamp = ToolsThreadVar.getTextOf(tmpVarAcc);
		
//		String nomChamp = elements.get(1).getSignature();
		CtVariableAccess elem = elementsClass1.get(0);
		
		// Create fields
		CtFieldImpl fieldToAddClass1 = new CtFieldImpl();
		fieldToAddClass1.setType(elem.getType());
		fieldToAddClass1.setSimpleName(nomChamp);
		threadClass1.addField(fieldToAddClass1);
		
		// Create constructor params
		CtParameter param1 = ToolsThreadVar.getFactory().Core().createParameter();
		param1.setSimpleName(nomChamp);
		param1.setType(elem.getType());
		parameters.add(param1);
		
		CtFieldImpl fieldToAddClass;
		CtParameterImpl param;

		// For each arg
		for(CtExpression arg : args)
		{
			fieldToAddClass = new CtFieldImpl();
			fieldToAddClass.setType(arg.getType());
			fieldToAddClass.setSimpleName(ToolsThreadVar.getTextOf(arg));
			threadClass1.addField(fieldToAddClass);
			
			// Create constructor params
			param = new CtParameterImpl();
			param.setSimpleName(ToolsThreadVar.getTextOf(arg));
			param.setType(arg.getType());
			parameters.add(param);
		}
		
		// Create constructor from params
		CtConstructor constructeur = factory.Core().createConstructor();
		constructeur.setParent(threadClass1);
		CtBlock body = new CtBlockImpl();
		
		if(!alreadyAssigned)
		{
			parameters.remove(0);
		}
		constructeur.setParameters(parameters);
		
		CtCodeSnippetStatement statementConstructeur;

		// Create code lines of constructor
		for(CtParameter params : parameters)
		{
			// Create constructor code lines
			statementConstructeur = ToolsThreadVar.createCodeLine("this."+params.getSimpleName()+"="+params.getSimpleName());
			body.addStatement(statementConstructeur);
		}
		
		// Set constructor body, and add it to class
		constructeur.setBody(body);
		threadClass1.addConstructor(constructeur);

		// Create run method
		CtMethod runMethod = new CtMethodImpl();
		runMethod.setSimpleName("run");
		Set<ModifierKind> modifiersRun = new HashSet<ModifierKind>();
		modifiersRun.add(ModifierKind.PUBLIC);
		runMethod.setModifiers(modifiersRun);
		CtTypeReference typeRunMethod = new CtTypeReferenceImpl();
		typeRunMethod.setSimpleName("void");
		runMethod.setType(typeRunMethod);
		
		// Run initial code lines
		CtBlock bodyRun = new CtBlockImpl();
		CtCodeSnippetStatement statementRun = ToolsThreadVar.createCodeLine(assignment.toString());
		bodyRun.addStatement(statementRun);
		runMethod.setBody(bodyRun);
		threadClass1.addMethod(runMethod);

		// return it
		return threadClass1;
	}
	
	/**
	 * 
	 * @param s1 First statement
	 * @param s2 Second statement
	 * @return True if statements are independents, else false
	 */
	public boolean areIndependants(CtStatement s1, CtStatement s2)
	{
		return !hasSameParam(s1, s2);
	}
	
	/**
	 * 
	 * @param s1 First statement
	 * @param s2 Second statement
	 * @return True if s1 and s2 has a same paramn, false else
	 */
	public boolean hasSameParam(CtStatement s1, CtStatement s2)
	{
		// Get elements of each statement
		List<CtVariableAccess> elementsOfS1 = s1.getElements(new TypeFilter<CtVariableAccess>(CtVariableAccess.class));
		List<CtVariableAccess> elementsOfS2 = s2.getElements(new TypeFilter<CtVariableAccess>(CtVariableAccess.class));				
		
		String ss1, ss2;
		
		// Compare elements name of each statement, if there is a same param then true, else false
		for(CtVariableAccess eos1 : elementsOfS1)
		{
			for(CtVariableAccess eos2 : elementsOfS2)
			{
				ss1 = eos1.getSignature();
				ss2 = eos2.getSignature();
				
				if(ss1.equals(ss2))
				{
					return true;
				}
			}
		}
		return false;
	}
}
