package lu.uni.dapreco.parser.lrml;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import lu.uni.dapreco.parser.XPathParser;

public class InScope extends BaseLRMLElement {

	private String key;

	private InScope(Element node) {
		super(node, null);
		key = node.getAttribute("key");
	}

	public String getKey() {
		return key;
	}

	public static InScope create(String k, XPathParser xpath) {
		String search = "/lrml:LegalRuleML/lrml:Context/lrml:inScope[@keyref='#" + k + "']";
		NodeList nl = xpath.parse(search);
		if (nl.getLength() == 0)
			return null;
		return new InScope((Element) nl.item(0));
	}

}
