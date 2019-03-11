package be.uantwerpen.idlab.cobra.common.models.symbols;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Scope
{
	private Scope parent;
	private String name;
	private List<Symbol> symbols;

	public Scope(String name, Scope parent)
	{
		this.parent = parent;
		this.name = name;
		this.symbols = new LinkedList<Symbol>();
	}

	public Scope(String name, Scope parent, List<Symbol> symbols)
	{
		this.parent = parent;
		this.name = name;
		this.symbols = symbols;
	}

	public Scope insertSymbol(Symbol symbol)
	{
		this.symbols.add(symbol);

		return this;
	}

	public Scope getParent()
	{
		return this.parent;
	}

	public List<Symbol> getList()
	{
		return this.symbols;
	}

	public Iterator<Symbol> iterator()
	{
		return this.symbols.iterator();
	}

	public String toString(int level)
	{
		StringBuilder resultBuilder = new StringBuilder();

		for(int i = 0; i < level; i++)
		{
			resultBuilder.append('\t');
		}

		resultBuilder.append(this.name);
		resultBuilder.append('\n');

		for(int i = 0; i < level; i++)
		{
			resultBuilder.append('\t');
		}

		resultBuilder.append("{\n");

		for(Symbol symbol : this.symbols)
		{
			for(int i = 0; i < (level + 1); i++)
			{
				resultBuilder.append('\t');
			}

			resultBuilder.append(symbol.toString());
			resultBuilder.append('\n');
		}

		// Recursive Symbol Table:
		// You can recursively call toString() here, don't forget to call it with 'level + 1' as the argument!

		for(int i = 0; i < (level + 1); i++)
		{
			resultBuilder.append('\t');
		}

		resultBuilder.append("}\n");

		return resultBuilder.toString();
	}

	public String toString()
	{
		StringBuilder resultBuilder = new StringBuilder();

		resultBuilder.append(this.name);
		resultBuilder.append("\n{\n");

		for(Symbol symbol : this.symbols)
		{
			resultBuilder.append(symbol.toString());
			resultBuilder.append('\n');
		}

		// Recursive Symbol Table:
		// You can recursively call toString() here, don't forget to call it with '1' as the argument!

		resultBuilder.append("}\n");

		return resultBuilder.toString();
	}

	public Element toXMLNode(Document doc, Element parent)
	{
		Element scopeElement = doc.createElement("scope");
		scopeElement.setAttribute("name", this.name);

		for(Symbol symbol : this.symbols)
		{
			scopeElement.appendChild(symbol.toXMLNode(doc, scopeElement));
		}

		// Recursive Symbol Table:
		// You can recursively add children here, don't forget to pass 'scopeElement' as parent!

		if(parent != null)
		{
			parent.appendChild(scopeElement);
		}

		return scopeElement;
	}

	@Override
	public boolean equals(Object other)
	{
		if(other instanceof Scope)
		{
			Scope otherScope = (Scope)other;

			if((this.parent != null) && (otherScope.parent != null))
			{
				return (this.name.equals(otherScope.name)) && (this.parent.equals(otherScope.parent));
			}
			else
			{
				return (this.name.equals(otherScope.name));
			}
		}
		else
		{
			return false;
		}
	}
}
