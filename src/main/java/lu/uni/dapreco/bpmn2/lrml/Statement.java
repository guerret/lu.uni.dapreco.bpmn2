package lu.uni.dapreco.bpmn2.lrml;

import org.w3c.dom.Node;

import lu.uni.dapreco.bpmn2.XPathParser;
import lu.uni.dapreco.bpmn2.lrml.LRMLParser.RuleType;

public class Statement extends BaseLRMLElement {

	private String name;
	private RuleType type;
	private Rule rule;

	public Statement(Node node, XPathParser xpath) {
		super(node, xpath);
		name = root.getAttribute("key");
		Node child = root.getFirstChild();
		while (!child.getNodeName().equals("ruleml:Rule"))
			child = child.getNextSibling();
		type = LRMLParser.contextMap.getRuleType(name);
		rule = new Rule(child, type, xpath);
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

	public String translate() {
		return /* name + " ==> " + getRuleType() + "\n" + */ rule.translate() + "\n";
	}

	public boolean analyze() {
		return rule.analyze();
	}

}
