package lu.uni.dapreco.bpmn2.lrml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lu.uni.dapreco.bpmn2.XPathParser;

public class StatementSet extends BaseLRMLElement {

	private String name;

	public StatementSet(Node node) {
		super(node);
		name = ((Element) root).getAttribute("key");
	}

	public String getName() {
		return name;
	}

	Statement[] getStatements(XPathParser xpath) {
		String search = "lrml:ConstitutiveStatement";
		NodeList nl = xpath.parseNode(search, root);
		Statement[] statements = new Statement[nl.getLength()];
		for (int i = 0; i < nl.getLength(); i++)
			statements[i] = new Statement(nl.item(i));
		return statements;
	}

	public static StatementSet create(String statementSet, XPathParser xpath) {
		String search = "/lrml:LegalRuleML/lrml:Statements[@key='" + statementSet + "']";
		NodeList nl = xpath.parse(search);
		if (nl.getLength() == 0)
			return null;
		return new StatementSet(nl.item(0));
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

	public void translate(XPathParser xpath) {
		for (Statement statement : getStatements(xpath))
			statement.translate(xpath);
	}

}
