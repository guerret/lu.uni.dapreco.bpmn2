package lu.uni.dapreco.parser.lrml.rioonto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.parser.XPathParser;
import lu.uni.dapreco.parser.lrml.Atom;
import lu.uni.dapreco.parser.lrml.RuleMLBlock;
import lu.uni.dapreco.parser.lrml.Side;
import lu.uni.dapreco.parser.lrml.Variable;

public class DeonticAtom extends GenericRioOntoAtom {

	public DeonticAtom(Element node, String pred, Side s, XPathParser xpath) {
		super(node, pred, s, xpath);
		owner.getOwnerRule().setBearer(this);
	}

	@Override
	public List<RuleMLBlock> getArgumentsToTranslate() {
		List<RuleMLBlock> arguments = getArguments();
		// second argument is time
		return Arrays.asList(arguments.get(0), arguments.get(2));
	}

	@Override
	public List<String> translate() {
		List<String> ret = new ArrayList<String>();
		List<RuleMLBlock> arguments = getArgumentsToTranslate();
		BooleanAtom booleanAtom = ((Variable) arguments.get(0)).getBooleanAtom();
		// If child is a rioOnto:not, then it is rather a Prohibited
		if (booleanAtom instanceof NotAtom) {
			if (getLocalPredicate().equals("Obliged")) {
				predicateIRI = "rioOnto:Prohibited";
				// But then I must not translate the not, but its child (not negated)
				RuleMLBlock newArg = booleanAtom.getArguments().get(0);
				// arguments.set(0, newArg);
				setArgument(0, newArg);
				ret.add(toString());
			} else if (getLocalPredicate().equals("Permitted")) {
				// But then I must not translate the not, but its child (negated)
				Variable v = (Variable) arguments.get(0);
				RuleMLBlock newArg = booleanAtom.getArguments().get(0);
				setArgument(0, newArg);
				ret.add("At time " + getArguments().get(1).getName() + ", " + getArguments().get(2) + " is "
						+ getLocalPredicate() + " that " + "</span>"
						+ ((Variable) newArg).writeAsNegation(v.getName()));
			}
		} else if (booleanAtom instanceof ConjunctionAtom) { // ConjunctionAtom
			Atom atom = getDefinitionAtoms(arguments.get(0), this).get(0);
			List<String> translation = atom.translate();
			translation.set(0, "At time " + getArguments().get(1).getName() + ", " + getArguments().get(2) + " is "
					+ getLocalPredicate() + " to </span>" + translation.get(0));
			ret.addAll(translation);
		} else
			ret.add(toString());
		return ret;
	}

	@Override
	public String toString() {
		String pred = getLocalPredicate();
		List<RuleMLBlock> arguments = getArguments();
		Variable v = (Variable) arguments.get(0);
		String object = v.toString();
		return "At time " + arguments.get(1).getName() + ", " + arguments.get(2) + " is " + pred + " to " + object
				+ "</span><br />";
	}

	@Override
	public List<RuleMLBlock> getTranslated() {
		List<RuleMLBlock> ret = new ArrayList<RuleMLBlock>();
		ret.add(this);
		return ret;
	}

}
