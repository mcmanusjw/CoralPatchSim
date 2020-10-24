package mcmanusjw.CoralPatchSimJuly2019;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
//import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
//import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.EncryptedDocumentException;
//import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * 
 * @author John McManus
 *
 */
class StaticInputRunInfo 
{
	private static TableRunParamsListEnumMapsString runParamsTable;
	private static TableSpeciesListEnumMapsString   speciesInputTable;
	
	private static double originalSampleArea = 0;
	
	private static List < IntDouble > originalDataList = new ArrayList < IntDouble > ();
	
	private static Workbook workBook;
	
	private static File file;
    
	static boolean readInputFile( ) 
	{
//		System.out.println( "\n Starting readInputFile in StaticInputRunInfo\n ");//////////////////////////
		//String fileName = 
		//		("C:\\Users\\John McManus\\Documents\\CoralPatchSim\\CoralPatchSimInputAndOutputSample.xls");
		
		boolean fileSelected = false;
		while( !fileSelected )
		{
/*
			JFileChooser           chooser = new JFileChooser();
			chooser.setDialogTitle( "Select an Input Spreadsheet Workbook" );
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel file", "xls", "xlsx");
			chooser.setFileFilter( filter );
			File workingDirectory          = new File(System.getProperty("user.dir"));
			chooser.setCurrentDirectory(workingDirectory);
			int returnVal                  = chooser.showOpenDialog(null);
			if(returnVal == JFileChooser.APPROVE_OPTION) 
			{
				file = chooser.getSelectedFile();
			}
*/
			
			file = StaticRunSynchronizedFileChooser.getFile();
			
			if( file == null ) return false;

			// Set up a confirmation window
			Object[] options = {"Yes", "No", "Cancel"};
			JFrame frame = new JFrame();
			int n = JOptionPane.showOptionDialog( frame, "Is this the file you want? " + file.getName(),
					"Just to be Sure",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
					null, options, options[2]);
			switch ( n )
			{
				case 0: fileSelected = true; 
				case 1: break; // also moves to this from Case 0.
				case 2: //UtilJM.warner( "Using default data");
					    return false; // exits readInputFile
			}
		}
		
		try {
		
			InputStream inp = 
					new FileInputStream( file );

			workBook = WorkbookFactory.create(inp);
			Sheet sheet0 = workBook.getSheetAt( 0 );
			
			// Get number of series from row 1 cell 1 to dimension runParamsTable table
			Row  row  = sheet0.getRow ( 1 );
			Cell cell = row   .getCell( 1 );
			//System.out.println( cell );
			int numSeries  =  (int) Double.parseDouble( cell.toString() );
			runParamsTable = new TableRunParamsListEnumMapsString( numSeries );
			
			for( int i = 0; i < numSeries; i++ ) // loop by series map
			{
				//System.out.println( "Inputting series number: " + numSeries);/////////////////
				
				// Read run parameters
				int counter = 1; // Begins on second file line to index 1
				for( EnumRunParams field : EnumRunParams.values() ) //paramMap.keySet() )
				{
					//System.out.print( field + " " ); //////////////////////////////
					row  = sheet0.getRow ( counter );
					cell = row  .getCell( 1 );
					//System.out.println( cell );////////////////////////
					if (cell == null)
					{
						UtilJM.warner( "Found null entry in first sheet for entry: "
										+ counter  + " Stopping input.", true );
						return false;
					}
			
					runParamsTable.put( i, field.text, cell.toString() );
					counter++;
				}
			}
			
			int numSpecies = (int) runParamsTable.getDouble( 0, "NumBenthicTypes" );
			//System.out.println("\n numSpecies: " + numSpecies + "\n" );
			
			speciesInputTable = new TableSpeciesListEnumMapsString( numSpecies );

			Sheet sheet1 = workBook.getSheetAt( 1 );

			int counter = 0; // start on row 0
			for( EnumSpeciesInputField field : EnumSpeciesInputField.values() ) // by value = row
			{
				for ( int i = 0; i < speciesInputTable.numMaps(); i++ ) // by species = column
				{
					row  = sheet1.getRow( counter );
					cell = row  .getCell( i + 1   );
					//System.out.println( " cell: " + cell );
					if (cell == null)
					{
						UtilJM.warner( "Found null entry in workbook for species: "
					                    + i  + " Stopping input.", true );
						return false;
					}
					speciesInputTable.put( i, field.text, cell.toString() );
				}
				counter++;
			}
			
			boolean getOriginalData = runParamsTable.getBoolean( 0, "IncludeOriginalSizeDataTF" );
			
			if ( !getOriginalData ) return true;  // Skip the rest if no original data wanted
			
			Sheet sheet2 = workBook.getSheetAt( 2 );
			
			boolean firstLoop = true;
		    Iterator<Row> rowIterator = sheet2.rowIterator();
		    while (rowIterator.hasNext()) 
		    {
		        row = rowIterator.next();
		        if ( ( firstLoop ) && ( row.getCell( 1 ) != null ) ) //used to skip the 2 label lines 
		        {
		        	//originalSampleArea = Double.parseDouble( row.getCell( 1 ).toString() );
		            //System.out.println( "\n originalSampleArea: " + originalSampleArea );/////////////////
		            rowIterator.next(); // skip label line
		            rowIterator.next(); // skip 2nd label line ---- added 15 Dec 2019 <<<<<<<<<<<<<<<<<<<<<<<<<<<<
		            firstLoop = false;
		            continue;
		         }
		            
		         if ( ( row.getCell( 0 ) != null ) && ( row.getCell( 1 ) != null ) )
		         {	
		        	 int    x = ( int ) Double.parseDouble( row.getCell( 0 ).toString() );
		        	 double y =         Double.parseDouble( row.getCell( 1 ).toString() );
		            
		        	 originalDataList.add( new IntDouble ( x, y ) );
		          } 
		          else
		          {
		        	  //System.out.println( "Done reading this many original data entires: " 
		              //                    + originalDataList.size() );
		            	break;
		           }
		      }
 
		} 
		catch (EncryptedDocumentException e) 
		{
	    	UtilJM.warner( "Problem with input file encryption" );
	    	return false;

//			e.printStackTrace();
		} 
		//catch (InvalidFormatException e) {
//			e.printStackTrace();
//		}
	    catch (IOException e) 
	    {
	    	UtilJM.warner( "Problem with file input" );
	    	return false;
	   
			//e.printStackTrace();
		}
    
		//System.out.println( "Done reading sheet.\n");
		
		// //////////flag follows <<<<<<<<<<<<<<<<<<<<<<<
//		for( EnumRunParams field : EnumRunParams.values() )
//		{
			//System.out.println( field.getText() + ", \t\t" + runParamsTable.getString( 0, field.getText() ) );
			
//		}
//		for( EnumSpeciesInputField field : EnumSpeciesInputField.values() ) // by value = row
//		{
//			for ( int i = 0; i < speciesInputTable.numMaps(); i++ ) // by species = column
//			{
				//System.out.print( speciesInputTable.getValue( i, field) + "\t\t " );
//			}
			//System.out.println();
//		}
//		System.out.println();
//		for (IntDouble intDouble : originalDataList) 
//		{
			//System.out.println( intDouble );
//		}
		
	return true;
	}

	static TableRunParamsListEnumMapsString getRunParamsTable() {
		return runParamsTable;
	}

	static TableSpeciesListEnumMapsString getSpeciesInputTable() {
		return speciesInputTable;
	}

	static double getOriginalSampleArea() {
		return originalSampleArea;
	}

	static List<IntDouble> getOriginalDataList() {
		return originalDataList;
	}
}
