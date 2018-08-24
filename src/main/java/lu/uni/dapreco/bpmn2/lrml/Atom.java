package lu.uni.dapreco.bpmn2.lrml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lu.uni.dapreco.bpmn2.XPathParser;

public class Atom extends BaseLRMLElement {

	private String predicateIRI;

	protected boolean reified;

	private String[] variables;

	public Atom(Node node, XPathParser xpath) {
		super(node, xpath);
		String search = "ruleml:Rel/@iri | ruleml:Fun/@iri";
		reified = root.hasAttribute("keyref");
		predicateIRI = xpath.parseNode(search, root).item(0).getNodeValue();
		if (predicateIRI.equals("rioOnto:Obliged") || predicateIRI.equals("rioOnto:Permitted"))
			reified = false;
		search = "ruleml:Var | ruleml:Expr | ruleml:Ind";
		NodeList nl = xpath.parseNode(search, root);
		variables = new String[nl.getLength()];
		for (int i = 0; i < nl.getLength(); i++) {
			Node item = nl.item(i);
			if (item.getNodeName().equals("ruleml:Var")) {
				Element elem = (Element) nl.item(i);
				variables[i] = elem.hasAttribute("key") ? elem.getAttribute("key") : elem.getAttribute("keyref");
			} else if (item.getNodeName().equals("ruleml:Ind"))
				variables[i] = "Ind:" + item.getTextContent();
			else if (item.getNodeName().equals("ruleml:Expr"))
				variables[i] = "Expr:" + countExpr(i);
		}
	}

	private int countExpr(int index) {
		int count = 1;
		for (int i = 0; i < index; i++)
			if (variables[i].startsWith("Expr"))
				count++;
		return count;
	}

	public String getPredicateIRI() {
		return predicateIRI;
	}

	public String getLocalPredicate() {
		return predicateIRI.substring(predicateIRI.indexOf(":") + 1);
	}

	public boolean isReified() {
		return reified;
	}

	public String getFirstVariable() {
		return variables[0];
	}

	public String getId() {
		if (reified)
			return variables[0];
		return null;
	}

	public String getVariableName(int index) {
		return variables[index];
	}

	public String[] getVariables() {
		return variables;
	}

	public String[] getReifiedArguments() {
		return Arrays.copyOfRange(variables, 1, variables.length);
	}

	public String[] getNonReifiedArguments() {
		return variables;
	}

	public String getArgumentText(int index) {
		String v = variables[reified ? index + 1 : index];
		if (v.startsWith("Expr")) {
			String pred = getLocalPredicate();
			String exprIndex = v.substring(v.indexOf(":") + 1);
			String search = "ruleml:Expr[" + exprIndex + "]";
			Node item = xpath.parseNode(search, root).item(0);
			Expression e = new Expression(item, xpath);
			String[] arguments = e.getNonReifiedArguments();
			String ret = "[the " + pred + " of ";
			for (int i = 0; i < arguments.length; i++) {
				ret += e.getArgumentText(i);
				if (i < arguments.length - 1)
					ret += ", ";
			}
			ret += "]";
			return ret;
		} else if (v.startsWith("Ind:"))
			return v.substring("Ind:".length());
		return v;
	}

	public String getArgumentTextButBetter(int index) {
		String v = variables[reified ? index + 1 : index];
		String pred = getLocalPredicate();
		if (v.startsWith("Expr")) {
			String exprIndex = v.substring(v.indexOf(":") + 1);
			String search = "ruleml:Expr[" + exprIndex + "]";
			Node item = xpath.parseNode(search, root).item(0);
			Expression e = new Expression(item, xpath);
			String[] arguments = e.getNonReifiedArguments();
			String ret = "[the " + pred + " of ";
			for (int i = 0; i < arguments.length; i++) {
				ret += e.getArgumentText(i);
				if (i < arguments.length - 1)
					ret += ", ";
			}
			ret += "]";
			return ret;
		}
		if (v.startsWith("Ind:"))
			return v.substring("Ind:".length());
		Atom[] atoms = findDefinitionAtomsForVariable(v, true);
		if (atoms.length > 0)
			if (atoms[0].predicateIRI.startsWith("rioOnto:"))
				return v;
			else
				return atoms[0].getLocalPredicate() + " (" + v + ")";
		else
			return "PROBLEM: MISSING CONCEPT (" + v + ")";
	}

