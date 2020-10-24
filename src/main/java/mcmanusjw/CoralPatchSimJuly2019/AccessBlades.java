package mcmanusjw.CoralPatchSimJuly2019;

import java.util.ArrayList;
import java.util.List;

class AccessBlades implements InterfaceForAccessClasses 
{
	static final String enumString = "EnumBlades";	
	
	static final boolean topAndBottom = false;
	
	@Override
	public Double calculateFromString( String propertyString, double maxHeight, double maxWidth, Double diameter ) 
	{
		return ( EnumBlades.fromString( propertyString ) ).calculate( maxHeight,  maxWidth,  diameter );
	}

	@Override
	public String getEnumString() 
	{
		return enumString;
	}

	@Override	
	public List < String > getAllPropertyStrings()
	{
		List < String > propertyStrings = new ArrayList < String > ();
		
		for ( EnumBlades value : EnumBlades.values() )
		{
			propertyStrings.add( value.getText() );
		}
		return propertyStrings;
	}
	
	@Override
	public boolean isTopandBottom() 
	{
		return topAndBottom;
	}
}