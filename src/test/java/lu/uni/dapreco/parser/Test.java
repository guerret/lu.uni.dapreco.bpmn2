package lu.uni.dapreco.parser;

import lu.uni.dapreco.parser.akn.AKNParser;
import lu.uni.dapreco.parser.lrml.jaxb.LRMLParser;

public class Test {

	private final static String resDir = "resources";

	private final static String lrmlName = "rioKB_GDPR.xml";
	private final static String lrmlURI = "https://raw.githubusercontent.com/dapreco/daprecokb/master/gdpr/" + lrmlName;
	// private final static String lrmlLocal = resDir +l "/" + lrmlName;

	private final static String aknName = "akn-act-gdpr-full.xml";
	private static final String aknURI = "https://raw.githubusercontent.com/guerret/lu.uni.dapreco.parser/master/"
			+ resDir + "/" + aknName;
	// private static final String aknLocal = resDir + "/" + aknName;

	private static String aknPrefix = "GDPR";
	private static String ontoPrefix = "prOnto";
	private static String predicate = "Transmit";

	public static void main(String[] args) {
		// PrOntoParser p = new PrOntoParser(true);
		LRMLParser lParser = new LRMLParser(lrmlURI);
		AKNParser aParser = new AKNParser(aknURI);
		// String[] actions = p.getActions();
		// for (String predicate : actions) {
		System.out.println("PROVISIONS FOR ACTION: " + predicate);
		String[] exclusions = { ontoPrefix + ":ThirdCountryx" };
//		String[] articles = lParser.findArticles(ontoPrefix + ":" + predicate);
//		for (String s : articles) {
//			s = s.substring((aknPrefix + ":").length());
//			System.out.println(aParser.getTextFromEId(s));
//			System.out.println();
//		}
		// }
	}

}
