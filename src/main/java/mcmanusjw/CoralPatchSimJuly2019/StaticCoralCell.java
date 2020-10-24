package mcmanusjw.CoralPatchSimJuly2019;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import sim.field.continuous.Continuous2D;
import sim.field.grid.IntGrid2D;
import sim.field.grid.ObjectGrid2D;
import sim.util.Int2D;

/**
 * Contains all static variables and methods 
 * applicable to coral cells. This keeps creation 
 * and killing plus all collection modifications
 * in a simple single method. 
 * 
 * @author John McManus
 *
 */
class StaticCoralCell 
{
	private static final int maxCoralCellsAllTypesCombined = StaticBenthosData.getMaxCoralCellsAllTypesCombined();//89999; //10000; // For indexing all coral cells in the simulation	

    // Grid dimensions
	private static int height;
	private static int width; 

	private static int numCoralCellsAllCoralsEverMade;
	private static int numCoralCellsNow;

	private static Set < Int2D > allFilledLocs; 
	
	// CoralCells will be placed here  -- ObjectGrid2D allows only one object per space
	private static ObjectGrid2D coralCellGrid;

	// Only species numbers + 1 (0 is background) will be mapped here just for visualization
	private static IntGrid2D speciesCellGrid;//	

	// Continuous space is used here only to calculate toroidal euclidean distances.
	private	static Continuous2D continuousSpace;
	
	// Accumumlator for caco3 production, will be read and cleared every year in StepCalculateAndStore
	private static double caco3AnnualCumulator;
	private static List < Double > speciesCaco3AnnualCumulator; 
	
	StaticCoralCell()
	{
		init();
	}
	
    /**
    *  This is called for cycling through multiple simulations
    */
	static void init()
	{
		int seriesNumber = StepLooper.getSeriesNumber();
		//height = (int) StaticBenthosData.getRunParamsTable().getDouble( seriesNumber, "Height" );
		width  = (int) StaticBenthosData.getRunParamsTable().getDouble( seriesNumber, "Width"  );
		height = width;
		
		numCoralCellsAllCoralsEverMade = 0;
		numCoralCellsNow               = 0;
		
		allFilledLocs = new HashSet < Int2D > (); 
		coralCellGrid = new ObjectGrid2D( height, width );
		speciesCellGrid  = new IntGrid2D( height, width );
		speciesCellGrid.setTo( 0 );
		continuousSpace = new Continuous2D( 10d, height, width );
		caco3AnnualCumulator = 0;
		speciesCaco3AnnualCumulator = new ArrayList < Double > (); 
	}
	
	static void replicateInit()
	{
		int seriesNumber = StepLooper.getSeriesNumber();
		width  = (int) StaticBenthosData.getRunParamsTable().getDouble( seriesNumber, "Width"  );
		height = width;
		
		numCoralCellsAllCoralsEverMade = 0;
		numCoralCellsNow               = 0;
		
		allFilledLocs = new HashSet < Int2D > (); 
		coralCellGrid = new ObjectGrid2D( height, width );
		//speciesCellGrid  = new IntGrid2D( height, width );
		speciesCellGrid.setTo( 0 );
		continuousSpace = new Continuous2D( 10d, height, width );
		caco3AnnualCumulator = 0;
		speciesCaco3AnnualCumulator = new ArrayList < Double > (); 
	}

