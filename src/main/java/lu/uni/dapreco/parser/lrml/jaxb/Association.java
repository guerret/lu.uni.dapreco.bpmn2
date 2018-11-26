package lu.uni.dapreco.parser.lrml.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Association", namespace = LegalRuleML.lrmlNS)
public class Association {

	private static class AssociationData {

		@XmlAttribute
		public String keyref;

	}

	@XmlElement(name = "appliesSource", namespace = LegalRuleML.lrmlNS)
	private AssociationData source;

	public String getSource() {
		return source.keyref;
	}

	@XmlElement(name = "toTarget", namespace = LegalRuleML.lrmlNS)
	private AssociationData target;

	public String getTarget() {
		return target.keyref;
	}

}
