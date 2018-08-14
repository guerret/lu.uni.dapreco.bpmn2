package lu.uni.dapreco.bpmn2;

import lu.uni.dapreco.bpmn2.lrml.LRMLParser.RuleType;

public class PredicateLister2 {

	private DKBValidation dkb;

	PredicateLister2() {
		dkb = new DKBValidation();
	}

	private void workOn(RuleType type) {
		dkb.boh(type);
	}

	public static void main(String[] args) {
		PredicateLister2 test = new PredicateLister2();
		test.workOn(RuleType.PERMISSIONS);
	}

}
