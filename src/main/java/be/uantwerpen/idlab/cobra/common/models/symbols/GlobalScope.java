package be.uantwerpen.idlab.cobra.common.models.symbols;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class GlobalScope extends Scope
{
    public GlobalScope()
    {
        super(-1L, -1L, null);
    }

    public GlobalScope(List<Symbol> symbols)
    {
        super(-1L, -1L, null, symbols);
    }

    public String toString(int level)
    {
        StringBuilder resultBuilder = new StringBuilder();

        for(int i = 0; i < level; i++)
        {
            resultBuilder.append('\t');
        }

        resultBuilder.append("Global scope");
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

    public Element toXMLNode(Document doc, Element parent)
    {
        Element scopeElement = doc.createElement("globalscope");

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
        if(other instanceof GlobalScope)
        {
            GlobalScope otherScope = (GlobalScope)other;

            if((this.parent != null) && (otherScope.parent != null))
            {
                return (this.blockId.equals(otherScope.blockId)) && (this.parent.equals(otherScope.parent));
            }
            else
            {
                return (this.blockId.equals(otherScope.blockId));
            }
        }
        else
        {
            return false;
        }
    }
}
