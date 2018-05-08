package be.uantwerpen.idlab.cobra.blockgen.tools.blocks;

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

	public String toString ()
	{
		return this.type + " " + this.identifier + "[" + this.size + "]";
	}

	public Element toXMLNode (Document doc, Element parent)
	{
		Element symbolNode = doc.createElement("Array");
		symbolNode.setAttribute("type", this.type);
		symbolNode.setAttribute("id", this.identifier);
		symbolNode.setAttribute("size", Integer.toString(this.size));

		if (parent != null)
		{
			parent.appendChild(symbolNode);
		}

		return symbolNode;
	}
}
