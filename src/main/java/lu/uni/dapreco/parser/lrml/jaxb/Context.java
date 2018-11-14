package lu.uni.dapreco.parser.lrml.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Context", namespace = LegalRuleML.lrmlNS)
public class Context {

	private static class InScope {

		@XmlAttribute
		public String keyref;

	}

	@XmlAttribute
	private String key;

	@XmlAttribute
	private String type;

	public String getType() {
		return type;
	}

	@XmlElement(name = "inScope", namespace = LegalRuleML.lrmlNS)
	private List<InScope> inScopes;

	private List<String> statements;

	public List<String> getStatements() {
		if (statements == null) {
			statements = new ArrayList<String>();
			for (InScope i : inScopes)
				statements.add(i.keyref);
		}
		return statements;
	}

}
