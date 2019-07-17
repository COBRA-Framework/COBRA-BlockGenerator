package be.uantwerpen.idlab.cobra.common.models.symbols;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class SymbolTable
{
	private HashMap<Long, Scope> scopes;
	private Scope globalScope;

	public SymbolTable(Scope globalScope)
	{
		this.globalScope = globalScope;
		this.scopes = new HashMap<Long, Scope>();
		insertScopesRecursively(globalScope);
	}

	public SymbolTable insertScope(Scope scope)
	{
		insertScopesRecursively(scope);
		return this;
	}

	public Collection<Scope> getScopes()
	{
		return this.scopes.values();
	}

	public Iterator<Scope> iterator()
	{
		return this.scopes.values().iterator();
	}

	public Scope getScopeById(Long scopeId)
	{
		return this.scopes.get(scopeId);
	}

	public void insert(Scope scope, Symbol symbol)
	{
		if(this.scopes.get(scope.getBlockId()) == null)
		{
			this.scopes.put(scope.getBlockId(), scope);
		}

		this.scopes.get(scope.getBlockId()).insertSymbol(symbol);
	}

	public boolean contains(Scope scope)
	{
		return this.scopes.values().contains(scope);
	}

	public String toString()
	{
		StringBuilder resultBuilder = new StringBuilder();

		resultBuilder.append(this.globalScope.toString());

		return resultBuilder.toString();
	}

	public Element toXMLNode(Document doc, Element parent)
	{
		Element symbolTableElement = doc.createElement("table");

		symbolTableElement.appendChild(this.globalScope.toXMLNode(doc, symbolTableElement));

		if(parent != null)
		{
			parent.appendChild(symbolTableElement);
		}

		return symbolTableElement;
	}

	private void insertScopesRecursively(Scope scope)
	{
		this.scopes.put(scope.getBlockId(), scope);

		for(Scope childScope : scope.getChildren())
		{
			insertScopesRecursively(childScope);
		}
	}
}