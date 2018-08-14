package lu.uni.dapreco.bpmn2.lrml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import lu.uni.dapreco.bpmn2.lrml.LRMLParser.RuleType;

public class Side extends BaseLRMLElement {

	public enum SideType {
		IF, THEN
	};

	private SideType position;

	private RuleType type;

	private Node child;

	public Side(Node node, SideType p, RuleType t) {
		super(node);
		position = p;
		type = t;
		child = root.getFirstChild();
		while (child.getNodeType() != Node.ELEMENT_NODE)
			child = child.getNextSibling();
	}

	public SideType getPosition() {
		return position;
	}

	public RuleType getType() {
		return type;
	}

	public void translate() {
		System.out.print(position);
		if (position == SideType.THEN)
			switch (type) {
			case PERMISSIONS:
				System.out.println(" it is allowed that");
				break;
			case OBLIGATIONS:
				System.out.println(" it must happen that");
				break;
			case CONSTITUTIVE:
				System.out.println(" it follows that");
				break;
			default:
				System.out.println(" UNKNOWN IMPLICATION");
			}
		else
			System.out.println();
		translateChild(child, "  ");
	}

	private void translateChild(Node n, String indent) {
		switch (n.getNodeName()) {
		case "ruleml:Exists":
			translateExists(n, indent);
			break;
		case "ruleml:Var":
			translateVar(n, indent);
			break;
		case "ruleml:And":
			translateAnd(n, indent);
			break;
		case "ruleml:Atom":
			translateAtom(n, indent);
			break;
		case "ruleml:Naf":
			translateNaf(n, indent);
			break;
		default:
			System.out.println("O questo? " + n.getNodeName());
		}
	}

	private void translateExists(Node n, String indent) {
		checkThatAtomsOrAndsAreExactlyOne(n);
		Node child = n.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE)
				translateChild(child, indent);
			child = child.getNextSibling();
		}
	}

	private void translateVar(Node n, String indent) {
	}

	private void translateAnd(Node n, String indent) {
		indent += "- ";
		Node child = n.getFirstChild();
		while (child != null) {
			if (child.getNodeType() == Node.ELEMENT_NODE)
				translateChild(child, indent);
			child = child.getNextSibling();
		}
	}

	private void translateAtom(Node n, String indent) {
		Node child = n.getFirstChild();
		if (!child.getNodeName().equals("ruleml:Rel"))
			child = child.getNextSibling();
		System.out.println(indent + "Predicato: " + ((Element) child).getAttribute("iri"));
	}

	private void translateNaf(Node n, String indent) {
		checkThatAtomsOrAndsAreExactlyOne(n);
		System.out.println(indent + "the following has not been found:");
		Node child = n.getFirstChild();
		while (child.getNodeType() != Node.ELEMENT_NODE)
			child = child.getNextSibling();
		translateChild(child, indent);
	}

	private void checkThatAtomsOrAndsAreExactlyOne(Node n) {
		int count = 0;
		Node child = n.getFirstChild();
		while (child != null) {
			if (child.getNodeName().equals("ruleml:Atom") || child.getNodeName().equals("ruleml:And"))
				count++;
			child = child.getNextSibling();
		}
		if (count != 1) {
			System.out.println("Numero sbagliato di atomi o and");
			System.exit(0);
		}
	}

}
