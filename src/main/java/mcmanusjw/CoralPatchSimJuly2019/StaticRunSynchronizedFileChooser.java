package mcmanusjw.CoralPatchSimJuly2019;

import java.io.File;

import javafx.stage.FileChooser;

class StaticRunSynchronizedFileChooser 
{
	static File getFile()
	{
		File file = null;
		
//        @SuppressWarnings("unused") // Not sure why Sergei put put the following line in, but I will leave it with a suppress warnings.
//		javafx.embed.swing.JFXPanel dummy = new javafx.embed.swing.JFXPanel();
        
//        Platform.setImplicitExit(false);

		try {
            SynchronousJFXFileChooser chooser = new SynchronousJFXFileChooser(() -> {
                FileChooser ch = new FileChooser();
                ch.setTitle("Choose the file with your input data");
                return ch;
            });
            file = chooser.showOpenDialog();
            //System.out.println(file);
            
            // this will throw an exception: NOTE: deleted next line to prevent illegal shutdown exception
            // chooser.showDialog(ch -> ch.showOpenDialog(null), 1, TimeUnit.NANOSECONDS);
        
        }
        finally 
        {
 //           Platform.exit();
        }

        return file;
	}
	

}
