package lu.uni.dapreco.parser.lrml.jaxb;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Statements", namespace = LegalRuleML.lrmlNS)
public class Statements {

	@XmlElement(name = "ConstitutiveStatement", namespace = LegalRuleML.lrmlNS)
	private List<Statement> statementList;

	@XmlAttribute(name = "key")
	private String name;

	public String getName() {
		return name;
	}

}
