package lu.uni.dapreco.parser.lrml.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public class Side {

	@XmlElements({ @XmlElement(name = "Exists", namespace = LegalRuleML.rulemlNS, type = Exists.class) })
	private RuleMLBlock content;

	public RuleMLBlock getContent() {
		return content;
	}

}
