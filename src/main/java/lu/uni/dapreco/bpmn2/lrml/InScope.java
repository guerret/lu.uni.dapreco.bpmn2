package lu.uni.dapreco.bpmn2.lrml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lu.uni.dapreco.bpmn2.XPathParser;

public class InScope extends BaseLRMLElement {

	private String key;

	private InScope(Node node) {
		super(node, null);
		key = ((Element) node).getAttribute("key");
	}

	public String getKey() {
		return key;
	}

	public static InScope create(String k, XPathParser xpath) {
		String search = "/lrml:LegalRuleML/lrml:Context/lrml:inScope[@keyref='#" + k + "']";
		NodeList nl = xpath.parse(search);
		if (nl.getLength() == 0)
			return null;
		return new InScope(nl.item(0));
	}

}
