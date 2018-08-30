package lu.uni.dapreco.bpmn2.lrml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.bpmn2.XPathParser;

public class PredAtom extends Atom {

	public PredAtom(Element node, String pred, Side s, XPathParser xpath) {
		super(node, pred, s, xpath);
	}

	@Override
	public List<RuleMLBlock> getNewArgumentsToTranslate() {
		List<RuleMLBlock> arguments = getArguments();
		switch (predicateIRI) {
		case "pred:numeric-greater-than-or-equal":
			// only first argument, the second is the time
			return new ArrayList<RuleMLBlock>();
		default:
			return arguments;
		}
	}

	@Override
	public List<String> translate() {
		List<String> ret = new ArrayList<String>();
		List<RuleMLBlock> arguments = getNewArgumentsToTranslate();
		String translation = toString();
		switch (getLocalPredicate()) {
		case "numeric-greater-than-or-equal":
		default:
			translation += "<br />";
		}
		ret.add(translation);
		List<Atom> definitionAtoms = getNewDefinitionAtoms(arguments);
		for (Atom d : definitionAtoms)
			if (d.getArguments().size() > 1)
				ret.addAll(d.translate());
		return ret;
	}

	@Override
	public String toString() {
		String pred = getLocalPredicate();
		switch (pred) {
		case "numeric-greater-than-or-equal":
			return "<span>" + children.get(0).getName() + " is greater than, or at least equal to, "
					+ children.get(1).getName() + "</span>";
		default:
			return "UNKNOWN PRED PREDICATE: " + predicateIRI;
		}
	}

}
