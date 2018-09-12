package lu.uni.dapreco.parser.lrml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.parser.XPathParser;
import lu.uni.dapreco.parser.rioonto.DeonticAtom;
import lu.uni.dapreco.parser.rioonto.GenericRioOntoAtom;

public class Atom extends RuleMLBlock {

	protected String predicateIRI;

	protected boolean reified;

	protected String negation;

	private boolean translated;

	protected Atom(Element node, String pred, Side s, XPathParser xpath) {
		super(node, s, xpath);
		predicateIRI = pred;
		reified = root.hasAttribute("keyref");
		if (type == RuleMLType.ATOM)
			for (RuleMLBlock child : children)
				if (child.type == RuleMLType.VAR)
					owner.addVariable(child.getName(), this);
		negation = null;
		translated = false;
	}

	public static Atom create(Element node, Side s, XPathParser xpath) {
		if (node.getNodeName().equals("ruleml:Expr")) {
			// String search = "ruleml:Fun/@iri";
			String pred;
			// pred = xpath.parseNode(search, node).item(0).getNodeValue();
			pred = ((Element) node.getElementsByTagNameNS(LRMLParser.rulemlNS, "Fun").item(0)).getAttribute("iri");
			return new Expression(node, pred, s, xpath);
		}
		if (node.getNodeName().equals("ruleml:Atom")) {
			// String search = "ruleml:Rel/@iri";
			String pred;
			// pred = xpath.parseNode(search, node).item(0).getNodeValue();
			pred = ((Element) node.getElementsByTagNameNS(LRMLParser.rulemlNS, "Rel").item(0)).getAttribute("iri");
			String prefix = pred.substring(0, pred.indexOf(":"));
			if (prefix.equals("rioOnto"))
				return GenericRioOntoAtom.create(node, pred, s, xpath);
			if (prefix.equals("pred"))
				return new PredAtom(node, pred, s, xpath);
			return new Atom(node, pred, s, xpath);
		}
		return null;
	}

