package lu.uni.dapreco.parser.rioonto;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.parser.XPathParser;
import lu.uni.dapreco.parser.lrml.RuleMLBlock;
import lu.uni.dapreco.parser.lrml.Side;

public class LogicAtom extends GenericRioOntoAtom {

	public LogicAtom(Element node, String pred, Side s, XPathParser xpath) {
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
		ret.add(toString() + "<br />");
		return ret;
	}

	@Override
	public String toString() {
		List<RuleMLBlock> arguments = getArguments();
		switch (getLocalPredicate()) {
		case "cause":
			return "<span>The fact " + arguments.get(0) + " is the cause of the fact " + arguments.get(1) + "</span>";
		case "imply":
			return "<span>" + arguments.get(1) + " implies " + arguments.get(2) + "</span>";
		case "partOf":
			return "<span>" + arguments.get(0) + " is part of " + arguments.get(1) + "</span>";
		default:
			return "UNKNOWN RIOONTO PREDICATE: " + predicateIRI;
		}
	}

	@Override
	public List<RuleMLBlock> getTranslated() {
		List<RuleMLBlock> ret = new ArrayList<RuleMLBlock>();
		ret.add(this);
		return ret;
	}

}
