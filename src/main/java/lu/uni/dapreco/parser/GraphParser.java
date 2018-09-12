package lu.uni.dapreco.parser;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

import lu.uni.dapreco.parser.lrml.LRMLParser.RuleType;

public class GraphParser {

	private DocumentBuilder builder;
	private Document doc;
	private XPathParser xpath;

	public static String graphmlPrefix = "ns";
	public static String graphmlNS = "http://graphml.graphdrawing.org/xmlns";
	public static String yPrefix = "y";
	public static String yNS = "http://www.yworks.com/xml/graphml";
	public static String xmlPrefix = "xml";
	public static String xmlNS = XMLConstants.XML_NS_URI;

	public static final Map<String, String> map = Map.ofEntries(Map.entry(graphmlPrefix, graphmlNS),
			Map.entry(yPrefix, yNS), Map.entry(xmlPrefix, xmlNS));

	public GraphParser(String graph) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			builder = factory.newDocumentBuilder();
			doc = builder.parse(graph);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		xpath = new XPathParser(doc, map);
	}

	public NodeList findElements(String name) {
		String search = "//ns:node[descendant::y:NodeLabel[text()='" + name
				+ "']]|//ns:edge[descendant::y:EdgeLabel[text()='" + name + "']]";
		return xpath.parse(search);
	}

	public NodeList findEdgesConnectingTo(String name) {
		String search = "//ns:edge[@source='" + name + "' or @target='" + name + "']";
		return xpath.parse(search);
	}

	public void removeNodeByName(String name) {
		String search = "//ns:node[descendant::y:NodeLabel[text()='" + name
				+ "']]|//ns:edge[descendant::y:EdgeLabel[text()='" + name + "']]";
		NodeList nl = xpath.parse(search);
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			removeNode(node);
		}
	}

	private void removeNode(Node node) {
		switch (node.getLocalName()) {
		case "node":
			Element e = (Element) node;
			String id = e.getAttribute("id");
			NodeList nl2 = findEdgesConnectingTo(id);
			for (int j = 0; j < nl2.getLength(); j++) {
				Node edge = nl2.item(j);
				edge.getParentNode().removeChild(edge);
			}
			node.getParentNode().removeChild(node);
			break;
		case "edge":
			if (node.getParentNode() != null)
				node.getParentNode().removeChild(node);
			break;
		default:
		}
	}

	public void writeReducedDocument(Document newDocument, RuleType ruleType) {
		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			Result output = new StreamResult(
					new File("outputs/pronto-" + ruleType.toString().toLowerCase() + "-minimal.graphml"));
			Source input = new DOMSource(newDocument);

			transformer.transform(input, output);
		} catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	public Document removeUnusedElements(String[] predicates) {
		Document copiedDocument = builder.newDocument();
		Node copiedRoot = copiedDocument.importNode(doc.getDocumentElement(), true);
		copiedDocument.appendChild(copiedRoot);
		Set<String> preds = new HashSet<String>(Arrays.asList(predicates));
		String search = "/ns:graphml/ns:graph/ns:node|/ns:graphml/ns:graph/ns:edge";
		NodeList nl = xpath.parseNode(search, copiedRoot);
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			search = "descendant::y:NodeLabel|descendant::y:EdgeLabel";
			Node item = xpath.parseNode(search, node).item(0);
			if (item != null) {
				String name = item.getTextContent().trim();
				if (!name.equals("") && !name.startsWith("is subclass of") && !name.startsWith("xsd:"))
					if (!preds.contains(name))
						removeNode(node);
			}
		}
		return copiedDocument;
	}

}
