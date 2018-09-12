package lu.uni.dapreco.parser.lrml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.parser.XPathParser;

public class PredAtom extends Atom {

	public PredAtom(Element node, String pred, Side s, XPathParser xpath) {
		super(node, pred, s, xpath);
	}

	public List<RuleMLBlock> getArgumentsToTranslate() {
		List<RuleMLBlock> arguments = getArguments();
		switch (getLocalPredicate()) {
		case "numeric-greater-than-or-equal":
		case "numeric-less-than":
			return new ArrayList<RuleMLBlock>();
		default:
			return arguments;
		}
	}

	@Override
	public List<String> translate() {
		List<String> ret = new ArrayList<String>();
		List<RuleMLBlock> arguments = getArgumentsToTranslate();
		String translation = toString();
		switch (getLocalPredicate()) {
		case "numeric-greater-than-or-equal":
		case "numeric-less-than":
		default:
			translation += "<br />";
		}
		ret.add(translation);
		List<Atom> definitionAtoms = getDefinitionAtoms(arguments);
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
			return "<span>" + children.get(0) + " is greater than, or at least equal to, " + children.get(1)
					+ "</span>";
		case "numeric-less-than":
			return "<span>" + children.get(0) + " is less than " + children.get(1) + "</span>";
		default:
			return "UNKNOWN PRED PREDICATE: " + predicateIRI;
		}
	}

	@Override
	public boolean inline() {
		return false;
	}

}
