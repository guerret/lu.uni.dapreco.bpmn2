package lu.uni.dapreco.bpmn2.lrml;

import lu.uni.dapreco.bpmn2.XPathParser;

public class Association {

	private String source;
	private String target;

	private Association(String s, String t) {
		source = s;
		target = t;
	}

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public static Association createFromStatement(String statement, XPathParser xpath) {
		String target = "#" + statement;
		String search = "/lrml:LegalRuleML/lrml:Associations/lrml:Association[lrml:toTarget[@keyref='" + target
				+ "']]/lrml:appliesSource/@keyref";
		String source = xpath.parse(search).item(0).getNodeValue();
		return new Association(source, target);
	}

	public static Association createFromArticle(String article, XPathParser xpath) {
		String source = "#" + article;
		String search = "/lrml:LegalRuleML/lrml:Associations/lrml:Association[lrml:appliesSource[@keyref='" + source
				+ "']]/lrml:toTarget/@keyref";
		String target = xpath.parse(search).item(0).getNodeValue();
		return new Association(source, target);
	}

}
