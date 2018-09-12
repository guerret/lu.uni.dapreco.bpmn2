package lu.uni.dapreco.parser.akn;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lu.uni.dapreco.parser.XPathParser;

public class Paragraph {

	private Node root;
	private XPathParser xpath;

	public Paragraph(Node node, XPathParser x) {
		root = node;
		xpath = x;
	}

	public String toString() {
		String ret = "";
		ret += xpath.parseNode("parent::*/ns:num", root).item(0).getTextContent().replaceAll("\\s+", " ").trim();
		NodeList paragraph = xpath.parseNode("ns:num", root);
		if (paragraph.getLength() > 0)
			ret += "." + paragraph.item(0).getTextContent().replaceAll("\\s+", " ").trim();
		ret += "\n";
		NodeList children = xpath.parseNode("ns:*[not(self::ns:num)]", root);
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals("list")) {
				List l = new List(children.item(i), xpath);
				ret += l.toString();
			} else
				ret += children.item(i).getTextContent().replaceAll("\\s+", " ");
		}
		return ret;
	}

}
