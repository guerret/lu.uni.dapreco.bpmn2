package lu.uni.dapreco.parser;

import lu.uni.dapreco.parser.lrml.LRMLParser.RuleType;

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
