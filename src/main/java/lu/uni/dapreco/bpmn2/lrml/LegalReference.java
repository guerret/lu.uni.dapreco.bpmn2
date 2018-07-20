package lu.uni.dapreco.bpmn2.lrml;

import org.w3c.dom.NodeList;

import lu.uni.dapreco.bpmn2.XPathParser;

public class LegalReference {

	private String refersTo;
	private String refID;

	private LegalReference(String refers, String id) {
		refersTo = refers;
		refID = id;
	}

	public String getRefersTo() {
		return refersTo;
	}

	public String getRefID() {
		return refID;
	}

	public static LegalReference createFromReference(String refersTo, XPathParser xpath) {
		String search = "/lrml:LegalRuleML/lrml:LegalReferences/lrml:LegalReference[@refersTo='" + refersTo
				+ "']/@refID";
		String refID = xpath.parse(search).item(0).getNodeValue();
		return new LegalReference(refersTo, refID);
	}

	public static LegalReference createFromRefId(String refID, XPathParser xpath) {
		String search = "/lrml:LegalRuleML/lrml:LegalReferences/lrml:LegalReference[@refID='" + refID + "']/@refersTo";
		NodeList nl = xpath.parse(search);
		if (nl.getLength() == 0)
			return null;
		String refersTo = nl.item(0).getNodeValue();
		return new LegalReference(refersTo, refID);
	}

}