	/**
	 * This method creates and returns a single SubStepCoralCell. 
	 * @param loc
	 * @param stepCoral
	 */
	static boolean newCoralCell( Int2D loc, StepCoral stepCoral ) //SubStepCoralCell newCoralCell(Int2D loc, StepCoral coral)
	{
		if( stepCoral == null ) 
		{
			//System.out.println( " StepCoral is null in StaticCoralCell -- now returning. " );
			return false;
		}
		
		if ( allFilledLocs.contains( loc ) ) return false;
		
		if( numCoralCellsNow + 1 > maxCoralCellsAllTypesCombined ) 
			{
			    //UtilJM.warner( "Max overall coral cells reached" );
				return false;
			}
		// CoralCellId starts with 0, then increases continuously as new cells are created
		//              which gives a unique identifier to each SubStepCoralCell
		int coralCellId      = numCoralCellsAllCoralsEverMade;
		int coralId          = stepCoral.getCoralId();
		int speciesId        = stepCoral.getSpeciesId();
		int battleSkill      = stepCoral.getBattleSkill();
		int maxRadius        = stepCoral.getMaxRadius();
		
		double density = StaticBenthosData.getSpeciesInputTable().getDouble( speciesId, "SkeletalDensity" );
		
		// Here is where we create a new coral cell using the stepped builder pattern in SubStepCoralCell
		SubStepCoralCell cell = SubStepCoralCell.builder() 
								  .withCoralCellId      ( coralCellId      )
								  .withSpeciesId        ( speciesId        )
								  .withBattleSkill      ( battleSkill      )
								  .withMaxRadius        ( maxRadius        )
								  .withLoc              ( loc              )
								  .withCoralId          ( coralId          )
								  .withDensity          ( density          ) 
								  
								  // Now building the coral cell
								  .build();
		// End of builder
				
		coralCellGrid  .set( loc.x, loc.y, cell          );
		speciesCellGrid.set( loc.x, loc.y, speciesId + 1 ); 	// Adding 1 prevents 0 values, which are for background only (black)
		allFilledLocs.add( loc );
		
		boolean addedOK = stepCoral.getCells().add( cell );
		if ( !addedOK )
		{
			//UtilJM.warner( "Could not add cell Cell: "+ cell + "/ncells present:/n" + stepCoral.getCells());////////////
			
			UtilJM.warner( "One new cell could not be added in StaticCoralCell", true );////////////
		}

		numCoralCellsAllCoralsEverMade++; 						// Total number among all corals combined
		numCoralCellsNow++;
		
		stepCoral.setCellsAdded( true );// Used with 'damaged' to avoid checking unaltered corals in StepRecenterAndSplit
		
		return true;
	}

	/**
	 * 
	 * @param cell
	 * @param fromCoralStaticKill
	 * @return
	 */
	static boolean killFromCoralKill( SubStepCoralCell cell, StepCoral stepCoral ) // returns killCoral = true if no more cells in that coral
	{

		boolean killedCell = false;
		Int2D   loc       = cell.getLoc();
		
    	estimateReleasedCaCO3( cell, stepCoral );
		
		numCoralCellsNow--; 					  // Reduce total number of cells among all corals combined
		speciesCellGrid.set( loc.x, loc.y, 0    );
		coralCellGrid  .set( loc.x, loc.y, null ); 
		
		stepCoral.getCells().remove( cell );  // Remove this cell
		
		//Flag follows
		if ( stepCoral.getCells().contains( cell ) )
		{
			UtilJM.warner( "removed cell still exists (StaticCoralCell.killFromCoralCell) ");
		}
		
		stepCoral.setDamaged( true );
		
		cell = null;
		killedCell = true;
		
		allFilledLocs.remove( loc );

		return killedCell;
	}
	
	/**
	 * 
	 *  ToDo: Must ensure that all cells are removed before getting rid of any coral. 
	 * 
	 * @param cell
	 * @param fromCoralStaticKill
	 * @return
	 */
	static boolean killNotFromCoralKill ( SubStepCoralCell cell ) // returns killCoral = true if no more cells in that coral
	{
		boolean killedCell = false;
		int     coralId   = cell.getCoralId();
		Int2D   loc       = cell.getLoc();
		StepCoral   stepCoral     = StaticCoral.getCoralIdToCoralObject().get(coralId);
		
		estimateReleasedCaCO3( cell, stepCoral );
		
		numCoralCellsNow--; 					  // Reduce total number of cells among all corals combined
		speciesCellGrid.set( loc.x, loc.y, 0    );
		coralCellGrid  .set( loc.x, loc.y, null ); // is it gone immediately? problem with not growing into ellipse...<<<<<<<<<<<<check this

		stepCoral.getCells().remove( cell );  // Remove this cell
		
		//Flag follows
		if ( stepCoral.getCells().contains( cell ) )
		{
			UtilJM.warner( "removed cell still exists ( StaticCoralCell.killNotFromCoralCell ) ");
		}
		
		stepCoral.setDamaged( true );
		
		// If no cells left for a coral, the coral dies, but not if this call came from a dying coral
		if (  stepCoral.getCells().size() < 1 )  
		{
//			System.out.println( "killing a coral from StaticCoralCell.kill(...)" );
			StaticCoral.killFromCellKill( stepCoral );
		}
		cell = null;
		killedCell = true;
		
		allFilledLocs.remove( loc );

		return killedCell;
	}
	
