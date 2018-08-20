package lu.uni.dapreco.bpmn2.lrml;

import org.w3c.dom.Node;

import lu.uni.dapreco.bpmn2.XPathParser;

public class Expression extends Atom {

	public Expression(Node node, XPathParser xpath) {
		super(node, xpath);
		reified = false;
	}

}
