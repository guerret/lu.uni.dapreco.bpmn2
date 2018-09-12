package lu.uni.dapreco.parser.lrml;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import lu.uni.dapreco.parser.XPathParser;

public class LegalReference extends BaseLRMLElement {

	private String refersTo;
	private String refID;

	public LegalReference(Element node) {
		super(node, null);
		refersTo = root.getAttribute("refersTo");
		refID = root.getAttribute("refID");
	}

	public String getRefersTo() {
		return refersTo;
	}

	public String getRefID() {
		return refID;
	}

	public static LegalReference createFromReference(String refersTo, XPathParser xpath) {
		String search = "/lrml:LegalRuleML/lrml:LegalReferences/lrml:LegalReference[@refersTo='" + refersTo + "']";
		NodeList nl = xpath.parse(search);
		if (nl.getLength() == 0)
			return null;
		return new LegalReference((Element) nl.item(0));
	}

	public static LegalReference createFromRefId(String refID, XPathParser xpath) {
		String search = "/lrml:LegalRuleML/lrml:LegalReferences/lrml:LegalReference[@refID='" + refID + "']";
		NodeList nl = xpath.parse(search);
		if (nl.getLength() == 0)
			return null;
		return new LegalReference((Element) nl.item(0));
	}

}
