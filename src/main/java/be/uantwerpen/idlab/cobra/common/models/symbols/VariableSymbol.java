package be.uantwerpen.idlab.cobra.common.models.symbols;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Objects;

public class VariableSymbol implements Symbol
{
	protected long id;
	protected String type;
	protected String identifier;

	public VariableSymbol(long id, String type, String identifier)
	{
		this.id = id;
		this.type = type;
		this.identifier = identifier;
	}

	public long getId()
	{
		return this.id;
	}

	public String getType()
	{
		return this.type;
	}

	public String getIdentifier()
	{
		return this.identifier;
	}

	public String toString()
	{
		return this.type + " " + this.identifier;
	}

	public Element toXMLNode(Document doc, Element parent)
	{
		Element symbolNode = doc.createElement("variable");
		symbolNode.setAttribute("id", Long.toString(this.id));
		symbolNode.setAttribute("type", this.type);
		symbolNode.setAttribute("name", this.identifier);

		if(parent != null)
		{
			parent.appendChild(symbolNode);
		}

		return symbolNode;
	}

	@Override
	public boolean equals(Object object)
	{
		if(this == object)
		{
			return true;
		}

		if(object == null)
		{
			return false;
		}

		if(getClass() != object.getClass())
		{
			return false;
		}

		VariableSymbol variableSymbol = (VariableSymbol) object;

		return Objects.equals(id, variableSymbol.getId());
	}
}
