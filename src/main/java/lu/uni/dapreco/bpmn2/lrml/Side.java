package lu.uni.dapreco.bpmn2.lrml;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;

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

	// private List<String> translateChild(Node n) {
	// switch (n.getNodeName()) {
	// case "ruleml:Exists":
	// return translateExists(n);
	// case "ruleml:Var":
	// return translateVar(n);
	// case "ruleml:And":
	// return translateAnd(n);
	// case "ruleml:Atom":
	// return translateAtom(Atom.create((Element) n, this, xpath));
	// case "ruleml:Naf":
	// return translateNaf(n);
	// default:
	// List<String> l = new ArrayList<String>();
	// l.add("O questo? " + n.getNodeName());
	// return l;
	// }
	// }

	// private List<String> translateExists(Node n) {
	// List<String> ret = new ArrayList<String>();
	// Node child = n.getFirstChild();
	// while (child != null) {
	// if (child.getNodeType() == Node.ELEMENT_NODE) {
	// ret.addAll(translateChild(child));
	// }
	// child = child.getNextSibling();
	// }
	// return ret;
	// }

	// private List<String> translateVar(Node n) {
	// return new ArrayList<String>();
	// }

	// private List<String> translateAnd(Node n) {
	// // the last one is an error
	// String search = "ruleml:Atom[ruleml:Rel[@iri='rioOnto:RexistAtTime']] |
	// ruleml:Atom[ruleml:Rel[@iri='rioOnto:Permitted']] |
	// ruleml:Atom[ruleml:Rel[@iri='rioOnto:Obliged']] | ruleml:Atom";
	// NodeList nl = xpath.parseNode(search, n);
	// // should be the first line
	// // translateAtom(new Atom(nl.item(0), xpath), indent);
	// List<String> ret = translateChild(nl.item(0));
	// search = "ruleml:Naf";
	// nl = xpath.parseNode(search, n);
	// for (int i = 0; i < nl.getLength(); i++)
	// ret.addAll(translateNaf(nl.item(i)));
	// return ret;
	// }

	// private List<String> translateAtom(Atom atom) {
	// List<String> ret = new ArrayList<String>();
	// String[] variables = atom.isReified() ? atom.getReifiedArguments() :
	// atom.getNonReifiedArguments();
	// String tag = "";
	// String translation = atom.translateButBetter();
	// switch (atom.getPredicateIRI()) {
	// case "rioOnto:RexistAtTime":
	// // only first argument, the second is the time
	// translation += "<ol>";
	// tag = "li";
	// variables = Arrays.copyOfRange(variables, 0, 1);
	// break;
	// case "rioOnto:Permitted":
	// case "rioOnto:Obliged":
	// // first argument is time
	// translation += "<ol>";
	// tag = "li";
	// variables = new String[] { variables[0], variables[2] };
	// break;
	// case "rioOnto:and":
	// case "rioOnto:or":
	// // first argument is reification (although not reified), the rest are useful
	// variables = Arrays.copyOfRange(variables, 1, variables.length);
	// if (variables.length == 1)
	// translation = "";
	// else {
	// translation += "<ol>";
	// tag = "li";
	// }
	// break;
	// case "rioOnto:not":
	// // first argument is reification (although not reified), the second is useful
	// variables = Arrays.copyOfRange(variables, 1, 2);
	// translation += "<ul>";
	// tag = "li";
	// break;
	// case "rioOnto:cause":
	// case "rioOnto:imply":
	// case "rioOnto:partOf":
	// variables = Arrays.copyOfRange(variables, 1, variables.length);
	// break;
	// default:
	// variables = atom.getArgumentsToTranslate();
	// translation += "<br />";
	// }
	// ret.add(translation);
	// Atom[] definitionAtoms = atom.getDefinitionAtoms(variables);
	// for (Atom d : definitionAtoms)
	// if (d != null && d.getArgumentsToTranslateButBetter().length > 1) {
	// if (!tag.equals(""))
	// ret.add("<" + tag + ">");
	// ret.addAll(translateChild(d.root));
	// if (!tag.equals(""))
	// ret.add("</" + tag + ">");
	// }
	// if (translation.contains("<ol>"))
	// ret.add("</ol>");
	// if (translation.contains("<ul>"))
	// ret.add("</ul>");
	// return ret;
	// }

	// private List<String> translateNaf(Node n) {
	// List<String> ret = new ArrayList<String>();
	// ret.add("<span>The following exception does not occur (see exceptions
	// document):</span><ul><li>");
	// Node child = n.getFirstChild();
	// while (child.getNodeType() != Node.ELEMENT_NODE)
	// child = child.getNextSibling();
	// ret.addAll(translateChild(child));
	// ret.add("</li>");
	// ret.add("</ul>");
	// return ret;
	// }

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

	public boolean equals(Side side) {
		return content.equals(side.content);
	}

}
