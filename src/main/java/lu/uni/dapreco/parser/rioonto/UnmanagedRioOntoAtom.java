package lu.uni.dapreco.parser.rioonto;

import java.util.List;

import org.w3c.dom.Element;

import lu.uni.dapreco.parser.XPathParser;
import lu.uni.dapreco.parser.lrml.Side;

public class UnmanagedRioOntoAtom extends GenericRioOntoAtom {

	public UnmanagedRioOntoAtom(Element node, String pred, Side s, XPathParser xpath) {
		super(node, pred, s, xpath);
		// timeOf
		// likely
		// possible
		// atTime
		// relatedTo
		// necessary
		System.err.println("UNMANAGED ATOM FOUND: " + pred);
	}

	@Override
	public List<String> translate() {
		return null;
	}

	@Override
	public String toString() {
		return null;
	}

}
