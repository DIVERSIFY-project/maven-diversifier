package cohen.process;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.declaration.CtParameterImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodTampon extends AbstractCohenProcessor<CtInvocation> {

	@Override
	public void process(CtInvocation arg0) {
		// TODO Auto-generated method stub
		try {
			String name = arg0.getExecutable().getSimpleName();
			if (!name.equals("<init>")) {

				CtClass cc = arg0.getParent(CtClass.class);

				List<CtExpression> lce = arg0.getArguments();

				Map<CtParameter, CtExpression> mcp = createListParameter(lce);

				CtExpression ce = arg0.getTarget();
				if (ce != null) {
					CtParameter cp = new CtParameterImpl();
					cp.setSimpleName(nameConstant());
					cp.setType(ce.getType());
					mcp.put(cp, ce);
				}

				List<CtParameter> lcp = new ArrayList<CtParameter>(mcp.keySet());
				Collections.shuffle(lcp);

				CtCodeSnippetStatement ccss = createCallMethod(arg0, name, mcp, lce);

				String nomMethod = nameMethod();
				CtMethod method = createMethod(cc, lcp, arg0.getType(), ccss, nomMethod);
				arg0.replace(getReplace(method.getReference(), mcp, lcp));
			}
		} catch (Exception e) {
		}
	}

	protected CtStatement getReplace(CtExecutableReference reference, Map<CtParameter, CtExpression> mcp,
			List<CtParameter> lcp) {
		CtInvocation inv = getFactory().Core().createInvocation();
		inv.setExecutable(reference);

		for (CtParameter arg : lcp) {
			CtExpression ce = mcp.get(arg);
			CtCodeSnippetExpression ccse = getFactory().Core().createCodeSnippetExpression();
			ccse.setValue(ce.toString());
			inv.addArgument(ccse);
		}
		return inv;
	}

	protected CtParameter getKey(Map<CtParameter, CtExpression> mcp, CtExpression exp) {
		for (Map.Entry<CtParameter, CtExpression> entry : mcp.entrySet()) {
			if (exp.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	protected CtCodeSnippetStatement createCallMethod(CtInvocation invoc, String name,
			Map<CtParameter, CtExpression> mcp, List<CtExpression> lce) {
		CtCodeSnippetStatement ccss = getFactory().Core().createCodeSnippetStatement();
		CtExpression ce = invoc.getTarget();
		String str = "";
		if (!invoc.getType().toString().equals("void")) {
			str += "return ";
		}
		if (ce != null) {
			str += getKey(mcp, invoc.getTarget()).getSimpleName() + ".";
		}
		str += name + "(";
		boolean virgule = false;
		for (CtExpression arg : lce) {
			if (virgule) {
				str += ",";
			}
			str += getKey(mcp, arg).getSimpleName();
			virgule = true;
		}
		str += ")";
		ccss.setValue(str);
		return ccss;
	}

	protected CtMethod createMethod(CtClass c, List<CtParameter> param, CtTypeReference type, CtStatement s,
			String name) {
		CtBlock cb = getFactory().Core().createBlock();
		List<CtStatement> ls = new ArrayList<CtStatement>();
		ls.add(s);
		cb.setStatements(ls);
		CtMethod m = getFactory().Core().createMethod();
		m.setParameters(param);
		m.setSimpleName(name);
		m.setBody(cb);
		m.addModifier(ModifierKind.PUBLIC);
		CtTypeReference typeRet = getFactory().Core().createTypeReference();
		if (type.getSimpleName() != null && !type.getSimpleName().equals("?")) {
			typeRet.setSimpleName(type.getSimpleName());
		} else {
			typeRet.setSimpleName("void");
		}
		m.setType(typeRet);
		c.addMethod(m);
		return m;
	}

	protected Map<CtParameter, CtExpression> createListParameter(List<CtExpression> lce) {
		Map<CtParameter, CtExpression> lcp = new HashMap<CtParameter, CtExpression>();
		for (CtExpression ce : lce) {
			CtParameter cp = new CtParameterImpl();
			cp.setSimpleName(nameConstant());
			cp.setType(ce.getType());
			lcp.put(cp, ce);
		}
		return lcp;
	}

}
