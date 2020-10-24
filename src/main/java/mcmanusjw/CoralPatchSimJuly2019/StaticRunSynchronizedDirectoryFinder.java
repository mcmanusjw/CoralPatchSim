package mcmanusjw.CoralPatchSimJuly2019;

import java.io.File;

import javafx.application.Platform;
import javafx.stage.DirectoryChooser;

class StaticRunSynchronizedDirectoryFinder 
{
	static String getDirectoryString()
	{
//        @SuppressWarnings("unused") // Sergie seems to have put the following in to initialize JavaFX
//    	javafx.embed.swing.JFXPanel dummy = new javafx.embed.swing.JFXPanel();
        
        String directoryString = " ";
        
//        Platform.setImplicitExit(false);
        try 
        {
        	SynchronousJFXDirectoryChooser chooser = new SynchronousJFXDirectoryChooser
        	(   () -> 
        		{
        			DirectoryChooser ch = new DirectoryChooser();
        			ch.setTitle("Choose a folder for output");
        			return ch;
        		}
        	);
            
        	File file = chooser.showSaveDialog();
        	 
        	if ( file != null ) 
        	{
        		directoryString = file.getAbsolutePath();
        	}
        	else
        	{
        		// Use this only because this comes before the Mason graphics are set up (which need special shutdown)
        		Platform.exit();
        		System.exit( 0 ); 
        	}
        	
        	//System.out.println( directoryString ); // This is the String we are after
            
            // this will throw an exception: NOTE: I (JM) commented this out to kill an illegal shutdown exception.
            //chooser.showDialog( ch -> ch.showDialog( null ), 1, TimeUnit.NANOSECONDS );
        }
        finally 
        {
//             Platform.exit();
         }
        
        return directoryString;
	}

}
