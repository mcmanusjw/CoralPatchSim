package mcmanusjw.CoralPatchSimJuly2019;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javafx.application.Platform;

/**
 * Todo: PUT LOOP AROUND DIRECTORY CHOOSER IN CASE NOTHING CHOSEN <<<<<<<<<<<<<<<<<<<<<<<
 * 
 * Maybe move the creation of the input tables and Map to here, then get a copy in the StaticInputRunInfo class.
 * 
 * This will enable density-dependent recruitment (large area issue like coastal stretch)
 * or not (small area damage issue such as dredging or boat damage). Use original site data
 * to estimate recruitsPerSqM per unit surface area. Find existing surface area by species (assume 
 * same over large area). Then scale recruitment up or down accordingly. 
 * Make sure it is never zero -- maybe set to 1 recruit per species per year minimum. 
 * 
 * Add switch for elliptical damage on or off, and one for single ellipse centered,
 * plus one for natural mortality on or off. Also put all switches into a control panel
 * for use with the user interface graphics runs. 
 *  
 * IMPORTANT: All column and column combination corals, including branchlets on tables, must
 * have column heights of at least 1.5 times column width, lest the refuge equations return 
 * negative numbers. 
 * 
 * Note: All recruitment rates should be per square m. We will upgrade them to the selected 
 * simulation area size in StepRecruiter.
 * 
 * We use macroalgae as a 'coral' with 0 battle skill (non-aggressive fleshy macroalgae) 
 * but fast patch growth (12 cm per year?), no net natural mortality (the macroalgae will
 * quickly fill in within its own subcommunity) and lots of recruitment. It should basically
 * just minimize coral settlement via r-type competition. Any 'coralID' should be fine
 *  -- it won't matter. Perhaps add the macroalgae after 10 years so the community has a chance
 *  to grow? Macroalgae will probably ultimately win out due to normal levels of natural
 *  coral mortality. They tend to be seasonal, but generally other algae remain -- 
 *  so ignore seasonality initially. Can calculate its CaCO3 as done below. 
 * 
 * Also put species colors here so anyone can assign them. Make them into a ColorMap early on.
 * Olive works great for algae. 
 * 
 * @title   StaticBenthosData
 * @author  John McManus
 * @date	September 19, 2018
 * 
 * Reads parameter input data from a file.
 * 
 * Also sets up the default Benthos data from Apra Harbor Site 80 as:
 * 
 * Name               		Form	Recruits BattleSkills GrowthRates MaxDiameter OriginalAbundance Mortalities   
 * Porites rus       	 encrusting       1      1 		    3 	    	160           10 			0.018   	   	     
 * Porites rus       	 plate   		104      1 			3 	    	160		    1130			0.018     	  	     
 * Porites rus        	 mixed    		 10    	 1 			3 	    	160	         110            0.018         	     
 * Pavona cactus       	 foliose  		 90      2 			3 	     	 80	         200            0.24          	    
 * Porites horizontalata plate			 86    	 1 			1 	    	160           30            0.054         	     
 * Padina spp            seagrass      1000      0         12           900        10000            0.0          
 *
 * 			 where OriginalAbundance is the number per 100 sq m from the field estimates.
 */

class StaticBenthosData 
{
	private static final int stepsPerYear = 12;
	private static       int stepsToEnd;              // = stepsPerYear * yearsToEnd done below
	
	// Values passed to StaticCoral -- subtract 10 to allow for operational steppables to be scheduled after all individuals
    private final static int maxCoralsAllTypesCombined 	   = Integer.MAX_VALUE - 10; 
	private final static int maxCoralCellsAllTypesCombined = Integer.MAX_VALUE - 10; 

	private static TableRunParamsListEnumMapsString runParamsTable;
	
	private static TableSpeciesListEnumMapsString speciesInputTable 
	             = new TableSpeciesListEnumMapsString( 6 ); // set up initially for default data: 6 species

	private static List < IntDouble > originalDataList = new ArrayList < IntDouble > ();
	
	private static TableOutputListEnumMapsDouble origSpeciesResults;
	private static TableOutputListEnumMapsDouble origOverallResults;
	
	private static double originalSampleArea;
	
	private static boolean dataSourceChoosingDone = false;
	
	private static boolean usingDefaultData = false;
	
	private static String outputDirectoryString;
	
	static void init()
	{
    	// Allow user to choose between default data and input data, or just to cancel
    	if ( !dataSourceChoosingDone ) 
    	{
    		setupDataOptionChoicePanel();
    		dataSourceChoosingDone = true;
    	}
	}
	
