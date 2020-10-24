package mcmanusjw.CoralPatchSimJuly2019;

import java.util.Objects;

import sim.engine.SimState;
import sim.util.Double2D;
import sim.util.Int2D;

class SubStepCoralCell implements Comparable < SubStepCoralCell >
{
	//Properties of this cell
	private final long  coralCellId; // actually this could just as well be int because they go into indexed ArrayList which cannot exceed Integer.MAX
	private final int   speciesId;  // <<<<<<<<<<<<<<<<maybe do not need this
	private final int   battleSkill; 
	private final int   maxRadius;
	private final int   maxRadiusSquared;
	private final Int2D loc;
	
	//Mutable field -- may reassign the current cell to another coral formed from a fragment via assigning it a new coralId 
	private int coralId;
	
	// Will get skeletal density for a particular species when the cell is assigned a species name
	private final double density;

	//Generated("SparkTools")
	private SubStepCoralCell(Builder builder) {
		this.coralCellId = builder.coralCellId;
		this.speciesId = builder.speciesId;
		this.battleSkill = builder.battleSkill;
		this.maxRadius = builder.maxRadius;
		this.loc = builder.loc;
		this.coralId = builder.coralId;
		this.density = builder.density;
		
		maxRadiusSquared = maxRadius * maxRadius;
	}
	
	/**
	 * Tries to grow cell in 8 Moore directions in a torus.
	 * If this battleSkill is higher than that encountered,
	 * the existing cell is replaced by a new one of this colony.
	 * The grid should not be SparseGrid, because we assume only
	 * one coral possible per cell. Note that all coral
	 * colonies exist until all portions have been destroyed.
	 * 
	 * This returns true if growth occurred (not usually an indication of a problem).
	 * 
	 */
	public boolean step(StepCoral stepCoral, Int2D centerLoc, double radius, SimState state)
	{
		boolean growth = false;
		
		Double2D tempDouble   = new Double2D( ( double ) loc.x,       ( double ) loc.y       );
		Double2D centerDouble = new Double2D( ( double ) centerLoc.x, ( double ) centerLoc.y ); // this used below
		
		// We use the continuous space only to borrow its squared toriodal distance calculator tds().
		// This requires inputs as Double2D.
		double distSquared = StaticCoralCell.getContinuousSpace().tds(centerDouble, tempDouble) ;
		
		//Return if this cell is too far away from the center 
		//-- this should be unnecessary as a live cell should be within radius
		double radiusSquared = radius * radius;
		
		// Find toroidal Moore neighbors
		// Loop across neighbors (including oneself for simplicity -- wherein nothing will happen)
		for(int i = 0; i < 9; i++)
		{   
			// Incrementing uses two formulas below for all neighbors and itself.
			int dx = ( (int) ( i / 3 ) - 1 );
			int dy = ( ( i % 3 ) - 1 );
			if ( ( ( dx == dy ) && ( dx == 0 ) ) ) continue; // skip self
			
			// Skip if too far from colony center
			// We use a Continous2D space for this calculation, because it has a toroidal distance method.
			int xTemp   = StaticCoralCell.getCoralCellGrid().stx( loc.x + dx );
			int yTemp   = StaticCoralCell.getCoralCellGrid().sty( loc.y + dy );
			
			tempDouble  = new Double2D( ( double ) xTemp, ( double ) yTemp   );
			distSquared = StaticCoralCell.getContinuousSpace().tds(centerDouble, tempDouble);
			
			// Skip to next neighbor cell if beyond current radius, or if beyond max size of coral.
			if ( ( distSquared > radiusSquared ) || ( distSquared > maxRadiusSquared ) ) continue;
			
			// Get object in that cell if present
			SubStepCoralCell neighborCell = (SubStepCoralCell) StaticCoralCell.getCoralCellGrid().get(xTemp, yTemp);
			
			// If there is an existing coral cell, compare battle skills 
			// and either kill the neighbor cell if weaker and take its place, 
			// or continue to next neighbor
			if ( neighborCell instanceof SubStepCoralCell )
			{
				// Compare battle skills and kill if appropriate
				// Don't make this <=, or the cell will kill one of the others in the same coral (resulting in a slow run)
				if (neighborCell.getBattleSkill() < this.battleSkill)
				{
				    // This kills the coral cell, and coral also if all of that colony's cells are gone 
					StaticCoralCell.killNotFromCoralKill( neighborCell );
				}
				else
				{ 
					continue; // If you cannot beat that cell, skip to new loop item
				}
			}	
			
			// Put new cell of this colony in that space
			Int2D tempLoc = new Int2D( xTemp, yTemp );

			if ( stepCoral == null ) 
			{
				UtilJM.warner ( " StepCoral is null in SubStepCoralCell. Returning. " , true );
				return false;
			}
			
			// This creates a new coral cell and returns it to see if null
			boolean tryNew = StaticCoralCell.newCoralCell( tempLoc, stepCoral ); 
			
			if ( !tryNew ) return growth; // Too many cells for simulation to continue 
										  // so return growth still as false as set above.
			growth = true;
			
		} // End neighbors loop

		return growth;
	
	} // End step method
	
