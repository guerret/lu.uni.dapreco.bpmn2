package lu.uni.dapreco.bpmn2.lrml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lu.uni.dapreco.bpmn2.XPathParser;
import lu.uni.dapreco.bpmn2.lrml.LRMLParser.RuleType;

public class Side extends BaseLRMLElement {

	public enum SideType {
		IF, THEN
	};

	private SideType position;

	private Rule rule;

	private Node child;

	private enum QuantifierType {
		EXISTENCE, UNIVERSAL
	};

	private QuantifierType quantifier;

	public Side(Node node, SideType p, Rule r, XPathParser xpath) {
		super(node, xpath);
		position = p;
		rule = r;
		child = root.getFirstChild();
		while (child.getNodeType() != Node.ELEMENT_NODE)
			child = child.getNextSibling();
		if (child.getNodeName().equals("ruleml:Exists"))
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
		ret.add(e + ", " + writeQuantifier());
		ret.addAll(translateChild(child, "  "));
		return ret;
	}

	private String writeQuantifier() {
		if (quantifier == QuantifierType.EXISTENCE)
			return "in at least a situation";
		else
			return "in all possible situations";
	}

	private List<String> translateChild(Node n, String indent) {
		switch (n.getNodeName()) {
		case "ruleml:Exists":
			return translateExists(n, indent);
		case "ruleml:Var":
			return translateVar(n, indent);
		case "ruleml:And":
			return translateAnd(n, indent);
		case "ruleml:Atom":
			return translateAtom(new Atom(n, xpath), indent);
		case "ruleml:Naf":
			return translateNaf(n, indent);
		default:
			List<String> l = new ArrayList<String>();
			l.add("O questo? " + n.getNodeName());
			return l;
		}
	}

