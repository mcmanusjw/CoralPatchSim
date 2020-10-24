package mcmanusjw.CoralPatchSimJuly2019;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.EnumSet;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellUtil;

class UtilPoi 
{
	static boolean makeOutputWorkbook( String outputDirectoryString, String fileName, 
									   EnumSet< EnumOutputFields > labels, int numSpecies )
	{
		boolean  ok = false;
		TableSpeciesListEnumMapsString inputTable = StaticBenthosData.getSpeciesInputTable();
		Workbook wb = new HSSFWorkbook(); 
		HSSFCellStyle style = ( HSSFCellStyle ) wb.createCellStyle();
		style.setAlignment( HorizontalAlignment.RIGHT );
		
		int seriesNumber = StepLooper.getSeriesNumber();
		boolean originalDataAvailable 
		= StaticBenthosData.getRunParamsTable().getBoolean( seriesNumber, "IncludeOriginalSizeDataTF" );
		
		if( originalDataAvailable ) // make spreadsheets at start for ecological indices
		{
			// Note that in an EnumSet, the order follows that of declaration in the Enum. These are very fast for looping.
			// Example: EnumSet< EnumOutputFields > labels = EnumSet.allOf( EnumOutputFields.class );
			EnumSet < EnumIndices > indices = EnumSet.allOf( EnumIndices.class );
			for ( EnumIndices index : indices )
			{
				Sheet sheet = wb.createSheet  ( index.text );
				Row       r = CellUtil.getRow ( 0, sheet );
							  CellUtil.getCell( r, 0 ).setCellValue( index.text + " Output" );
						  
						  r = CellUtil.getRow ( 3, sheet );
			    HSSFCell  c = ( HSSFCell ) CellUtil.getCell( r, 0 );
			                  c.setCellStyle  ( style  ); 
					          c.setCellValue  ( "Year" );
			}
		}
		
		String setLabel = " ";
		String longName = " ";
		
		for (int i = 0; i < numSpecies + 1; i++) // + 1 is for the Overall data which leads off
		{
			if ( i == 0 ) 
			{
				setLabel = "Overall";
				longName = " Output";  
			}
			else
			{
				setLabel = "Sp" + ( i );
				longName = " " + inputTable.getString( i - 1, "Name" ); 
			}
			for ( EnumOutputFields label: labels )
			{
				if ( label.text == "Year" ) continue; // Skip the year label for new worksheets
				if ( ( setLabel != "Overall" ) && ( !label.spLevel ) ) continue;       // Skip final fields if this is for a species 
				
				Sheet sheet = wb.createSheet  ( setLabel + label.text );
				Row       r = CellUtil.getRow ( 0, sheet );
							  CellUtil.getCell( r, 0 ).setCellValue( setLabel + " " + label.text + " " + longName );
						  
						  r = CellUtil.getRow ( 3, sheet );
			    HSSFCell  c = ( HSSFCell ) CellUtil.getCell( r, 0 );
			                  c.setCellStyle  ( style  ); 
					          c.setCellValue  ( "Year" );

			}
			
		} // end outer loop by species

		File file = new File( outputDirectoryString, fileName );
		FileOutputStream fileOut;
		
	    try 
	    {
			fileOut = new FileOutputStream( file );
	        wb.write(fileOut);
		    fileOut.close();
			wb.close();
		    ok = true;
	        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    return ok;
	}

	/**
	 * Note: To use, must put all data into a long List of Doubles, 
	 * and all output field texts into a short List.
	 * 
	 * @param fileName
	 * @param annualOutputList
	 * @param outputFieldTexts
	 * @param runNumber
	 * @param year
	 * @param numSpecies
	 * @return
	 */
	
	static boolean enterAnnualData( String outputDirectoryString, String fileName, 
			                        //List < Double > annualOutputList, List < String > outputFieldTexts,
									TableOutputListEnumMapsDouble  origResults,
									TableOutputListEnumMapsDouble  annualOverallResultsTable,
									TableOutputListEnumMapsDouble  annualSpeciesResultsTable,
									EnumMap < EnumIndices, Double >	mapSimIndex,
			                        int runNumber, int year, int numSpecies )
	{
		
		boolean ok = false;
		
		//origResults.printAllTextValuePairs(); /////////////////////////////
		
		try 
		{
			File file = new File( outputDirectoryString, fileName );
			InputStream inp = new FileInputStream( file);
			Workbook wb = WorkbookFactory.create( inp ); // this returns a new HSSFWorkbook() in a Stream
			inp.close();
			
			int     seriesNumber     = StepLooper.getSeriesNumber();
			boolean outputDataExists = StaticBenthosData.getRunParamsTable().getBoolean( seriesNumber, "IncludeOriginalSizeDataTF" ); 
			HSSFCellStyle style      = ( HSSFCellStyle ) wb.createCellStyle();
			
			// Output ecological indices if original data available
			if ( outputDataExists )
			{
				for ( EnumIndices index : EnumIndices.values() )
	       		{
					Sheet sheet = wb.getSheet( index.text ); 
	       			if ( year == 1 ) // For first year, label the run number for each column 
					{
						Row r = CellUtil.getRow ( 3, sheet );
						HSSFCell c =  ( HSSFCell ) CellUtil.getCell( r, runNumber + 1 );
				       	c.setCellStyle( style );
				       	c.setCellValue( "S"+ (seriesNumber + 1) + " Run " + runNumber ); // Humans count from 1
					}
	      			Row r = CellUtil.getRow  ( year + 3, sheet );
	       			if ( runNumber == 0 ) CellUtil.getCell ( r, 0 ).setCellValue(  year  ); // Put years only on first runNumber
	       			
	       			// Changed runNumber to runNumber + 1 to avoid overwriting the year 
				    CellUtil.getCell ( r, runNumber + 1 ).setCellValue( mapSimIndex.get( index ) );
	       		}
			}
					
	       	// Output original data if available
			if ( ( year == 1 ) && ( runNumber == 0 ) &&  outputDataExists ) 				
	       	{
				System.out.println( "Outputting orig data in UtilPoi" );/////////////////////
				
				// For overall spreadsheets
				String setLabel = "Overall";
				for ( EnumOutputFields field : EnumOutputFields.values() )
	       		{
	       			Sheet sheet = wb.getSheet( setLabel + field.text ); 
	       			
	       			Row r = CellUtil.getRow  ( 2, sheet );
				    CellUtil.getCell ( r, 0 ).setCellValue( "Original: " );
				    CellUtil.getCell ( r, 1 ).setCellValue(  origResults.getValue( 0,  field.text ) );
	       		}
				
				// Same but by species
				for (int i = 0; i < numSpecies; i++) 
				{
					int spNum = i + 1;
					setLabel = "Sp" + spNum;
					for ( EnumOutputFields field : EnumOutputFields.values() )
		       		{
						if ( !field.spLevel ) continue;       // Skip final fields 
						Sheet sheet = wb.getSheet( setLabel + field.text ); 
		       			
		       			Row r = CellUtil.getRow  ( 2, sheet );
					    CellUtil.getCell ( r, 0 ).setCellValue( "Original: " );
					    CellUtil.getCell ( r, 1 ).setCellValue(  origResults.getValue( 0,  field.text ) );
		       		}
				}
	       	}
			
			// Output annual overall data

			String setLabel = "Overall";
			//int offsetCols = ( seriesNumber + 1 ) * ( runNumber + 1 );
			
       		for ( EnumOutputFields field : EnumOutputFields.values() )
       		{
       			Sheet sheet = wb.getSheet( setLabel + field.text ); 
       			
       			if ( year == 1 ) // For first year, label the run number for each column 
				{
					Row r = CellUtil.getRow ( 3, sheet );
					HSSFCell c =  ( HSSFCell ) CellUtil.getCell( r, runNumber + 1 );
			       	c.setCellStyle( style );
			       	c.setCellValue( "S"+ (seriesNumber + 1) + " Run " + runNumber ); // Humans count from 1
				}
       			
       			Row r = CellUtil.getRow  ( year + 3, sheet );
       			if ( runNumber == 0 ) CellUtil.getCell ( r, 0 ).setCellValue(  year  ); // Put years only on first runNumber
       			
       			// Changed runNumber to runNumber + 1 to avoid overwriting the year 
			    CellUtil.getCell ( r, runNumber + 1 ).setCellValue( annualOverallResultsTable.getValue( 0,  field.text ) );
       		}
       		
       	    // Output annual data by species
			for( int i = 0; i < numSpecies; i++ )
			{
				int spNum = i + 1;
				setLabel = "Sp" + spNum;
	       		for ( EnumOutputFields field : EnumOutputFields.values() )
	       		{
	       			if ( !field.spLevel ) continue;       // Skip final fields
	       			Sheet sheet = wb.getSheet( setLabel + field.text ); 
		       			
	      			if ( year == 1 ) // For first year, label the run number for each column 
					{
						Row r = CellUtil.getRow ( 3, sheet );
						HSSFCell c =  ( HSSFCell ) CellUtil.getCell( r, runNumber + 1 );
				       	c.setCellStyle( style );
				       	c.setCellValue( "  Run " + runNumber );
					}
	      			
	      			Row r = CellUtil.getRow  ( year + 3, sheet );
	       			if ( runNumber == 0) CellUtil.getCell ( r, 0 ).setCellValue(  year  ); // Put years only on first runNumber
				    CellUtil.getCell ( r, runNumber + 1 )
				                     .setCellValue(  annualSpeciesResultsTable.getValue( i,  field.text ) );
	       		}
			}
			
			FileOutputStream fileOut = new FileOutputStream( file );
            
	        wb.write( fileOut );
	        fileOut.flush();
		    fileOut.close();
			wb.close();
		    ok = true;
	        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //catch (InvalidFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return ok;
	}
}
