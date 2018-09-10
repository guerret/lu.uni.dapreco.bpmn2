package lu.uni.dapreco.bpmn2.lrml.rioonto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.bpmn2.XPathParser;
import lu.uni.dapreco.bpmn2.lrml.Atom;
import lu.uni.dapreco.bpmn2.lrml.RuleMLBlock;
import lu.uni.dapreco.bpmn2.lrml.Side;
import lu.uni.dapreco.bpmn2.lrml.Variable;
import lu.uni.dapreco.bpmn2.lrml.Side.SideType;

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
		if (getLocalPredicate().equals("Obliged")) {
			// If child is a rioOnto:not, then it is rather a Prohibited
			List<Atom> atoms = ((Variable) arguments.get(0)).getDefinitionAtoms(owner);
			atoms.remove(this);
			if (atoms.isEmpty() && owner.getPosition() == SideType.THEN)
				atoms = ((Variable) arguments.get(0)).getDefinitionAtoms(owner.getOwnerRule().getLHS());
			if (!atoms.isEmpty() && atoms.get(0).getClass() == NotAtom.class) {
				predicateIRI = "rioOnto:Prohibited";
				// But then I must not translate the not, but its child (not negated)
				arguments.set(0, atoms.get(0).getArguments().get(1));
				setArgument(0, atoms.get(0).getArguments().get(1));
			}
		}
		ret.add(toString() + "<br />");
		return ret;
	}

	@Override
	public String toString() {
		String pred = getLocalPredicate();
		List<RuleMLBlock> arguments = getArguments();
		String object = arguments.get(0).toString();
		return "At time " + arguments.get(1).getName() + ", " + arguments.get(2) + " is " + pred + " to " + object
				+ "</span>";
	}

	@Override
	public List<RuleMLBlock> getTranslated() {
		List<RuleMLBlock> ret = new ArrayList<RuleMLBlock>();
		ret.add(this);
		return ret;
	}

}
