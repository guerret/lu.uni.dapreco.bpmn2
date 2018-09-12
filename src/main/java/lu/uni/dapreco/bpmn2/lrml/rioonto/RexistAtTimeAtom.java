package lu.uni.dapreco.bpmn2.lrml.rioonto;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.bpmn2.XPathParser;
import lu.uni.dapreco.bpmn2.lrml.Atom;
import lu.uni.dapreco.bpmn2.lrml.RuleMLBlock;
import lu.uni.dapreco.bpmn2.lrml.Side;
import lu.uni.dapreco.bpmn2.lrml.Side.SideType;

public class RexistAtTimeAtom extends GenericRioOntoAtom {

	public RexistAtTimeAtom(Element node, String pred, Side s, XPathParser xpath) {
		super(node, pred, s, xpath);
	}

	@Override
	public List<RuleMLBlock> getArgumentsToTranslate() {
		// only first argument, the second is the time
		return getArguments().subList(0, 1);
	}

	@Override
	public List<String> translate() {
		Atom bearer = owner.getOwnerRule().getBearer();
		if (owner.getPosition() == SideType.THEN && bearer != null)
			return bearer.translate();
		List<String> ret = new ArrayList<String>();
		List<RuleMLBlock> arguments = getArgumentsToTranslate();
		String list = "<ul>";
		ret.add(toString() + list);
		List<Atom> definitionAtoms = getDefinitionAtoms(arguments);
		for (Atom d : definitionAtoms)
			if (d.getArguments().size() > 1) {
				ret.add("<li>");
				ret.addAll(d.translate());
				ret.add("</li>");
			}
		ret.add("</ul>");
		return ret;
	}

	@Override
	public String toString() {
		if (owner.getPosition() == SideType.IF)
			return "<span>At time " + children.get(1).getName() + ", the following situation exists:</span>";
		else {
			// First check if I found a bearer
			Atom bearer = owner.getOwnerRule().getBearer();
			if (bearer != null) {
				return bearer.toString();
			} else {
				SideType whereDefined = owner.getOwnerRule().whereDefined(children.get(0).getName());
				if (whereDefined == SideType.THEN)
					return "<span>At time " + children.get(1).getName() + ", the following situation exists:</span>";
				else if (whereDefined == SideType.IF)
					return "<span>At time " + children.get(1).getName() + ", the above situation exists</span>";
				else
					return "<span>At time " + children.get(1).getName() + ", undetermined situation "
							+ children.get(0).getName() + " exists</span>";
			}
		}
	}

	@Override
	public List<RuleMLBlock> getTranslated() {
		List<RuleMLBlock> ret = new ArrayList<RuleMLBlock>();
		List<RuleMLBlock> arguments = getArgumentsToTranslate();
		ret.add(this);
		Atom bearer = owner.getOwnerRule().getBearer();
		if (owner.getPosition() != SideType.THEN || bearer == null || bearer != null) {
			List<Atom> definitionAtoms = getDefinitionAtoms(arguments);
			for (Atom d : definitionAtoms)
				ret.addAll(d.getTranslated());
		}
		return ret;
	}

}
