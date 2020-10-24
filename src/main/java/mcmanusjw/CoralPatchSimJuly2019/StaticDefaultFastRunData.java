package mcmanusjw.CoralPatchSimJuly2019;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * @author John McManus
 *
 */


public class StaticDefaultFastRunData 
{
	private static TableRunParamsListEnumMapsString runParamsTable
								= new TableRunParamsListEnumMapsString( 1 ); 
	
	private static TableSpeciesListEnumMapsString speciesInputTable; 
//	             = new TableSpeciesListEnumMapsString( 6 ); // set up initially for default data: 6 species

	private static List < IntDouble > originalDataList = new ArrayList < IntDouble > ();
	
	static TableRunParamsListEnumMapsString loadDefaultRunParams()
	{

		//System.out.println( " starting StaticDefaultFastRunData " );
		
		//runParamsTable = new TableRunParamsListEnumMapsString( 1 );

		int     numberOfSeries            = 1;
		int     numBenthicTypes           = 5;  
		int     yearsToEnd                = 25; //return this to 25 //30; // 100; //<<<<<<<<<<<return this to 30<<<<<<<<<<<<
		int     replicateRuns             = 1;
		int     width                     = 500; // width = height of simulation area in cm
		boolean includeOriginalSizeDataTF = true; // false;
		int     originalSiteArea          = 100000; // added 14 Dec 2019
		boolean varRecruitmentTF          = false;
		boolean includeBackgroundMortTF   = true;
	    boolean includeBroadDamageTF      = false;
		int     lowerYearBroad            = 0;
		int     upperYearBroad 			  = 0;	 
		double  lowerBroadStrength        = 0.0;  
		double  upperBroadStrength        = 0.0;
	    boolean includeEllipticalDamageTF = false; // was true
		int     siteDepth                 = 1; //Depth in meters -- set this for the new site chosen
		boolean adjustImpactForDepthTF    = true;
		int     lowerYearEllipse          = 2; 
		int     upperYearEllipse           = 3;
		int     ellipseLengthMin          = 100;//200;  //The percent damage can cause slipping on the right if these too big.
		int     ellipseLengthMax          = 200;//300;
		int     ellipseWidthMin           = 25; //50;
		int     ellipseWidthMax           = 75;//100;
		int     angleDegreesMin           = 100;
		int     angleDegreesMax           = 120;
		double  lowerPercentEllipse       = 0.80; // percent of grid cells to be 'touched' (considered)
		double  upperPercentEllipse       = 0.90; // percent of grid cells to be 'touched' (considered)
		double  lowerWaveStrength         = 0.80;
		double  upperWaveStrength         = 0.90;
		boolean centerEllipseTF           = false;
		boolean singleEllipseTF           = false;
		
		
		// Note that runParamsTable automatically converts int, double and boolean to String.
		runParamsTable.put( 0, "NumberOfSeries"            , numberOfSeries            );
		runParamsTable.put( 0, "NumBenthicTypes"           , numBenthicTypes           );
		runParamsTable.put( 0, "YearsToEnd"                , yearsToEnd                );
		runParamsTable.put( 0, "NumberOfReplicates"        , replicateRuns             );
		runParamsTable.put( 0, "Width"                     , width                     );
		runParamsTable.put( 0, "IncludeOriginalSizeDataTF" , includeOriginalSizeDataTF );
		runParamsTable.put( 0, "VarRecruitmentTF"          , varRecruitmentTF          );
		runParamsTable.put( 0, "IncludeBackgroundMortTF"   , includeBackgroundMortTF   );
		runParamsTable.put( 0, "OriginalSiteArea"          , originalSiteArea          ); // added 14 Dec 2019
		runParamsTable.put( 0, "IncludeBroadDamageTF"      , includeBroadDamageTF      );
		runParamsTable.put( 0, "LowerYearBroad"            , lowerYearBroad            );
		runParamsTable.put( 0, "UpperYearBroad"            , upperYearBroad 		   );	 	 
		runParamsTable.put( 0, "LowerBroadStrength"        , lowerBroadStrength        ); 
		runParamsTable.put( 0, "UpperBroadStrength"        , upperBroadStrength        );
		runParamsTable.put( 0, "IncludeEllipticalDamageTF" , includeEllipticalDamageTF );
		runParamsTable.put( 0, "SiteDepth"                 , siteDepth                 );
		runParamsTable.put( 0, "AdjustImpactForDepthTF"    , adjustImpactForDepthTF    );
		runParamsTable.put( 0, "LowerYearEllipse"          , lowerYearEllipse          );
		runParamsTable.put( 0, "UpperYearEllipse"          , upperYearEllipse          );
		runParamsTable.put( 0, "EllipseLengthMin"          , ellipseLengthMin          );
		runParamsTable.put( 0, "EllipseLengthMax"          , ellipseLengthMax          );
		runParamsTable.put( 0, "EllipseWidthMin"           , ellipseWidthMin           );
		runParamsTable.put( 0, "EllipseWidthMax"           , ellipseWidthMax           );
		runParamsTable.put( 0, "AngleDegreesMin"           , angleDegreesMin           );
		runParamsTable.put( 0, "AngleDegreesMax"           , angleDegreesMax           );
		runParamsTable.put( 0, "LowerPercentEllipse"       , lowerPercentEllipse       );
		runParamsTable.put( 0, "UpperPercentEllipse"       , upperPercentEllipse       );
		runParamsTable.put( 0, "LowerWaveStrength"         , lowerWaveStrength         );
		runParamsTable.put( 0, "UpperWaveStrength"         , upperWaveStrength         );
		runParamsTable.put( 0, "CenterEllipseTF"           , centerEllipseTF           );
		runParamsTable.put( 0, "SingleEllipseTF"           , singleEllipseTF           );
		
		//System.out.println( runParamsTable.getInt( 0, "NumBenthicTypes" ) );
		
		return runParamsTable;
	}
	
