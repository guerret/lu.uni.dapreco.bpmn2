package lu.uni.dapreco.bpmn2.lrml;

import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;

import lu.uni.dapreco.bpmn2.XPathParser;

public class Atom extends RuleMLBlock {

	protected String predicateIRI;

	protected boolean reified;

	// private String[] variables;

	protected Atom(Element node, String pred, Side s, XPathParser xpath) {
		super(node, s, xpath);
		predicateIRI = pred;
		reified = root.hasAttribute("keyref");
		if (type == RuleMLType.ATOM)
			for (RuleMLBlock child : children)
				if (child.type == RuleMLType.VAR)
					owner.addVariable(child.getName(), this);
	}

	// protected Atom(Element node, Side s, XPathParser xpath, String sx) {
	// super(node, s, xpath);
	// String search = "ruleml:Rel/@iri | ruleml:Fun/@iri";
	// predicateIRI = xpath.parseNode(search, node).item(0).getNodeValue();
	// reified = root.hasAttribute("keyref");
	// search = "ruleml:Var | ruleml:Expr | ruleml:Ind";
	// NodeList nl = xpath.parseNode(search, root);
	// variables = new String[nl.getLength()];
	// for (int i = 0; i < nl.getLength(); i++) {
	// Node item = nl.item(i);
	// if (item.getNodeName().equals("ruleml:Var")) {
	// Element elem = (Element) nl.item(i);
	// variables[i] = elem.hasAttribute("key") ? elem.getAttribute("key") :
	// elem.getAttribute("keyref");
	// } else if (item.getNodeName().equals("ruleml:Ind"))
	// variables[i] = "Ind:" + item.getTextContent();
	// else if (item.getNodeName().equals("ruleml:Expr"))
	// variables[i] = "Expr:" + countExpr(i);
	// }
	// }

	public static Atom create(Element node, Side s, XPathParser xpath) {
		if (node.getNodeName().equals("ruleml:Expr")) {
			String search = "ruleml:Fun/@iri";
			String pred = xpath.parseNode(search, node).item(0).getNodeValue();
			return new Expression(node, pred, s, xpath);
		}
		if (node.getNodeName().equals("ruleml:Atom")) {
			String search = "ruleml:Rel/@iri";
			String pred = xpath.parseNode(search, node).item(0).getNodeValue();
			String prefix = pred.substring(0, pred.indexOf(":"));
			if (prefix.equals("rioOnto"))
				return new RioOntoAtom(node, pred, s, xpath);
			return new Atom(node, pred, s, xpath);
		}
		return null;
	}

	// private int countExpr(int index) {
	// int count = 1;
	// for (int i = 0; i < index; i++)
	// if (variables[i].startsWith("Expr"))
	// count++;
	// return count;
	// }

	// public String getPredicateIRI() {
	// return predicateIRI;
	// }

	public String getLocalPredicate() {
		return predicateIRI.substring(predicateIRI.indexOf(":") + 1);
	}

	public boolean isReified() {
		return reified;
	}

	@Override
	public String getName() {
		if (reified)
			return children.get(0).getName();
		String pred = getLocalPredicate();
		if (pred.equals("PersonalData") || pred.equals("Controller") || pred.equals("CompensationFor"))
			return children.get(0).getName();
		return null;
	}

	// public String getFirstVariable() {
	// return variables[0];
	// }

	// public String getId() {
	// if (reified)
	// return variables[0];
	// return null;
	// }

	// public String getVariableName(int index) {
	// return variables[index];
	// }

	// public String[] getVariables() {
	// return variables;
	// }

	public List<RuleMLBlock> getArguments() {
		return children.subList(reified ? 1 : 0, children.size());
	}

	public List<RuleMLBlock> getNewArgumentsToTranslate() {
		List<RuleMLBlock> ret = getArguments();
		String pred = getLocalPredicate();
		if (pred.equals("PersonalData") || pred.equals("Controller") || pred.equals("Store")
				|| pred.equals("CompensationFor") || pred.equals("RelatedTo") || ret.size() == 1)
			return children.subList(1, children.size());
		return ret;
	}

