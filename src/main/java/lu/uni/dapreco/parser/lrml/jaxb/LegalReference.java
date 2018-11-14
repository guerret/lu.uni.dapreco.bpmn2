package lu.uni.dapreco.parser.lrml.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "LegalReference")
public class LegalReference {

	@XmlAttribute
	private String refersTo;

	@XmlAttribute
	private String refID;

}