	public String getPrefix() {
		return predicateIRI.substring(0, predicateIRI.indexOf(":"));
	}

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
		return pred;
	}

	public List<RuleMLBlock> getArguments() {
		return children.subList(reified ? 1 : 0, children.size());
	}

	// public List<RuleMLBlock> getArgumentsToTranslate() {
	// List<RuleMLBlock> ret = getArguments();
	// String pred = getLocalPredicate();
	// if (pred.equals("PersonalData") || pred.equals("Controller") ||
	// pred.equals("Store")
	// || pred.equals("CompensationFor") || pred.equals("RelatedTo") || ret.size()
	// == 1)
	// return children.subList(1, children.size());
	// return ret;
	// }

	protected void setArgument(int index, RuleMLBlock b) {
		children.set(reified ? index + 1 : index, b);
	}

	public boolean isTranslated() {
		return translated;
	}

	public void setTranslated(boolean t) {
		translated = t;
	}

	@Override
	public List<String> translate() {
		List<String> ret = new ArrayList<String>();
		ret.add(toString() + "<br />");
		return ret;
	}

	@Override
	public String toString() {
		String pred = getLocalPredicate();
		List<RuleMLBlock> arguments = getArguments();
		String predText = "<strong>" + pred + "</strong>";
		String name = getName();
		if (!name.equals(pred))
			predText += " (" + name + ")";
		String not = "";
		String eNot = "";
		if (negation != null) {
			not = "<strong>not</strong> (" + getName() + ") ";
			eNot = "<strong>does not</strong> (" + getName() + ") ";
		}
		switch (pred) {
		// 1 argument
		case "HasBeenDamaged":
			return "There is " + not + "a situation in which " + arguments.get(0) + " " + predText;
		case "Risk":
			return "There is " + not + "a " + predText + " for the subject " + arguments.get(0);
		case "accurate":
			return arguments.get(0) + " are " + not + predText;
		case "lawfulness":
		case "fairness":
		case "transparency":
			return arguments.get(0) + " respects " + not + "the principle of " + predText;
		case "ViolationOf":
			return "There is " + not + "a " + predText + " " + arguments.get(0);
		case "EthnicData":
		case "OpinionData":
		case "ReligiousOrPhilosophicalBeliefsData":
		case "TradeUnionMembership":
		case "GeneticData":
		case "BiometricData":
		case "HealthData":
		case "SexualData":
		case "SexualOrientationData":
		case "upToDate":
			return arguments.get(0) + " are " + not + predText;
		case "security":
			return predText + " is " + not + "applied to " + arguments.get(0);
		case "specified":
			return arguments.get(0) + " is " + not + predText;
		case "riskinessRightsFreedoms":
			return arguments.get(0) + " " + eNot + "pose " + predText;

		// 2 arguments
		case "AuthorizedBy":
			return arguments.get(0) + " is " + not + predText + " " + arguments.get(1);
		case "PersonalData":
			return predText + " is " + not + "relating to " + arguments.get(1);
		case "nominates":
			return arguments.get(0) + " " + eNot + predText + " " + arguments.get(1);
		case "Controller":
			return "The " + predText + " is " + not + "controlling " + arguments.get(1);
		case "PersonalDataProcessing":
			return "The " + predText + " is " + not + "performed by " + arguments.get(0) + " over the "
					+ arguments.get(1);
		case "AbleTo":
		case "PhysicallyUnableTo":
		case "LegallyUnableTo":
			return arguments.get(0) + " is " + not + predText + " perform operation " + arguments.get(1);
		case "Identify":
			return arguments.get(0) + " " + eNot + predText + " " + arguments.get(1);
		case "isBasedOn":
			return "There is " + not + "a situation where " + arguments.get(0) + " " + predText + " "
					+ arguments.get(1);
		case "GiveConsent":
		case "WithdrawConsent":
			return "There is " + not + "a " + predText + " operation performed by " + arguments.get(0) + " concerning "
					+ arguments.get(1);
		case "Request":
			return "A " + predText + " is " + not + "made by " + arguments.get(0) + " concerning " + arguments.get(1);
		case "Contain":
			return arguments.get(1) + " " + eNot + predText + " " + arguments.get(0);
		case "Describe":
		case "Implement":
			return arguments.get(0) + " " + eNot + predText + " " + arguments.get(1);
		case "CompatibleWith":
			return "There is a situation where " + arguments.get(0) + " is " + not + predText + " " + arguments.get(1);
		case "Delete":
		case "Rectify":
			return "There is " + not + "a " + predText + " operation performed by " + arguments.get(0) + " over "
					+ arguments.get(1);
		case "Store":
			return arguments.get(0) + " are " + not + "placed in " + predText + " inside " + arguments.get(1);
		case "Execute":
			return "There is " + not + "a " + predText + " operation performed by " + arguments.get(0) + " over task "
					+ arguments.get(1);
		case "RelatedTo":
			return "Task " + arguments.get(0) + " is " + not + predText + " " + arguments.get(1);
		case "Hold":
		case "Demonstrate":
			return arguments.get(0) + " " + eNot + predText + " " + arguments.get(1);
		case "CompensationFor":
			return "There is " + not + "a " + predText + " the " + arguments.get(1);
		case "AdequateWith":
		case "RelevantTo":
		case "LimitedTo":
			return arguments.get(0) + " are " + not + predText + " the " + arguments.get(1);
		case "IdentifiableFrom":
		case "PartyOf":
			return arguments.get(0) + " is " + not + predText + " the " + arguments.get(1);
		case "Enter":
		case "Protect":
		case "Provide":
			return arguments.get(0) + " " + eNot + predText + " the " + arguments.get(1);

		// 3 arguments
		case "Transmit":
			return arguments.get(0) + " " + eNot + predText + " " + arguments.get(1) + " to destination "
					+ arguments.get(2);
		case "Communicate":
			return arguments.get(0) + " " + eNot + predText + " " + "the information " + arguments.get(2)
					+ " to recipient " + arguments.get(1);
		case "Lodge":
			return arguments.get(0) + " " + eNot + predText + " " + "a " + arguments.get(1) + " towards "
					+ arguments.get(2);
		case "ReceiveFrom":
			return arguments.get(0) + " " + eNot + predText + " " + arguments.get(2) + " the " + arguments.get(1);
		default:
			String beforeText = "", afterText = "";
			String[] varNames = new String[arguments.size()];
			for (int i = 0; i < arguments.size(); i++)
				varNames[i] = arguments.get(i).getName();
			if (arguments.size() > 0) {
				beforeText = "MISSING: ";
				afterText = " with " + arguments.size() + " arguments, called " + String.join(", ", varNames);
			}
			return beforeText + "There is a " + not + predText + afterText;
		}
	}

	protected List<Atom> getDefinitionAtoms(RuleMLBlock argument, RuleMLBlock exclusion) {
		List<Atom> atoms = new ArrayList<Atom>();
		if (argument.type == RuleMLType.VAR)
			atoms.addAll(((Variable) argument).getDefinitionAtoms(owner));
		atoms.remove(this);
		atoms.remove(exclusion);
		return atoms;
	}

	protected List<Atom> getDefinitionAtoms(List<RuleMLBlock> arguments) {
		List<Atom> atoms = new ArrayList<Atom>();
		for (RuleMLBlock a : arguments)
			if (a.type == RuleMLType.VAR)
				atoms.addAll(((Variable) a).getDefinitionAtoms(owner));
		atoms.remove(this);
		return atoms;
	}

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

	/**
	 * Method that verifies if the atom must only be expressed inline, without any
	 * additional line specifying. The additional line would just say what the
	 * variable is. This happens only for atoms that have only one argument, and not
	 * always.
	 * 
	 * @return true if the atom must only be shown inline, false if it needs a
	 *         separate line.
	 */
	public boolean inline() {
		if (getArguments().size() > 1 || reified)
			return false;
		switch (getLocalPredicate()) {
		case "HasBeenDamaged":
		case "Risk":
		case "accurate":
		case "lawfulness":
		case "fairness":
		case "EthnicData":
		case "OpinionData":
		case "ReligiousOrPhilosophicalBeliefsData":
		case "TradeUnionMembership":
		case "GeneticData":
		case "BiometricData":
		case "HealthData":
		case "SexualData":
		case "SexualOrientationData":
		case "ViolationOf":
		case "isBasedOn":
		case "RelatedTo":
			return false;
		default:
			return true;
		}
	}

	/**
	 * Method to determine whether an atom needs to be excluded from the potential
	 * definitions, if the required variable is its first argument. No reified atom
	 * is ever excluded. If it is not reified, then it is excluded if it is not a
	 * dapreco: or prOnto: atom. Even if it is one, then it is excluded if it does
	 * not define the atom.
	 * 
	 * @return
	 */
	public boolean isExclusion() {
		if (reified || this instanceof DeonticAtom || this instanceof PredAtom)
			return true;
		switch (getLocalPredicate()) {
		case "isBasedOn":
		case "RelatedTo":
		case "Store":
			return true;
		default:
			return false;
		}
	}

	public void setNegation(String n) {
		negation = n;
	}

}
