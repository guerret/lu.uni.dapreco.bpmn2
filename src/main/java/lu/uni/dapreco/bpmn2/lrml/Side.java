package lu.uni.dapreco.bpmn2.lrml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import lu.uni.dapreco.bpmn2.XPathParser;
import lu.uni.dapreco.bpmn2.lrml.LRMLParser.RuleType;
import lu.uni.dapreco.bpmn2.lrml.RuleMLBlock.RuleMLType;

public class Side extends BaseLRMLElement {

	public enum SideType {
		IF, THEN
	};

	private SideType position;

	private Rule rule;

	private enum QuantifierType {
		EXISTENCE, UNIVERSAL
	};

	private QuantifierType quantifier;

	private RuleMLBlock content;

	private Map<String, List<Atom>> variables;

	public Side(Element node, SideType p, Rule r, XPathParser xpath) {
		super(node, xpath);
		position = p;
		rule = r;
		variables = new HashMap<String, List<Atom>>();
		Node child = root.getFirstChild();
		while (child.getNodeType() != Node.ELEMENT_NODE)
			child = child.getNextSibling();
		switch (child.getLocalName()) {
		case "Atom":
			content = Atom.create((Element) child, this, xpath);
			break;
		default:
			content = new RuleMLBlock((Element) child, this, xpath);
		}
		if (content.getType() == RuleMLType.EXISTS)
			quantifier = QuantifierType.EXISTENCE;
		else
			quantifier = QuantifierType.UNIVERSAL;
	}

	public SideType getPosition() {
		return position;
	}

	public RuleType getType() {
		return rule.getType();
	}

	public Rule getOwnerRule() {
		return rule;
	}

	public Set<String> getVariables() {
		return variables.keySet();
	}

	public void addVariable(String var, Atom atom) {
		if (!variables.containsKey(var))
			variables.put(var, new ArrayList<Atom>());
		variables.get(var).add(atom);
	}

	public List<Atom> getVariableUses(String var) {
		if (variables.containsKey(var))
			return variables.get(var);
		return new ArrayList<Atom>();
	}

	public boolean isDefined(String var) {
		return variables.containsKey(var);
	}

	public List<String> translate() {
		List<String> ret = new ArrayList<String>();
		String e = position.toString();
		if (position == SideType.THEN) {
			switch (getType()) {
			case PERMISSIONS:
				e += " " + "it is allowed that";
				break;
			case OBLIGATIONS:
				e += " " + "it must happen that";
				break;
			case CONSTITUTIVE:
				e += " " + "it follows that";
				break;
			default:
				e = "UNKNOWN IMPLICATION";
			}
		}
		ret.add(e + ", " + writeQuantifier() + ", ");
		ret.addAll(content.translate());
		// ret.addAll(translateChild(content.root));
		return ret;
	}

	private String writeQuantifier() {
		if (quantifier == QuantifierType.EXISTENCE)
			return "in at least a situation";
		else
			return "in all possible situations";
	}

	public boolean analyze() {
		if (content.getType() != RuleMLType.ATOM && content.getType() != RuleMLType.EXISTS)
			return true;
		Node child = content.root.getNextSibling();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE)
				return true;
			child = child.getNextSibling();
		}
		return false;
	}

	public SideType getSide() {
		if (this == rule.getLHS())
			return SideType.IF;
		return SideType.THEN;
	}

	public boolean equals(Side side) {
		return content.equals(side.content);
	}

	public String toString() {
		return rule.getOwnerStatement().getName() + " - " + position;
	}

}
