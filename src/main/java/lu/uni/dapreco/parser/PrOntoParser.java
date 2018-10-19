package lu.uni.dapreco.parser;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLClass;

import lu.uni.dapreco.parser.owl.Ontology;

public class PrOntoParser {

	public static final String OWL_PATH = "/resources";
	public static final String OWL_FILE = OWL_PATH + "/pronto.owl";
	protected static Ontology ontology;
	private static final String ontoPrefix = "https://w3id.org/ontology/pronto#";

	private static final String taskexns = "http://www.ontologydesignpatterns.org/cp/owl/taskexecution.owl";
	private static final String taskexnsseparator = "#";

	public PrOntoParser() {
		URL url = PrOntoParser.class.getResource(OWL_FILE);
		ontology = new Ontology(url, ontoPrefix);
	}

	public PrOntoParser(boolean local) {
		URL url = null;
		if (local)
			try {
				url = new File(System.getProperty("user.dir") + OWL_FILE).toURI().toURL();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		else
			url = PrOntoParser.class.getResource(OWL_FILE);
		ontology = new Ontology(url, "https://w3id.org/ontology/pronto#");
	}

	public String[] getMissingPredicates(String[] predicates) {
		Vector<String> missVec = new Vector<String>();
		for (String p : predicates) {
			if (ontology.getClassByFullLabel(p, ontoPrefix) == null
					&& ontology.getObjectPropertyByFullLabel(p, ontoPrefix) == null
					&& ontology.getDataPropertyByFullLabel(p, ontoPrefix) == null)
				missVec.add(p);
		}
		String[] missing = new String[missVec.size()];
		return missVec.toArray(missing);
	}

	public String[] getActions() {
		OWLClass actionClass = ontology.getClassByFullLabel("Action", taskexns + taskexnsseparator);
		Set<OWLClass> subClasses = ontology.getDirectSubClasses(actionClass);
		return ontology.getLabels(subClasses);
	}

	public static void main(String[] args) {
		PrOntoParser p = new PrOntoParser(true);
		String[] actions = p.getActions();
		for (String a : actions)
			System.out.println(a);
	}

}
