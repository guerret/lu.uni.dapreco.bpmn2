package lu.uni.dapreco.bpmn2;

import lu.uni.dapreco.bpmn2.lrml.LRMLParser.RuleType;

public class MissingPredicatesFinder {

	private DKBValidation dkb;

	private MissingPredicatesFinder() {
		dkb = new DKBValidation();
	}

	public void printMissingFromSet(RuleType type) {
		String[] missing = dkb.parsePredicatesInSet(type, DKBValidation.daprecoPrefix);
		if (missing.length > 0) {
			System.out.println(type);
			for (String c : missing)
				System.out.println(c);
			System.out.println();
		}
	}

	public static void main(String[] args) {
		MissingPredicatesFinder test = new MissingPredicatesFinder();
		test.printMissingFromSet(RuleType.PERMISSIONS);
		test.printMissingFromSet(RuleType.OBLIGATIONS);
		test.printMissingFromSet(RuleType.CONSTITUTIVE);
	}

}
