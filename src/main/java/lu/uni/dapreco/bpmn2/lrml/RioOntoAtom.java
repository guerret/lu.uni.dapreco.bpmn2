package lu.uni.dapreco.bpmn2.lrml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.bpmn2.XPathParser;
import lu.uni.dapreco.bpmn2.lrml.Side.SideType;

public class RioOntoAtom extends Atom {

	public RioOntoAtom(Element node, String pred, Side s, XPathParser xpath) {
		super(node, pred, s, xpath);
	}

	@Override
	public List<RuleMLBlock> getNewArgumentsToTranslate() {
		List<RuleMLBlock> arguments = getArguments();
		switch (predicateIRI) {
		case "rioOnto:RexistAtTime":
			// only first argument, the second is the time
			return arguments.subList(0, 1);
		case "rioOnto:Permitted":
		case "rioOnto:Obliged":
			// second argument is time
			return Arrays.asList(arguments.get(0), arguments.get(2));
		case "rioOnto:and":
		case "rioOnto:or":
		case "rioOnto:cause":
		case "rioOnto:imply":
		case "rioOnto:partOf":
		case "rioOnto:not":
			// first argument is reification (although not reified), the rest are useful
			return arguments.subList(1, arguments.size());
		default:
			return arguments;
		}
	}

	@Override
	public List<String> translate() {
		List<String> ret = new ArrayList<String>();
		List<RuleMLBlock> arguments = getNewArgumentsToTranslate();
		String list = "";
		String translation = toString();
		switch (getLocalPredicate()) {
		case "RexistAtTime":
		case "not":
			list = "<ul>";
			translation += list;
			break;
		case "Permitted":
		case "Obliged":
			list = "<ol>";
			translation += list;
			break;
		case "and":
		case "or":
			if (arguments.size() == 1)
				translation = "";
			else {
				list = "<ol>";
				translation += list;
			}
			break;
		case "cause":
		case "imply":
		case "partOf":
			translation += "<br />";
			break;
		default:
			translation += "<br />";
		}
		ret.add(translation);
		List<Atom> definitionAtoms = getNewDefinitionAtoms(arguments);
		for (Atom d : definitionAtoms)
			if (d.getArguments().size() > 1) {
				if (!list.isEmpty())
					ret.add("<li>");
				ret.addAll(d.translate());
				if (!list.isEmpty())
					ret.add("</li>");
			}
		if (translation.contains("<ol>"))
			ret.add("</ol>");
		if (translation.contains("<ul>"))
			ret.add("</ul>");
		return ret;
	}

	@Override
	public String toString() {
		String pred = getLocalPredicate();
		switch (pred) {
		case "RexistAtTime":
			if (owner.getPosition() == SideType.IF)
				return "<span>At time " + children.get(1).getName() + ", the following situation exists:</span>";
			else {
				SideType whereDefined = owner.getOwnerRule().whereDefined(children.get(0).getName());
				if (whereDefined == SideType.THEN)
					return "<span>At time " + children.get(1).getName() + ", the following situation exists:</span>";
				else if (whereDefined == SideType.IF)
					return "<span>At time " + children.get(1).getName() + ", the above situation exists</span>";
				else
					return "<span>At time " + children.get(1).getName() + ", undetermined situation "
							+ children.get(0).getName() + " exists</span>";
			}
		case "Permitted":
		case "Obliged":
			return "At time " + children.get(1).getName() + ", " + children.get(2) + " is " + pred + " to "
					+ children.get(0) + "</span>";
		case "and":
			return "<span>(All of the following (" + children.get(0).getName() + "))</span>";
		case "or":
			return "<span>(At least one of the following (" + children.get(0).getName() + "))</span>";
		case "not":
			return "<span>The next line does not apply  (" + children.get(0).getName() + ")</span>";
		case "cause":
			return "<span>The fact " + children.get(0) + " is the cause of the fact " + children.get(1) + "</span>";
		case "imply":
			return "<span>" + children.get(1) + " implies " + children.get(2) + "</span>";
		case "partOf":
			return "<span>" + children.get(0) + " is part of " + children.get(1) + "</span>";
		default:
			if (pred.startsWith("exception"))
				return "<span>Exception (" + pred + ")</span>";
			return "UNKNOWN RIOONTO PREDICATE: " + predicateIRI;
		}
	}

}
