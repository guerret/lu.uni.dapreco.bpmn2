package lu.uni.dapreco.bpmn2.lrml.rioonto;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.bpmn2.XPathParser;
import lu.uni.dapreco.bpmn2.lrml.RuleMLBlock;
import lu.uni.dapreco.bpmn2.lrml.Side;
import lu.uni.dapreco.bpmn2.lrml.Variable;

public class NotAtom extends GenericRioOntoAtom {

	public NotAtom(Element node, String pred, Side s, XPathParser xpath) {
		super(node, pred, s, xpath);
	}

	@Override
	public List<RuleMLBlock> getArgumentsToTranslate() {
		List<RuleMLBlock> arguments = getArguments();
		// first argument is reification (although not reified), the rest are useful
		return arguments.subList(1, arguments.size());
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
