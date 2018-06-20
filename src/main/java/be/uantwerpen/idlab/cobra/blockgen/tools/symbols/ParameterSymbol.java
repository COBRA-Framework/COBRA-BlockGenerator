package be.uantwerpen.idlab.cobra.blockgen.tools.symbols;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ParameterSymbol implements Symbol
{
	private String type;
	private String identifier;

	public ParameterSymbol(String type, String identifier)
	{
		this.type = type;
		this.identifier = identifier;
	}

	public String toString()
	{
		return this.type + " " + this.identifier;
	}

	@Override
	public Element toXMLNode(Document doc, Element parent)
	{
		Element symbolNode = doc.createElement("parameter");
		symbolNode.setAttribute("type", this.type);
		symbolNode.setAttribute("id", this.identifier);

		if(parent != null)
		{
			parent.appendChild(symbolNode);
		}

		return symbolNode;
	}
}
