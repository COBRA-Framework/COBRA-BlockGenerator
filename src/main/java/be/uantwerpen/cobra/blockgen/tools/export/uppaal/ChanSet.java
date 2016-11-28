package be.uantwerpen.cobra.blockgen.tools.export.uppaal;

import java.util.Vector;

public class ChanSet {
	
	public Vector<String> ChanSet;
	
	public ChanSet()
	{
		this.ChanSet = new Vector<String>();
	}
	
	public void addChanSet(String Chan)
	{
		this.ChanSet.add(Chan);
	}
	
	public String getChanSet(int Ind)
	{
		return this.ChanSet.get(Ind);
	}
}
