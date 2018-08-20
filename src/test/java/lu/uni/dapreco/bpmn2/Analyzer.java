package lu.uni.dapreco.bpmn2;

public class Analyzer {

	private DKBValidation dkb;

	private Analyzer() {
		dkb = new DKBValidation();
	}

	private boolean analyze() {
		return dkb.analyze();
	}

	public static void main(String[] args) {
		Analyzer test = new Analyzer();
		if (test.analyze())
			System.out.println("Mmm... qualcosa non va");
		else System.out.println("Oh che goduria");
	}

}
