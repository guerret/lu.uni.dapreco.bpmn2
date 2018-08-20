package lu.uni.dapreco.bpmn2.lrml;

import java.util.Arrays;

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
			String pred = predicateIRI.substring(predicateIRI.indexOf(":") + 1);
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

	public String[] getArgumentsToTranslate() {
		String pred = predicateIRI.substring(predicateIRI.indexOf(":") + 1);
		String[] arguments = reified ? getReifiedArguments() : getNonReifiedArguments();
		if (pred.equals("PersonalData") || pred.equals("Controller") || pred.equals("Store")
				|| pred.equals("CompensationFor") || arguments.length == 1)
			return Arrays.copyOfRange(variables, 1, variables.length);
		return arguments;
	}

	public String translate(String indent) {
		String pred = predicateIRI.substring(predicateIRI.indexOf(":") + 1);
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
					+ " to controller " + getArgumentText(1);
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

}