	static void estimateReleasedCaCO3( SubStepCoralCell cell, StepCoral stepCoral )
	{
		if ( stepCoral == null ) return;
		// Get coral's total number of cells and determine percent loss from one cell loss
		double percentLost = 1d / stepCoral.getCells().size();
		
		// Get coral's growthForm and parameters
		String growthForm      = stepCoral.getGrowthForm();
		double maxColumnHeight = stepCoral.getMaxColumnHeight();
		double maxColumnWidth  = stepCoral.getMaxColumnDiameter();
		double diameter        = 2 * stepCoral.getRadius(); 
		
		//This gets the Accessor Class for that growthform
		//InterfaceForAccessClasses accessClass = StaticCoral.getTextToAccess().get( growthForm );
		InterfaceForAccessClasses accessClass =	EnumAccessor.fromString( growthForm ).getAccessor();
		
		double volume  = accessClass.calculateFromString ( "Volume"  , maxColumnHeight, maxColumnWidth, diameter );
		
		// Get CaCO3 for that volume
		double caco3 = cell.getDensity() * volume;
		
		// Estimate CaCO3 released times percent of total lost for that coral, and add to caco3AnnualCumulator
		double lostCaco3 = caco3 * percentLost;
		caco3AnnualCumulator += ( lostCaco3 );
		// Add to species specific cumulator.
		int speciesId = stepCoral.getSpeciesId();

		double tillNow = 0;
		if ( ( speciesCaco3AnnualCumulator.size() > speciesId ) // If there are two items (size = 2), highest index is 1 
		  && ( speciesCaco3AnnualCumulator.get( speciesId ) != null ) ) 
		{ 
			tillNow = speciesCaco3AnnualCumulator.get( speciesId );	
			speciesCaco3AnnualCumulator.set( speciesId, ( tillNow + lostCaco3 ) );
			
			//System.out.println( "StaticCoralCell Bottom, tillNow: " + tillNow + " lostCaco3: " + lostCaco3);
		}
		else
		{
			speciesCaco3AnnualCumulator.add( lostCaco3 );
		}
	}

