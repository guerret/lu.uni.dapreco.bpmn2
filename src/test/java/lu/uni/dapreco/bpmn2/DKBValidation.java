package lu.uni.dapreco.bpmn2;

import java.util.Vector;

import lu.uni.dapreco.bpmn2.LRMLParser.RuleType;
import lu.uni.dapreco.bpmn2.owl.PrOntoParser;

public class DKBValidation {

	private final static String resDir = "resources";

	private final static String lrmlName = "rioKB_GDPR.xml";
	private final static String lrmlURI = "https://raw.githubusercontent.com/dapreco/daprecokb/master/gdpr/" + lrmlName;
	// private final static String lrmlLocal = resDir +l "/" + lrmlName;

	private final static String aknName = "akn-act-gdpr-full.xml";
	private static final String aknURI = "https://raw.githubusercontent.com/guerret/lu.uni.dapreco.bpmn2/master/"
			+ resDir + "/" + aknName;
	// private static final String aknLocal = resDir + "/" + aknName;

	private static String aknPrefix = "GDPR";
	private static String ontoPrefix = "prOnto";

	private static final String[] permissions = { "art_45__para_1", "art_77__para_1", "art_11__para_1",
			"art_11__para_2" };
	private static final String[] obligations = { "art_7__para_1", "art_9__para_1", "art_82__para_1" };
	private static final String[] constitutive = { "art_5__para_1", "art_6__para_1",
			"art_6__para_1__content__list_1__point_a", "art_6__para_1__content__list_1__point_b" };

	private static LRMLParser lParser;
	private static AKNParser aParser;
	private static PrOntoParser oParser;

	private DKBValidation() {
		lParser = new LRMLParser(lrmlURI);
		aParser = new AKNParser(aknURI);
		oParser = new PrOntoParser(true);
	}

	private String[] parseMissingFromSet(String[] workingSet, LRMLParser.RuleType type) {
		Vector<String> missVec = new Vector<String>();
		for (String rule : workingSet) {
			String[] predicates = lParser.findPredicatesInArticle(aknPrefix + ":" + rule, ontoPrefix, type);
			String[] ruleMissing = oParser.getMissingPredicates(predicates);
			for (String m : ruleMissing)
				if (!missVec.contains(m))
					missVec.add(m);
		}
		String[] missing = new String[missVec.size()];
		return missVec.toArray(missing);
	}

	private void printMissingFromSet(String[] workingSet, RuleType ruleType) {
		String[] missing = parseMissingFromSet(workingSet, ruleType);
		if (missing.length > 0) {
			System.out.println(ruleType);
			for (String c : missing)
				System.out.println(c);
			System.out.println();
		}
	}

	private String[] parsePredicatesInSet(String[] workingSet, LRMLParser.RuleType type) {
		Vector<String> predVec = new Vector<String>();
		for (String rule : workingSet) {
			String[] rulePredicates = lParser.findPredicatesInArticle(aknPrefix + ":" + rule, ontoPrefix, type);
			for (String p : rulePredicates)
				if (!predVec.contains(p))
					predVec.add(p);
		}
		String[] predicates = new String[predVec.size()];
		return predVec.toArray(predicates);
	}

	private void printPredicatesInSet(String[] workingSet, RuleType ruleType) {
		String[] missing = parsePredicatesInSet(workingSet, ruleType);
		if (missing.length > 0) {
			System.out.println(ruleType);
			for (String c : missing)
				System.out.println(c);
			System.out.println();
		}
	}

	public static void main(String[] args) {
		DKBValidation dkb = new DKBValidation();
		// dkb.printMissingFromSet(permissions, LRMLParser.RuleType.PERMISSIONS);
		// dkb.printMissingFromSet(obligations, LRMLParser.RuleType.OBLIGATIONS);
		// dkb.printMissingFromSet(constitutive, LRMLParser.RuleType.CONSTITUTIVE);
		dkb.printPredicatesInSet(permissions, LRMLParser.RuleType.PERMISSIONS);
		dkb.printPredicatesInSet(obligations, LRMLParser.RuleType.OBLIGATIONS);
		dkb.printPredicatesInSet(constitutive, LRMLParser.RuleType.CONSTITUTIVE);
	}

}