	// public String[] getReifiedArguments() {
	// return Arrays.copyOfRange(variables, 1, variables.length);
	// }
	//
	// public String[] getNonReifiedArguments() {
	// return variables;
	// }

	// public String getArgumentTextButBetter(int index) {
	// String v = variables[reified ? index + 1 : index];
	// if (v.startsWith("Expr")) {
	// String exprIndex = v.substring(v.indexOf(":") + 1);
	// String search = "ruleml:Expr[" + exprIndex + "]";
	// Node item = xpath.parseNode(search, root).item(0);
	// Atom e = Atom.create((Element) item, owner, xpath);
	// String[] arguments = e.getNonReifiedArguments();
	// String ret = "[the " + e.getLocalPredicate() + " of ";
	// for (int i = 0; i < arguments.length; i++) {
	// ret += e.getArgumentTextButBetter(i);
	// if (i < arguments.length - 1)
	// ret += ", ";
	// }
	// ret += "]";
	// return ret;
	// }
	// if (v.startsWith("Ind:"))
	// return v.substring("Ind:".length());
	// Atom[] atoms = findDefinitionAtomsForVariable(v, true);
	// if (atoms.length > 0)
	// if (atoms[0].predicateIRI.startsWith("rioOnto:"))
	// return v;
	// else
	// return atoms[0].getLocalPredicate() + " (" + v + ")";
	// else
	// return "PROBLEM: MISSING CONCEPT (" + v + ")";
	// }

	// public String[] getArgumentsToTranslate() {
	// String pred = getLocalPredicate();
	// String[] arguments = reified ? getReifiedArguments() :
	// getNonReifiedArguments();
	// if (pred.equals("PersonalData") || pred.equals("Controller") ||
	// pred.equals("Store")
	// || pred.equals("CompensationFor") || pred.equals("RelatedTo") ||
	// arguments.length == 1)
	// return Arrays.copyOfRange(variables, 1, variables.length);
	// return arguments;
	// }

	// public String[] getArgumentsToTranslateButBetter() {
	// String[] arguments = reified ? getReifiedArguments() :
	// getNonReifiedArguments();
	// return arguments;
	// }

	@Override
	public List<String> translate() {
		List<String> ret = new ArrayList<String>();
		List<RuleMLBlock> arguments = getNewArgumentsToTranslate();
		String translation = toString() + "<br />";
		ret.add(translation);
		List<Atom> newDefinitionAtoms = getNewDefinitionAtoms(arguments);
		for (Atom d : newDefinitionAtoms)
			if (d.getArguments().size() > 1)
				ret.addAll(d.translate());
		if (translation.contains("<ol>"))
			ret.add("</ol>");
		if (translation.contains("<ul>"))
			ret.add("</ul>");
		return ret;
	}

