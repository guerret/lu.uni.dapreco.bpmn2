package lu.uni.dapreco.bpmn2;

import lu.uni.dapreco.bpmn2.lrml.LRMLParser.RuleType;

public class ReducedGraphCreator {

	private DKBValidation dkb;

	ReducedGraphCreator() {
		dkb = new DKBValidation();
	}

	public static void main(String[] args) {
		ReducedGraphCreator test = new ReducedGraphCreator();
		test.dkb.createReducedGraph(RuleType.PERMISSIONS);
		test.dkb.createReducedGraph(RuleType.OBLIGATIONS);
		test.dkb.createReducedGraph(RuleType.CONSTITUTIVE);
	}

}
