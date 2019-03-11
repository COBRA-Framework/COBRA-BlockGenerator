package be.uantwerpen.idlab.cobra.blockgen.tools.symbols;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class FunctionSymbol implements Symbol
{
	private String returnType;
	private String name;
	private ArrayList<ParameterSymbol> parameters;

	public FunctionSymbol(String returnType, String name, List<ParameterSymbol> parameters)
	{
		this.returnType = returnType;
		this.name = name;
		this.parameters = new ArrayList<>(parameters);
	}

	@Override
	public Element toXMLNode(Document doc, Element parent)
	{
		Element symbolNode = doc.createElement("function");
		symbolNode.setAttribute("return_type", this.returnType);
		symbolNode.setAttribute("name", this.name);

		for(ParameterSymbol parameter : this.parameters)
		{
			parameter.toXMLNode(doc, symbolNode);
		}

		if(parent != null)
		{
			parent.appendChild(symbolNode);
		}

		return symbolNode;
	}

	@Override
	public String toString()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.returnType);
		stringBuilder.append(' ');
		stringBuilder.append(this.name);
		stringBuilder.append('(');

		for(int i = 0; i < this.parameters.size(); i++)
		{
			stringBuilder.append(this.parameters.get(i).toString());

			if(this.parameters.size() != (i + 1))
			{
				stringBuilder.append(", ");
			}
		}

		stringBuilder.append(')');

		return stringBuilder.toString();
	}
}
