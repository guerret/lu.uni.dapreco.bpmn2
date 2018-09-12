package lu.uni.dapreco.bpmn2.lrml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.bpmn2.XPathParser;
import lu.uni.dapreco.bpmn2.lrml.Side.SideType;
import lu.uni.dapreco.bpmn2.lrml.rioonto.BooleanAtom;
import lu.uni.dapreco.bpmn2.lrml.rioonto.GenericRioOntoAtom;
import lu.uni.dapreco.bpmn2.lrml.rioonto.NotAtom;

public class Variable extends RuleMLBlock {

	private String name;
	private String predicate;
	private RuleMLBlock parent;

	public Variable(Element node, RuleMLBlock p, XPathParser xpath) {
		super(node, p.owner, xpath);
		parent = p;
		if (root.hasAttribute("key"))
			name = root.getAttribute("key");
		else
			name = root.getAttribute("keyref");
		predicate = null;
	}

	@Override
	public String getName() {
		return name;
	}

	public String toString() {
		return getPredicate() + " (" + name + ")";
	}

	public String getPredicate() {
		if (predicate == null) {
			List<Atom> atoms = getDefinitionAtoms(owner);
			if (atoms.isEmpty() && owner.getPosition() == SideType.THEN)
				atoms = getDefinitionAtoms(owner.getOwnerRule().getLHS());
			if (!atoms.isEmpty()) {
				Atom atom = atoms.get(0);
				if (atom instanceof GenericRioOntoAtom)
					predicate = "The <strong>" + atom.getLocalPredicate() + "</strong> situation denoted by";
				else
					predicate = "<strong>" + atom.getLocalPredicate() + "</strong>";
			} else
				predicate = "<strong>Thing</strong>";
		}
		return predicate;
	}

	public List<Atom> getDefinitionAtoms(Side side) {
		List<Atom> ret = new ArrayList<Atom>();
		for (Atom a : side.getVariableUses(name))
			if (a != parent) // Must not be the atom I'm in
				if (a.reified) {
					if (a.getName().equals(name))
						ret.add(a);
				} else {
					List<RuleMLBlock> arguments = a.getArguments();
					if (!a.isExclusion() && arguments.size() > 0 && arguments.get(0).type == RuleMLType.VAR
							&& arguments.get(0).getName().equals(name))
						ret.add(a);
				}
		return ret;
	}

	// public List<Atom> getDefinitionAtoms2(Side side) {
	// List<Atom> ret = new ArrayList<Atom>();
	// for (Atom a : side.getVariableUses(name))
	// if (a != parent // Must not be the atom I'm in
	// && ((a.reified && a.getName().equals(name)) || (!a.reified &&
	// a.children.size() > 0
	// && a.children.get(0).type == RuleMLType.VAR &&
	// a.children.get(0).getName().equals(name)))
	// && !a.isExclusion())
	// ret.add(a);
	// return ret;
	// }

	public String writeAsNegation(String negation) {
		List<Atom> atoms = getDefinitionAtoms(owner);
		if (atoms.isEmpty() && owner.getSide() == SideType.THEN)
			atoms = getDefinitionAtoms(owner.getOwnerRule().getLHS());
		if (atoms.isEmpty())
			return null;
		atoms.get(0).setNegation(negation);
		String ret = atoms.get(0).toString();
		atoms.get(0).setNegation(null);
		return ret;
	}

	public BooleanAtom getBooleanAtom() {
		List<Atom> atoms = getDefinitionAtoms(parent.owner);
		if (atoms.isEmpty() && parent.owner.getPosition() == SideType.THEN)
			atoms = getDefinitionAtoms(parent.owner.getOwnerRule().getLHS());
		for (Atom a : atoms) {
			if (a instanceof BooleanAtom)
				return (BooleanAtom) a;
		}
		return null;
	}

	public NotAtom getNotAtom() {
		BooleanAtom atom = getBooleanAtom();
		return atom instanceof NotAtom ? (NotAtom) atom : null;
	}

}
