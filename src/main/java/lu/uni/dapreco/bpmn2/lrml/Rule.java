package lu.uni.dapreco.bpmn2.lrml;

import org.w3c.dom.Node;

import lu.uni.dapreco.bpmn2.XPathParser;
import lu.uni.dapreco.bpmn2.lrml.LRMLParser.RuleType;
import lu.uni.dapreco.bpmn2.lrml.Side.SideType;

public class Rule extends BaseLRMLElement {

	private RuleType type;

	private Side lhs;
	private Side rhs;

	public Rule(Node node, RuleType t, XPathParser xpath) {
		super(node, xpath);
		type = t;
		Node child = root.getFirstChild();
		while (child.getNodeName() != "ruleml:if")
			child = child.getNextSibling();
		// type is needed only in RHS
		lhs = new Side(child, SideType.IF, this, xpath);
		child = root.getFirstChild();
		while (child.getNodeName() != "ruleml:then")
			child = child.getNextSibling();
		rhs = new Side(child, SideType.THEN, this, xpath);
	}

	public RuleType getType() {
		return type;
	}

	public Side getLHS() {
		return lhs;
	}

	public Side getRHS() {
		return rhs;
	}

	public String translate() {
		return lhs.translate() + rhs.translate();
	}

	public boolean analyze() {
		return lhs.analyze() || rhs.analyze();
	}

}