	private List<String> translateExists(Node n, String indent) {
		// checkThatAtomsOrAndsAreExactlyOne(n);
		List<String> ret = new ArrayList<String>();
		Node child = n.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				// String translateChild = translateChild(child, indent);
				// if (translateChild.length() > 0)
				// ret.add(translateChild);
				ret.addAll(translateChild(child, indent));
			}
			child = child.getNextSibling();
		}
		return ret;
	}

	private List<String> translateVar(Node n, String indent) {
		return new ArrayList<String>();
	}

	private List<String> translateAnd(Node n, String indent) {
		// checkThatThereIsOneRexistattime(n);
		// the last one is an error
		String search = "ruleml:Atom[ruleml:Rel[@iri='rioOnto:RexistAtTime']] | ruleml:Atom[ruleml:Rel[@iri='rioOnto:Permitted']] | ruleml:Atom[ruleml:Rel[@iri='rioOnto:Obliged']] | ruleml:Atom";
		NodeList nl = xpath.parseNode(search, n);
		// should be the first line
		// translateAtom(new Atom(nl.item(0), xpath), indent);
		List<String> ret = translateChild(nl.item(0), indent);
		search = "ruleml:Naf";
		nl = xpath.parseNode(search, n);
		for (int i = 0; i < nl.getLength(); i++)
			ret.addAll(translateNaf(nl.item(i), indent));
		return ret;
	}

	// private Atom[] getAtoms(Node n) {
	// NodeList nl = xpath.parseNode("ruleml:Atom", n);
	// Atom[] atoms = new Atom[nl.getLength()];
	// for (int i = 0; i < nl.getLength(); i++)
	// atoms[i] = new Atom(nl.item(i), xpath);
	// return atoms;
	// }

	private String translateAtom_old(Atom atom, String indent) {
		String ret;
		String[] variables;
		Atom[] atoms;
		String iri = atom.getPredicateIRI();
		switch (iri) {
		case "rioOnto:RexistAtTime":
			atoms = atom.findSiblingsByName(atom.getFirstVariable());
			if (atoms.length > 0) {
				ret = indent + "At time " + atom.getVariableName(1) + ", the following really exists:\n";
				ret += translateAtom_old(atoms[0], indent + "- ");
			} else {
				ret = indent + "At time " + atom.getVariableName(1) + ", the above really exists\n";
			}
			break;
		case "rioOnto:Permitted":
			variables = atom.getReifiedArguments();
			ret = indent + "It is permitted that, at time " + variables[0] + ", for " + variables[1] + " there is "
					+ variables[2] + "\n";
			indent += "- ";
			for (int i = 1; i < variables.length; i++)
				ret += parseSiblings(atom, indent, variables[i]);
			break;
		case "rioOnto:Obliged":
			variables = atom.getReifiedArguments();
			ret = indent + "At time " + variables[0] + ", " + variables[1] + " is obliged to " + variables[2] + "\n";
			for (int i = 1; i < variables.length; i++)
				ret += parseSiblings(atom, indent + "- ", variables[i]);
			break;
		case "rioOnto:and":
			variables = atom.getReifiedArguments();
			ret = indent + "All of the following apply:\n";
			indent += "- ";
			for (String v : variables)
				ret += parseSiblings(atom, indent, v);
			break;
		case "rioOnto:or":
			variables = atom.getReifiedArguments();
			ret = indent + "At least one of the following applies:\n";
			indent += "- ";
			for (String v : variables)
				ret += parseSiblings(atom, indent, v);
			break;
		case "rioOnto:not":
			variables = atom.getNonReifiedArguments();
			ret = indent + variables[0] + ": The following does not apply:\n";
			indent += "- ";
			ret += parseSiblings(atom, indent, variables[1]);
			break;
		case "rioOnto:cause":
			variables = atom.isReified() ? atom.getReifiedArguments() : atom.getNonReifiedArguments();
			ret = indent + "The fact " + variables[0] + " is the cause of the fact " + variables[1] + "\n";
			for (String v : variables)
				ret += parseSiblings(atom, indent + "- ", v);
			break;
		case "rioOnto:imply":
			variables = atom.getNonReifiedArguments();
			ret = indent + atom.getArgumentText(0) + ": " + atom.getArgumentText(1) + " implies "
					+ atom.getArgumentText(2) + "\n";
			break;
		default:
			ret = atom.translate(indent);
			variables = atom.getArgumentsToTranslate();
			for (String v : variables) {
				ret += parseSiblings(atom, indent + "- ", v);
			}
		}
		return ret;
	}

	private List<String> translateAtom(Atom atom, String indent) {
		List<String> ret = new ArrayList<String>();
		String[] variables = atom.isReified() ? atom.getReifiedArguments() : atom.getNonReifiedArguments();
		boolean ignoreLHS = false;
		switch (atom.getPredicateIRI()) {
		case "rioOnto:RexistAtTime":
			// only first argument, the second is the time
			variables = Arrays.copyOfRange(variables, 0, 1);
			break;
		case "rioOnto:Permitted":
		case "rioOnto:Obliged":
			// first argument is reification (although not reified), second is time
			variables = Arrays.copyOfRange(variables, 2, variables.length);
			break;
		case "rioOnto:and":
		case "rioOnto:or":
			// first argument is reification (although not reified), the rest are useful
			variables = Arrays.copyOfRange(variables, 1, variables.length);
			break;
		case "rioOnto:not":
			// first argument is reification (although not reified), the second is useful
			variables = Arrays.copyOfRange(variables, 1, 2);
			break;
		case "rioOnto:cause":
		case "rioOnto:imply":
			break;
		default:
			variables = atom.getArgumentsToTranslate();
		}
		Atom[] definitionAtoms = atom.getDefinitionAtoms(variables);
		ret.add(indent + atom.translateButBetter());
		for (Atom d : definitionAtoms)
			if (d != null && d.getArgumentsToTranslateButBetter().length > 1)
				ret.addAll(translateChild(d.root, indent + "  "));
		return ret;
	}

	private String parseSiblings(Atom atom, String indent, String v) {
		String ret = "";
		if (v.startsWith("Ind:")) {
		} else if (v.startsWith("Expr")) {
		} else {
			Atom[] atoms = atom.findSiblingsByName(v);
			if (atoms.length > 0)
				for (Atom a : atoms)
					ret += translateAtom_old(a, indent);
			else {
				Side lhs = rule.getLHS();
				if (this == lhs)
					ret += "PROBLEM IN ATOM " + atom.getPredicateIRI() + ": " + v + " undefined\n";
				else {
					String search = "descendant::ruleml:Atom[ruleml:Var[1]/@key='" + v
							+ "'] | descendant::ruleml:Atom[ruleml:Var[1]/@keyref='" + v + "']";
					NodeList nl = xpath.parseNode(search, lhs.root);
					if (nl.getLength() > 0)
						ret += indent + v + " (which is defined above in IF part)\n";
					else
						ret += "PROBLEM IN ATOM " + atom.getPredicateIRI() + ": " + v
								+ " undefined (not even in LHS)\n";
				}
			}
		}
		return ret;
	}

	private List<String> translateNaf(Node n, String indent) {
		// checkThatAtomsOrAndsAreExactlyOne(n);
		List<String> l = new ArrayList<String>();
		l.add(indent + "The following has not been found:");
		// String ret = indent + "the following has not been found:\n";
		Node child = n.getFirstChild();
		while (child.getNodeType() != Node.ELEMENT_NODE)
			child = child.getNextSibling();
		l.addAll(translateChild(child, indent + "  "));
		// return ret + translateChild(child, indent);
		return l;
	}

	private String checkThatThereIsOneRexistattime(Node n) {
		String search = "ruleml:Atom[ruleml:Rel[@iri='rioOnto:RexistAtTime']] | ruleml:Atom[ruleml:Rel[@iri='rioOnto:Permitted']] | ruleml:Atom[ruleml:Rel[@iri='rioOnto:Obliged']]";
		NodeList nl = xpath.parseNode(search, n);
		if (nl.getLength() != 1)
			return "WRONG number of atoms called rioOnto:RexistAtTime or rioOnto:Permitted or rioOnto:Obliged";
		return "";
	}

	private String checkThatAtomsOrAndsAreExactlyOne(Node n) {
		String search = "ruleml:Atom | ruleml:And";
		NodeList nl = xpath.parseNode(search, n);
		if (nl.getLength() != 1)
			return "WRONG number of Atom or And children";
		return "";
	}

	public boolean analyze() {
		if (!child.getNodeName().equals("ruleml:Atom") && !child.getNodeName().equals("ruleml:Exists"))
			return true;
		child = child.getNextSibling();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE)
				return true;
			child = child.getNextSibling();
		}
		return false;
	}

}
