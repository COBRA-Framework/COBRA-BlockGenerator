package be.uantwerpen.idlab.cobra.common.models.symbols;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.*;

public class SymbolTable
{
	private List<Scope> scopes;

	public SymbolTable()
	{
		this.scopes = new LinkedList<Scope>();
	}

	public SymbolTable insertScope(Scope scope)
	{
		this.scopes.add(scope);
		return this;
	}

	public List<Scope> getList()
	{
		return this.scopes;
	}

	public Iterator<Scope> iterator()
	{
		return this.scopes.iterator();
	}

	public void insert(Scope scope, Symbol symbol)
	{
		if(this.scopes.indexOf(scope) == -1)
		{
			this.scopes.add(scope);
		}

		this.scopes.get(this.scopes.indexOf(scope)).insertSymbol(symbol);
	}

	public boolean contains(Scope scope)
	{
		return this.scopes.contains(scope);
	}

	public String toString()
	{
		StringBuilder resultBuilder = new StringBuilder();

		for(Scope scope : this.scopes)
		{
			resultBuilder.append(scope.toString());
		}

		return resultBuilder.toString();
	}

	public Element toXMLNode(Document doc, Element parent)
	{
		Element symbolTableElement = doc.createElement("table");

		for(Scope scope : this.scopes)
		{
			symbolTableElement.appendChild(scope.toXMLNode(doc, symbolTableElement));
		}

		NodeList nodes = symbolTableElement.getChildNodes();

		if(parent != null)
		{
			parent.appendChild(symbolTableElement);
		}

		return symbolTableElement;
	}
}