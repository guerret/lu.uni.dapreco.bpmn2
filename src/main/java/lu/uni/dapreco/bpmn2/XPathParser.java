package lu.uni.dapreco.bpmn2;

import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathParser {

	private XPath xpath;
	private Document doc;

	public XPathParser(Document d, Map<String, String> map) {
		xpath = XPathFactory.newInstance().newXPath();
		xpath.setNamespaceContext(new NamespaceContext() {
			public String getNamespaceURI(String prefix) {
				if (prefix == null)
					throw new NullPointerException("Null prefix");
				if (map.containsKey(prefix))
					return map.get(prefix);
				return XMLConstants.NULL_NS_URI;
			}

			public String getPrefix(String uri) {
				throw new UnsupportedOperationException();
			}

			public Iterator<String> getPrefixes(String uri) {
				throw new UnsupportedOperationException();
			}
		});
		doc = d;
	}

	public NodeList parse(String search) {
		NodeList ret = null;
		try {
			ret = (NodeList) xpath.compile(search).evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public NodeList parseNode(String search, Node node) {
		NodeList ret = null;
		try {
			ret = (NodeList) xpath.compile(search).evaluate(node, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return ret;
	}

}