	@Override
	public String toString() {
		String pred = getLocalPredicate();
		List<RuleMLBlock> arguments = getArguments();
		String predText = "<strong>" + pred + "</strong>";
		String name = getName();
		if (name != null)
			predText += " (" + name + ")";
		switch (pred) {
		// 1 argument
		case "HasBeenDamaged":
			return "There is a situation in which the data subject " + arguments.get(0) + " " + predText;
		case "Risk":
			return "There is a " + predText + " for the subject " + arguments.get(0);
		case "accurate":
			return "There is a situation where " + arguments.get(0) + " are " + predText;
		case "lawfulness":
		case "fairness":
			return "There is a situation of " + predText + " for the processing " + arguments.get(0);

		// 2 arguments
		case "AuthorizedBy":
			return "There is a situation in which " + arguments.get(0) + " is " + predText + " " + arguments.get(1);
		case "PersonalData":
			return predText + " pertain to " + arguments.get(1);
		case "nominates":
			return "There is a situation in which " + arguments.get(0) + " " + predText + " " + arguments.get(1);
		case "Controller": // TODO problematic
			return "The " + predText + " is controlling " + arguments.get(1);
		case "PersonalDataProcessing":
			return "The " + predText + " is performed by " + arguments.get(0) + " over the " + arguments.get(1);
		case "AbleTo":
		case "PhysicallyUnableTo":
		case "LegallyUnableTo":
			return arguments.get(0) + " is " + predText + " perform operation " + arguments.get(1);
		case "Identify":
			return "There is an " + predText + " operation performed by " + arguments.get(0) + " on the data subject "
					+ arguments.get(1);
		case "isBasedOn":
			return "There is a situation where " + arguments.get(0) + " " + predText + " " + arguments.get(1);
		case "GiveConsent":
			return "There is a " + predText + " operation performed by " + arguments.get(0) + " concerning "
					+ arguments.get(1);
		case "Request":
			return "A " + predText + " is made by " + arguments.get(0) + " and concerns " + arguments.get(1);
		case "Contain":
			return "There is a situation where " + arguments.get(1) + " " + predText + " " + arguments.get(0);
		case "Describe":
		case "Implement":
			return "There is a situation where " + arguments.get(0) + " " + predText + " " + arguments.get(1);
		case "CompatibleWith":
			return "There is a situation where " + arguments.get(0) + " is " + predText + " " + arguments.get(1);
		case "Delete":
		case "Rectify":
			return "There is a " + predText + " operation performed by " + arguments.get(0) + " over "
					+ arguments.get(1);
		case "Store":
			return arguments.get(0) + " are placed in " + predText + " inside " + arguments.get(1);
		case "Execute":
			return "There is a " + predText + " operation performed by " + arguments.get(0) + " over task "
					+ arguments.get(1);
		case "RelatedTo":
			return "There is a situation where task " + arguments.get(0) + " is " + predText + " " + arguments.get(1);
		case "Hold":
			return "There is a situation where " + arguments.get(0) + " " + predText + " " + arguments.get(1);
		case "Demonstrate":
			return arguments.get(0) + " " + predText + " the fact " + arguments.get(1);
		case "CompensationFor":
			return "There is a " + predText + " the " + arguments.get(1);
		case "AdequateWith":
		case "RelevantTo":
		case "LimitedTo":
			return arguments.get(0) + " are " + predText + " the " + arguments.get(1);
		case "IdentifiableFrom":
		case "PartyOf":
			return arguments.get(0) + " is " + predText + " the " + arguments.get(1);
		case "Enter":
			return arguments.get(0) + " is in the process of " + predText + " the " + arguments.get(1);
		case "Protect":
		case "Provide":
			return arguments.get(0) + " is in the process to " + predText + " the " + arguments.get(1);

		// 3 arguments
		case "Transmit":
			return "There is a " + predText + " operation performed by " + arguments.get(0) + " concerning the data "
					+ arguments.get(1) + " to destination " + arguments.get(2);
		case "Communicate":
			return "There is a " + predText + " operation performed by " + arguments.get(0) + " to recipient "
					+ arguments.get(1) + " concerning the information " + arguments.get(2);
		case "Lodge":
			return arguments.get(0) + " " + predText + " towards " + arguments.get(2) + " concerning a "
					+ arguments.get(1);
		case "ReceiveFrom":
			return "There is a situation where data subject " + arguments.get(0) + " " + predText + " "
					+ arguments.get(2) + " the " + arguments.get(1);
		default:
			String beforeText = "", afterText = "";
			String[] varNames = new String[arguments.size()];
			for (int i = 0; i < arguments.size(); i++)
				varNames[i] = arguments.get(i).getName();
			if (arguments.size() > 1) {
				beforeText = "MISSING: ";
				afterText = " with " + arguments.size() + " arguments, called " + String.join(", ", varNames);
			}
			return beforeText + "There is a " + predText + afterText;
		}
	}

