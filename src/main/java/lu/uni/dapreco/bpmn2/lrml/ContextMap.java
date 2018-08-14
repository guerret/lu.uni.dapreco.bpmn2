package lu.uni.dapreco.bpmn2.lrml;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lu.uni.dapreco.bpmn2.XPathParser;
import lu.uni.dapreco.bpmn2.lrml.LRMLParser.RuleType;

public class ContextMap {

	private XPathParser xpath;

	private Document doc;

	public static String[] contextType = { "obligationRule", "permissionRule", "constitutiveRule",
			"constitutiveRule4MappingWithPrOnto" };

	private static final Map<RuleType, String> contextNames = Map.ofEntries(
			Map.entry(RuleType.OBLIGATIONS, contextType[0]), Map.entry(RuleType.PERMISSIONS, contextType[1]),
			Map.entry(RuleType.CONSTITUTIVE, contextType[2]), Map.entry(RuleType.MAPPING, contextType[3]),
			Map.entry(RuleType.ALL, ""));

	private Map<String, Node> contextMap;

	public ContextMap(Document d, XPathParser x) {
		doc = d;
		xpath = x;
		contextMap = new HashMap<String, Node>();
		NodeList contextsNodes = doc.getElementsByTagName("lrml:Context");
		for (int i = 0; i < contextsNodes.getLength(); i++) {
			Element elem = (Element) contextsNodes.item(i);
			String name = elem.getAttribute("type").substring(("rioOnto:").length());
			if (!contextMap.containsKey(name))
				contextMap.put(name, elem);
		}
	}

	public boolean isInContext(Element statement, RuleType type) {
		Node contextNode = getContextNode(type);
		if (contextNode != null) {
			String search = "lrml:inScope[@keyref='#" + statement.getAttribute("key") + "']";
			NodeList nl = xpath.parseNode(search, contextNode);
			if (nl.getLength() > 0)
				return true;
		}
		return false;
	}

	private Node getContextNode(RuleType type) {
		String contextName = contextNames.get(type);
		if (contextMap.containsKey(contextName))
			return contextMap.get(contextName);
		return null;
	}

	public String[] getAllStatementsInContext(RuleType type) {
		Node contextNode = getContextNode(type);
		if (contextNode == null)
			return new String[0];
		String search = "lrml:inScope/@keyref";
		NodeList nl = xpath.parseNode(search, contextNode);
		String[] statements = new String[nl.getLength()];
		for (int i = 0; i < nl.getLength(); i++)
			statements[i] = nl.item(i).getNodeValue();
		return statements;
	}

	public RuleType getRuleType(String statement) {
		String search = "//lrml:LegalRuleML/lrml:Context/lrml:inScope[@keyref='#" + statement + "']";
		NodeList nl = xpath.parse(search);
		if (nl.getLength() == 0)
			return RuleType.ALL;
		String contextName = ((Element) nl.item(0).getParentNode()).getAttribute("type").substring(8);
		Map<String, RuleType> inversed = contextNames.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
		RuleType ruleType = inversed.get(contextName);
		return ruleType;
	}

}