	@Override
	public String toString() {
		return "SubStepCoralCell: " + coralCellId;
	}

	@ Override
    public int compareTo( SubStepCoralCell user )
    {
	    return ( this.coralCellId < user.getCoralCellId() ) ? -1: ( this.coralCellId > user.getCoralCellId() ) ? 1:0;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!( o instanceof SubStepCoralCell )) {
            return false;
        }
        SubStepCoralCell user = ( SubStepCoralCell ) o;
        return coralCellId == user.getCoralCellId();
    }

    @Override
    public int hashCode() {
        return Objects.hash( coralCellId );
    }

	// ********************* Start Getters and Setters **********************
 	int getCoralId() {
		return coralId;
	}

	void setCoralId(int coralId) {
		this.coralId = coralId;
	}

	long getCoralCellId() {
		return coralCellId;
	}

	int getSpeciesId() {
		return speciesId;
	}

	int getBattleSkill() {
		return battleSkill;
	}

	int getMaxRadius() {
		return maxRadius;
	}

	Int2D getLoc() {
		return loc;
	}
	

	double getDensity() {
		return density;
	}
	
	

	/**
	 * Creates builder to build {@link SubStepCoralCell}.
	 * @return created builder
	 */
	//Generated("SparkTools")
	public static ICoralCellIdStage builder() {
		return new Builder();
	}

	//Generated("SparkTools")
	public interface ICoralCellIdStage {
		public ISpeciesIdStage withCoralCellId(long coralCellId);
	}

	//Generated("SparkTools")
	public interface ISpeciesIdStage {
		public IBattleSkillStage withSpeciesId(int speciesId);
	}

	//Generated("SparkTools")
	public interface IBattleSkillStage {
		public IMaxRadiusStage withBattleSkill(int battleSkill);
	}

	//Generated("SparkTools")
	public interface IMaxRadiusStage {
		public ILocStage withMaxRadius(int maxRadiusSquared);
	}

	//Generated("SparkTools")
	public interface ILocStage {
		public ICoralIdStage withLoc(Int2D loc);
	}

	//Generated("SparkTools")
	public interface ICoralIdStage {
		public IDensityStage withCoralId(int coralId);
	}

	//Generated("SparkTools")
	public interface IDensityStage {
		public IBuildStage withDensity(double density);
	}

	//Generated("SparkTools")
	public interface IBuildStage {
		public SubStepCoralCell build();
	}

	/**
	 * Builder to build {@link SubStepCoralCell}.
	 */
	//Generated("SparkTools")
	public static final class Builder implements ICoralCellIdStage, ISpeciesIdStage, IBattleSkillStage,
			IMaxRadiusStage, ILocStage, ICoralIdStage, IDensityStage, IBuildStage {
		private long coralCellId;
		private int speciesId;
		private int battleSkill;
		private int maxRadius;
		private Int2D loc;
		private int coralId;
		private double density;

		private Builder() {
		}

		@Override
		public ISpeciesIdStage withCoralCellId(long coralCellId) {
			this.coralCellId = coralCellId;
			return this;
		}

		@Override
		public IBattleSkillStage withSpeciesId(int speciesId) {
			this.speciesId = speciesId;
			return this;
		}

		@Override
		public IMaxRadiusStage withBattleSkill(int battleSkill) {
			this.battleSkill = battleSkill;
			return this;
		}

		@Override
		public ILocStage withMaxRadius(int maxRadiusSquared) {
			this.maxRadius = maxRadiusSquared;
			return this;
		}

		@Override
		public ICoralIdStage withLoc(Int2D loc) {
			this.loc = loc;
			return this;
		}

		@Override
		public IDensityStage withCoralId(int coralId) {
			this.coralId = coralId;
			return this;
		}

		@Override
		public IBuildStage withDensity(double density) {
			this.density = density;
			return this;
		}

		@Override
		public SubStepCoralCell build() {
			return new SubStepCoralCell(this);
		}
	}
	
	


}
