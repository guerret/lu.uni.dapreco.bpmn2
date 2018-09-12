package lu.uni.dapreco.parser.rioonto;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.parser.XPathParser;
import lu.uni.dapreco.parser.lrml.Atom;
import lu.uni.dapreco.parser.lrml.RuleMLBlock;
import lu.uni.dapreco.parser.lrml.Side;

public abstract class GenericRioOntoAtom extends Atom {

	public GenericRioOntoAtom(Element node, String pred, Side s, XPathParser xpath) {
		super(node, pred, s, xpath);
	}

	public static GenericRioOntoAtom create(Element node, String pred, Side s, XPathParser xpath) {
		if (pred.equals("rioOnto:RexistAtTime"))
			return new RexistAtTimeAtom(node, pred, s, xpath);
		if (pred.equals("rioOnto:not"))
			return new NotAtom(node, pred, s, xpath);
		if (pred.equals("rioOnto:and") || pred.equals("rioOnto:or"))
			return new ConjunctionAtom(node, pred, s, xpath);
		if (pred.equals("rioOnto:cause") || pred.equals("rioOnto:imply") || pred.equals("rioOnto:partOf"))
			return new LogicAtom(node, pred, s, xpath);
		if (pred.equals("rioOnto:Permitted") || pred.equals("rioOnto:Obliged"))
			return new DeonticAtom(node, pred, s, xpath);
		if (pred.startsWith("rioOnto:exception"))
			return new ExceptionAtom(node, pred, s, xpath);
		System.err.println("ERROR: UNKNOWN ATOM " + pred);
		System.exit(0);
		return null;
	}

	public abstract List<RuleMLBlock> getArgumentsToTranslate();

	@Override
	public abstract List<String> translate();

	@Override
	public abstract String toString();

	@Override
	public boolean inline() {
		return false;
	}

	@Override
	public List<RuleMLBlock> getTranslated() {
		List<RuleMLBlock> ret = new ArrayList<RuleMLBlock>();
		ret.add(this);
		return ret;
	}

}
