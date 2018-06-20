package lu.uni.dapreco.bpmn2;

public class Test {

	private final static String lrmlURI = "https://raw.githubusercontent.com/dapreco/daprecokb/master/gdpr/rioKB_GDPR.xml";
	private final static String aknFile = "akn-act-gdpr-full.xml";
	private static String aknPrefix = "GDPR";
	private static String ontoPrefix = "prOnto";
	private static String predicate = "Transmit";

	public static void main(String[] args) {
		LRMLParser lParser = new LRMLParser(lrmlURI);
		AKNParser aParser = new AKNParser(aknFile);
		String[] articles = lParser.findArticles(ontoPrefix + ":" + predicate);
		for (String s : articles) {
			s = s.substring((aknPrefix + ":").length());
			System.out.println(aParser.getTextFromEId(s));
			System.out.println();
		}
	}

}
