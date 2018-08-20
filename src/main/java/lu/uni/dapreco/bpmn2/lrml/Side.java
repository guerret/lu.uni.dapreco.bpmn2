package lu.uni.dapreco.bpmn2.lrml;

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

	enum QuantifierType {
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

	public String translate() {
		String ret = position.toString();
		if (position == SideType.THEN) {
			switch (getType()) {
			case PERMISSIONS:
				ret += " it is allowed that";
				break;
			case OBLIGATIONS:
				ret += " it must happen that";
				break;
			case CONSTITUTIVE:
				ret += " it follows that";
				break;
			default:
				ret += " UNKNOWN IMPLICATION";
			}
		}
		ret += writeQuantifier() + "\n";
		ret += translateChild(child, "  ");
		return ret;
	}

	private String writeQuantifier() {
		if (quantifier == QuantifierType.EXISTENCE)
			return ", in at least a situation";
		else
			return ", in all possible situations";
	}

	private String translateChild(Node n, String indent) {
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
			return "O questo? " + n.getNodeName() + "\n";
		}
	}

	private String translateExists(Node n, String indent) {
		checkThatAtomsOrAndsAreExactlyOne(n);
		String ret = "";
		Node child = n.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE)
				ret += translateChild(child, indent);
			child = child.getNextSibling();
		}
		return ret;
	}

	private String translateVar(Node n, String indent) {
		return "";
	}

	private String translateAnd(Node n, String indent) {
		String ret = ""; // checkThatThereIsOneRexistattime(n);
		indent += "- ";
		// the last one is an error
		String search = "ruleml:Atom[ruleml:Rel[@iri='rioOnto:RexistAtTime']] | ruleml:Atom[ruleml:Rel[@iri='rioOnto:Permitted']] | ruleml:Atom[ruleml:Rel[@iri='rioOnto:Obliged']] | ruleml:Atom";
		NodeList nl = xpath.parseNode(search, n);
		// should be the first line
		// translateAtom(new Atom(nl.item(0), xpath), indent);
		return ret + translateChild(nl.item(0), indent);
	}

	// private Atom[] getAtoms(Node n) {
	// NodeList nl = xpath.parseNode("ruleml:Atom", n);
	// Atom[] atoms = new Atom[nl.getLength()];
	// for (int i = 0; i < nl.getLength(); i++)
	// atoms[i] = new Atom(nl.item(i), xpath);
	// return atoms;
	// }

	private String translateAtom(Atom atom, String indent) {
		String ret;
		String[] variables;
		Atom[] atoms;
		String iri = atom.getPredicateIRI();
		switch (iri) {
		case "rioOnto:RexistAtTime":
			atoms = findSiblingsByName(atom, atom.getFirstVariable());
			if (atoms.length > 0) {
				ret = indent + "At time " + atom.getVariableName(1) + ", the following really exists:\n";
				indent += "- ";
				ret += translateAtom(atoms[0], indent);
			} else { // TODO controllare che ci sia nella parte IF
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
			indent += "- ";
			for (int i = 1; i < variables.length; i++)
				ret += parseSiblings(atom, indent, variables[i]);
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
			indent += "- ";
			variables = atom.getArgumentsToTranslate();
			for (String v : variables)
				ret += parseSiblings(atom, indent, v);
		}
		return ret;
	}

	private String parseSiblings(Atom atom, String indent, String v) {
		String ret = "";
		if (v.startsWith("Ind:")) {
		} else if (v.startsWith("Expr")) {
		} else {
			Atom[] atoms = findSiblingsByName(atom, v);
			if (atoms.length > 0)
				for (Atom a : atoms)
					ret += translateAtom(a, indent);
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

	private Atom[] findSiblingsByName(Atom a, String name) {
		String search = "preceding-sibling::ruleml:Atom[ruleml:Var[1]/@key='" + name
				+ "'] | preceding-sibling::ruleml:Atom[ruleml:Var[1]/@keyref='" + name
				+ "'] | following-sibling::ruleml:Atom[ruleml:Var[1]/@key='" + name
				+ "'] | following-sibling::ruleml:Atom[ruleml:Var[1]/@keyref='" + name + "']";
		NodeList nl = xpath.parseNode(search, a.root);
		Atom[] atoms = new Atom[nl.getLength()];
		for (int i = 0; i < atoms.length; i++)
			atoms[i] = new Atom(nl.item(i), xpath);
		return atoms;
	}

	private String translateNaf(Node n, String indent) {
		String ret = checkThatAtomsOrAndsAreExactlyOne(n);
		ret += indent + "the following has not been found:\n";
		Node child = n.getFirstChild();
		while (child.getNodeType() != Node.ELEMENT_NODE)
			child = child.getNextSibling();
		return ret + translateChild(child, indent);
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
