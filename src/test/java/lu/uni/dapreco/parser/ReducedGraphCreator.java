package lu.uni.dapreco.parser;

import lu.uni.dapreco.parser.lrml.LRMLParser.RuleType;

public class ReducedGraphCreator {

	private DKBValidation dkb;

	private ReducedGraphCreator() {
		dkb = new DKBValidation();
	}

	public static void main(String[] args) {
		ReducedGraphCreator test = new ReducedGraphCreator();
		test.dkb.createReducedGraph(RuleType.PERMISSIONS);
		test.dkb.createReducedGraph(RuleType.OBLIGATIONS);
		test.dkb.createReducedGraph(RuleType.CONSTITUTIVE);
	}

}
