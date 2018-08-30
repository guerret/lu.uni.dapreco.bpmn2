package lu.uni.dapreco.bpmn2.lrml;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import lu.uni.dapreco.bpmn2.XPathParser;

public class StatementSet extends BaseLRMLElement {

	private String name;
	private Statement[] statements;

	public StatementSet(Element node, XPathParser xpath) {
		super(node, xpath);
		name = root.getAttribute("key");
		String search = "/lrml:LegalRuleML/lrml:Statements[@key='" + this.name + "']/lrml:ConstitutiveStatement";
		NodeList nl = xpath.parse(search);
		statements = new Statement[nl.getLength()];
		for (int i = 0; i < statements.length; i++)
			statements[i] = new Statement((Element) nl.item(i), xpath, this);
	}

	public String getName() {
		return name;
	}

	public Statement[] getStatements() {
		return statements;
	}

	public static StatementSet create(String statementSet, XPathParser xpath) {
		String search = "/lrml:LegalRuleML/lrml:Statements[@key='" + statementSet + "']";
		NodeList nl = xpath.parse(search);
		if (nl.getLength() == 0)
			return null;
		return new StatementSet((Element) nl.item(0), xpath);
	}

	public static StatementSet createFromArticle(String article, XPathParser xpath) {
		LegalReference lr = LegalReference.createFromRefId(article, xpath);
		if (lr == null) // It may happen as there are more ids in the AKN than in the LRML
			return null;
		String refersTo = lr.getRefersTo();
		Association a = Association.createFromArticle(refersTo, xpath);
		String statementSet = a.getTarget().substring(1);
		return create(statementSet, xpath);
	}

	public void remove(XPathParser xpath) {
		super.remove();
		String search = "lrml:ConstitutiveStatement";
		NodeList nl = xpath.parseNode(search, root);
		for (int i = 0; i < nl.getLength(); i++) {
			String key = ((Element) nl.item(i)).getAttribute("key");
			InScope in = InScope.create(key, xpath);
			if (in != null)
				in.remove();
		}
	}

	public String translate() {
		String ret = "";
		for (Statement statement : getStatements())
			ret += statement.translate();
		return ret;
	}

	public boolean analyze() {
		for (Statement s : getStatements())
			if (s.analyze())
				return true;
		return false;
	}

	public List<String> getExceptions(List<String> exceptions) {
		for (Statement s : getStatements())
			exceptions = s.getExceptions(exceptions);
		return exceptions;
	}

}
