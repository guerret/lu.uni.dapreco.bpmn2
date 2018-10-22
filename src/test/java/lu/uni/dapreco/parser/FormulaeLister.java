package lu.uni.dapreco.parser;

import lu.uni.dapreco.parser.lrml.LRMLParser;
import lu.uni.dapreco.parser.lrml.LRMLParser.RuleType;

public class FormulaeLister {

	private final static String lrmlName = "rioKB_GDPR.xml";
	private final static String lrmlURI = "https://raw.githubusercontent.com/dapreco/daprecokb/master/gdpr/" + lrmlName;
	// private final static String lrmlLocal = resDir +l "/" + lrmlName;

	private static String ontoPrefix = "prOnto";
	private static String daprecoPrefix = "dapreco";
	private static String predicate = "Transmit";

	public static void main(String[] args) {
		// PrOntoParser p = new PrOntoParser(true);
		LRMLParser lParser = new LRMLParser(lrmlURI);
		// AKNParser aParser = new AKNParser(aknURI);
		System.out.println("PROVISIONS FOR ACTION: " + predicate);
		String[] exclusions = { daprecoPrefix + ":ThirdCountry" };
		String[] articles = lParser.findArticles(ontoPrefix + ":" + predicate);
		for (String s : articles) {
			String[] formulae = lParser.findFormulaeForArticleNotContaining(s, RuleType.ALL, exclusions);
			for (String f : formulae)
				System.out.println(f);
		}
	}

}
