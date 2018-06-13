package lu.uni.dapreco.bpmn2.akn;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lu.uni.dapreco.bpmn2.XPathParser;

public class Point {

	private Node root;
	private XPathParser xpath;

	public Point(Node node, XPathParser x) {
		root = node;
		xpath = x;
	}

	public String toString() {
		String ret = "";
		ret += xpath.parseNode("ancestor::ns:article/ns:num", root).item(0).getTextContent().replaceAll("\\s+", " ")
				.trim();
		NodeList paragraph = xpath.parseNode("parent::*/parent::ns:paragraph/ns:num", root);
		if (paragraph.getLength() > 0)
			ret += "." + paragraph.item(0).getTextContent().replaceAll("\\s+", " ").trim();
		ret += "\n";
		ret += xpath.parseNode("parent::*/ns:intro", root).item(0).getTextContent().replaceAll("\\s+", " ").trim()
				+ "\n";
		NodeList children = xpath.parseNode("ns:*", root);
		for (int i = 0; i < children.getLength(); i++)
			ret += children.item(i).getTextContent().replaceAll("\\s+", " ");
		return ret;
	}

}
