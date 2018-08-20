package lu.uni.dapreco.bpmn2;

import lu.uni.dapreco.bpmn2.lrml.LRMLParser.RuleType;

public class ExampleLRMLFileCreator {

	private DKBValidation dkb;

	private ExampleLRMLFileCreator() {
		dkb = new DKBValidation();
	}

	public static void main(String[] args) {
		ExampleLRMLFileCreator test = new ExampleLRMLFileCreator();
		test.dkb.createExampleFile(RuleType.PERMISSIONS);
		test.dkb.createExampleFile(RuleType.OBLIGATIONS);
		test.dkb.createExampleFile(RuleType.CONSTITUTIVE);
	}

}
