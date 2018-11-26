package lu.uni.dapreco.parser.lrml.jaxb;

import javax.xml.bind.annotation.XmlAttribute;

public class Variable extends RuleMLBlock {

	@XmlAttribute(name = "key")
	private String name;

}
