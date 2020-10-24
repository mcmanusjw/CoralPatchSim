package mcmanusjw.CoralPatchSimJuly2019;

/**
 * ToDo: try just an enum of enums, eliminating the accessors
 * 
 * @author John McManus
 *
 */

enum EnumAccessor 
{
    COLUMN            ( "Column"          , new AccessColumn()           ),	
    COLUMN_ENCRUSTING ( "ColumnEncrusting", new AccessColumnEncrusting() ), 
    COLUMN_PLATE      ( "ColumnPlate"     , new AccessColumnPlate()      ),
    ENCRUSTING        ( "Encrusting"      , new AccessEncrusting()       ),
    FOLIOSE           ( "Foliose"         , new AccessFoliose()          ), 
    FUNNEL            ( "Funnel"          , new AccessFunnel()           ),
    MASSIVE           ( "Massive"         , new AccessMassive()          ),
    PLATE             ( "Plate"           , new AccessPlate()            ),
    BLADES            ( "Blades"          , new AccessBlades()           ),
    TABLE             ( "Table"           , new AccessTable()            ),
    BARREL            ( "Barrel"          , new AccessBarrel()           );
	
	String text;
	InterfaceForAccessClasses accessor;
	
	EnumAccessor ( String text, InterfaceForAccessClasses accessor )
	{
		this.text     = text;
		this.accessor = accessor;
	}
	
	 public String getText()
	 {
	      return text;
	 }
	 
	 public InterfaceForAccessClasses getAccessor()
	 {
		 return accessor;
	 }
	 
	 public static EnumAccessor fromString( String text )
	 {
		if ( text != null )
		{
			for ( EnumAccessor b : EnumAccessor.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	 }

}
