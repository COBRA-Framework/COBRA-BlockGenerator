package be.uantwerpen.idlab.cobra.common.models.symbols;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class StatementScope extends Scope
{
    private String name;

    public StatementScope(Long blockId, String name, Scope parent)
    {
        super(blockId, parent);
        this.name = name;
    }

    public StatementScope(Long blockId, String name, Scope parent, List<Symbol> symbols)
    {
        super(blockId, parent, symbols);
        this.name = name;
    }

    public String toString(int level)
    {
        StringBuilder resultBuilder = new StringBuilder();

        for(int i = 0; i < level; i++)
        {
            resultBuilder.append('\t');
        }

        resultBuilder.append(this.name);
        resultBuilder.append(" - id: " + this.blockId);
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

        // Recursive Symbol Table
        for(Scope childScope : this.children)
        {
            resultBuilder.append(childScope.toString(level + 1));
        }

        for(int i = 0; i < (level + 1); i++)
        {
            resultBuilder.append('\t');
        }

        resultBuilder.append("}\n");

        return resultBuilder.toString();
    }

    public String getName()
    {
        return this.name;
    }

    public Element toXMLNode(Document doc, Element parent)
    {
        Element scopeElement = doc.createElement("statementscope");
        scopeElement.setAttribute("name", this.name);
        scopeElement.setAttribute("block_id", this.blockId.toString());

        for(Symbol symbol : this.symbols)
        {
            scopeElement.appendChild(symbol.toXMLNode(doc, scopeElement));
        }

        // Recursive Symbol Table
        for(Scope childScope : this.children)
        {
            scopeElement.appendChild(childScope.toXMLNode(doc, scopeElement));
        }

        if(parent != null)
        {
            parent.appendChild(scopeElement);
        }

        return scopeElement;
    }

    @Override
    public boolean equals(Object other)
    {
        if(other instanceof StatementScope)
        {
            StatementScope otherScope = (StatementScope)other;

            if((this.parent != null) && (otherScope.parent != null))
            {
                return (this.blockId.equals(otherScope.blockId)) && (this.name.equals(otherScope.name)) && (this.parent.equals(otherScope.parent));
            }
            else
            {
                return (this.blockId.equals(otherScope.blockId)) && (this.name.equals(otherScope.name));
            }
        }
        else
        {
            return false;
        }
    }
}
