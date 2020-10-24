package mcmanusjw.CoralPatchSimJuly2019;

enum EnumGrowthForms 
{
	COLUMN             ("EnumColumn"          , 60d, 10d ),
	COLUMN_ENCRUSTING  ("EnumColumnEncrusting", 60d, 10d ),
	COLUMN_PLATE       ("EnumColumnPlate"     , 60d, 10d ),
	ENCRUSTING         ("EnumEncrusting"      ,  0d,  0d ),
	FOLIOSE            ("EnumFoliose"         , 10d,  1d ),
	FUNNEL             ("EnumFunnel"          , 10d,  1d ),
	MASSIVE            ("EnumMassive"         ,  0d,  0d ),
	PLATE              ("EnumPlate"           , 10d,  1d ),
	BLADES             ("EnumBlades"          , 10d,  1d ),
	TABLE              ("EnumTable"           ,  3d,  2d ),
	BARREL             ("EnumBarrel"          ,  0d,  0d ),
	TUBE               ("EnumTube"            , 60d, 10d );
	
	// Note: no need for HOLE_LEDGE here, because those values do not change over our time-frame
	
	String text;
	double maxHeight; // Suggested test max height (0 for not needed)
	double maxWidth;  // Suggested test component max width or diameter  (0 for not needed)  
	
	EnumGrowthForms ( String text, double maxHeight, double maxWidth )
	{
		this.text      = text;
		this.maxHeight = maxHeight;
		this.maxWidth  = maxWidth;
	}
	
	public String getText()
	{
	     return text;
	}
	public double getTestMaxHeight() // Test values only
	{
	      return maxHeight;
	}
	public double getTestMaxWidth()  // Test values only
	{
		 return maxWidth;
	}
	 
	public static EnumGrowthForms fromString( String text )
	{
		if ( text != null )
		{
			for ( EnumGrowthForms b : EnumGrowthForms.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	}
}
