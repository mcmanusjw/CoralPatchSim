package mcmanusjw.CoralPatchSimJuly2019;

import static java.lang.Math.PI;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;

enum EnumTable 
{
	SURFACE      ( "Surface"   ),
	VOLUME       ( "Volume"    ),
	R_VOLUME     ( "R_Volume"  ),
	F_LENGTH     ( "F_Length"  ),
	N_HEIGHT     ( "N_Height"  ),
	R_VOL_TOP    ( "R_Vol_Top" ),
	F_LEN_TOP    ( "F_Len_Top" ),
	R_VOL_BOT    ( "R_Vol_Bot" ),
	F_LEN_BOT    ( "F_Len_Bot" );
	
	String text;
	
	EnumTable ( String text )
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
	        	
	        	//      ( 1  / 144  ) * dw * Pi * ( 71  * dw + 144  * Max[ 1,  ( dmc * ( dw - Min[ dw / 2 , hmc ] ) * Min[ dw / 2 , hmc ] ) / ( dw * hmc ) ] + 
	        	value = ( 1d / 144d ) * dw * PI * ( 71d * dw + 144d * max( 1d, ( dmc * ( dw - min( dw / 2d, hmc ) ) * min( dw / 2d, hmc ) ) / ( dw * hmc ) ) + 
	        			
	    	   //       24  * Min[ 10 , dw / 12  ] + ( 72  * dw * Min[ dw / 2 , hmc ] ) / Max[ 1  , ( dmc * (dw - Min[ dw / 2  , hmc ] ) *
	        	    	24d * min( 10d, dw / 12d ) + ( 72d * dw * min( dw / 2d, hmc ) ) / max( 1d , ( dmc * (dw - min( dw / 2d , hmc ) ) *
	        	
               //	    Min[ dw / 2 , hmc ] ) / ( dw * hmc ) ] )
	        	    	min( dw / 2d, hmc ) ) / ( dw * hmc ) ) );		
	        	
	        	//System.out.println( ">>>>>>>>>>>>>>>>>>>>>>>>>>>Table Surface value: " + value);
	        	
	        	return value;
	        	
	     	case VOLUME: 	     		
	     		
	     		//      ( 1  / 144  ) * Pi * ( 36  * dw^2  * Max[ 1,  ( dmc * ( dw - Min[ dw/2,  hmc ] ) * Min[ dw/2,  hmc ] ) / ( dw * hmc ) ] + 
	     		value = ( 1d / 144d ) * PI * ( 36d * dw*dw * max( 1d, ( dmc * ( dw - min( dw/2d, hmc ) ) * min( dw/2d, hmc ) ) / ( dw * hmc ) ) + 
	     		
	     		//      dw^2  * Min[ 10,  dw/12  ] + 3  * ( dw^3     -      ( dw - 2  * Min[ dw/2,  hmc ] )^3     ) )
	     				dw*dw * min( 10d, dw/12d ) + 3d * ( dw*dw*dw - pow( ( dw - 2d * min( dw/2d, hmc ) ), 3d ) ) );

	     		return value;
	     		
	     	case R_VOLUME:
	     		
	     		
	     		//       
	     		//      ( 1  / 144  ) * Pi * ( -18  * dw^2  * Max[ 1,  ( dmc * ( dw - Min[ dw/2,  hmc ] ) * Min [ dw/2,  hmc ] ) / ( dw * hmc ) ] +  
	     		value = ( 1d / 144d ) * PI * ( -18d * dw*dw * max( 1d, ( dmc * ( dw - min( dw/2d, hmc ) ) * min ( dw/2d, hmc ) ) / ( dw * hmc ) ) +  

	    	    //      35  * dw^2  * Min[ 10,  dw/12  ] + 6  * Min[ dw/2,  hmc ] * ( 3  * dw^2  - 6  * dw * Min[ dw/2,  hmc ] + 4  *       Min[ dw/2,  hmc ]^2       ) )
	     				35d * dw*dw * min( 10d, dw/12d ) + 6d * min( dw/2d, hmc ) * ( 3d * dw*dw - 6d * dw * min( dw/2d, hmc ) + 4d * pow( ( min( dw/2d, hmc ) ), 2 ) ) );
	     		
