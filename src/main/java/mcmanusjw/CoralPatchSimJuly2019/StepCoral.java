package mcmanusjw.CoralPatchSimJuly2019;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Int2D;

import java.util.ArrayList;
import java.util.Collections;

//import javax.annotation.Generated;
/**
 *  This uses as a Stepped Builder made using SparkTools to ensure
 *  that all required fields are filled on construction, but in a
 *  readable fashion (e.g. .withCoralId, etc.).
 *  
 *  Do not change declaration order or number of required fields.
 *  The actual Builder is at the bottom after getters and setters.
 *  See usage example in StaticCoral.
 *  
 * @author John McManus
 * @version December 29, 2017
 *
 */

@SuppressWarnings("serial")
class StepCoral implements Steppable, Comparable < StepCoral >
{
	// These are assigned and never change
	private final int          coralId;  // This is generated in StaticCoral.newCoral(...) and starts with 0
	private final int          speciesId;// This is generated in StepRecruiter and starts with 0
	
	// This must not exceed less of grid width/2 squared or height/2 squared
	private int                maxRadius; 

	// These change over time
	private int                radius;
	private Int2D              centerLoc;
	private Set < SubStepCoralCell >  cells;
	
	// Final values from StaticBenthosData
	private final String       name;          
    private final String       growthForm;
	private final double       maxColumnDiameter;
	private final double       maxColumnHeight;
	private final int          originalAbundance;
	private final int          battleSkill;
	private final int          growthRate;
	private final int          recruitmentRate;
	private final double       mortalityRate;     
	
	// Important state variables for optimizing efficiency
	private boolean damagedTF;    // Used to skip StepRecenterAndSplit routines when false
	private boolean cellsAddedTF; // Used with above to skip finding a new radius, especially for old corals 

	// This is initialized after construction when the coral is added to the schedule, 
	private Stoppable stopThis;

	//Generated("SparkTools")
	private StepCoral(Builder builder) {
		this.coralId = builder.coralId;
		this.speciesId = builder.speciesId;
		this.maxRadius = builder.maxRadius;
		this.radius = builder.radius;
		this.centerLoc = builder.centerLoc;
		this.cells = builder.cells;
		this.name = builder.name;
		this.growthForm = builder.growthForm;
		this.maxColumnDiameter = builder.maxColumnDiameter;
		this.maxColumnHeight = builder.maxColumnHeight;
		this.originalAbundance = builder.originalAbundance;
		this.battleSkill = builder.battleSkill;
		this.growthRate = builder.growthRate;
		this.recruitmentRate = builder.recruitmentRate;
		this.mortalityRate = builder.mortalityRate;
		this.damagedTF = builder.damaged;
		this.cellsAddedTF = builder.cellsAdded;
		this.stopThis = builder.stopThis;
		
		init();
	}
	
    void init()
	{
		// Important state variables for optimizing efficiency
		damagedTF    = false; // Used to skip StepRecenterAndSplit routines when false
		cellsAddedTF = false; // Used with above to skip finding a new radius, especially for old corals 
	}

	public void step( SimState state )
	{
		if ( !PanelDamageButtonsAndInfoText.isOkToGo() ) return;

		//Return if too many cells already among all corals
		if ( StaticCoralCell.getNumCoralCells() >=  StaticCoralCell.getMaxCoralCells() ) return;
		
		// Increment previous radius
		if ( cellsAddedTF || damagedTF ) // Some activity previous cycle
		{
			radius = StaticCoral.adjustRadius( centerLoc, maxRadius, cells, this ) + 1;
			cellsAddedTF = false; // already accounted for this, but damagedTF still goes to StepRecenterAndSplit
		}
		else
		{
			radius = radius + 1; // no activity previous cycle as in a trapped or max size coral so no need to check radius 
		}
		if ( radius > maxRadius ) radius = maxRadius;
		
		// Make a temporary List of the cells from the Set so it can be shuffled 
		List < SubStepCoralCell > cellList = new ArrayList < SubStepCoralCell >();
		cellList.addAll( cells );
		
		if (cellList.contains( null ) )
		{
			//System.out.println( "cellList contains a null in StepCoral.step(...) " );
			UtilJM.warner     ( "cellList contains a null in StepCoral.step(...) " );
		}
		
		if ( cellList.size() > 1)
		{	
			UtilJM.shuffleList( cellList, state );
		}
		int cellsThisCycle = cellList.size(); // numCells will change mid-cycle so grab the starting value here
		
		// One advantage of each coral triggering all its cells in one Mason step is that
		// none of the cells will be killed by other corals during that step, changing the List length.
		for (int i = 0; i < cellsThisCycle; i++)
		{
			SubStepCoralCell growingCell = cellList.get(i);
			growingCell.step( this, centerLoc, radius, state );// Here we step the coral cell
		}
	}
	
