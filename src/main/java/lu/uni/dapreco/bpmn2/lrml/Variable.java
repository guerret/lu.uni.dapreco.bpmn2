package lu.uni.dapreco.bpmn2.lrml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.bpmn2.XPathParser;
import lu.uni.dapreco.bpmn2.lrml.Side.SideType;
import lu.uni.dapreco.bpmn2.lrml.rioonto.GenericRioOntoAtom;

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
				if (atom.getClass() == GenericRioOntoAtom.class)
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
			if (a != parent && !a.isExclusion() && a.children.size() > 0 && a.children.get(0).type == RuleMLType.VAR
					&& a.children.get(0).getName().equals(name))
				ret.add(a);
		return ret;
	}

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

}
