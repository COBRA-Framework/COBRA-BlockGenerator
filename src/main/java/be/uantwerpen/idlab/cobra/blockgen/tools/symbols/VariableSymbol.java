package be.uantwerpen.idlab.cobra.blockgen.tools.symbols;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class VariableSymbol implements Symbol
{
	private String type;
	private String identifier;

	public VariableSymbol(String type, String identifier)
	{
		this.type = type;
		this.identifier = identifier;
	}

	public String toString()
	{
		return this.type + " " + this.identifier;
	}

	public Element toXMLNode(Document doc, Element parent)
	{
		Element symbolNode = doc.createElement("variable");
		symbolNode.setAttribute("type", this.type);
		symbolNode.setAttribute("id", this.identifier);

		if(parent != null)
		{
			parent.appendChild(symbolNode);
		}

		return symbolNode;
	}
}
