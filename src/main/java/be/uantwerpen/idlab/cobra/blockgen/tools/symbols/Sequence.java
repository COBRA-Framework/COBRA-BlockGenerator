package be.uantwerpen.idlab.cobra.blockgen.tools.symbols;

import java.util.LinkedList;
import java.util.List;

public class Sequence<T>
{
	public static final int END = -1;

	private LinkedList<T> list;

	public Sequence()
	{
		this.list = new LinkedList<T>();
	}

	public Sequence<T> insert(T element)
	{
		this.list.add(element);
		return this;
	}

	public T get(int index)
	{
		return this.list.get(index);
	}

	public int size()
	{
		return this.list.size();
	}

	public Sequence<T> clear()
	{
		this.list.clear();
		return this;
	}

	public int match(List<T> subSequence, int startIndex)
	{
		//Terminal.printTerminalWarning("Subsequence size: " + subSequence.size());
		//Terminal.printTerminalWarning("Start index: " + startIndex);
		//Terminal.printTerminalWarning("Full sequence size: " + this.size());

		for(int i = startIndex; (i + subSequence.size() - 1) < this.size(); i++)
		{
			boolean match = true;
			for(int j = 0; (j < subSequence.size()) && match; j++)
			{
				if(!this.list.get(i + j).equals(subSequence.get(j)))
				{
					match = false;
				}
			}

			//Terminal.printTerminalInfo("Match starting at " + i);

			if(match)
			{
				return i;
			}
		}

		return Sequence.END;
	}

	public int match(List<T> subSequence)
	{
		return this.match(subSequence, 0);
	}

	public int match(Sequence<T> subSequence, int startIndex)
	{
		return this.match(subSequence.list, startIndex);
	}

	public int match(Sequence<T> subSequence)
	{
		return this.match(subSequence, 0);
	}
}