	/**
	 * This fixes a strange problem in which cells remain on their grid even when the corals
	 * they belong to are null. Apparently, not all cells of the coral are being killed when 
	 * the coral is killed. A few (typically less than 100, but up to 1200 or so) seem to be missed.
	 * This issue would have very little impact on calculations across a million or more sq cm, 
	 * but the nulls create program exceptions as we try to access the coral from the cell
	 * as it is encountered for killing or other purposes. of course, we anticipate rounding errors
	 * from using Euclidean distances (doubles) on a grid with locations as ints, but it is not 
	 * clear how that could be a source of the problem. It may also be related to corals or cells
	 * somehow being omitted in the HashSets, or perhaps from removing null values from the Map of
	 * CoralIdToCoralObject. Even replacing the CoralCellGrid with a sparse grid designed to prevent 
	 * null entries would still require full searching, as it is the corals which are null, not 
	 * the cells to which they are linked.    
	 * 
	 * This slow routine is often needed after 7 or so simulation years (perhaps following storm damage)
	 * for both coral killing routines.
	 * Note that we have shown that the null-linked cells can be too widely found to use
	 * a search based on a center location and even up to 10 times a coral's maximum radius.
	 * It also fails when used only after StepReaper, as EllipseDamager then shows null-linked cells,
	 * even though StepReaper steps first. Calling from StaticCoral.radiusAndOutliers(...) also 
	 * does nothing. Putting it early and in loops in StaticEllipticalDamager might work, but would 
	 * slow that routine down and leave potential problems for future program extensions. 
	 * 
	 * The problem is somehow related to StepRecenterAndSplit, as it does not occur when that routine is
	 * not used. However, simply using this after that routine does not fix the problem. 
	 * 
	 * @return
	 */
	static boolean clearCoralCellGridOfCellsWithNullCorals ()
	{
		boolean nullsFound = false;
		
//		int count = 0;
		
		for ( int i = 0; i < coralCellGrid.getHeight(); i++ )
		{
			for ( int j = 0; j < coralCellGrid.getWidth(); j++ )
			{
				SubStepCoralCell cell = ( SubStepCoralCell ) coralCellGrid.get( i, j ); 
				if ( ( cell != null ) && ( cell instanceof SubStepCoralCell ) )
				{
					int coralId = cell.getCoralId();
					StepCoral stepCoral = StaticCoral.getCoralIdToCoralObject().get( coralId );
					if ( stepCoral == null )
					{
						coralCellGrid  .set( i, j, null);
						speciesCellGrid.set( i, j, 0   );
						nullsFound = true;
//						count++;
					}
				}
			}
		}
		// Also clear the HashMap of coralId to coral
		StaticCoral.getCoralIdToCoralObject().values().removeIf( Objects :: isNull );
		//if ( nullsFound ) System.out.println( "Bad cells found in coralCellGrid = " + count );
		
		return nullsFound;
	}
	
	// ********************* Start Getters and Setters **********************
	// Height, width and maxCoralCellsAllTypesCombined will be set if testing coral recentering, etc.
	
	static int getMaxCoralCells() {
		return maxCoralCellsAllTypesCombined;
	}

//	static int getInitCoralCells() {
//		return initCoralCells;
//	}

	static int getNumCoralCells() {
		return numCoralCellsAllCoralsEverMade;
	}

	static void setNumCoralCells(int numCoralCells) {
		StaticCoralCell.numCoralCellsAllCoralsEverMade = numCoralCells;
	}

	static ObjectGrid2D getCoralCellGrid() {
		return coralCellGrid;
	}

	static void setCoralCellGrid(ObjectGrid2D coralCellGrid) {
		StaticCoralCell.coralCellGrid = coralCellGrid;
	}

	static IntGrid2D getSpeciesCellGrid() {
		return speciesCellGrid;
	}

	static void setSpeciesCellGrid(IntGrid2D speciesCellGrid) {
		StaticCoralCell.speciesCellGrid = speciesCellGrid;
	}

	static int getMaxCoralCellsAllTypesCombined() {
		return maxCoralCellsAllTypesCombined;
	}

	static int getHeight() {
		return height;
	}

	static int getWidth() {
		return width;
	}
	
	static void setHeight(int height) {
		StaticCoralCell.height = height;
	}

	static void setWidth(int width) {
		StaticCoralCell.width = width;
	}

	static Continuous2D getContinuousSpace() {
		return continuousSpace;
	}

//	static void setInitcoralcells(int initcoralcells) {
//		initCoralCells = initcoralcells;
//	}

	static void setNumCoralCellsAllCoralsEverMade(int numCoralCellsAllCoralsEverMade) {
		StaticCoralCell.numCoralCellsAllCoralsEverMade = numCoralCellsAllCoralsEverMade;
	}

	static void setNumCoralCellsNow(int numCoralCellsNow) {
		StaticCoralCell.numCoralCellsNow = numCoralCellsNow;
	}

	static void setContinuousSpace(Continuous2D continuousSpace) {
		StaticCoralCell.continuousSpace = continuousSpace;
	}

	static double getCaco3AnnualCumulator() {
		return caco3AnnualCumulator;
	}

	static void setCaco3AnnualCumulator(double caco3AnnualCumulator) {
		StaticCoralCell.caco3AnnualCumulator = caco3AnnualCumulator;
	}

	static List<Double> getSpeciesCaco3AnnualCumulator() {
		return speciesCaco3AnnualCumulator;
	}
	
}
