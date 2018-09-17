package lu.uni.dapreco.parser.rioonto;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.parser.XPathParser;
import lu.uni.dapreco.parser.lrml.Atom;
import lu.uni.dapreco.parser.lrml.RuleMLBlock;
import lu.uni.dapreco.parser.lrml.Side;

public abstract class BooleanAtom extends GenericRioOntoAtom {

	public BooleanAtom(Element node, String pred, Side s, XPathParser xpath) {
		super(node, pred, s, xpath);
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
