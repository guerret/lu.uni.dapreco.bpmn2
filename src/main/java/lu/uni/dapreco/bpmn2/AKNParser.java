package lu.uni.dapreco.bpmn2;

import java.io.IOException;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import lu.uni.dapreco.bpmn2.akn.Paragraph;
import lu.uni.dapreco.bpmn2.akn.Point;

public class AKNParser {

	private static Document doc;
	private XPathParser xpath;

	public static String aknPrefix = "ns";
	public static String aknNS = "http://docs.oasis-open.org/legaldocml/ns/akn/3.0";
	public static String xmlPrefix = "xml";
	public static String xmlNS = XMLConstants.XML_NS_URI;

	public static final Map<String, String> map = Map.ofEntries(Map.entry(aknPrefix, aknNS),
			Map.entry(xmlPrefix, xmlNS));

	AKNParser(String akn) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.parse(akn);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		xpath = new XPathParser(doc, map);
	}

	public String getTextFromEId(String eId) {
		String search = "//*[@eId='" + eId + "']";
		Node node = xpath.parse(search).item(0);
		switch (node.getNodeName()) {
		case "point":
			return new Point(node, xpath).toString();
		case "paragraph":
			return new Paragraph(node, xpath).toString();
		default:
			return node.getNodeName();
		}
	}

}