	public String[] getArgumentsToTranslate() {
		String pred = getLocalPredicate();
		String[] arguments = reified ? getReifiedArguments() : getNonReifiedArguments();
		if (pred.equals("PersonalData") || pred.equals("Controller") || pred.equals("Store")
				|| pred.equals("CompensationFor") || arguments.length == 1)
			return Arrays.copyOfRange(variables, 1, variables.length);
		return arguments;
	}

	public String[] getArgumentsToTranslateButBetter() {
		// String pred = predicateIRI.substring(predicateIRI.indexOf(":") + 1);
		String[] arguments = reified ? getReifiedArguments() : getNonReifiedArguments();
		// if (pred.equals("PersonalData") || pred.equals("Controller") ||
		// pred.equals("Store")
		// || pred.equals("CompensationFor") || arguments.length == 1)
		// return Arrays.copyOfRange(variables, 1, variables.length);
		return arguments;
	}

	public String translate(String indent) {
		String pred = getLocalPredicate();
		String id = variables[0];
		String[] arguments = reified ? getReifiedArguments() : getNonReifiedArguments();
		String idtext = "";
		if (reified)
			idtext = id + ": ";
		String ret = indent + idtext;
		switch (pred) {
		// 1 argument
		case "HasBeenDamaged":
			ret += "There is a situation in which the data subject " + getArgumentText(0) + " " + pred;
			break;
		case "Risk":
			ret += "There is a " + pred + " for the subject " + getArgumentText(0);
			break;
		case "accurate":
			ret += "There is a situation where " + getArgumentText(0) + " are " + pred;
			break;
		case "lawfulness":
		case "fairness":
			ret += "There is a situation of " + pred + " for the processing " + getArgumentText(0);
			break;

		// 2 arguments
		case "AuthorizedBy":
			ret += "There is a situation in which " + getArgumentText(0) + " is " + pred + " " + getArgumentText(1);
			break;
		case "PersonalData":
			ret += "There are " + pred + " called " + getArgumentText(0) + " pertaining to " + getArgumentText(1);
			break;
		case "nominates":
			ret += "There is a situation in which " + getArgumentText(0) + " " + pred + " " + getArgumentText(1);
			break;
		case "Controller":
			ret += "There is a " + pred + " called " + getArgumentText(0) + " controlling data " + getArgumentText(1);
			break;
		case "PersonalDataProcessing":
			ret += "There is a " + pred + " performed by " + getArgumentText(0) + " over the data "
					+ getArgumentText(1);
			break;
		case "AbleTo":
			ret += "There is a situation where " + getArgumentText(0) + " is " + pred + " perform operation "
					+ getArgumentText(1);
			break;
		case "Identify":
			ret += "There is an " + pred + " operation performed by " + getArgumentText(0) + " on the data subject "
					+ getArgumentText(1);
			break;
		case "isBasedOn":
			ret += "There is a situation where " + getArgumentText(0) + " " + pred + " " + getArgumentText(1);
			break;
		case "GiveConsent":
			ret += "There is a " + pred + " operation performed by data subject " + getArgumentText(0)
					+ " concerning consent " + getArgumentText(1);
			break;
		case "Request":
			ret += "There is a situation where the event " + getArgumentText(1) + " is requested from controller "
					+ getArgumentText(0);
			break;
		case "Contain":
			ret += "There is a situation where " + getArgumentText(1) + " " + pred + " " + getArgumentText(0);
			break;
		case "Describe":
			ret += "There is a situation where " + getArgumentText(0) + " " + pred + " " + getArgumentText(1);
			break;
		case "Implement":
			ret += "There is a situation where " + getArgumentText(0) + " " + pred + " " + getArgumentText(1);
			break;
		case "CompatibleWith":
			// case "CompatibileWith":
			ret += "There is a situation where " + getArgumentText(0) + " is " + pred + " " + getArgumentText(1);
			break;
		case "Delete":
			ret += "There is a " + pred + " operation performed by controller " + getArgumentText(0) + " over data "
					+ getArgumentText(1);
			break;
		case "Rectify":
			ret += "There is a " + pred + " operation performed by controller " + getArgumentText(0) + " over data "
					+ getArgumentText(1);
			break;
		case "Store":
			ret += getArgumentText(0) + " are placed in " + pred + " inside " + getArgumentText(1);
			break;
		case "Execute":
			ret += "There is a " + pred + " operation performed by controller " + getArgumentText(0) + " over task "
					+ getArgumentText(1);
			break;
		case "RelatedTo":
			ret += "There is a situation where task " + getArgumentText(0) + " is " + pred + " " + getArgumentText(1);
			break;
		case "Hold":
			ret += "There is a situation where controller " + getArgumentText(0) + " " + pred + " "
					+ getArgumentText(1);
			break;
		case "Demonstrate":
			ret += "There is a " + pred + " operation performed by " + getArgumentText(0) + " concerning the fact "
					+ getArgumentText(1);
			break;
		case "CompensationFor": // reified?
			ret += "There is a " + pred + " the damage " + getArgumentText(1) + ", called " + getArgumentText(0);
			break;

		// 3 arguments
		case "Transmit":
			ret += "There is a " + pred + " operation performed by " + getArgumentText(0) + " concerning the data "
					+ getArgumentText(1) + " to destination " + getArgumentText(2);
			break;
		case "Communicate":
			ret += "There is a " + pred + " operation performed by " + getArgumentText(0) + " to recipient "
					+ getArgumentText(1) + " concerning the information " + getArgumentText(2);
			break;
		case "Lodge":
			ret += "There is a " + pred + " operation performed by " + getArgumentText(0) + " towards "
					+ getArgumentText(2) + " concerning a complaint " + getArgumentText(1);
			break;
		case "ReceiveFrom":
			ret += "There is a situation where data subject " + getArgumentText(0) + " " + pred + " controller "
					+ getArgumentText(2) + " the compensation " + getArgumentText(1);
			break;
		default:
			String beforeText = "", afterText = "";
			if (arguments.length > 1) {
				beforeText = "MISSING: ";
				afterText = " with " + arguments.length + " arguments, called " + String.join(", ", arguments);
			}
			ret += beforeText + "There is a " + pred + " called " + id + afterText;
		}
		return ret + "\n";
	}

