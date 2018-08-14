package lu.uni.dapreco.bpmn2.lrml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lu.uni.dapreco.bpmn2.XPathParser;

public class LegalReference extends BaseLRMLElement {

	private String refersTo;
	private String refID;

	private LegalReference(Node node) {
		super(node);
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
		return new LegalReference(nl.item(0));
	}

	public static LegalReference createFromRefId(String refID, XPathParser xpath) {
		String search = "/lrml:LegalRuleML/lrml:LegalReferences/lrml:LegalReference[@refID='" + refID + "']";
		NodeList nl = xpath.parse(search);
		if (nl.getLength() == 0)
			return null;
		return new LegalReference(nl.item(0));
	}

}
