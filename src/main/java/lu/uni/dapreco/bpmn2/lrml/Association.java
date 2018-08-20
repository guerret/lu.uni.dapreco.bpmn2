package lu.uni.dapreco.bpmn2.lrml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lu.uni.dapreco.bpmn2.XPathParser;

public class Association extends BaseLRMLElement {

	private String source;
	private String target;

	private Association(Node node) {
		super(node, null);
		Node child = root.getFirstChild();
		while (child != null) {
			if (child.getNodeName().equals("lrml:appliesSource"))
				source = ((Element) child).getAttribute("keyref");
			if (child.getNodeName().equals("lrml:toTarget"))
				target = ((Element) child).getAttribute("keyref");
			child = child.getNextSibling();
		}
	}

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public static Association createFromStatement(String statement, XPathParser xpath) {
		String target = "#" + statement;
		String search = "/lrml:LegalRuleML/lrml:Associations/lrml:Association[lrml:toTarget[@keyref='" + target + "']]";
		NodeList nl = xpath.parse(search);
		if (nl.getLength() == 0)
			return null;
		return new Association(nl.item(0));
	}

	public static Association createFromArticle(String article, XPathParser xpath) {
		String source = "#" + article;
		String search = "/lrml:LegalRuleML/lrml:Associations/lrml:Association[lrml:appliesSource[@keyref='" + source
				+ "']]";
		NodeList nl = xpath.parse(search);
		if (nl.getLength() == 0)
			return null;
		return new Association(nl.item(0));
	}

}
