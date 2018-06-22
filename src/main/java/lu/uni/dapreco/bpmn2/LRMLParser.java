package lu.uni.dapreco.bpmn2;

import java.io.IOException;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lu.uni.dapreco.bpmn2.lrml.Association;
import lu.uni.dapreco.bpmn2.lrml.LegalReference;

public class LRMLParser {

	private static Document doc;
	private XPathParser xpath;

	public static String lrmlPrefix = "lrml";
	public static String lrmlNS = "http://docs.oasis-open.org/legalruleml/ns/v1.0/";
	public static String rulemlPrefix = "ruleml";
	public static String rulemlNS = "http://ruleml.org/spec";
	public static String xmlPrefix = "xml";
	public static String xmlNS = XMLConstants.XML_NS_URI;

	public static final Map<String, String> map = Map.ofEntries(Map.entry(lrmlPrefix, lrmlNS),
			Map.entry(rulemlPrefix, rulemlNS), Map.entry(xmlPrefix, xmlNS));

	public LRMLParser(String lrml) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(lrml);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		xpath = new XPathParser(doc, map);
	}

	public String[] findArticles(String predicate) {
		String[] statements = getStatements(predicate);
		String[] articles = new String[statements.length];
		for (int i = 0; i < statements.length; i++) {
			String statement = statements[i];
			Association association = Association.createFromStatement(statement, xpath);
			String source = association.getSource().replaceAll("#", "");
			LegalReference lr = LegalReference.createFromReference(source, xpath);
			String refID = lr.getRefID();
			articles[i] = refID;
		}
		return articles;
	}

	private String[] getStatements(String predicate) {
		String search = "/lrml:LegalRuleML/lrml:Statements[descendant::ruleml:if[descendant::ruleml:Rel[@iri='"
				+ predicate + "']]]/@key";
		String[] statements = null;
		NodeList nl = xpath.parse(search);
		statements = new String[nl.getLength()];
		for (int i = 0; i < nl.getLength(); i++) {
			statements[i] = nl.item(i).getNodeValue();
		}
		return statements;
	}

}
