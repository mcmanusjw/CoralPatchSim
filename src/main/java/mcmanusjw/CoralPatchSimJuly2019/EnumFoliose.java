package mcmanusjw.CoralPatchSimJuly2019;

import static java.lang.Math.PI;
import static java.lang.Math.min;

enum EnumFoliose 
{
	// Should not put species specific values here because this is only a growth form, not a species
	SURFACE  ( "Surface"  ),
	VOLUME   ( "Volume"   ),
	R_VOLUME ( "R_Volume" ),
	F_LENGTH ( "F_Length" ),
	N_HEIGHT ( "N_Height" );
	
	String text;
	
	EnumFoliose ( String text )
	{
		this.text = text;
	}
	
	// Use switch to pass parameters and diameter to desired formula copied from Mathematica notebooks (in comments above each formula
	double calculate ( double hmc, double dmc, double dw)
	{
		double value= 0;
		switch (this) 
	     {
	        case SURFACE:
	        	
	        	//      ( 1  / 4  ) * dw * Pi * ( dw + ( 4  + dw / Min[ dmc, dw/2  ] ) * Min[ dw/2,  hmc ] )
	        	value = ( 1d / 4d ) * dw * PI * ( dw + ( 4d + dw / min( dmc, dw/2d ) ) * min( dw/2d, hmc ) );
	        			
	        	return value;
	        	
	     	case VOLUME:
	     		
	     		//      ( 1  / 8  ) * dw^2  * Pi * Min[ dw/2,  hmc ]
	     		value = ( 1d / 8d ) * dw*dw * PI * min( dw/2d, hmc );
	     		
	     		return value;
	     		
	     	case R_VOLUME:
	     		
                //	            ( 1  / 8  ) * dw^2  * Pi * Min[ dw/2,  hmc ]
	    	     		value = ( 1d / 8d ) * dw*dw * PI * min( dw/2d, hmc );
	    	     		
	     		return value;
	     		
	     	case F_LENGTH:
	     		
	     	    //      4    Min[ dmc, dw/2  ]
	     		value=  4d * min( dmc, dw/2d );
	     		
	     		return value;
	     		
	     	case N_HEIGHT:
	     		
	     		//      Min[ dw/2,  hmc ]
	     		value = min( dw/2d, hmc );
	     		
	     		return value;	    
	     		
            default:
	              throw new AssertionError("Unknown operations" + this);
	      }
	 }
	
	 public String getText()
	 {
	      return text;
	 }
	 
	 public static EnumFoliose fromString( String text )
	 {
		if ( text != null )
		{
			for ( EnumFoliose b : EnumFoliose.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	 }
}