	/**
	 * Allow user to choose between default data and input data, or just to cancel
	 * 
	 * Maybe later put this into a general options input page?
	 * 
	 * Use the System.exit(0) here allowed only because this comes before the Mason graphics
	 * are set up (which need special shutdown).
	 * 
	 */
    private static void setupDataOptionChoicePanel() 
	{
		String[] options  = new String[] {"Use input file", "Use default data", "Cancel"};

		JFrame frame = new JFrame();
		//frame.setSize( 1200, 760 );
		
		// From https://stackoverflow.com/questions/25635636/eclipse-exported-runnable-jar-not-showing-images
		// See Example 1 of Paul Samsotha
		// There are many ways to import the image as a file, but none work in an executable jar. This works:
		URL url = StaticBenthosData.class.getResource( "/CoralPatchSim1_0Title.png");
		ImageIcon icon = new ImageIcon(url);

		frame.add(new JLabel(icon));
        frame.pack();
		frame.setLocationRelativeTo(null); // This centers it (note that DirectoryFinder does not get centered)
		frame.setVisible( true );
		
		outputDirectoryString = null;
		
		int      response = JOptionPane.showOptionDialog( frame, 
														"Choose Data Source", 
														"Data Source Chooser",
														 JOptionPane.DEFAULT_OPTION, 
														 JOptionPane.PLAIN_MESSAGE,
														 null, 
														 options, 
														 options[0]                  );
		
		//Setting up JavaFX for the directory finder and optional file chooser
        @SuppressWarnings("unused") 
		javafx.embed.swing.JFXPanel dummy = new javafx.embed.swing.JFXPanel();
        Platform.setImplicitExit(false);
        
		switch( response )
		{
			case 0: 
			{
				boolean fileOK = StaticInputRunInfo.readInputFile();
				if ( !fileOK ) // then do default data instead?
				{
					// Set up a confirmation window
					Object[] probOptions = {"Yes", "No", "Cancel"};
					frame = new JFrame();
					int n = JOptionPane.showOptionDialog( frame, "Do you want to use default data instead? ",
							"Just to keep going",
							JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
							null, probOptions, probOptions[2]);
					switch ( n )
					{
						case 0: break; 
						case 1: System.exit(0);
						case 2: System.exit(0);
					}

					outputDirectoryString = StaticRunSynchronizedDirectoryFinder.getDirectoryString();//chooseOutputDirectory();
					usingDefaultData      = true;
					runParamsTable        = new TableRunParamsListEnumMapsString( 1 );
					loadAndPrepDefaultData();
					
					break; 
				}
				
				outputDirectoryString = StaticRunSynchronizedDirectoryFinder.getDirectoryString();//chooseOutputDirectory();
				usingDefaultData      = false;
				runParamsTable        = StaticInputRunInfo.getRunParamsTable();
				speciesInputTable     = StaticInputRunInfo.getSpeciesInputTable();
				originalDataList      = StaticInputRunInfo.getOriginalDataList();
				originalSampleArea    = StaticInputRunInfo.getOriginalSampleArea();
				origSpeciesResults    = UtilOriginalDataPrepper.getOrigSpeciesResults();
				origOverallResults    = UtilOriginalDataPrepper.getOrigOverallResults();
				
				break;
			}
			case 1:
			{
				outputDirectoryString = StaticRunSynchronizedDirectoryFinder.getDirectoryString();//chooseOutputDirectory();
				usingDefaultData      = true;
				runParamsTable        = new TableRunParamsListEnumMapsString( 1 );
				loadAndPrepDefaultData();

				break; 
			}
			case 2: System.exit( 0 );
		}
		
		//Close JavaFX
		Platform.exit();
		
		// Close Frame
		//frame.dispose();
		
		// In either data entry case, do the following:
		int yearsToEnd = (int) runParamsTable.getDouble( 0, "YearsToEnd" );
		stepsToEnd     = stepsPerYear * yearsToEnd;
	}
   

    /**
	 * This runs the methods which load default data into the same
	 * map and tables used when loading user data from spreadsheets. 
	 * It loads run parameters, data on species parameters, and 
	 * default 'original data' of benthic types by size, beginning 
	 * for the latter with the size of area sampled to get that data. 
	 * 
	 */
	static void loadAndPrepDefaultData()
	{
		runParamsTable    = StaticDefaultFastRunData.loadDefaultRunParams();
		
		//loadDefaultSpeciesInfo();
		speciesInputTable = StaticDefaultFastRunData.loadDefaultSpeciesInfo();

		//loadDefaultTypeBySizeData();
		originalDataList  = StaticDefaultFastRunData.loadDefaultTypeBySizeData();
		
		origSpeciesResults    = UtilOriginalDataPrepper.getOrigSpeciesResults();
		origOverallResults    = UtilOriginalDataPrepper.getOrigOverallResults();

	}

	
	
/* Now replaced with Synchronous JavaFX routine
	static String chooseOutputDirectory()
	{
		Font font = new Font("monospaced", Font.BOLD, 12);
		
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Select an Output Directory");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFont( font );
		
		if ( chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION ) {
		  //System.out.println("getCurrentDirectory(): " + chooser.getCurrentDirectory());
		  //System.out.println("getSelectedFile() : " + chooser.getSelectedFile());
		} else {
		  //System.out.println("No Selection ");
		}
		
		System.out.println( "absolute path: " + chooser.getSelectedFile().getAbsolutePath() );
		
		return chooser.getSelectedFile().getAbsolutePath(); //.getCanonicalPath();
	}
*/	

	// Getters only follow:

	static int getStepsPerYear() {
		return stepsPerYear;
	}

	static int getStepsToEnd() {
		return stepsToEnd;
	}

	static int getMaxCoralsAllTypesCombined() {
		return maxCoralsAllTypesCombined;
	}

	static int getMaxCoralCellsAllTypesCombined() {
		return maxCoralCellsAllTypesCombined;
	}

	static TableRunParamsListEnumMapsString getRunParamsTable() {
		return runParamsTable;
	}

	static TableSpeciesListEnumMapsString getSpeciesInputTable() {
		return speciesInputTable;
	}

	static List<IntDouble> getOriginalDataList() {
		return originalDataList;
	}

	static double getOriginalSampleArea() {
		return originalSampleArea;
	}

	static TableOutputListEnumMapsDouble getOrigSpeciesResults() {
		return origSpeciesResults;
	}

	static TableOutputListEnumMapsDouble getOrigOverallResults() {
		return origOverallResults;
	}
	
	static boolean isUsingDefaultData() {
		return usingDefaultData;
	}

	static String getOutputDirectoryString() {
		return outputDirectoryString;
	}
	
}
