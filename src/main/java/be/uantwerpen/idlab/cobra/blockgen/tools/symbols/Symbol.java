package be.uantwerpen.idlab.cobra.blockgen.tools.symbols;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface Symbol
{
	Element toXMLNode(Document doc, Element parent);
}
