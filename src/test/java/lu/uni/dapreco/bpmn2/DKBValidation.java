package lu.uni.dapreco.bpmn2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.w3c.dom.Document;

import lu.uni.dapreco.bpmn2.akn.AKNParser;
import lu.uni.dapreco.bpmn2.lrml.LRMLParser;
import lu.uni.dapreco.bpmn2.lrml.LRMLParser.RuleType;

public class DKBValidation {

	private final static String resDir = "resources";

	private final static String lrmlName = "rioKB_GDPR.xml";
	private final static String lrmlURI = "https://raw.githubusercontent.com/dapreco/daprecokb/master/gdpr/" + lrmlName;
	// private final static String lrmlLocal = resDir +l "/" + lrmlName;

	private final static String aknName = "akn-act-gdpr-full.xml";
	private static final String aknURI = "https://raw.githubusercontent.com/guerret/lu.uni.dapreco.bpmn2/master/"
			+ resDir + "/" + aknName;
	private static final String aknLocal = resDir + "/" + aknName;

	private final static String graphName = "pronto-v8.graphml";
	private static final String graphURI = "https://raw.githubusercontent.com/guerret/lu.uni.dapreco.bpmn2/master/"
			+ resDir + "/" + graphName;
	// private static final String aknLocal = resDir + "/" + aknName;

	public static String aknPrefix = "GDPR";
	private static String ontoPrefix = "prOnto";
	private static String daprecoPrefix = "dapreco";

	private static String[] permissions = {}, obligations = {}, constitutive = {};

	public static Map<LRMLParser.RuleType, String[]> typeMap;

	private LRMLParser lParser;
	private AKNParser aParser;
	// private PrOntoParser oParser;
	private GraphParser gParser;

	public DKBValidation() {
		lParser = new LRMLParser(lrmlURI);
		aParser = new AKNParser(aknLocal);
		// oParser = new PrOntoParser(true);
		gParser = new GraphParser(graphURI);
		permissions = readExamples(RuleType.PERMISSIONS);
		obligations = readExamples(RuleType.OBLIGATIONS);
		constitutive = readExamples(RuleType.CONSTITUTIVE);
		typeMap = Map.ofEntries(Map.entry(LRMLParser.RuleType.PERMISSIONS, permissions),
				Map.entry(LRMLParser.RuleType.OBLIGATIONS, obligations),
				Map.entry(LRMLParser.RuleType.CONSTITUTIVE, constitutive),
				Map.entry(LRMLParser.RuleType.ALL, new String[0]));
	}

	public String[] readExamples(RuleType type) {
		List<String> lines = new ArrayList<String>();
		String filename = "examples_" + type.toString().toLowerCase() + ".txt";
		try {
			BufferedReader abc = new BufferedReader(new FileReader("resources/" + filename));

			String line;
			while ((line = abc.readLine()) != null) {
				lines.add(line);
			}
			abc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] data = lines.toArray(new String[] {});
		return data;
	}

	public void createExampleFile(RuleType type) {
		LRMLParser copy = lParser.copy();
		String[] workingSet = typeMap.get(type);
		String[] extended = aParser.getExtendedRuleSet(workingSet);
		copy.removeAllLegalReferencesExceptForRules(extended, aknPrefix);
		copy.write("rioKB_GDPR_examples_" + type + ".xml");
	}

	public String[] parseMissingFromSet(RuleType type) {
		Vector<String> missVec = new Vector<String>();
		String[] workingSet = typeMap.get(type);
		for (String rule : workingSet) {
			// In the new Dapreco KB, the non-aligned predicates have a "dapreco:" prefix
			String[] daprecoPredicates = lParser.findPredicatesInArticle(aknPrefix + ":" + rule, daprecoPrefix, type);
			// String[] ruleMissing = oParser.getMissingPredicates(predicates);
			for (String m : daprecoPredicates)
				if (!missVec.contains(m))
					missVec.add(m);
		}
		String[] missing = new String[missVec.size()];
		return missVec.toArray(missing);
	}

	public String[] parsePredicatesInSet(RuleType type) {
		String[] workingSet = typeMap.get(type);
		Vector<String> predVec = new Vector<String>();
		for (String baseRule : workingSet) {
			String[] extended = aParser.getExtendedRuleSet(baseRule);
			for (String rule : extended) {
				String[] rulePredicates = lParser.findPredicatesInArticle(aknPrefix + ":" + rule, ontoPrefix, type);
				for (String p : rulePredicates)
					if (!predVec.contains(p))
						predVec.add(p);
			}
		}
		String[] predicates = new String[predVec.size()];
		return predVec.toArray(predicates);
	}

	public int countOccurrencesInGraph(String predicate) {
		return gParser.findElements(predicate).getLength();
	}

	public void createReducedGraph(RuleType type) {
		String[] predicates = parsePredicatesInSet(type);
		Document doc = gParser.removeUnusedElements(predicates);
		gParser.writeReducedDocument(doc, type);
	}

	public String translate(RuleType type) {
		String ret = "";
		String[] workingSet = typeMap.get(type);
		for (String baseRule : workingSet) {
			String[] extended = aParser.getExtendedRuleSet(baseRule);
			ret += lParser.translate(extended, aknPrefix);
		}
		return ret;
	}

	public boolean analyze() {
		return lParser.analyze();
	}

}
