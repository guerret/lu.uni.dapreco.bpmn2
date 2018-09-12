package lu.uni.dapreco.parser;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import lu.uni.dapreco.parser.lrml.LRMLParser.RuleType;

public class Translator {

	private DKBValidation dkb;

	private Translator() {
		dkb = new DKBValidation();
	}

	private TranslatorOutput translate(RuleType type) {
		return dkb.translate(type);
	}

	private void writeTranslation(RuleType type) {
		System.out.println("Processing " + type);
		TranslatorOutput translation = translate(type);
		try (PrintWriter out = new PrintWriter("outputs/rioKB_GDPR_translations_" + type + ".html")) {
			System.out.print("Writing provisions... ");
			out.println(translation.getProvisions());
			System.out.println("Done!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try (PrintWriter out = new PrintWriter("outputs/rioKB_GDPR_exceptions_" + type + ".html")) {
			System.out.print("Writing exceptions... ");
			out.println(translation.getExceptions());
			System.out.println("Done!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Translator test = new Translator();
		test.writeTranslation(RuleType.PERMISSIONS);
		test.writeTranslation(RuleType.OBLIGATIONS);
		test.writeTranslation(RuleType.CONSTITUTIVE);
	}

}
