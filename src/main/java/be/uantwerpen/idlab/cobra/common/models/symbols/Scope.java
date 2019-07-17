package be.uantwerpen.idlab.cobra.common.models.symbols;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class Scope
{
	protected Scope parent;
	protected List<Scope> children;
	protected Long blockId;
	protected List<Symbol> symbols;

	public Scope(Long blockId, Scope parent)
	{
		this.setParentScope(parent);
		this.children = new ArrayList<Scope>();
		this.blockId = blockId;
		this.symbols = new LinkedList<Symbol>();
	}

	public Scope(Long blockId, Scope parent, List<Symbol> symbols)
	{
		this.setParentScope(parent);
		this.children = new ArrayList<Scope>();
		this.blockId = blockId;
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

	public List<Scope> getChildren()
	{
		return this.children;
	}

	public List<Symbol> getList()
	{
		return this.symbols;
	}

	public Iterator<Symbol> iterator()
	{
		return this.symbols.iterator();
	}

	public abstract String toString(int level);

	public Long getBlockId()
	{
		return this.blockId;
	}

	public String toString()
	{
		return this.toString(0);
	}

	public abstract Element toXMLNode(Document doc, Element parent);

	protected void addChild(Scope scope)
	{
		this.children.add(scope);
	}

	private void setParentScope(Scope parentScope)
	{
		this.parent = parentScope;

		if(parentScope != null)
		{
			parentScope.addChild(this);
		}
	}
}
