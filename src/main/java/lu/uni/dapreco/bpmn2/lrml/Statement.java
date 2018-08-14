package lu.uni.dapreco.bpmn2.lrml;

import org.w3c.dom.Node;

import lu.uni.dapreco.bpmn2.XPathParser;
import lu.uni.dapreco.bpmn2.lrml.LRMLParser.RuleType;

public class Statement extends BaseLRMLElement {

	private String name;
	private RuleType type;
	private Rule rule;

	public Statement(Node node) {
		super(node);
		name = root.getAttribute("key");
		Node child = root.getFirstChild();
		while (!child.getNodeName().equals("ruleml:Rule"))
			child = child.getNextSibling();
		type = LRMLParser.contextMap.getRuleType(name);
		rule = new Rule(child, type);
	}

	public String getName() {
		return name;
	}

	public Rule getRule(XPathParser xpath) {
		return rule;
	}

	public RuleType getRuleType() {
		return type;
	}

	public void translate(XPathParser xpath) {
		System.out.println(name + " ==> " + getRuleType());
		rule.translate();
		System.out.println();
	}

}
