package lu.uni.dapreco.bpmn2.lrml;

import java.util.ArrayList;
import java.util.List;

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
		List<String> ret = new ArrayList<String>();
		List<String> l = lhs.translate();
		for (String s : l) {
			String g = s.trim();
			if (!in_array(g, ret))
				ret.add(s);
		}
		// ret.addAll(l);
		List<String> r = rhs.translate();
		for (String s : r) {
			String g = s.trim();
			if (!in_array(g, ret))
				ret.add(s);
		}
		// ret.addAll(r);
		ret.add("");
		return String.join("\n", ret);
	}

	private boolean in_array(String s, List<String> ret) {
		for (String t : ret)
			if (s.equals(t.trim()))
				return true;
		return false;
	}

	public boolean analyze() {
		return lhs.analyze() || rhs.analyze();
	}

}
