package lu.uni.dapreco.parser.lrml.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Rule", namespace = LegalRuleML.lrmlNS)
public class Rule {

	@XmlElement(name = "if", namespace = LegalRuleML.rulemlNS)
	private Side lhs;

	@XmlElement(name = "then", namespace = LegalRuleML.rulemlNS)
	private Side rhs;

}
