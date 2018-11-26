package lu.uni.dapreco.parser.lrml.jaxb;

import javax.xml.bind.annotation.XmlAttribute;

public class Atom extends RuleMLBlock {

	@XmlAttribute(name = "keyref")
	private String reificationRef;

	public boolean isReified() {
		return reificationRef != null;
	}

}
