package lu.uni.dapreco.parser.lrml.jaxb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import lu.uni.dapreco.parser.lrml.ContextMap;

@XmlRootElement(name = "LegalRuleML", namespace = LegalRuleML.lrmlNS)
public class LegalRuleML {

	private Map<String, Statements> statements;
	private Map<String, Context> contexts;

	@XmlElementWrapper(name = "LegalReferences", namespace = LegalRuleML.lrmlNS)
	@XmlElement(name = "LegalReference", namespace = lrmlNS)
	private List<LegalReference> legalReferences;

	public List<LegalReference> getLegalReferences() {
		return legalReferences;
	}

	@XmlElementWrapper(name = "Associations", namespace = LegalRuleML.lrmlNS)
	@XmlElement(name = "Association", namespace = lrmlNS)
	private List<Association> associations;

	public List<Association> getAssociations() {
		return associations;
	}

	@XmlElement(name = "Statements", namespace = lrmlNS)
	private List<Statements> statementsList;

	public void setStatements(List<Statements> statements) {
		statementsList = statements;
		this.statements = new HashMap<String, Statements>();
		for (Statements s : statementsList)
			this.statements.put(s.getName(), s);
	}

	public Map<String, Statements> getStatements() {
		if (statements == null) {
			statements = new HashMap<String, Statements>();
			for (Statements s : statementsList)
				this.statements.put(s.getName(), s);
		}
		return statements;
	}

	@XmlElement(name = "Context", namespace = lrmlNS)
	private List<Context> contextList;

	public Map<String, Context> getContexts() {
		if (contexts == null) {
			contexts = new HashMap<String, Context>();
			for (Context c : contextList)
				contexts.put(c.getType(), c);
		}
		return contexts;
	}

	public static String lrmlPrefix = "lrml";
	public static final String lrmlNS = "http://docs.oasis-open.org/legalruleml/ns/v1.0/";
	public static String rulemlPrefix = "ruleml";
	public static final String rulemlNS = "http://ruleml.org/spec";
	public static String xmlPrefix = "xml";
	public static String xmlNS = XMLConstants.XML_NS_URI;

	public static final Map<String, String> map = Map.ofEntries(Map.entry(lrmlPrefix, lrmlNS),
			Map.entry(rulemlPrefix, rulemlNS), Map.entry(xmlPrefix, xmlNS));

	public static enum RuleType {
		OBLIGATIONS, PERMISSIONS, CONSTITUTIVE, MAPPING, ALL
	};

	public static ContextMap contextMap;

}
