package mcmanusjw.CoralPatchSimJuly2019;

import java.util.ArrayList;
import java.util.List;

class AccessColumn implements InterfaceForAccessClasses 
{
	static final String enumString = "EnumColumn";
	
	static final boolean topAndBottom = false;
	
	@Override
	public Double calculateFromString( String propertyString, double maxHeight, double maxWidth, Double diameter ) 
	{
		return ( EnumColumn.fromString( propertyString ) ).calculate( maxHeight,  maxWidth,  diameter );
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
		
		for ( EnumColumn value : EnumColumn.values() )
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
