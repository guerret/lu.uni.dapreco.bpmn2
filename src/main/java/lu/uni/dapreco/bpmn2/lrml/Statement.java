package lu.uni.dapreco.bpmn2.lrml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

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
	private Statement identical;

	public Statement(Element node, XPathParser xpath, StatementSet set) {
		super(node, xpath);
		name = root.getAttribute("key");
		Node child = root.getFirstChild();
		while (!child.getNodeName().equals("ruleml:Rule"))
			child = child.getNextSibling();
		type = LRMLParser.contextMap.getRuleType(name);
		owner = set;
		rule = new Rule((Element) child, type, xpath, this);
		identical = null;
		if (owner != null) {
			Statement[] statements = owner.getStatements();
			for (int i = 0; i < statements.length; i++)
				if (statements[i] != null && statements[i].rule.getLHS().equals(rule.getLHS())) {
					identical = statements[i];
					statements[i].rule.setBearer(rule.getBearer());
					break;
				}
		}
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
		if (identical != null)
			return "";
		Statement bearerStatement = rule.getBearerStatement();
		if (bearerStatement != null && bearerStatement != this)
			return "<h3>Statements " + name + " and " + bearerStatement.name + "</h3>" + rule.translate() + "\n";
		return "<h3>Statement " + name + "</h3>" + rule.translate() + "\n";
	}

	public String[] findPredicates(String prefix) {
		Vector<String> predicateVector = new Vector<String>();
		String search = "descendant::ruleml:Atom/ruleml:Rel[starts-with(@iri,'" + prefix + ":')]/@iri";
		NodeList nl = xpath.parseNode(search, root);
		for (int i = 0; i < nl.getLength(); i++) {
			String predicate = nl.item(i).getNodeValue().substring(prefix.length() + 1);
			if (!predicateVector.contains(predicate))
				predicateVector.add(predicate);
		}
		String[] predicates = new String[predicateVector.size()];
		return predicateVector.toArray(predicates);
	}

	public Map<String, List<Statement>> getExceptions(Map<String, List<Statement>> exceptions) {
		String search = "ruleml:Rule/ruleml:if/descendant::ruleml:Atom/ruleml:Rel[starts-with(@iri, 'rioOnto:exception')]/@iri";
		NodeList exceptionNames = xpath.parseNode(search, root);
		for (int i = 0; i < exceptionNames.getLength(); i++) {
			String exceptionName = exceptionNames.item(i).getNodeValue();
			// If already in there, then exception has been parsed recursively
			if (!exceptions.containsKey(exceptionName)) {
				exceptions.put(exceptionName, new ArrayList<Statement>());
				search = "/lrml:LegalRuleML/lrml:Statements/lrml:ConstitutiveStatement[ruleml:Rule/ruleml:then/descendant::ruleml:Atom/ruleml:Rel[@iri='"
						+ exceptionName + "']]";
				NodeList nl = xpath.parseNode(search, root);
				for (int j = 0; j < nl.getLength(); j++) {
					Statement statement = new Statement((Element) nl.item(j), xpath, null);
					if (!exceptions.get(exceptionName).contains(statement))
						exceptions.get(exceptionName).add(statement);
					exceptions = statement.getExceptions(exceptions);
				}
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

}
