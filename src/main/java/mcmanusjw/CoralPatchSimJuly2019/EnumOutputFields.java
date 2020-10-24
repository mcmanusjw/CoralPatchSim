package mcmanusjw.CoralPatchSimJuly2019;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum EnumOutputFields 
{
	NUMCORALS         ( "NumCorals"         , true  ),
    COUNTCELLS        ( "CountCells"        , true  ),
    VOLUME            ( "Volume"            , true  ),
    SURFACE           ( "Surface"           , true  ), 
    BOTTOMCOVER       ( "BottomCover"       , true  ),
    CACO3LIVE         ( "Caco3Live"         , true  ),
    CACO3CHANGE       ( "Caco3Change"       , true  ),
    CACO3DEAD         ( "Caco3Dead"         , true  ),
    CACO3DEADSOFAR    ( "Caco3DeadSoFar"    , true  ),
    SMLFISHVOL        ( "SmlFishVol"        , true  ),
    MEDFISHVOL        ( "MedFishVol"        , true  ), 
    LRGFISHVOL        ( "LrgFishVol"        , true  ),
	NEIGHBORMEDFISHVOL( "NeighborMedFishVol", false ),
	NEIGHBORLRGFISHVOL( "NeighborLrgFishVol", false ),
	COMBINEDMEDVOL    ( "CombinedMedFishVol", false ),
	COMBINEDLRGVOL    ( "CombinedLrgFishVol", false );

	String text;
	boolean spLevel;
	static Map < String, Integer > ordinalFieldTextMap = new HashMap < String, Integer > ();
	
	EnumOutputFields ( String text, boolean spLevel )
	{
		this.text    = text;
		this.spLevel = spLevel;
	}
	
	 public String getText()
	 {
	      return text;
	 }
	 
	 public static List < String> getAllTexts()
	 {
		 List < String > texts = new ArrayList < String >();
		 for ( EnumOutputFields b : EnumOutputFields.values() )
		 {
			 texts.add( b.text );
		 }
		 return texts;
	 }
	 
	 public boolean getSpLevel()
	 {
	      return spLevel;
	 }
	 
	 public boolean getSpLevelFromString( String text )
	 {
		 return EnumOutputFields.fromString(text).spLevel;
	 }
	 
	 public static Integer getOrdinalForFieldText( String text )
	 {
		 return ordinalFieldTextMap.get( text );
	 }
	 
	 public static EnumOutputFields fromString( String text )
	 {
		if ( text != null )
		{
			for ( EnumOutputFields b : EnumOutputFields.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	 }
}
