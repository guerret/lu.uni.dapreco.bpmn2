package lu.uni.dapreco.parser.akn;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lu.uni.dapreco.parser.XPathParser;

public class List {

	private Node root;
	private XPathParser xpath;

	public List(Node node, XPathParser x) {
		root = node;
		xpath = x;
	}

	public String toString() {
		String ret = "";
		NodeList children = xpath.parseNode("ns:*", root);
		for (int i = 0; i < children.getLength(); i++)
			ret += children.item(i).getTextContent().replaceAll("\\s+", " ");
		return ret;
	}

}