	/**
	 * This loads default species parameters into the speciesInputTable.
	 * Because the latter includes mixed types, and is the 
	 * same Map used to load mixed data from Excel sheets
	 * elsewhere, all data are in the form of ints.
	 * This method is a bit redundant, but is done this way
	 * to make it easy to spot and change values.
	 * 
	 * Ignore this for this example>>
	 * Note: Padina dimensions are maxima for Padina australis from 
	 * https://florabase.dpaw.wa.gov.au/browse/profile.php/27113 
	 * CaCO3 is 21 % of dry weight from a study cited at end of Okazaki et al 1986 
	 * "A study of calcium carbonate deposition in the genus Padina (Phaeophyceae, Dictyotales)"
	 * 
	 * Ignore this>>
	 * Conservatively assume the Padina blade is a complete vertical circle of radius 5 cm, 
	 * the volume is 1 cm width x Pi r-sqrd = 79 cc. 21% of that is 16.5 cc. 
	 * Wikipedia gives 2.7 gm/cc for calcite. So, 2.7 x 16.5 = 44.6 gm max. 
	 * Thus 44.6 / 79 = 0.56 gm/cc density
	 *   
	 */
	static TableSpeciesListEnumMapsString loadDefaultSpeciesInfo()
	{
		int numBenthicTypes =  runParamsTable.getInt( 0, "NumBenthicTypes" ); 
		
		// System.out.println( "loadDefaultSpeciesInfo: numBenthicTypes: " + numBenthicTypes  );///////////////////////////////
		
		speciesInputTable   = new TableSpeciesListEnumMapsString( numBenthicTypes );
		
		 List < String > names       = List.of( 
				        "Pavona decussata: Foliose", "Pocillop. damicornis: Branched", "Porites cylindrica: Branched", 	
			            "Porites lutea: Massive",    "Porites rus: Column on Encrust." );
		
		 List < String > growthForms = List.of( 
				 		"Foliose", "Column", "Column", "Massive", "ColumnEncrusting" );

		 List < String > recruitsPerSqM 	    = List.of( "0.92",   "0.65",   "0.64",   "0.28",  "0.96"   );
		 List < String > battleSkills 		    = List.of( "4", 	 "5",	   "2", 	 "1",     "3"      );
		 List < String > growthRates 		    = List.of( "2.4", 	 "3.3",    "1.9", 	 "1.4",   "1.5"    );
		 List < String > maxRadii 			    = List.of( "30", 	 "60", 	   "60", 	 "60", 	  "240"    ); 
		 List < String > originalAbundances	    = List.of( "43", 	 "28",     "37", 	 "22",    "37"     );
		 List < String > mortalities		    = List.of( "0.07", "0.25",    "0.06",    "0.04", "0.02"    );
		 List < String > maxColumnDiameters	    = List.of( "1.0",    "3.0",    "3.0",    "0.0",   "4.0"    );
		 List < String > maxColumnHeights	    = List.of( "4.0",   "10.0",    "15.0",   "0.0",  "16.0"    );
		 List < String > skeletalDensities	    = List.of( "1.96",   "1.93",   "1.42",   "1.28",  "1.23"   );
		 List < String > broadVulnerabilities	= List.of( "0.24",   "0.3",    "0.4",    "0.23",  "0.22"   ); // e.g. bleaching
		 List < String > ellipseVulnerabilities	= List.of( "0.6",    "0.7",    "0.8",    "0.1",   "0.6"    ); // e.g. waves or boats

		 for ( int i = 0; i < numBenthicTypes; i++ ) 
		 {
			 speciesInputTable.put( i, "Name"				  , names                  .get( i ) );
			 speciesInputTable.put( i, "GrowthForm"			  , growthForms            .get( i ) );
			 speciesInputTable.put( i, "RecruitsPerSqM"		  , recruitsPerSqM         .get( i ) );
			 speciesInputTable.put( i, "BattleSkill"		  , battleSkills           .get( i ) );
			 speciesInputTable.put( i, "GrowthRate"			  , growthRates            .get( i ) );
			 speciesInputTable.put( i, "MaxRadius"			  , maxRadii               .get( i ) );
			 speciesInputTable.put( i, "OriginalAbundance"	  , originalAbundances     .get( i ) );
			 speciesInputTable.put( i, "Mortality"			  , mortalities            .get( i ) );
			 speciesInputTable.put( i, "MaxColumnDiameter"	  , maxColumnDiameters     .get( i ) );
			 speciesInputTable.put( i, "MaxColumnHeight"	  , maxColumnHeights       .get( i ) );
			 speciesInputTable.put( i, "SkeletalDensity"	  , skeletalDensities      .get( i ) );
			 speciesInputTable.put( i, "BroadVulnerability"   , broadVulnerabilities   .get( i ) );
			 speciesInputTable.put( i, "EllipseVulnerability" , ellipseVulnerabilities .get( i ) );
		 }
		 
		 return speciesInputTable;
	}
	