	public String translateButBetter() {
		String pred = getLocalPredicate();
		String[] arguments = reified ? getReifiedArguments() : getNonReifiedArguments();
		String predText = (reified || pred.equals("PersonalData") || pred.equals("Controller")
				|| pred.equals("CompensationFor")) ? pred + " (" + variables[0] + ")" : pred;
		if (predicateIRI.substring(0, predicateIRI.indexOf(":")).equals("rioOnto"))
			return translateRioOnto(pred);
		switch (pred) {
		// 1 argument
		case "HasBeenDamaged":
			return "There is a situation in which the data subject " + getArgumentTextButBetter(0) + " " + predText;
		case "Risk":
			return "There is a " + predText + " for the subject " + getArgumentTextButBetter(0);
		case "accurate":
			return "There is a situation where " + getArgumentTextButBetter(0) + " are " + predText;
		case "lawfulness":
		case "fairness":
			return "There is a situation of " + predText + " for the processing " + getArgumentTextButBetter(0);
		// 2 arguments
		case "AuthorizedBy":
			return "There is a situation in which " + getArgumentTextButBetter(0) + " is " + predText + " "
					+ getArgumentTextButBetter(1);
		case "PersonalData": // TODO problematic
			return "There are " + predText + " pertaining to " + getArgumentTextButBetter(1);
		case "nominates":
			return "There is a situation in which " + getArgumentTextButBetter(0) + " " + predText + " "
					+ getArgumentTextButBetter(1);
		case "Controller": // TODO problematic
			return "There is a " + predText + " controlling " + getArgumentTextButBetter(1);
		case "PersonalDataProcessing":
			return "There is a " + predText + " performed by " + getArgumentTextButBetter(0) + " over the data "
					+ getArgumentTextButBetter(1);
		case "AbleTo":
			return "There is a situation where " + getArgumentTextButBetter(0) + " is " + predText
					+ " perform operation " + getArgumentTextButBetter(1);
		case "Identify":
			return "There is an " + predText + " operation performed by " + getArgumentTextButBetter(0)
					+ " on the data subject " + getArgumentTextButBetter(1);
		case "isBasedOn":
			return "There is a situation where " + getArgumentTextButBetter(0) + " " + predText + " "
					+ getArgumentTextButBetter(1);
		case "GiveConsent":
			return "There is a " + predText + " operation performed by data subject " + getArgumentTextButBetter(0)
					+ " concerning consent " + getArgumentTextButBetter(1);
		case "Request":
			return "There is a situation where the event " + getArgumentTextButBetter(1)
					+ " is requested from controller " + getArgumentTextButBetter(0);
		case "Contain":
			return "There is a situation where " + getArgumentTextButBetter(1) + " " + predText + " "
					+ getArgumentTextButBetter(0);
		case "Describe":
			return "There is a situation where " + getArgumentTextButBetter(0) + " " + predText + " "
					+ getArgumentTextButBetter(1);
		case "Implement":
			return "There is a situation where " + getArgumentTextButBetter(0) + " " + predText + " "
					+ getArgumentTextButBetter(1);
		case "CompatibleWith":
			// case "CompatibileWith":
			return "There is a situation where " + getArgumentTextButBetter(0) + " is " + predText + " "
					+ getArgumentTextButBetter(1);
		case "Delete":
			return "There is a " + predText + " operation performed by controller " + getArgumentTextButBetter(0)
					+ " over data " + getArgumentTextButBetter(1);
		case "Rectify":
			return "There is a " + predText + " operation performed by controller " + getArgumentTextButBetter(0)
					+ " over data " + getArgumentTextButBetter(1);
		case "Store":
			return getArgumentTextButBetter(0) + " are placed in " + predText + " inside "
					+ getArgumentTextButBetter(1);
		case "Execute":
			return "There is a " + predText + " operation performed by controller " + getArgumentTextButBetter(0)
					+ " over task " + getArgumentTextButBetter(1);
		case "RelatedTo":
			return "There is a situation where task " + getArgumentTextButBetter(0) + " is " + predText + " "
					+ getArgumentTextButBetter(1);
		case "Hold":
			return "There is a situation where controller " + getArgumentTextButBetter(0) + " " + predText + " "
					+ getArgumentTextButBetter(1);
		case "Demonstrate":
			return getArgumentTextButBetter(0) + " " + predText + " the fact " + getArgumentTextButBetter(1);
		case "CompensationFor": // TODO problematic
			return "There is a " + predText + " the " + getArgumentTextButBetter(1);
		case "AdequateWith":
		case "RelevantTo":
		case "LimitedTo":
			return getArgumentTextButBetter(0) + " are " + predText + " the " + getArgumentTextButBetter(1);
		case "IdentifiableFrom":
		case "PartyOf":
			return getArgumentTextButBetter(0) + " is " + predText + " the " + getArgumentTextButBetter(1);
		case "Enter":
			return getArgumentTextButBetter(0) + " is in the process of " + predText + " the "
					+ getArgumentTextButBetter(1);
		case "Protect":
			return getArgumentTextButBetter(0) + " is in the process of " + predText + " the "
					+ getArgumentTextButBetter(1);

		// 3 arguments
		case "Transmit":
			return "There is a " + predText + " operation performed by " + getArgumentTextButBetter(0)
					+ " concerning the data " + getArgumentTextButBetter(1) + " to destination "
					+ getArgumentTextButBetter(2);
		case "Communicate":
			return "There is a " + predText + " operation performed by " + getArgumentTextButBetter(0)
					+ " to recipient " + getArgumentTextButBetter(1) + " concerning the information "
					+ getArgumentTextButBetter(2);
		case "Lodge":
			return getArgumentTextButBetter(0) + " " + predText + " towards " + getArgumentTextButBetter(2)
					+ " concerning a " + getArgumentTextButBetter(1);
		case "ReceiveFrom":
			return "There is a situation where data subject " + getArgumentTextButBetter(0) + " " + predText + " "
					+ getArgumentTextButBetter(2) + " the " + getArgumentTextButBetter(1);
		default:
			String beforeText = "", afterText = "";
			if (arguments.length > 1) {
				beforeText = "MISSING: ";
				afterText = " with " + arguments.length + " arguments, called " + String.join(", ", arguments);
			}
			return beforeText + "There is a " + predText + afterText;
		}
	}

