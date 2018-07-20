package lu.uni.dapreco.bpmn2;

import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
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

	public static enum RuleType {
		ALL, OBLIGATIONS, PERMISSIONS, CONSTITUTIVE
	};

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

	public String[] findPredicatesInArticle(String article, String prefix, RuleType type) {
		Node[] statements = getStatementNodesForArticle(article, type);
		Vector<String> predicateVector = new Vector<String>();
		for (Node s : statements) {
			String search = "descendant::ruleml:Atom/ruleml:Rel[starts-with(@iri,'" + prefix + ":')]/@iri";
			NodeList nl = xpath.parseNode(search, s);
			for (int i = 0; i < nl.getLength(); i++) {
				String predicate = nl.item(i).getNodeValue().substring(prefix.length() + 1);
				if (!predicateVector.contains(predicate))
					predicateVector.add(predicate);
			}
		}
		String[] predicates = new String[predicateVector.size()];
		return predicateVector.toArray(predicates);
	}

	public String[] findFormulaeForArticle(String article, RuleType type) {
		Node[] statements = getStatementNodesForArticle(article, type);
		String[] formulae = new String[statements.length];
		for (int i = 0; i < statements.length; i++) {
			String search = "comment()";
			formulae[i] = xpath.parseNode(search, statements[i]).item(0).getNodeValue().trim();
		}
		return formulae;
	}

	private Node[] getStatementNodesForArticle(String article, RuleType type) {
		String[] statementSets = getStatementListForArticle(article);
		Vector<Node> statementsVector = new Vector<Node>();
		for (String set : statementSets) {
			String search;
			switch (type) {
			case OBLIGATIONS:
				search = "/lrml:LegalRuleML/lrml:Statements[@key='" + set
						+ "']/lrml:PrescriptiveStatement[ruleml:Rule/ruleml:then/lrml:Obligation]";
				break;
			case PERMISSIONS:
				search = "/lrml:LegalRuleML/lrml:Statements[@key='" + set
						+ "']/lrml:PrescriptiveStatement[ruleml:Rule/ruleml:then/lrml:Permission]";
				break;
			case CONSTITUTIVE:
				search = "/lrml:LegalRuleML/lrml:Statements[@key='" + set + "']/lrml:ConstitutiveStatement";
				break;
			default:
				search = "/lrml:LegalRuleML/lrml:Statements[@key='" + set
						+ "']/lrml:PrescriptiveStatement|/lrml:LegalRuleML/lrml:Statements[@key='" + set
						+ "']/lrml:ConstitutiveStatement";
			}
			NodeList nl = xpath.parse(search);
			for (int i = 0; i < nl.getLength(); i++)
				statementsVector.add(nl.item(i));
		}
		Node[] statements = new Node[statementsVector.size()];
		return statementsVector.toArray(statements);
	}

	private String[] getStatementsForArticle(String article) {
		String[] statementSets = getStatementListForArticle(article);
		Vector<String> statementsVector = new Vector<String>();
		for (String set : statementSets) {
			String search = "/lrml:LegalRuleML/lrml:Statements[@key='" + set
					+ "']/lrml:PrescriptiveStatement/@key|/lrml:LegalRuleML/lrml:Statements[@key='" + set
					+ "']/lrml:ConstitutiveStatement/@key";
			NodeList nl = xpath.parse(search);
			for (int i = 0; i < nl.getLength(); i++)
				statementsVector.add(nl.item(i).getNodeValue());
		}
		String[] statements = new String[statementsVector.size()];
		return statementsVector.toArray(statements);
	}

	private String[] getStatementListForArticle(String article) {
		String search = "/lrml:LegalRuleML/lrml:LegalReferences/lrml:LegalReference[@refID='" + article
				+ "']/@refersTo";
		String[] statements = null;
		NodeList nl = xpath.parse(search);
		statements = new String[nl.getLength()];
		for (int i = 0; i < nl.getLength(); i++) {
			String ref = nl.item(i).getNodeValue();
			search = "/lrml:LegalRuleML/lrml:Associations/lrml:Association[lrml:appliesSource/@keyref='#" + ref
					+ "']/lrml:toTarget/@keyref";
			statements[i] = xpath.parse(search).item(0).getNodeValue().substring(1);
		}
		return statements;
	}

}
