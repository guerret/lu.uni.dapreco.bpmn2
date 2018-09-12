package lu.uni.dapreco.bpmn2.lrml.rioonto;

import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.bpmn2.XPathParser;
import lu.uni.dapreco.bpmn2.lrml.RuleMLBlock;
import lu.uni.dapreco.bpmn2.lrml.Side;

public abstract class BooleanAtom extends GenericRioOntoAtom {

	public BooleanAtom(Element node, String pred, Side s, XPathParser xpath) {
		super(node, pred, s, xpath);
	}

	@Override
	public List<RuleMLBlock> getArgumentsToTranslate() {
		List<RuleMLBlock> arguments = getArguments();
		// first argument is reification (although not reified), the rest are useful
		return arguments.subList(1, arguments.size());
	}

}
