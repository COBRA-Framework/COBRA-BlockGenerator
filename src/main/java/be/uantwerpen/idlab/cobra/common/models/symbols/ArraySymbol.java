package be.uantwerpen.idlab.cobra.common.models.symbols;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ArraySymbol extends VariableSymbol implements Symbol
{
	protected int size;

	public ArraySymbol(long id, String type, String identifier, int size)
	{
		super(id, type, identifier);

		this.size = size;
	}

	public int getSize()
	{
		return this.size;
	}

	public String toString()
	{
		return this.type + " " + this.identifier + "[" + this.size + "]";
	}

	public Element toXMLNode(Document doc, Element parent)
	{
		Element symbolNode = doc.createElement("array");
		symbolNode.setAttribute("id", Long.toString(this.id));
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
