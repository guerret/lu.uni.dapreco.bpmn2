package lu.uni.dapreco.parser.translator;

public class TranslatorOutput {

	private String provisions;
	private String exceptions;

	public TranslatorOutput(String p, String e) {
		provisions = p;
		exceptions = e;
	}

	public String getProvisions() {
		return provisions;
	}

	public String getExceptions() {
		return exceptions;
	}

}
