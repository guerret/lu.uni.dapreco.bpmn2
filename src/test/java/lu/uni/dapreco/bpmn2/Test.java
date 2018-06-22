package lu.uni.dapreco.bpmn2;

import java.io.File;

public class Test {

	private final static String resDir = "resources";

	private final static String lrmlName = "rioKB_GDPR.xml";
	private final static String lrmlLocal = resDir + File.separator + lrmlName;
	private final static String lrmlURI = "https://raw.githubusercontent.com/dapreco/daprecokb/master/gdpr/" + lrmlName;

	private final static String aknName = "akn-act-gdpr-full.xml";
	private static final String aknLocal = resDir + File.separator + aknName;
	private static final String aknURI = "https://raw.githubusercontent.com/guerret/lu.uni.dapreco.bpmn2/master/"
			+ resDir + File.separator + aknName;

	private static String aknPrefix = "GDPR";
	private static String ontoPrefix = "prOnto";
	private static String predicate = "Transmit";

	public static void main(String[] args) {
		LRMLParser lParser = new LRMLParser(lrmlURI);
		AKNParser aParser = new AKNParser(aknURI);
		String[] articles = lParser.findArticles(ontoPrefix + ":" + predicate);
		for (String s : articles) {
			s = s.substring((aknPrefix + ":").length());
			System.out.println(aParser.getTextFromEId(s));
			System.out.println();
		}
	}

}
