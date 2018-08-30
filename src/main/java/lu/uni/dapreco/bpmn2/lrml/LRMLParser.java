package lu.uni.dapreco.bpmn2.lrml;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import lu.uni.dapreco.bpmn2.XPathParser;

public class LRMLParser {

	private Document doc;
	private XPathParser xpath;

	public static String lrmlPrefix = "lrml";
	public static String lrmlNS = "http://docs.oasis-open.org/legalruleml/ns/v1.0/";
	public static String rulemlPrefix = "ruleml";
	public static String rulemlNS = "http://ruleml.org/spec";
	public static String xmlPrefix = "xml";
	public static String xmlNS = XMLConstants.XML_NS_URI;

	public static final Map<String, String> map = Map.ofEntries(Map.entry(lrmlPrefix, lrmlNS),
			Map.entry(rulemlPrefix, rulemlNS), Map.entry(xmlPrefix, xmlNS));

	public static enum RuleType {
		OBLIGATIONS, PERMISSIONS, CONSTITUTIVE, MAPPING, ALL
	};

	public static ContextMap contextMap;

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
		contextMap = new ContextMap(doc, xpath);
	}

	public LRMLParser(Document d) {
		doc = d;
		xpath = new XPathParser(doc, map);
		contextMap = new ContextMap(doc, xpath);
	}

	public String[] getStatementsOfType(RuleType type) {
		return contextMap.getAllStatementsInContext(type);
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

	public Node[] getStatementNodesForArticle(String article, RuleType type) {
		String statementSet = getStatementSetForArticle(article);
		if (statementSet == null)
			return new Node[0];
		Vector<Node> statementsVector = new Vector<Node>();
		String search = "/lrml:LegalRuleML/lrml:Statements[@key='" + statementSet + "']/lrml:ConstitutiveStatement";
		NodeList nl = xpath.parse(search);
		for (int i = 0; i < nl.getLength(); i++) {
			Element elem = (Element) nl.item(i);
			if (type == RuleType.ALL || contextMap.isInContext(elem, type))
				statementsVector.add(elem);
		}
		Node[] statements = new Node[statementsVector.size()];
		return statementsVector.toArray(statements);
	}

	// private String[] getStatementsForArticle(String article) {
	// String statementSet = getStatementListForArticle(article);
	// if (statementSet == null)
	// return new String[0];
	// Vector<String> statementsVector = new Vector<String>();
	// String search = "/lrml:LegalRuleML/lrml:Statements[@key='" + statementSet
	// +
	// "']/lrml:PrescriptiveStatement/@key|/lrml:LegalRuleML/lrml:Statements[@key='"
	// + statementSet
	// + "']/lrml:ConstitutiveStatement/@key";
	// NodeList nl = xpath.parse(search);
	// for (int i = 0; i < nl.getLength(); i++)
	// statementsVector.add(nl.item(i).getNodeValue());
	// String[] statements = new String[statementsVector.size()];
	// return statementsVector.toArray(statements);
	// }

	private String getStatementSetForArticle(String article) {
		LegalReference lr = LegalReference.createFromRefId(article, xpath);
		if (lr == null)
			return null;
		String refersTo = lr.getRefersTo();
		Association association = Association.createFromArticle(refersTo, xpath);
		return association.getTarget().substring(1);
	}

	public void removeAllLegalReferencesExceptForRules(String[] workingSet, String prefix) {
		String search = "/lrml:LegalRuleML/lrml:LegalReferences/lrml:LegalReference";
		NodeList nl = xpath.parse(search);
		for (int i = 0; i < nl.getLength(); i++) {
			String refID = ((Element) nl.item(i)).getAttribute("refID");
			if (!inWorkingSet(refID, workingSet, prefix))
				remove(refID);
		}
		StatementSet s = StatementSet.create("statements276", xpath);
		s.remove(xpath);
		String[] atoms = getReificationAtoms();
		removeUnusedAtoms(atoms);
	}

	private boolean inWorkingSet(String article, String[] workingSet, String prefix) {
		for (String rule : workingSet)
			if (article.equals(prefix + ":" + rule))
				return true;
		return false;
	}

	private String[] getReificationAtoms() {
		String search = "/lrml:LegalRuleML/lrml:Statements/descendant::ruleml:Atom/@keyref";
		NodeList nl = xpath.parse(search);
		String[] atoms = new String[nl.getLength()];
		for (int i = 0; i < nl.getLength(); i++)
			atoms[i] = nl.item(i).getNodeValue().substring(1);
		return atoms;
	}

	private void removeUnusedAtoms(String[] atoms) {
		String search = "/lrml:LegalRuleML/lrml:Associations/lrml:Association[lrml:appliesModality]";
		Node node = xpath.parse(search).item(0).getFirstChild();
		while (node != null) {
			Node next = node.getNextSibling();
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (!isAtomUsed(((Element) node).getAttribute("keyref"), atoms)) {
					Node parent = node.getParentNode();
					parent.removeChild(node);
					if (next != null && next.getNodeType() == Node.TEXT_NODE
							&& next.getTextContent().trim().isEmpty()) {
						Node temp = next.getNextSibling();
						parent.removeChild(next);
						next = temp;
					}
				}
			}
			node = next;
		}
	}

	private boolean isAtomUsed(String atom, String[] usedAtoms) {
		for (String u : usedAtoms)
			if (atom.equals("#" + u))
				return true;
		return false;
	}

	public void remove(String article) {
		LegalReference lr = LegalReference.createFromRefId(article, xpath);
		if (lr != null) {
			lr.remove();
			String refersTo = lr.getRefersTo();
			Association a = Association.createFromArticle(refersTo, xpath);
			if (a != null) {
				a.remove();
				String target = a.getTarget();
				StatementSet s = StatementSet.create(target.substring(1), xpath);
				s.remove(xpath);
			}
		}
	}

	public LRMLParser copy() {
		LRMLParser copy = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document copiedDocument = builder.newDocument();
			Node copiedRoot = copiedDocument.importNode(doc.getDocumentElement(), true);
			copiedDocument.appendChild(copiedRoot);
			copy = new LRMLParser(copiedDocument);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return copy;
	}

	public int countStatements() {
		return doc.getElementsByTagName("lrml:Statements").getLength();
	}

	public void write(String filename) {
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			Result output = new StreamResult(new File("outputs/" + filename));
			Source input = new DOMSource(doc);
			transformer.transform(input, output);
		} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	public String translate(String[] extended, String prefix) {
		String ret = "";
		for (String rule : extended) {
			StatementSet statementSet = StatementSet.createFromArticle(prefix + ":" + rule, xpath);
			if (statementSet != null)
				ret += "<h2>" + rule + "</h2>\n" + statementSet.translate();
		}
		return ret;
	}

	public List<String> getExceptions(String[] extended, String prefix, List<String> exceptions) {
		for (String rule : extended) {
			StatementSet statementSet = StatementSet.createFromArticle(prefix + ":" + rule, xpath);
			if (statementSet != null)
				exceptions = statementSet.getExceptions(exceptions);
		}
		return exceptions;
	}

	public boolean analyze() {
		Node child = doc.getDocumentElement().getFirstChild();
		while (!child.getNodeName().equals("lrml:Statements"))
			child = child.getNextSibling();
		while (child != null && child.getNodeName().equals("lrml:Statements")) {
			StatementSet statementSet = new StatementSet((Element) child, null);
			if (statementSet.analyze()) {
				System.out.println(statementSet.getName());
				return true;
			}
			child = child.getNextSibling();
		}
		return false;
	}

	public String translateExceptions(List<String> exceptions) {
		String ret = "";
		for (String e : exceptions) {
			String search = "/lrml:LegalRuleML/lrml:Statements/lrml:ConstitutiveStatement[ruleml:Rule/ruleml:then/descendant::ruleml:Atom/ruleml:Rel[@iri='"
					+ e + "']]";
			NodeList nl = xpath.parse(search);
			if (nl.getLength() == 0)
				ret += "PROBLEM: no definition found for exception " + e + "\n";
			else if (nl.getLength() == 1)
				ret += nl.getLength() + " definition found for exception " + e + "\n";
			else
				ret += nl.getLength() + " definitions found for exception " + e + "\n";
			for (int i = 0; i < nl.getLength(); i++) {
				Statement s = new Statement((Element) nl.item(i), xpath, null);
				ret += s.translate();
			}
		}
		return ret;
	}

}
