package lu.uni.dapreco.bpmn2.lrml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lu.uni.dapreco.bpmn2.XPathParser;
import lu.uni.dapreco.bpmn2.lrml.LRMLParser.RuleType;

public class Statement extends BaseLRMLElement {

	private String name;
	private RuleType type;
	private StatementSet owner;
	private Rule rule;

	public Statement(Element node, XPathParser xpath, StatementSet set) {
		super(node, xpath);
		name = root.getAttribute("key");
		Node child = root.getFirstChild();
		while (!child.getNodeName().equals("ruleml:Rule"))
			child = child.getNextSibling();
		type = LRMLParser.contextMap.getRuleType(name);
		owner = set;
		rule = new Rule((Element) child, type, xpath);
	}

	public String getName() {
		return name;
	}

	public Rule getRule() {
		return rule;
	}

	public RuleType getRuleType() {
		return type;
	}

	public String translate() {
		String ret = "<h3>Statement " + name + "</h3>" + rule.translate() + "\n";
		if (owner != null) {
			Statement[] statements = owner.getStatements();
			for (int i = 0; i < statements.length && statements[i] != this; i++)
				if (statements[i].getRule().getLHS().equals(getRule().getLHS()))
					ret += "<em>The IF part is identical to that of " + statements[i].name + "</em>";
		}
		return ret;
	}

	// public List<String> getExceptions(List<String> exceptions) {
	// String search =
	// "ruleml:Rule/ruleml:if/descendant::ruleml:Atom/ruleml:Rel[starts-with(@iri,
	// 'rioOnto:exception')]/@iri";
	// NodeList exceptionNames = xpath.parseNode(search, root);
	// for (int i = 0; i < exceptionNames.getLength(); i++) {
	// String exceptionName = exceptionNames.item(i).getNodeValue();
	// if (!exceptions.contains(exceptionName))
	// exceptions.add(exceptionName);
	// search =
	// "/lrml:LegalRuleML/lrml:Statements/lrml:ConstitutiveStatement[ruleml:Rule/ruleml:then/descendant::ruleml:Atom/ruleml:Rel[@iri='"
	// + exceptionName + "']]";
	// NodeList nl = xpath.parseNode(search, root);
	// for (int j = 0; j < nl.getLength(); j++)
	// exceptions = new Statement((Element) nl.item(j), xpath,
	// null).getExceptions(exceptions);
	// }
	// return exceptions;
	// }

	public Map<String, List<Statement>> getExceptions() {
		Map<String, List<Statement>> exceptions = new HashMap<String, List<Statement>>();
		String search = "ruleml:Rule/ruleml:if/descendant::ruleml:Atom/ruleml:Rel[starts-with(@iri, 'rioOnto:exception')]/@iri";
		NodeList exceptionNames = xpath.parseNode(search, root);
		for (int i = 0; i < exceptionNames.getLength(); i++) {
			String exceptionName = exceptionNames.item(i).getNodeValue();
			exceptions.putIfAbsent(exceptionName, new ArrayList<Statement>());
			search = "/lrml:LegalRuleML/lrml:Statements/lrml:ConstitutiveStatement[ruleml:Rule/ruleml:then/descendant::ruleml:Atom/ruleml:Rel[@iri='"
					+ exceptionName + "']]";
			NodeList nl = xpath.parseNode(search, root);
			for (int j = 0; j < nl.getLength(); j++) {
				Statement statement = new Statement((Element) nl.item(j), xpath, null);
				if (!exceptions.get(exceptionName).contains(statement))
					exceptions.get(exceptionName).add(statement);
				Map<String, List<Statement>> next = statement.getExceptions();
				for (String e : next.keySet())
					if (exceptions.containsKey(e))
						for (Statement s : next.get(e)) {
							if (!s.inList(exceptions.get(e)))
								exceptions.get(e).add(s);
						}
					else
						exceptions.put(e, next.get(e));
			}
		}
		return exceptions;
	}

	public boolean analyze() {
		return rule.analyze();
	}

	@Override
	public String toString() {
		return name;
	}

	boolean inList(List<Statement> haystack) {
		for (Statement s : haystack)
			if (name.equals(s.getName()))
				return true;
		return false;
	}

}
