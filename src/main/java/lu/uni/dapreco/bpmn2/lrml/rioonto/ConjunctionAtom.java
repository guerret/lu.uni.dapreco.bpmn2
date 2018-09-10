package lu.uni.dapreco.bpmn2.lrml.rioonto;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.bpmn2.XPathParser;
import lu.uni.dapreco.bpmn2.lrml.Atom;
import lu.uni.dapreco.bpmn2.lrml.RuleMLBlock;
import lu.uni.dapreco.bpmn2.lrml.Side;

public class ConjunctionAtom extends GenericRioOntoAtom {

	public ConjunctionAtom(Element node, String pred, Side s, XPathParser xpath) {
		super(node, pred, s, xpath);
	}

	@Override
	public List<RuleMLBlock> getArgumentsToTranslate() {
		List<RuleMLBlock> arguments = getArguments();
		// first argument is reification (although not reified), the rest are useful
		return arguments.subList(1, arguments.size());
	}

	@Override
	public List<String> translate() {
		List<String> ret = new ArrayList<String>();
		List<RuleMLBlock> arguments = getArgumentsToTranslate();
		String translation = toString();
		if (arguments.size() > 1)
			translation += "<ol>";
		ret.add(translation);
		List<Atom> definitionAtoms = getDefinitionAtoms(arguments);
		for (Atom d : definitionAtoms)
			if (!d.isRestriction()) {
				if (arguments.size() > 1)
					ret.add("<li>");
				ret.addAll(d.translate());
				if (arguments.size() > 1)
					ret.add("</li>");
			}
		if (arguments.size() > 1)
			ret.add("</ol>");
		return ret;
	}

	@Override
	public String toString() {
		if (getArgumentsToTranslate().size() == 1)
			return "";
		// Why children.get(0)? Because they are reified but without apex 
		String name = children.get(0).getName();
		if (getLocalPredicate().equals("and"))
			return "<span>(All of the following (" + name + "))</span>";
		return "<span>(At least one of the following (" + name + "))</span>";
	}

	@Override
	public List<RuleMLBlock> getTranslated() {
		List<RuleMLBlock> ret = new ArrayList<RuleMLBlock>();
		ret.add(this);
		List<RuleMLBlock> arguments = getArgumentsToTranslate();
		List<Atom> definitionAtoms = getDefinitionAtoms(arguments);
		for (Atom d : definitionAtoms)
			ret.addAll(d.getTranslated());
		return ret;
	}

}
