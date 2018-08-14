package lu.uni.dapreco.bpmn2.lrml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class BaseLRMLElement {

	Element root;

	public BaseLRMLElement(Node node) {
		root = (Element) node;
	}

	public Element getRoot() {
		return root;
	}

	public void remove() {
		Node next = root.getNextSibling();
		Node parent = root.getParentNode();
		parent.removeChild(root);
		if (next != null && next.getNodeType() == Node.TEXT_NODE && next.getTextContent().trim().isEmpty())
			parent.removeChild(next);
	}

}
