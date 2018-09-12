package lu.uni.dapreco.parser.lrml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import lu.uni.dapreco.parser.XPathParser;
import lu.uni.dapreco.parser.lrml.LRMLParser.RuleType;

public class ContextMap {

	private XPathParser xpath;

	private Document doc;

	public static String[] contextType = { "obligationRule", "permissionRule", "constitutiveRule",
			"constitutiveRule4MappingWithPrOnto" };

	private static final Map<RuleType, String> contextNames = Map.ofEntries(
			Map.entry(RuleType.OBLIGATIONS, contextType[0]), Map.entry(RuleType.PERMISSIONS, contextType[1]),
			Map.entry(RuleType.CONSTITUTIVE, contextType[2]), Map.entry(RuleType.MAPPING, contextType[3]),
			Map.entry(RuleType.ALL, ""));

	private Map<String, List<String>> contextMapping;

	public ContextMap(Document d, XPathParser x) {
		doc = d;
		xpath = x;
		contextMapping = new HashMap<String, List<String>>();
		NodeList contextsNodes = doc.getElementsByTagNameNS(LRMLParser.lrmlNS, "Context");
		for (int i = 0; i < contextsNodes.getLength(); i++) {
			Element elem = (Element) contextsNodes.item(i);
			String name = elem.getAttribute("type").substring(("rioOnto:").length());
			if (!contextMapping.containsKey(name)) {
				// contextMap.put(name, elem);
				ArrayList<String> statementList = new ArrayList<String>();
				NodeList statements = elem.getElementsByTagNameNS(LRMLParser.lrmlNS, "inScope");
				for (int j = 0; j < statements.getLength(); j++)
					statementList.add(((Element) statements.item(j)).getAttribute("keyref").substring("#".length()));
				contextMapping.put(name, statementList);
			}
		}
		return;
	}

	public boolean isInContext(Element statement, RuleType type) {
		String typeName = contextNames.get(type);
		if (contextMapping.containsKey(typeName))
			return contextMapping.get(typeName).contains(statement.getAttribute("key"));
		return false;
	}

	public String[] getAllStatementsInContext(RuleType type) {
		if (!contextMapping.containsKey(contextNames.get(type)))
			return new String[0];
		List<String> list = contextMapping.get(contextNames.get(type));
		String[] statements = new String[list.size()];
		for (int i = 0; i < list.size(); i++)
			statements[i] = list.get(i);
		return statements;
	}

	public RuleType getRuleType(String statement) {
		for (String contextName : contextMapping.keySet()) {
			if (contextMapping.get(contextName).contains(statement)) {
				Map<String, RuleType> inversed = contextNames.entrySet().stream()
						.collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
				return inversed.get(contextName);
			}
		}
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