	/**
	 * This inputs into originalDataList a set of IntDoubles
	 * carrying data pairs of species code and diameters, which
	 * are field data on individual corals. 
	 */
	static List < IntDouble > loadDefaultTypeBySizeData() 
	{
		originalDataList.add( new IntDouble( 0, 3.5d )  );
		originalDataList.add( new IntDouble( 0, 3.5d )  );
		originalDataList.add( new IntDouble( 0, 3.5d )  );
		originalDataList.add( new IntDouble( 0, 3.5d )  );
		originalDataList.add( new IntDouble( 0, 7.5d )  );
		originalDataList.add( new IntDouble( 0, 7.5d )  );
		originalDataList.add( new IntDouble( 0, 7.5d )  );
		originalDataList.add( new IntDouble( 0, 7.5d )  );
		originalDataList.add( new IntDouble( 0, 7.5d )  );
		originalDataList.add( new IntDouble( 0, 7.5d )  );
		originalDataList.add( new IntDouble( 0, 7.5d )  );
		originalDataList.add( new IntDouble( 0, 7.5d )  );
		originalDataList.add( new IntDouble( 0, 7.5d )  );
		originalDataList.add( new IntDouble( 0, 7.5d )  );
		originalDataList.add( new IntDouble( 0, 15d )  );
		originalDataList.add( new IntDouble( 0, 15d )  );
		originalDataList.add( new IntDouble( 0, 15d )  );
		originalDataList.add( new IntDouble( 0, 15d )  );
		originalDataList.add( new IntDouble( 0, 15d )  );
		originalDataList.add( new IntDouble( 0, 15d )  );
		originalDataList.add( new IntDouble( 0, 15d )  );
		originalDataList.add( new IntDouble( 0, 15d )  );
		originalDataList.add( new IntDouble( 0, 15d )  );
		originalDataList.add( new IntDouble( 0, 15d )  );
		originalDataList.add( new IntDouble( 0, 15d )  );
		originalDataList.add( new IntDouble( 0, 15d )  );
		originalDataList.add( new IntDouble( 0, 15d )  );
		originalDataList.add( new IntDouble( 0, 15d )  );
		originalDataList.add( new IntDouble( 0, 15d )  );
		originalDataList.add( new IntDouble( 0, 15d )  );
		originalDataList.add( new IntDouble( 0, 15d )  );
		originalDataList.add( new IntDouble( 0, 30d )  );
		originalDataList.add( new IntDouble( 0, 30d )  );
		originalDataList.add( new IntDouble( 0, 30d )  );
		originalDataList.add( new IntDouble( 0, 30d )  );
		originalDataList.add( new IntDouble( 0, 30d )  );
		originalDataList.add( new IntDouble( 0, 30d )  );
		originalDataList.add( new IntDouble( 0, 30d )  );
		originalDataList.add( new IntDouble( 0, 30d )  );
		originalDataList.add( new IntDouble( 0, 30d )  );
		originalDataList.add( new IntDouble( 0, 30d )  );
		originalDataList.add( new IntDouble( 0, 30d )  );
		originalDataList.add( new IntDouble( 0, 30d )  );
		originalDataList.add( new IntDouble( 1, 3.5d )  );
		originalDataList.add( new IntDouble( 1, 3.5d )  );
		originalDataList.add( new IntDouble( 1, 7.5d )  );
		originalDataList.add( new IntDouble( 1, 7.5d )  );
		originalDataList.add( new IntDouble( 1, 7.5d )  );
		originalDataList.add( new IntDouble( 1, 7.5d )  );
		originalDataList.add( new IntDouble( 1, 7.5d )  );
		originalDataList.add( new IntDouble( 1, 15d )  );
		originalDataList.add( new IntDouble( 1, 15d )  );
		originalDataList.add( new IntDouble( 1, 15d )  );
		originalDataList.add( new IntDouble( 1, 15d )  );
		originalDataList.add( new IntDouble( 1, 15d )  );
		originalDataList.add( new IntDouble( 1, 15d )  );
		originalDataList.add( new IntDouble( 1, 15d )  );
		originalDataList.add( new IntDouble( 1, 15d )  );
		originalDataList.add( new IntDouble( 1, 30d )  );
		originalDataList.add( new IntDouble( 1, 30d )  );
		originalDataList.add( new IntDouble( 1, 30d )  );
		originalDataList.add( new IntDouble( 1, 30d )  );
		originalDataList.add( new IntDouble( 1, 30d )  );
		originalDataList.add( new IntDouble( 1, 30d )  );
		originalDataList.add( new IntDouble( 1, 30d )  );
		originalDataList.add( new IntDouble( 1, 30d )  );
		originalDataList.add( new IntDouble( 1, 30d )  );
		originalDataList.add( new IntDouble( 1, 30d )  );
		originalDataList.add( new IntDouble( 1, 30d )  );
		originalDataList.add( new IntDouble( 1, 30d )  );
		originalDataList.add( new IntDouble( 1, 30d )  );
		originalDataList.add( new IntDouble( 2, 3.5d )  );
		originalDataList.add( new IntDouble( 2, 3.5d )  );
		originalDataList.add( new IntDouble( 2, 3.5d )  );
		originalDataList.add( new IntDouble( 2, 7.5d )  );
		originalDataList.add( new IntDouble( 2, 7.5d )  );
		originalDataList.add( new IntDouble( 2, 7.5d )  );
		originalDataList.add( new IntDouble( 2, 7.5d )  );
		originalDataList.add( new IntDouble( 2, 7.5d )  );
		originalDataList.add( new IntDouble( 2, 7.5d )  );
		originalDataList.add( new IntDouble( 2, 7.5d )  );
		originalDataList.add( new IntDouble( 2, 7.5d )  );
		originalDataList.add( new IntDouble( 2, 7.5d )  );
		originalDataList.add( new IntDouble( 2, 7.5d )  );
		originalDataList.add( new IntDouble( 2, 7.5d )  );
		originalDataList.add( new IntDouble( 2, 7.5d )  );
		originalDataList.add( new IntDouble( 2, 7.5d )  );
		originalDataList.add( new IntDouble( 2, 7.5d )  );
		originalDataList.add( new IntDouble( 2, 7.5d )  );
		originalDataList.add( new IntDouble( 2, 15d )  );
		originalDataList.add( new IntDouble( 2, 15d )  );
		originalDataList.add( new IntDouble( 2, 15d )  );
		originalDataList.add( new IntDouble( 2, 15d )  );
		originalDataList.add( new IntDouble( 2, 15d )  );
		originalDataList.add( new IntDouble( 2, 15d )  );
		originalDataList.add( new IntDouble( 2, 15d )  );
		originalDataList.add( new IntDouble( 2, 15d )  );
		originalDataList.add( new IntDouble( 2, 15d )  );
		originalDataList.add( new IntDouble( 2, 15d )  );
		originalDataList.add( new IntDouble( 2, 15d )  );
		originalDataList.add( new IntDouble( 2, 30d )  );
		originalDataList.add( new IntDouble( 2, 30d )  );
		originalDataList.add( new IntDouble( 2, 30d )  );
		originalDataList.add( new IntDouble( 2, 30d )  );
		originalDataList.add( new IntDouble( 2, 30d )  );
		originalDataList.add( new IntDouble( 2, 30d )  );
		originalDataList.add( new IntDouble( 2, 30d )  );
		originalDataList.add( new IntDouble( 2, 30d )  );
		originalDataList.add( new IntDouble( 3, 3.5d )  );
		originalDataList.add( new IntDouble( 3, 7.5d )  );
		originalDataList.add( new IntDouble( 3, 15d )  );
		originalDataList.add( new IntDouble( 3, 15d )  );
		originalDataList.add( new IntDouble( 3, 15d )  );
		originalDataList.add( new IntDouble( 3, 15d )  );
		originalDataList.add( new IntDouble( 3, 15d )  );
		originalDataList.add( new IntDouble( 3, 15d )  );
		originalDataList.add( new IntDouble( 3, 15d )  );
		originalDataList.add( new IntDouble( 3, 30d )  );
		originalDataList.add( new IntDouble( 3, 30d )  );
		originalDataList.add( new IntDouble( 3, 30d )  );
		originalDataList.add( new IntDouble( 3, 30d )  );
		originalDataList.add( new IntDouble( 3, 30d )  );
		originalDataList.add( new IntDouble( 3, 30d )  );
		originalDataList.add( new IntDouble( 3, 30d )  );
		originalDataList.add( new IntDouble( 3, 30d )  );
		originalDataList.add( new IntDouble( 3, 30d )  );
		originalDataList.add( new IntDouble( 3, 30d )  );
		originalDataList.add( new IntDouble( 3, 60d )  );
		originalDataList.add( new IntDouble( 3, 60d )  );
		originalDataList.add( new IntDouble( 3, 60d )  );
		originalDataList.add( new IntDouble( 4, 3.5d )  );
		originalDataList.add( new IntDouble( 4, 7.5d )  );
		originalDataList.add( new IntDouble( 4, 7.5d )  );
		originalDataList.add( new IntDouble( 4, 7.5d )  );
		originalDataList.add( new IntDouble( 4, 7.5d )  );
		originalDataList.add( new IntDouble( 4, 7.5d )  );
		originalDataList.add( new IntDouble( 4, 7.5d )  );
		originalDataList.add( new IntDouble( 4, 7.5d )  );
		originalDataList.add( new IntDouble( 4, 7.5d )  );
		originalDataList.add( new IntDouble( 4, 7.5d )  );
		originalDataList.add( new IntDouble( 4, 15d )  );
		originalDataList.add( new IntDouble( 4, 15d )  );
		originalDataList.add( new IntDouble( 4, 15d )  );
		originalDataList.add( new IntDouble( 4, 15d )  );
		originalDataList.add( new IntDouble( 4, 15d )  );
		originalDataList.add( new IntDouble( 4, 15d )  );
		originalDataList.add( new IntDouble( 4, 15d )  );
		originalDataList.add( new IntDouble( 4, 15d )  );
		originalDataList.add( new IntDouble( 4, 15d )  );
		originalDataList.add( new IntDouble( 4, 15d )  );
		originalDataList.add( new IntDouble( 4, 15d )  );
		originalDataList.add( new IntDouble( 4, 15d )  );
		originalDataList.add( new IntDouble( 4, 15d )  );
		originalDataList.add( new IntDouble( 4, 30d )  );
		originalDataList.add( new IntDouble( 4, 30d )  );
		originalDataList.add( new IntDouble( 4, 30d )  );
		originalDataList.add( new IntDouble( 4, 30d )  );
		originalDataList.add( new IntDouble( 4, 30d )  );
		originalDataList.add( new IntDouble( 4, 30d )  );
		originalDataList.add( new IntDouble( 4, 30d )  );
		originalDataList.add( new IntDouble( 4, 30d )  );
		originalDataList.add( new IntDouble( 4, 30d )  );
		originalDataList.add( new IntDouble( 4, 30d )  );
		originalDataList.add( new IntDouble( 4, 30d )  );
		originalDataList.add( new IntDouble( 4, 30d )  );
		originalDataList.add( new IntDouble( 4, 30d )  );
		originalDataList.add( new IntDouble( 4, 30d )  );
		
		return originalDataList;

	}
}