	     		return value;
	     		
	     	case F_LENGTH:
	     		
	     	    //     ( 1  / 2  ) * ( 4  * Max[ 1,  ( dmc * ( dw - Min[ dw/2,  hmc ] ) * Min[ dw/2,  hmc ] ) / ( dw * hmc ) ] + 
	     		value= ( 1d / 2d ) * ( 4d * max( 1d, ( dmc * ( dw - min( dw/2d, hmc ) ) * min( dw/2d, hmc ) ) / ( dw * hmc ) ) +
	     		
	     		//     Min[ 0.8  * dw , 4  * Min[ 10,  dw/12  ] ] ) 
	     			   min( 0.8d * dw , 4d * min( 10d, dw/12d ) ) );	// Note that 4/5 in notebooks = 0.8 here
	     		
	     		return value;
	     		
	     	case N_HEIGHT:
	     		
	     		//      Max[ 1  , ( dmc * ( dw - Min[ dw / 2  , hmc ] ) * Min[ dw / 2  , hmc ] ) / ( dw * hmc ) ] + 
	     		value = max( 1d , ( dmc * ( dw - min( dw / 2d , hmc ) ) * min( dw / 2d , hmc ) ) / ( dw * hmc ) ) + 
	     		
	     		//      Min [10 , dw / 12  ] + ( 1  / 2  ) * Min[ dw / 2 , hmc ]
	     				min( 10d, dw / 12d ) + ( 1d / 2d ) * min( dw / 2d, hmc );
	     		

	     		return value;	
	     	
	     	case R_VOL_TOP:
	     		
	     		//      ( 1  / 24  ) * Pi * ( -3  * dw^2  * Max[ 1,  ( dmc * ( dw - Min[ dw/2,  hmc ] ) * Min[ dw/2,  hmc ] ) / ( dw * hmc ) ] + 
	     		value = ( 1d / 24d ) * PI * ( -3d * dw*dw * max( 1d, ( dmc * ( dw - min( dw/2d, hmc ) ) * min( dw/2d, hmc ) ) / ( dw * hmc ) ) + 

	    	    //      Min[ dw/2,  hmc ] * ( 3  * dw^2  - 6  * dw * Min[ dw/2,  hmc ] + 4 *       Min[ dw/2, hmc ]^2      ) )
	     				min( dw/2d, hmc ) * ( 3d * dw*dw - 6d * dw * min( dw/2d, hmc ) + 4d * pow( min( dw/2d, hmc ), 2d ) ) );
	     		
	     		return value;
	     		
	     	case F_LEN_TOP:
	     		
	     	    //     4  * Max[ 1,  ( dmc * ( dw - Min[ dw/2,  hmc ] ) * Min[ dw/2,  hmc ] ) / ( dw * hmc ) ]
	     		value= 4d * max( 1d, ( dmc * ( dw - min( dw/2d, hmc ) ) * min( dw/2d, hmc ) ) / ( dw * hmc ) );
	     		
	     		return value;

	     	case R_VOL_BOT:
	     		
	     		//      ( 35  / 144  ) * dw^2  * Pi * Min[ 10,  dw/12  ] 
	     		value = ( 35d / 144d ) * dw*dw * PI * min( 10d, dw/12d );  
	     		
	     		return value;
	     		
	     	case F_LEN_BOT:
	     		
	     	    //     Min[ 0.8  * dw, 4  * Min[ 10, dw/12  ] ]
	     		value= min( 0.8d * dw, 4d * min( 10, dw/12d ) );       // Note that 4/5 in notebooks = 0.8 here
	     		
	     		return value;	
	     		
	     		
            default:
	              throw new AssertionError("Unknown operations" + this);
	      }
	 }
	
	 public String getText()
	 {
	      return text;
	 }
	 
	 public static EnumTable fromString( String text )
	 {
		if ( text != null )
		{
			for ( EnumTable b : EnumTable.values() )
			{
				if ( text.equalsIgnoreCase( b.text ) ) { return b; }
			}
		}
		// >>>>>>>>>>>>>>>>>add error warning panel>>>>>>>>>>>>>>>>>>>
		return null;
	 }
}

