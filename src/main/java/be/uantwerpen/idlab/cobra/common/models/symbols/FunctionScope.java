package be.uantwerpen.idlab.cobra.common.models.symbols;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class FunctionScope extends Scope
{
    private String name;
    private String returnType;
    private List<VariableSymbol> parameters;

    public FunctionScope(Long id, Long blockId, String name, String returnType, Scope parent)
    {
        super(id, blockId, parent);
        this.name = name;
        this.returnType = returnType;
        this.parameters = new ArrayList<VariableSymbol>();
    }

    public FunctionScope addParameter(VariableSymbol parameter)
    {
        this.parameters.add(parameter);

        return this;
    }

    public FunctionScope addParameters(List<VariableSymbol> parameters)
    {
        this.parameters.addAll(parameters);

        return this;
    }

    public String toString(int level)
    {
        StringBuilder resultBuilder = new StringBuilder();

        for(int i = 0; i < level; i++)
        {
            resultBuilder.append('\t');
        }

        resultBuilder.append(this.returnType);
        resultBuilder.append(' ');
        resultBuilder.append(this.name);
        resultBuilder.append('(');

        for(int i = 0; i < this.parameters.size(); i++)
        {
            resultBuilder.append(this.parameters.get(i).toString());

            if(this.parameters.size() != (i + 1))
            {
                resultBuilder.append(", ");
            }
        }

        resultBuilder.append(")\n");

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

    public String getReturnType()
    {
        return this.returnType;
    }

    public List<VariableSymbol> getParameters()
    {
        return this.parameters;
    }

    public Element toXMLNode(Document doc, Element parent)
    {
        Element scopeElement = doc.createElement("functionscope");
        scopeElement.setAttribute("id", Long.toString(this.id));
        scopeElement.setAttribute("return_type", this.returnType);
        scopeElement.setAttribute("name", this.name);
        scopeElement.setAttribute("block_id", this.blockId.toString());

        Element parametersNode = doc.createElement("parameters");
        for(VariableSymbol parameter : this.parameters)
        {
            parameter.toXMLNode(doc, parametersNode);
        }

        if(this.parameters.size() != 0)
        {
            scopeElement.appendChild(parametersNode);
        }

        for(Symbol symbol : this.symbols)
        {
            scopeElement.appendChild(symbol.toXMLNode(doc, scopeElement));
        }

        //Recursive Symbol Table
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
        if(other instanceof FunctionScope)
        {
            FunctionScope otherScope = (FunctionScope)other;

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
