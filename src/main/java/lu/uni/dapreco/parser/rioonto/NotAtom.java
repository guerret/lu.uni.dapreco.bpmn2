package lu.uni.dapreco.parser.rioonto;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.parser.XPathParser;
import lu.uni.dapreco.parser.lrml.RuleMLBlock;
import lu.uni.dapreco.parser.lrml.Side;
import lu.uni.dapreco.parser.lrml.Variable;

public class NotAtom extends BooleanAtom {

	public NotAtom(Element node, String pred, Side s, XPathParser xpath) {
		super(node, pred, s, xpath);
	}

	@Override
	public List<String> translate() {
		List<String> ret = new ArrayList<String>();
		List<RuleMLBlock> arguments = getArgumentsToTranslate();
		ret.add(((Variable) arguments.get(0)).writeAsNegation(getName()));
		return ret;
	}

	@Override
	public String toString() {
		return "";
	}

}
