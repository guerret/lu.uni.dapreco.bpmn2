package lu.uni.dapreco.parser.lrml.rioonto;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.parser.XPathParser;
import lu.uni.dapreco.parser.lrml.Atom;
import lu.uni.dapreco.parser.lrml.RuleMLBlock;
import lu.uni.dapreco.parser.lrml.Side;

public class ConjunctionAtom extends BooleanAtom {

	public ConjunctionAtom(Element node, String pred, Side s, XPathParser xpath) {
		super(node, pred, s, xpath);
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
			if (!d.inline()) {
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
		if (getArgumentsToTranslate().size() < 2)
			return "";
		if (getLocalPredicate().equals("and"))
			return "<span>(All of the following (" + getName() + "))</span>";
		return "<span>(At least one of the following (" + getName() + "))</span>";
	}

}
