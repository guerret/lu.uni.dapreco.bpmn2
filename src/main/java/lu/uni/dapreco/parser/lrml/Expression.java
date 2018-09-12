package lu.uni.dapreco.parser.lrml;

import org.w3c.dom.Element;

import lu.uni.dapreco.parser.XPathParser;

public class Expression extends Atom {

	public Expression(Element node, String pred, Side s, XPathParser xpath) {
		super(node, pred, s, xpath);
	}

	public String toString() {
		String ret = "[the <strong>" + getLocalPredicate() + "</strong> of ";
		for (RuleMLBlock child : children) {
			ret += child.toString();
			if (children.indexOf(child) < children.size() - 1)
				ret += ", ";
		}
		ret += "]";
		return ret;
	}

}