	private String translateRioOnto(String pred) {
		switch (pred) {
		case "RexistAtTime":
			if (findDefinitionAtomsForVariable(variables[0], false).length > 0)
				return "At time " + variables[1] + ", the following really exists:";
			else
				return "At time " + variables[1] + ", the above really exists";
		case "Permitted":
			return "At time " + variables[1] + ", " + getArgumentTextButBetter(2) + " is permitted to "
					+ getArgumentTextButBetter(3);
		case "Obliged":
			return "At time " + variables[1] + ", " + getArgumentTextButBetter(2) + " is obliged to "
					+ getArgumentTextButBetter(3);
		case "and":
			return "All of the following apply:";
		case "or":
			return "At least one of the following apply (" + variables[0] + "):";
		case "not":
			return "The next line does not apply:";
		case "cause":
			return "The fact " + getArgumentTextButBetter(0) + " is the cause of the fact "
					+ getArgumentTextButBetter(1);
		case "imply":
			return getArgumentTextButBetter(1) + " implies " + getArgumentTextButBetter(2);
		default:
			if (pred.startsWith("exception"))
				return "An exception (" + pred + ") applies";
			return "UNKNOWN RIOONTO PREDICATE";
		}
	}

	public Atom[] findDefinitionAtomsForVariable(String v, boolean searchInLHS) {
		Atom[] atoms = findSiblingsByName(v);
		if (atoms.length == 0) { // no atoms here
			String search = "ancestor::ruleml:then";
			NodeList nl = xpath.parseNode(search, root);
			if (searchInLHS && nl.getLength() > 0) { // I am in then part, shall I search elsewhere?
				search = "ruleml:if/descendant::ruleml:Atom[ruleml:Var[1]/@key='" + v
						+ "'] | ruleml:if/descendant::ruleml:Atom[ruleml:Var[1]/@keyref='" + v + "']";
				nl = xpath.parseNode(search, nl.item(0).getParentNode());
				if (nl.getLength() > 0) { // there is something in if part
					atoms = new Atom[nl.getLength()];
					for (int i = 0; i < nl.getLength(); i++)
						atoms[i] = new Atom(nl.item(i), xpath);
				} else
					System.out.println("PROBLEM IN ATOM " + predicateIRI + ": " + v + " undefined (not even in LHS)");
			}
		}
		return atoms;
	}

	public Atom[] findSiblingsByName(String name) {
		String search = "preceding-sibling::ruleml:Atom[ruleml:Var[1]/@key='" + name
				+ "'] | preceding-sibling::ruleml:Atom[ruleml:Var[1]/@keyref='" + name
				+ "'] | following-sibling::ruleml:Atom[ruleml:Var[1]/@key='" + name
				+ "'] | following-sibling::ruleml:Atom[ruleml:Var[1]/@keyref='" + name + "']";
		NodeList nl = xpath.parseNode(search, root);
		Atom[] atoms = new Atom[nl.getLength()];
		for (int i = 0; i < atoms.length; i++)
			atoms[i] = new Atom(nl.item(i), xpath);
		return atoms;
	}

	public Atom[] getDefinitionAtoms(String[] variables) {
		List<Atom> atomArray = new ArrayList<Atom>();
		for (int i = 0; i < variables.length; i++)
			if (!variables[i].startsWith("Ind:") && !variables[i].startsWith("Expr:")) {
				Atom[] atoms = findDefinitionAtomsForVariable(variables[i], true);
				for (Atom a : atoms)
					atomArray.add(a);
			}
		Atom[] atoms = new Atom[atomArray.size()];
		return atomArray.toArray(atoms);
	}

}