	@Override
	public String toString()
	{
		String code = " coral_" + coralId; // + " species_" + this.speciesId 
				    //+ " growthform_" + this.growthForm + "\n Diameter_" + (2 * this.radius)
				    //+ " number_cells_" + cells.size() + " center_" + centerLoc;
		return code;
	}
	
	/**
	 * Set up sorting criteria which is based on coral id numbers.
	 * This compareTo() requires that the id values coming in be positive integers. 
	 */
	@ Override
    public int compareTo( StepCoral testCoral )
    {
	    return ( this.coralId < testCoral.getCoralId() ) ? -1: ( this.coralId > testCoral.getCoralId() ) ? 1:0;
    }
	
    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!( o instanceof StepCoral )) {
            return false;
        }
        StepCoral user = ( StepCoral ) o;
        return coralId == user.getCoralId();
    }

    @Override
    public int hashCode() {
        return Objects.hash( coralId );
    }

    // ********************* Start Getters and Setters **********************
 
	int getRadius() {
		return radius;
	}

	void setRadius(int radius) {
		this.radius = radius;
	}

	Int2D getCenterLoc() {
		return centerLoc;
	}

	void setCenterLoc(Int2D centerLoc) {
		this.centerLoc = centerLoc;
	}

	Set<SubStepCoralCell> getCells() {
		return cells;
	}

	void setCells(Set<SubStepCoralCell> cells) {
		this.cells = cells;
	}

	int getBattleSkill() {
		return battleSkill;
	}

	int getGrowthRate() {
		return growthRate;
	}

	int getRecruitmentRate() {
		return recruitmentRate;
	}

	double getMortalityRate() {
		return mortalityRate;
	}

	Stoppable getStopThis() {
		return stopThis;
	}

	void setStopThis(Stoppable stopThis) {
		this.stopThis = stopThis;
	}

	int getCoralId() {
		return coralId;
	}

	int getSpeciesId() {
		return speciesId;
	}

	int getMaxRadius() {
		return maxRadius;
	}

	String getName() {
		return name;
	}

	String getGrowthForm() {
		return growthForm;
	}

	double getMaxColumnDiameter() {
		return maxColumnDiameter;
	}

	double getMaxColumnHeight() {
		return maxColumnHeight;
	}

	int getOriginalAbundance() {
		return originalAbundance;
	}

	boolean isDamaged() {
		return damagedTF;
	}

	void setDamaged(boolean damaged) {
		this.damagedTF = damaged;
	}
	

	boolean isCellsAdded() {
		return cellsAddedTF;
	}

	void setCellsAdded(boolean cellsAdded) {
		this.cellsAddedTF = cellsAdded;
	}

	/**
	 * Use this for testing only
	 * @param maxRadius
	 */
	void setMaxRadius(int maxRadius) {
		this.maxRadius = maxRadius;
	}

	/**
	 * Creates builder to build {@link StepCoral}.
	 * @return created builder
	 */
	//Generated("SparkTools")
	public static ICoralIdStage builder() {
		return new Builder();
	}

	//Generated("SparkTools")
	public interface ICoralIdStage {
		public ISpeciesIdStage withCoralId(int coralId);
	}

	//Generated("SparkTools")
	public interface ISpeciesIdStage {
		public IMaxRadiusStage withSpeciesId(int speciesId);
	}

	//Generated("SparkTools")
	public interface IMaxRadiusStage {
		public IRadiusStage withMaxRadius(int maxRadius);
	}

	//Generated("SparkTools")
	public interface IRadiusStage {
		public ICenterLocStage withRadius(int radius);
	}

	//Generated("SparkTools")
	public interface ICenterLocStage {
		public ICellsStage withCenterLoc(Int2D centerLoc);
	}

	//Generated("SparkTools")
	public interface ICellsStage {
		public INameStage withCells(Set<SubStepCoralCell> cells);
	}

	//Generated("SparkTools")
	public interface INameStage {
		public IGrowthFormStage withName(String name);
	}

	//Generated("SparkTools")
	public interface IGrowthFormStage {
		public IMaxColumnDiameterStage withGrowthForm(String growthForm);
	}

	//Generated("SparkTools")
	public interface IMaxColumnDiameterStage {
		public IMaxColumnHeightStage withMaxColumnDiameter(double maxColumnDiameter);
	}

	//Generated("SparkTools")
	public interface IMaxColumnHeightStage {
		public IOriginalAbundanceStage withMaxColumnHeight(double maxColumnHeight);
	}

	//Generated("SparkTools")
	public interface IOriginalAbundanceStage {
		public IBattleSkillStage withOriginalAbundance(int originalAbundance);
	}

	//Generated("SparkTools")
	public interface IBattleSkillStage {
		public IGrowthRateStage withBattleSkill(int battleSkill);
	}

	//Generated("SparkTools")
	public interface IGrowthRateStage {
		public IRecruitmentRateStage withGrowthRate(int growthRate);
	}

	//Generated("SparkTools")
	public interface IRecruitmentRateStage {
		public IMortalityRateStage withRecruitmentRate(int recruitmentRate);
	}

	//Generated("SparkTools")
	public interface IMortalityRateStage {
		public IBuildStage withMortalityRate(double mortalityRate);
	}

	//Generated("SparkTools")
	public interface IBuildStage {
		public IBuildStage withDamaged(boolean damaged);

		public IBuildStage withCellsAdded(boolean cellsAdded);

		public IBuildStage withStopThis(Stoppable stopThis);

		public StepCoral build();
	}

	/**
	 * Builder to build {@link StepCoral}.
	 */
	//Generated("SparkTools")
	public static final class Builder
			implements ICoralIdStage, ISpeciesIdStage, IMaxRadiusStage, IRadiusStage, ICenterLocStage, ICellsStage,
			INameStage, IGrowthFormStage, IMaxColumnDiameterStage, IMaxColumnHeightStage, IOriginalAbundanceStage,
			IBattleSkillStage, IGrowthRateStage, IRecruitmentRateStage, IMortalityRateStage, IBuildStage {
		private int coralId;
		private int speciesId;
		private int maxRadius;
		private int radius;
		private Int2D centerLoc;
		private Set<SubStepCoralCell> cells = Collections.emptySet();
		private String name;
		private String growthForm;
		private double maxColumnDiameter;
		private double maxColumnHeight;
		private int originalAbundance;
		private int battleSkill;
		private int growthRate;
		private int recruitmentRate;
		private double mortalityRate;
		private boolean damaged;
		private boolean cellsAdded;
		private Stoppable stopThis;

		private Builder() {
		}

		@Override
		public ISpeciesIdStage withCoralId(int coralId) {
			this.coralId = coralId;
			return this;
		}

		@Override
		public IMaxRadiusStage withSpeciesId(int speciesId) {
			this.speciesId = speciesId;
			return this;
		}

		@Override
		public IRadiusStage withMaxRadius(int maxRadius) {
			this.maxRadius = maxRadius;
			return this;
		}

		@Override
		public ICenterLocStage withRadius(int radius) {
			this.radius = radius;
			return this;
		}

		@Override
		public ICellsStage withCenterLoc(Int2D centerLoc) {
			this.centerLoc = centerLoc;
			return this;
		}

		@Override
		public INameStage withCells(Set<SubStepCoralCell> cells) {
			this.cells = cells;
			return this;
		}

		@Override
		public IGrowthFormStage withName(String name) {
			this.name = name;
			return this;
		}

		@Override
		public IMaxColumnDiameterStage withGrowthForm(String growthForm) {
			this.growthForm = growthForm;
			return this;
		}

		@Override
		public IMaxColumnHeightStage withMaxColumnDiameter(double maxColumnDiameter) {
			this.maxColumnDiameter = maxColumnDiameter;
			return this;
		}

		@Override
		public IOriginalAbundanceStage withMaxColumnHeight(double maxColumnHeight) {
			this.maxColumnHeight = maxColumnHeight;
			return this;
		}

		@Override
		public IBattleSkillStage withOriginalAbundance(int originalAbundance) {
			this.originalAbundance = originalAbundance;
			return this;
		}

		@Override
		public IGrowthRateStage withBattleSkill(int battleSkill) {
			this.battleSkill = battleSkill;
			return this;
		}

		@Override
		public IRecruitmentRateStage withGrowthRate(int growthRate) {
			this.growthRate = growthRate;
			return this;
		}

		@Override
		public IMortalityRateStage withRecruitmentRate(int recruitmentRate) {
			this.recruitmentRate = recruitmentRate;
			return this;
		}

		@Override
		public IBuildStage withMortalityRate(double mortalityRate) {
			this.mortalityRate = mortalityRate;
			return this;
		}

		@Override
		public IBuildStage withDamaged(boolean damaged) {
			this.damaged = damaged;
			return this;
		}

		@Override
		public IBuildStage withCellsAdded(boolean cellsAdded) {
			this.cellsAdded = cellsAdded;
			return this;
		}

		@Override
		public IBuildStage withStopThis(Stoppable stopThis) {
			this.stopThis = stopThis;
			return this;
		}

		@Override
		public StepCoral build() {
			return new StepCoral(this);
		}
	}


}
