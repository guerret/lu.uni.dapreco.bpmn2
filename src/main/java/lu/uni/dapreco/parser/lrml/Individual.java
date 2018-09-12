package lu.uni.dapreco.parser.lrml;

import org.w3c.dom.Element;

import lu.uni.dapreco.parser.XPathParser;

public class Individual extends RuleMLBlock {

	private String value;

	public Individual(Element node, Side s, XPathParser xpath) {
		super(node, s, xpath);
		value = node.getTextContent();
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return "<em>" + value + "</em>";
	}

}
