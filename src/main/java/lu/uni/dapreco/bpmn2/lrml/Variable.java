package lu.uni.dapreco.bpmn2.lrml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.bpmn2.XPathParser;
import lu.uni.dapreco.bpmn2.lrml.Side.SideType;

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
		return "<strong>" + getPredicate() + "</strong> (" + name + ")";
	}

	public String getPredicate() {
		if (predicate == null) {
			List<Atom> atoms = getDefinitionAtoms(owner);
			if (atoms.isEmpty() && owner.getPosition() == SideType.THEN)
				atoms = getDefinitionAtoms(owner.getOwnerRule().getLHS());
			if (!atoms.isEmpty())
				predicate = atoms.get(0).getLocalPredicate();
			else
				predicate = "Thing";
		}
		return predicate;
	}

	public List<Atom> getDefinitionAtoms(Side side) {
		List<Atom> ret = new ArrayList<Atom>();
		for (Atom a : side.getVariableUses(name))
			if (a != parent && a.children.size() > 0 && a.children.get(0).type == RuleMLType.VAR
					&& a.children.get(0).getName().equals(name))
				ret.add(a);
		return ret;
	}

}
