package be.uantwerpen.idlab.cobra.common.models.symbols;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface Symbol
{
	long getId();
	Element toXMLNode(Document doc, Element parent);
}