	// private String translateRioOnto(String pred) {
	// switch (pred) {
	// case "RexistAtTime":
	// if (findDefinitionAtomsForVariable(variables[0], false).length > 0)
	// return "<span>At time " + variables[1] + ", the following situation
	// exists:</span>";
	// else
	// return "<span>At time " + variables[1] + ", the above situation
	// exists</span>";
	// case "Permitted":
	// case "Obliged":
	// return "At time " + variables[1] + ", " + getArgumentTextButBetter(2) + " is
	// " + pred + " to "
	// + getArgumentTextButBetter(0) + "</span>";
	// case "and":
	// return "<span>(All of the following (" + variables[0] + "))</span>";
	// case "or":
	// return "<span>(At least one of the following (" + variables[0] + "))</span>";
	// case "not":
	// return "<span>The next line does not apply (" + variables[0] + ")</span>";
	// case "cause":
	// return "<span>The fact " + getArgumentTextButBetter(0) + " is the cause of
	// the fact "
	// + getArgumentTextButBetter(1) + "</span>";
	// case "imply":
	// return "<span>" + getArgumentTextButBetter(1) + " implies " +
	// getArgumentTextButBetter(2) + "</span>";
	// case "partOf":
	// return "<span>" + getArgumentTextButBetter(0) + " is part of " +
	// getArgumentTextButBetter(1) + "</span>";
	// default:
	// if (pred.startsWith("exception"))
	// return "<span>Exception (" + pred + ")</span>";
	// return "UNKNOWN RIOONTO PREDICATE";
	// }
	// }

	// public Atom[] findDefinitionAtomsForVariable(String v, boolean searchInLHS) {
	// Atom[] atoms = findSiblingsByName(v);
	// if (atoms.length == 0) { // no atoms here
	// String search = "ancestor::ruleml:then";
	// NodeList nl = xpath.parseNode(search, root);
	// if (searchInLHS && nl.getLength() > 0) { // I am in then part, shall I search
	// elsewhere?
	// search = "ruleml:if/descendant::ruleml:Atom[ruleml:Var[1]/@key='" + v
	// + "'] | ruleml:if/descendant::ruleml:Atom[ruleml:Var[1]/@keyref='" + v +
	// "']";
	// nl = xpath.parseNode(search, nl.item(0).getParentNode());
	// if (nl.getLength() > 0) { // there is something in if part
	// atoms = new Atom[nl.getLength()];
	// for (int i = 0; i < nl.getLength(); i++)
	// atoms[i] = Atom.create((Element) nl.item(i), owner, xpath);
	// } else
	// System.out.println("PROBLEM IN ATOM " + predicateIRI + ": " + v + " undefined
	// (not even in LHS)");
	// }
	// }
	// return atoms;
	// }

	// public Atom[] findSiblingsByName(String name) {
	// String search = "preceding-sibling::ruleml:Atom[ruleml:Var[1]/@key='" + name
	// + "'] | preceding-sibling::ruleml:Atom[ruleml:Var[1]/@keyref='" + name
	// + "'] | following-sibling::ruleml:Atom[ruleml:Var[1]/@key='" + name
	// + "'] | following-sibling::ruleml:Atom[ruleml:Var[1]/@keyref='" + name +
	// "']";
	// NodeList nl = xpath.parseNode(search, root);
	// Atom[] atoms = new Atom[nl.getLength()];
	// for (int i = 0; i < atoms.length; i++)
	// atoms[i] = Atom.create((Element) nl.item(i), owner, xpath);
	// return atoms;
	// }

