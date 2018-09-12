package lu.uni.dapreco.parser.rioonto;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.parser.XPathParser;
import lu.uni.dapreco.parser.lrml.RuleMLBlock;
import lu.uni.dapreco.parser.lrml.Side;
import lu.uni.dapreco.parser.lrml.Side.SideType;

public class ExceptionAtom extends GenericRioOntoAtom {

	public ExceptionAtom(Element node, String pred, Side s, XPathParser xpath) {
		super(node, pred, s, xpath);
	}

	@Override
	public List<RuleMLBlock> getArgumentsToTranslate() {
		return getArguments();
	}

	@Override
	public List<String> translate() {
		List<String> ret = new ArrayList<String>();
		ret.add(toString() + "<br />");
		return ret;
	}

	@Override
	public String toString() {
		String pred = getLocalPredicate();
		RuleMLBlock object = getArguments().get(0);
		if (owner.getPosition() == SideType.IF)
			if (negation == null)
				return "<span>The exception called " + pred + " (see exceptions document) applies to " + object
						+ "</span>";
			else
				return "<span>The exception called " + pred + " (see exceptions document) does not apply to " + object
						+ "</span>";
		return "<span>An exception called " + pred + " applies to " + object + "</span>";
	}

}
