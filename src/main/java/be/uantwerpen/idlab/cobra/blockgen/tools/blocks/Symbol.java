package be.uantwerpen.idlab.cobra.blockgen.tools.blocks;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface Symbol
{
	public Element toXMLNode (Document doc, Element parent);
}
