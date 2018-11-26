package lu.uni.dapreco.parser.lrml.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

public abstract class RuleMLBlock {

	@XmlElements({ @XmlElement(name = "Var", namespace = LegalRuleML.rulemlNS, type = Variable.class),
			@XmlElement(name = "And", namespace = LegalRuleML.rulemlNS, type = And.class),
			@XmlElement(name = "Atom", namespace = LegalRuleML.rulemlNS, type = Atom.class) })
	private List<RuleMLBlock> children;

}
