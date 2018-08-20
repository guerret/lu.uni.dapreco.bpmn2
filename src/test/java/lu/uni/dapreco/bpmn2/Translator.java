package lu.uni.dapreco.bpmn2;

import lu.uni.dapreco.bpmn2.lrml.LRMLParser.RuleType;

public class Translator {

	private DKBValidation dkb;

	private Translator() {
		dkb = new DKBValidation();
	}

	private String translate(RuleType type) {
		return dkb.translate(type);
	}

	public static void main(String[] args) {
		Translator test = new Translator();
		System.out.println(test.translate(RuleType.PERMISSIONS));
		// System.out.println(test.translate(RuleType.OBLIGATIONS));
		// System.out.println(test.translate(RuleType.CONSTITUTIVE));
	}

}
