package lu.uni.dapreco.bpmn2;

import lu.uni.dapreco.bpmn2.lrml.LRMLParser.RuleType;

public class PredicateLister {

	private DKBValidation dkb;

	private PredicateLister() {
		dkb = new DKBValidation();
	}

	private void printPredicatesInSet(RuleType type) {
		String[] predicates = dkb.parsePredicatesInSet(type, DKBValidation.ontoPrefix);
		if (predicates.length > 0) {
			System.out.println(type);
			for (String c : predicates) {
				System.out.print(c);
				int occurrences = dkb.countOccurrencesInGraph(c);
				if (occurrences == 0)
					System.out.print(" MISSING");
				if (occurrences > 1)
					System.out.print(" DUPLICATE (" + occurrences + ")");
				System.out.println();
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		PredicateLister test = new PredicateLister();
		test.printPredicatesInSet(RuleType.PERMISSIONS);
		test.printPredicatesInSet(RuleType.OBLIGATIONS);
		test.printPredicatesInSet(RuleType.CONSTITUTIVE);
	}

}
