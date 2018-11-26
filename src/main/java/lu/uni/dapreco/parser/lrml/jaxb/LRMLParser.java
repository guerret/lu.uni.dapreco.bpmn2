package lu.uni.dapreco.parser.lrml.jaxb;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import lu.uni.dapreco.parser.XPathParser;
import lu.uni.dapreco.parser.lrml.ContextMap;

public class LRMLParser {

	private final static String lrmlName = "rioKB_GDPR.xml";
	private final static String lrmlURI = "https://raw.githubusercontent.com/dapreco/daprecokb/master/gdpr/" + lrmlName;

	private Document doc;
	private XPathParser xpath;

	private Map<String, Statements> statements;

	private List<LegalReference> legalReferences;

	private List<Association> associations;

	private Map<String, Context> contexts;

	public static final Map<String, String> map = Map.ofEntries(Map.entry(LegalRuleML.lrmlPrefix, LegalRuleML.lrmlNS),
			Map.entry(LegalRuleML.rulemlPrefix, LegalRuleML.rulemlNS),
			Map.entry(LegalRuleML.xmlPrefix, LegalRuleML.xmlNS));

	public static enum RuleType {
		OBLIGATIONS, PERMISSIONS, CONSTITUTIVE, MAPPING, ALL
	};

	public static ContextMap contextMap;

	public LRMLParser(String lrmlURI) {
		LegalRuleML lrml;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(LegalRuleML.class);
			URL url = new URL(lrmlURI);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.addRequestProperty("User-Agent", "Mozilla/4.76");
			InputStream is = http.getInputStream();
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			lrml = (LegalRuleML) jaxbUnmarshaller.unmarshal(is);
			legalReferences = lrml.getLegalReferences();
			associations = lrml.getAssociations();
			statements = lrml.getStatements();
			contexts = lrml.getContexts();
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
	}

	public String[] findArticles(String predicate) {
		String[] statements = getStatementsForPredicate(predicate);
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

	private String[] getStatementsForPredicate(String predicate) {
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

	public static void main(String[] args) {
		LRMLParser lrmlParser = new LRMLParser(lrmlURI);
		System.out.println("ciao");
	}

}
