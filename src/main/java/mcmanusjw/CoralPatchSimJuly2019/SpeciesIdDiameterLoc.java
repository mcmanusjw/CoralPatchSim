package mcmanusjw.CoralPatchSimJuly2019;

import sim.util.Int2D;

final class SpeciesIdDiameterLoc 
{
	private int speciesId;
	private int diameter;
	private Int2D loc;
	
	SpeciesIdDiameterLoc( int speciesId, int diameter, Int2D loc )
	{
		this.speciesId  = speciesId;
		this.diameter = diameter;
		this.loc      = loc;
	}

	@Override
	public String toString()
	{
		return "ID:" +speciesId + " with diameter " + diameter + "cm, at: [ " + loc.x + ", " + loc.y + " ]";
	}
	
	public int getSpeciesId() {
		return speciesId;
	}

	public int getDiameter() {
		return diameter;
	}
	
	public Int2D getLoc() {
		return loc;
	}
}
