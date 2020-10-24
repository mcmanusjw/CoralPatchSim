package mcmanusjw.CoralPatchSimJuly2019;

enum EnumSpeciesInputField 
{
   NAME                 ( "Name"                , "String"  ),		   
   GROWTHFORM           ( "GrowthForm"	        , "String"  ),   
   RECRUITS_PER_SQ_CM   ( "RecruitsPerSqM"      , "Double"  ),	   
   BATTLESKILL          ( "BattleSkill"         , "Integer" ),	   
   GROWTH_RATE          ( "GrowthRate" 	        , "Integer" ),
   MAX_RADIUS           ( "MaxRadius" 		    , "Integer" ), 
   ORIGINAL_ABUNDANCE   ( "OriginalAbundance"   , "Integer" ),
   MORTALITY            ( "Mortality" 	        , "Double"  ),
   MAX_COLUMN_DIAMETER  ( "MaxColumnDiameter"   , "Double"  ),
   MAX_COLUMN_HEIGHT    ( "MaxColumnHeight"     , "Double"  ),
   SKELETAL_DENSITY     ( "SkeletalDensity"     , "Double"  ),
   BROAD_VULNERABILTIY  ( "BroadVulnerability"  , "Double"  ),
   ELLIPSE_VULNERABILTIY( "EllipseVulnerability", "Double"  );

	String text;
	String type;
	
	EnumSpeciesInputField ( String text, String type )
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
	 
	 public static EnumSpeciesInputField fromString( String text )
	 {
		if ( text != null )
		{
			for ( EnumSpeciesInputField b : EnumSpeciesInputField.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	 }

}