	// public Atom[] getDefinitionAtoms(String[] variables) {
	// List<Atom> atomArray = new ArrayList<Atom>();
	// for (int i = 0; i < variables.length; i++)
	// if (!variables[i].startsWith("Ind:") && !variables[i].startsWith("Expr:")) {
	// Atom[] atoms = findDefinitionAtomsForVariable(variables[i], true);
	// for (Atom a : atoms)
	// atomArray.add(a);
	// }
	// Atom[] atoms = new Atom[atomArray.size()];
	// return atomArray.toArray(atoms);
	// }

	protected List<Atom> getNewDefinitionAtoms(List<RuleMLBlock> arguments) {
		List<Atom> atoms = new ArrayList<Atom>();
		for (RuleMLBlock a : arguments)
			if (a.type == RuleMLType.VAR)
				atoms.addAll(((Variable) a).getDefinitionAtoms(owner));
		atoms.remove(this);
		return atoms;
	}

	// public String translateButBetter() {
	// String pred = getLocalPredicate();
	// String[] arguments = reified ? getReifiedArguments() :
	// getNonReifiedArguments();
	// String predText = (reified || pred.equals("PersonalData") ||
	// pred.equals("Controller")
	// || pred.equals("CompensationFor")) ? pred + " (" + variables[0] + ")" : pred;
	// if (predicateIRI.substring(0, predicateIRI.indexOf(":")).equals("rioOnto"))
	// return translateRioOnto(pred);
	// switch (pred) {
	// // 1 argument
	// case "HasBeenDamaged":
	// return "There is a situation in which the data subject " +
	// getArgumentTextButBetter(0) + " " + predText;
	// case "Risk":
	// return "There is a " + predText + " for the subject " +
	// getArgumentTextButBetter(0);
	// case "accurate":
	// return "There is a situation where " + getArgumentTextButBetter(0) + " are "
	// + predText;
	// case "lawfulness":
	// case "fairness":
	// return "There is a situation of " + predText + " for the processing " +
	// getArgumentTextButBetter(0);
	// // 2 arguments
	// case "AuthorizedBy":
	// return "There is a situation in which " + getArgumentTextButBetter(0) + " is
	// " + predText + " "
	// + getArgumentTextButBetter(1);
	// case "PersonalData":
	// return predText + " pertain to " + getArgumentTextButBetter(1);
	// case "nominates":
	// return "There is a situation in which " + getArgumentTextButBetter(0) + " " +
	// predText + " "
	// + getArgumentTextButBetter(1);
	// case "Controller": // TODO problematic
	// return "The " + predText + " is controlling " + getArgumentTextButBetter(1);
	// case "PersonalDataProcessing":
	// return "The " + predText + " is performed by " + getArgumentTextButBetter(0)
	// + " over the "
	// + getArgumentTextButBetter(1);
	// case "AbleTo":
	// case "PhysicallyUnableTo":
	// case "LegallyUnableTo":
	// return getArgumentTextButBetter(0) + " is " + predText + " perform operation
	// "
	// + getArgumentTextButBetter(1);
	// case "Identify":
	// return "There is an " + predText + " operation performed by " +
	// getArgumentTextButBetter(0)
	// + " on the data subject " + getArgumentTextButBetter(1);
	// case "isBasedOn":
	// return "There is a situation where " + getArgumentTextButBetter(0) + " " +
	// predText + " "
	// + getArgumentTextButBetter(1);
	// case "GiveConsent":
	// return "There is a " + predText + " operation performed by " +
	// getArgumentTextButBetter(0) + " concerning "
	// + getArgumentTextButBetter(1);
	// case "Request":
	// return "A " + predText + " is made by " + getArgumentTextButBetter(0) + " and
	// concerns "
	// + getArgumentTextButBetter(1);
	// case "Contain":
	// return "There is a situation where " + getArgumentTextButBetter(1) + " " +
	// predText + " "
	// + getArgumentTextButBetter(0);
	// case "Describe":
	// case "Implement":
	// return "There is a situation where " + getArgumentTextButBetter(0) + " " +
	// predText + " "
	// + getArgumentTextButBetter(1);
	// case "CompatibleWith":
	// return "There is a situation where " + getArgumentTextButBetter(0) + " is " +
	// predText + " "
	// + getArgumentTextButBetter(1);
	// case "Delete":
	// case "Rectify":
	// return "There is a " + predText + " operation performed by " +
	// getArgumentTextButBetter(0) + " over "
	// + getArgumentTextButBetter(1);
	// case "Store":
	// return getArgumentTextButBetter(0) + " are placed in " + predText + " inside
	// "
	// + getArgumentTextButBetter(1);
	// case "Execute":
	// return "There is a " + predText + " operation performed by " +
	// getArgumentTextButBetter(0) + " over task "
	// + getArgumentTextButBetter(1);
	// case "RelatedTo":
	// return "There is a situation where task " + getArgumentTextButBetter(0) + "
	// is " + predText + " "
	// + getArgumentTextButBetter(1);
	// case "Hold":
	// return "There is a situation where " + getArgumentTextButBetter(0) + " " +
	// predText + " "
	// + getArgumentTextButBetter(1);
	// case "Demonstrate":
	// return getArgumentTextButBetter(0) + " " + predText + " the fact " +
	// getArgumentTextButBetter(1);
	// case "CompensationFor":
	// return "There is a " + predText + " the " + getArgumentTextButBetter(1);
	// case "AdequateWith":
	// case "RelevantTo":
	// case "LimitedTo":
	// return getArgumentTextButBetter(0) + " are " + predText + " the " +
	// getArgumentTextButBetter(1);
	// case "IdentifiableFrom":
	// case "PartyOf":
	// return getArgumentTextButBetter(0) + " is " + predText + " the " +
	// getArgumentTextButBetter(1);
	// case "Enter":
	// return getArgumentTextButBetter(0) + " is in the process of " + predText + "
	// the "
	// + getArgumentTextButBetter(1);
	// case "Protect":
	// case "Provide":
	// return getArgumentTextButBetter(0) + " is in the process to " + predText + "
	// the "
	// + getArgumentTextButBetter(1);
	//
	// // 3 arguments
	// case "Transmit":
	// return "There is a " + predText + " operation performed by " +
	// getArgumentTextButBetter(0)
	// + " concerning the data " + getArgumentTextButBetter(1) + " to destination "
	// + getArgumentTextButBetter(2);
	// case "Communicate":
	// return "There is a " + predText + " operation performed by " +
	// getArgumentTextButBetter(0)
	// + " to recipient " + getArgumentTextButBetter(1) + " concerning the
	// information "
	// + getArgumentTextButBetter(2);
	// case "Lodge":
	// return getArgumentTextButBetter(0) + " " + predText + " towards " +
	// getArgumentTextButBetter(2)
	// + " concerning a " + getArgumentTextButBetter(1);
	// case "ReceiveFrom":
	// return "There is a situation where data subject " +
	// getArgumentTextButBetter(0) + " " + predText + " "
	// + getArgumentTextButBetter(2) + " the " + getArgumentTextButBetter(1);
	// default:
	// String beforeText = "", afterText = "";
	// if (arguments.length > 1) {
	// beforeText = "MISSING: ";
	// afterText = " with " + arguments.length + " arguments, called " +
	// String.join(", ", arguments);
	// }
	// return beforeText + "There is a " + predText + afterText;
	// }
	// }

	@Override
	public boolean equals(RuleMLBlock block) {
		if (block.type != RuleMLType.ATOM)
			return false;
		Atom atom = (Atom) block;
		if (!predicateIRI.equals(atom.predicateIRI) || reified != atom.reified
				|| children.size() != atom.children.size())
			return false;
		for (int i = 0; i < children.size(); i++) {
			boolean found = false;
			for (int j = 0; j < children.size(); j++) {
				if (children.get(i).equals(atom.children.get(j))) {
					found = true;
					break;
				}
			}
			if (!found)
				return false;
		}
		return true;
	}

}
