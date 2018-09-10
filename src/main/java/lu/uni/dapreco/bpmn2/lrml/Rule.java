package lu.uni.dapreco.bpmn2.lrml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import lu.uni.dapreco.bpmn2.XPathParser;
import lu.uni.dapreco.bpmn2.lrml.LRMLParser.RuleType;
import lu.uni.dapreco.bpmn2.lrml.Side.SideType;
import lu.uni.dapreco.bpmn2.lrml.rioonto.DeonticAtom;

public class Rule extends BaseLRMLElement {

	private RuleType type;

	private DeonticAtom bearer;

	private Side lhs;
	private Side rhs;

	private Statement owner;

	public Rule(Element node, RuleType t, XPathParser xpath, Statement o) {
		super(node, xpath);
		type = t;
		bearer = null;
		owner = o;
		Node child = root.getFirstChild();
		while (child.getNodeName() != "ruleml:if")
			child = child.getNextSibling();
		// type is needed only in RHS
		lhs = new Side((Element) child, SideType.IF, this, xpath);
		child = root.getFirstChild();
		while (child.getNodeName() != "ruleml:then")
			child = child.getNextSibling();
		rhs = new Side((Element) child, SideType.THEN, this, xpath);
	}

	public RuleType getType() {
		return type;
	}

	public DeonticAtom getBearer() {
		return bearer;
	}

	public void setBearer(DeonticAtom a) {
		bearer = a;
	}

	public Side getLHS() {
		return lhs;
	}

	public Side getRHS() {
		return rhs;
	}

	public Statement getOwnerStatement() {
		return owner;
	}

	public String translate() {
		List<String> ret = new ArrayList<String>();
		List<String> l = lhs.translate();
		for (String t : l) {
			if (!in_array(t, ret))
				ret.add(t);
		}
		List<String> r = rhs.translate();
		for (String t : r) {
			if (!in_array(t, ret))
				ret.add(t);
		}
		ret.add("");
		String[] output = new String[ret.size()];
		for (int i = 0; i < ret.size(); i++) {
			output[i] = ret.get(i).toString();
		}
		return String.join("", output);
	}

	private boolean in_array(String text, List<String> list) {
		for (String t : list)
			if (!text.startsWith("<span>") && !text.contains("<ol>") && !text.equals("</ol>") && !text.equals("<br />")
					&& !text.equals("<ul>") && !text.equals("</ul>") && !text.equals("<li>") && !text.equals("</li>")
					&& text.equals(t))
				return true;
		return false;
	}

	public boolean analyze() {
		return lhs.analyze() || rhs.analyze();
	}

	public SideType whereDefined(String var) {
		if (lhs.isDefined(var))
			return SideType.IF;
		if (rhs.isDefined(var))
			return SideType.THEN;
		return null;
	}

	public Statement getBearerStatement() {
		if (bearer == null)
			return null;
		return bearer.getOwnerSide().getOwnerRule().getOwnerStatement();
	}

}
