package lu.uni.dapreco.parser.lrml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import lu.uni.dapreco.parser.XPathParser;
import lu.uni.dapreco.parser.lrml.rioonto.ExceptionAtom;

public class RuleMLBlock extends BaseLRMLElement {

	enum RuleMLType {
		VAR, IND, EXPR, EXISTS, AND, NAF, ATOM, UNKNOWN
	};

	protected RuleMLType type;

	protected List<RuleMLBlock> children;

	protected Side owner;

	protected RuleMLBlock(Element node, Side s, XPathParser xpath) {
		super(node, xpath);
		owner = s;
		switch (node.getLocalName()) {
		case "Var":
			type = RuleMLType.VAR;
			break;
		case "Ind":
			type = RuleMLType.IND;
			break;
		case "Expr":
			type = RuleMLType.EXPR;
			break;
		case "Exists":
			type = RuleMLType.EXISTS;
			break;
		case "And":
			type = RuleMLType.AND;
			break;
		case "Naf":
			type = RuleMLType.NAF;
			break;
		case "Atom":
			type = RuleMLType.ATOM;
			break;
		default:
			type = RuleMLType.UNKNOWN;
		}
		children = new ArrayList<RuleMLBlock>();
		String search = "ruleml:*";
		NodeList nl = xpath.parseNode(search, root);
		for (int i = 0; i < nl.getLength(); i++) {
			Element item = (Element) nl.item(i);
			switch (item.getLocalName()) {
			case "Atom":
				children.add(Atom.create(item, owner, xpath));
				break;
			case "Expr":
				children.add(Atom.create(item, owner, xpath));
				break;
			case "Ind":
				children.add(new Individual(item, owner, xpath));
				break;
			case "Var":
				children.add(new Variable(item, this, xpath));
				break;
			case "Rel":
			case "Fun":
				break;
			default:
				children.add(new RuleMLBlock(item, owner, xpath));
			}
		}
	}

	public String getName() {
		return "";
	}

	public RuleMLType getType() {
		return type;
	}

	public Side getOwnerSide() {
		return owner;
	}

	public List<String> translate() {
		switch (type) {
		case EXISTS:
			return translateExists();
		case VAR:
			return new ArrayList<String>();
		case AND:
			return translateAnd();
		case ATOM:
			// NEVER here (overridden)
			System.err.println("Non dovrei essere qui!");
			// return translateAtom();
			// break;
		case NAF:
			return translateNaf();
		default:
			List<String> l = new ArrayList<String>();
			l.add("O questo? " + root.getNodeName());
			return l;
		}
	}

	private List<String> translateExists() {
		List<String> ret = new ArrayList<String>();
		List<RuleMLBlock> elems = getRealChildren();
		for (RuleMLBlock child : elems)
			ret.addAll(child.translate());
		return ret;
	}

	// private List<String> translateAnd2() {
	// List<String> ret = new ArrayList<String>();
	// ret.add("<ul>");
	// List<RuleMLBlock> elems = getRealChildren();
	// for (RuleMLBlock child : elems) {
	// if (child.type == RuleMLType.ATOM) {
	// Atom atom = (Atom) child;
	// String currentText = ret.toString();
	// if (!currentText.contains(child.toString())
	// // && !currentText.contains(atom.getLocalPredicate())
	// && !atom.isDefinition()) {
	// ret.add("<li>");
	// ret.addAll(child.translate());
	// ret.add("</li>");
	// }
	// } else if (child.type == RuleMLType.NAF) {
	// ret.add("<li>");
	// ret.addAll(child.translate());
	// ret.add("</li>");
	// } else
	// ret.add("<li>O cosa ci fa?</li>");
	// }
	// ret.add("</ul>");
	// return ret;
	// }

	private List<String> translateAnd() {
		List<String> ret = new ArrayList<String>();
		List<RuleMLBlock> elems = getRealChildren();
		String list = null;
		if (elems.size() > 1)
			list = "<ul>";
		if (list != null)
			ret.add("<ul>");
		while (!elems.isEmpty()) {
			RuleMLBlock child = elems.get(0);
			if (child.type == RuleMLType.ATOM) {
				Atom atom = (Atom) child;
				String currentText = ret.toString();
				if (!currentText.contains(child.toString()) && !atom.inline()) {
					if (list != null)
						ret.add("<li>");
					ret.addAll(child.translate());
					if (list != null)
						ret.add("</li>");
				}
			} else if (child.type == RuleMLType.NAF) {
				if (list != null)
					ret.add("<li>");
				ret.addAll(child.translate());
				if (list != null)
					ret.add("</li>");
			} else
				ret.add("<li>PROBLEM: no idea what this is</li>");
			List<RuleMLBlock> translated = child.getTranslated();
			elems.removeAll(translated);
		}
		if (list != null)
			ret.add("</ul>");
		return ret;
	}

	private List<String> translateNaf() {
		List<String> ret = new ArrayList<String>();
		ExceptionAtom exception = (ExceptionAtom) children.get(0);
		exception.setNegation(getName());
		ret.add(exception.toString());
		exception.setNegation(null);
		return ret;
	}

	private List<RuleMLBlock> getRealChildren() {
		List<RuleMLBlock> ret = new ArrayList<RuleMLBlock>();
		for (RuleMLBlock child : children)
			if (child.type != RuleMLType.VAR && child.type != RuleMLType.EXPR && child.type != RuleMLType.IND)
				ret.add(child);
		return ret;
	}

	public boolean equals(RuleMLBlock block) {
		List<RuleMLBlock> elems = getRealChildren();
		List<RuleMLBlock> blockElems = block.getRealChildren();
		if (type != block.type || !getName().equals(block.getName()) || elems.size() != blockElems.size())
			return false;
		for (int i = 0; i < elems.size(); i++) {
			boolean found = false;
			for (int j = 0; j < elems.size(); j++) {
				if (elems.get(i).equals(blockElems.get(j))) {
					found = true;
					break;
				}
			}
			if (!found)
				return false;
		}
		return true;
	}

	public List<RuleMLBlock> getTranslated() {
		List<RuleMLBlock> ret = new ArrayList<RuleMLBlock>();
		ret.add(this);
		return ret;
	}

	public boolean hasPredicate(String pred) {
		for (RuleMLBlock child : children)
			if (child.hasPredicate(pred))
				return true;
		return false;
	}

}
