package be.uantwerpen.idlab.cobra.blockgen.tools.symbols;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ArraySymbol implements Symbol
{
	private String type;
	private String identifier;
	private int size;

	public ArraySymbol(String type, String identifier, int size)
	{
		this.type = type;
		this.identifier = identifier;
		this.size = size;
	}

	public String toString()
	{
		return this.type + " " + this.identifier + "[" + this.size + "]";
	}

	public Element toXMLNode(Document doc, Element parent)
	{
		Element symbolNode = doc.createElement("array");
		symbolNode.setAttribute("type", this.type);
		symbolNode.setAttribute("name", this.identifier);

		if(this.size > 0)
		{
			symbolNode.setAttribute("size", Integer.toString(this.size));
		}

		if(parent != null)
		{
			parent.appendChild(symbolNode);
		}

		return symbolNode;
	}
}