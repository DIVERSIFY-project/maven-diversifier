package cohen.process;

import cohen.tools.Tools;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.support.reflect.declaration.CtParameterImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IfEquivalence extends AbstractCohenProcessor<CtIf> {

	@Override
	public boolean isToBeProcessed(CtIf candidate) {
		CtMethod cf = ((CtMethod) (candidate.getParent(CtMethodImpl.class)));
		Set<ModifierKind> mk = null;
		if (cf != null) {
			mk = cf.getModifiers();
		}
		CtStatement cs = candidate.getElseStatement();
		return (mk == null || !mk.contains(ModifierKind.STATIC)) && cs != null;
	}

	@Override
	public void process(CtIf arg0) {
		String nameMethod = nameMethod();
		CtClass cc = arg0.getParent(CtClass.class);
		Set<CtVariableReference> scva = detectParam(arg0.getThenStatement());
		Set<CtVariableReference> scvu = detectParamUpdate(arg0.getThenStatement());
		Set<CtVariableReference> scvl = detectLocalVariable(arg0.getThenStatement());
		CtTypeReference type1 = detectTypeReturn(arg0.getThenStatement());
		CtTypeReference type2 = detectTypeReturn(arg0.getElseStatement());
		CtTypeReference type = type1 == null ? type2 : type1;
		scva.addAll(detectParam(arg0.getElseStatement()));
		scvu.addAll(detectParamUpdate(arg0.getElseStatement()));
		scvl.addAll(detectLocalVariable(arg0.getElseStatement()));
		scva.removeAll(scvl);
		scvu.removeAll(scvl);
		if (type == null) {
			type = detectTypeReturn(arg0.getElseStatement());
		}
		List<CtParameter> lcp = createListParameter(scva);
		createMethod(cc, scvu, lcp, type, arg0.getThenStatement(), nameMethod + "_0");
		createMethod(cc, scvu, lcp, type, arg0.getElseStatement(), nameMethod + "_1");
		createCallMethod(arg0, nameMethod, scva, scvu, type);
	}

	protected void createCallMethod(CtIf ci, String nameMethod, Set<CtVariableReference> scva,
			Set<CtVariableReference> scvu, CtTypeReference type) {
		CtExpression<Boolean> ce = ci.getCondition();
		String nameConst = nameConstant();
		CtCodeSnippetStatement snippet = getFactory().Core().createCodeSnippetStatement();
		String invokeMethod =
				"this.getClass().getMethod(\"" + nameMethod + "_\"+" + nameConst + getTypeArg(scva) + ").invoke(this"
						+ stringifyArguments(scva) + ");";
		String st = "int " + nameConst + "=(Boolean.compare(false,(" + ce.toString() + "))+1)%2;\r\n";
		st += "try{\r\n";
		CtVariableReference[] tcvu = scvu.toArray(new CtVariableReference[0]);
		if (type != null) {
			st += "return (" + type.getSimpleName() + ")";
		}
		if (scvu.size() == 0) {
			st += invokeMethod;
		} else if (scvu.size() == 1) {
			st += tcvu[0].getSimpleName() + "=(" + tcvu[0].getType() + ")" + invokeMethod;
		} else {
			String nameConst2 = nameConstant();
			st += "java.util.Map<String,Object> " + nameConst2 + "=(java.util.Map<String,Object>)" + invokeMethod;
			for (CtVariableReference cva : tcvu) {
				String nameV = cva.getSimpleName();
				st += "\r\n" + nameV + "=(" + cva.getType() + ")" + nameConst2 + ".get(\"" + nameV + "\");";
			}
		}
		st += "\r\n}catch(Exception e){e.printStackTrace();}";
		st += getReturnAfterCall(type);
		snippet.setValue(st);
		ci.replace(snippet);
	}

	protected String getReturnAfterCall(CtTypeReference type) {
		String st = "";
		if (type != null) {
			if (type.isPrimitive()) {
				if (type.getClass().equals("char")) {
					st += "\r\n return (char)0";
				} else if (type.getClass().equals("float")) {
					st += "\r\n return 0f";
				} else {
					st += "\r\n return 0";
				}
			} else {
				st += "\r\n return null";
			}
		}
		return st;
	}

	private String getTypeArg(Set<CtVariableReference> scva) {
		// TODO Auto-generated method stub
		String st = "";
		for (CtVariableReference cva : scva) {
			CtTypeReference ctr = cva.getType();
			if (ctr.getActualClass().getPackage() != null) {
				st += ", " + ctr.getActualClass().getPackage().getName() + "." + ctr.getSimpleName() + ".class";
			} else {
				st += ", " + ctr.getSimpleName() + ".class";
			}
		}
		return st;
	}

	protected String stringifyArguments(Set<CtVariableReference> scva) {
		String s = "";
		for (CtVariableReference cva : scva) {
			s += ", " + cva.getSimpleName();
		}
		return s;
	}

	protected List<CtParameter> createListParameter(Set<CtVariableReference> scva) {
		List<CtParameter> lcp = new ArrayList<CtParameter>();
		for (CtVariableReference cva : scva) {
			CtParameter cp = new CtParameterImpl();
			cp.setSimpleName(cva.getSimpleName());
			cp.setType(cva.getType());
			lcp.add(cp);
		}
		return lcp;
	}

	protected void createMethod(CtClass c, Set<CtVariableReference> scvu, List<CtParameter> param, CtTypeReference type,
			CtStatement s, String name) {
		CtBlock cb = createBlockMethod(s, scvu);
		CtMethod m = getFactory().Core().createMethod();
		m.setParameters(param);
		m.setSimpleName(name);
		m.setBody(cb);
		m.addModifier(ModifierKind.PUBLIC);
		CtTypeReference typeRet = getTypeReturn(scvu, type);
		m.setType(typeRet);
		c.addMethod(m);
	}

	protected CtTypeReference getTypeReturn(Set<CtVariableReference> scvu, CtTypeReference type) {
		CtTypeReference typeRet = getFactory().Core().createTypeReference();
		CtVariableReference[] tcvu = scvu.toArray(new CtVariableReference[0]);
		if (scvu.size() == 0) {
			if (type == null) {
				typeRet.setSimpleName("void");
			} else {
				typeRet.setSimpleName(type.getSimpleName());
			}
		} else if (scvu.size() == 1) {
			typeRet.setSimpleName(tcvu[0].getType().getSimpleName());
		} else {
			typeRet.setSimpleName("java.util.Map<String,Object>");
		}
		return typeRet;
	}

	protected List<CtStatement> cloneStatements(List<CtStatement> statements) {
		List<CtStatement> ls = new ArrayList<CtStatement>();
		for (CtStatement s : statements) {
			CtCodeSnippetStatement sTmp = getFactory().Core().createCodeSnippetStatement();
			sTmp.setValue(s.toString());
			ls.add(sTmp);
		}
		return ls;
	}

	protected CtBlock createBlockMethod(CtStatement s, Set<CtVariableReference> scvu) {
		CtBlock cb = getFactory().Core().createBlock();
		List<CtStatement> ls = new ArrayList<CtStatement>();
		if (s != null) {
			if (s instanceof CtBlock) {
				ls = cloneStatements(((CtBlock) s).getStatements());
			} else {
				ls.add(s);
			}
		}
		CtVariableReference[] tcvu = scvu.toArray(new CtVariableReference[0]);
		if (scvu.size() == 1) {
			CtCodeSnippetStatement sTmp = getFactory().Core().createCodeSnippetStatement();
			sTmp.setValue("return " + tcvu[0].getSimpleName());
			ls.add(sTmp);
		} else if (scvu.size() > 1) {
			String nameMap = nameConstant();
			String st = "java.util.Map<String,Object> " + nameMap + "= new java.util.HashMap<String,Object>();\r\n";
			for (CtVariableReference cvu : tcvu) {
				String nameV = cvu.getSimpleName();
				st += nameMap + ".put(\"" + nameV + "\"," + nameV + ");\r\n";
			}
			CtCodeSnippetStatement sTmp = getFactory().Core().createCodeSnippetStatement();
			sTmp.setValue(st + "return " + nameMap);
			ls.add(sTmp);
		}
		cb.setStatements(ls);
		return cb;
	}

	protected Set<CtVariableReference> detectParam(CtStatement s) {
		return new Tools().getVariableAccess(s);
	}

	protected Set<CtVariableReference> detectParamUpdate(CtStatement s) {
		// TODO Auto-generated method stub
		return (new Tools()).getVariableUpdate(s);
	}

	protected Set<CtVariableReference> detectLocalVariable(CtStatement s) {
		// TODO Auto-generated method stub
		return (new Tools()).getLocalVariable(s);
	}

	protected CtTypeReference detectTypeReturn(CtStatement s) {
		// TODO Auto-generated method stub
		return (new Tools()).getTypeReturn(s);
	}

}
