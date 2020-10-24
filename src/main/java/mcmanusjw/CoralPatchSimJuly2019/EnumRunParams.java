package mcmanusjw.CoralPatchSimJuly2019;

enum EnumRunParams 
{
	NUMBER_OF_SERIES              ( "NumberOfSeries"            , "Integer" ),
	NUMBER_OF_REPLICATES          ( "NumberOfReplicates"        , "Integer" ), 
	NUM_BENTHIC_TYPES             ( "NumBenthicTypes"           , "Integer" ),  
 	YEARS_TO_END                  ( "YearsToEnd"                , "Integer" ),
	WIDTH                         ( "Width"                     , "Integer" ),
	INCLUDE_ORIGINAL_SIZE_DATA_TF ( "IncludeOriginalSizeDataTF" , "Boolean" ),  
	ORIGINAL_SITE_AREA			  ( "OriginalSiteArea"          , "Integer" ), // added this 14 Dec 2019
	VAR_RECRUITMENT_TF            ( "VarRecruitmentTF"          , "Boolean" ),
    INCLUDE_BACKGROUND_MORT_TF    ( "IncludeBackgroundMortTF"   , "Boolean" ),
    INCLUDE_BROAD_DAMAGE_TF       ( "IncludeBroadDamageTF"      , "Boolean" ),
	LOWER_YEAR_BROAD              ( "LowerYearBroad"            , "Integer" ),
	UPPER_YEAR_BROAD              ( "UpperYearBroad"            , "Integer" ), 
	LOWER_BROAD_STRENGTH          ( "LowerBroadStrength"        , "Double"  ), 
	UPPER_BROAD_STRENGTH          ( "UpperBroadStrength"        , "Double"  ),
    INCLUDE_ELLIPTICAL_DAMAGE_TF  ( "IncludeEllipticalDamageTF" , "Boolean" ),
    SITE_DEPTH                    ( "SiteDepth"                 , "Integer" ),   
    ADJUST_IMPACT_FOR_DEPTH_TF    ( "AdjustImpactForDepthTF"    , "Boolean" ), 
	LOWER_YEAR_ELLIPSE            ( "LowerYearEllipse"          , "Integer" ),
	UPPER_YEAR_ELLIPSE            ( "UpperYearEllipse"          , "Integer" ), 
	ELLIPSE_LENGTH_MIN            ( "EllipseLengthMin"          , "Integer" ),
	ELLIPSE_LENGTH_MAX            ( "EllipseLengthMax"          , "Integer" ), 
	ELLIPSE_WIDTH_MIN             ( "EllipseWidthMin"           , "Integer" ),
	ELLIPSE_WIDTH_MAX             ( "EllipseWidthMax"           , "Integer" ),
	ANGLE_DEGREES_MIN             ( "AngleDegreesMin"           , "Double"  ),   
	ANGLE_DEGREES_MAX             ( "AngleDegreesMax"           , "Double"  ),
	LOWER_PERCENT_ELLIPSE         ( "LowerPercentEllipse"       , "Double"  ), 
	UPPER_PERCENT_ELLIPSE         ( "UpperPercentEllipse"       , "Double"  ),
	lOWER_WAVE_STENGTH            ( "LowerWaveStrength"         , "Double"  ),
	UPPER_WAVE_STRENGTH           ( "UpperWaveStrength"         , "Double"  ),
	CENTER_ELLIPSE_TF             ( "CenterEllipseTF"           , "Boolean" ),
	SINGLE_ELLIPSE_TF             ( "SingleEllipseTF"           , "Boolean" );

	
	String text;
	String type;
	
	EnumRunParams ( String text, String type )
	{
		this.text = text;
		this.type = type;
	}
	
	 public String getText()
	 {
	      return text;
	 }
	 
	 public String getType()
	 {
	      return type;
	 }
	 
	 public static EnumRunParams fromString( String text )
	 {
		if ( text != null )
		{
			for ( EnumRunParams b : EnumRunParams.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	 }

}
