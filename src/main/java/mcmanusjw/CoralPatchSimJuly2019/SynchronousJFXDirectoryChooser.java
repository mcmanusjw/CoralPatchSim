package mcmanusjw.CoralPatchSimJuly2019;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import javafx.stage.DirectoryChooser;

/**
 * from: https://stackoverflow.com/questions/28920758/javafx-filechooser-in-swing
 * posted by Sergei Tachenov
 * 
 * @author (modifier) John McManus
 *
 * A utility class that summons JavaFX DirectoryChooser from the Swing EDT (Event Dispatch Thread).
 * (Or anywhere else for that matter.) JavaFX should be initialized prior to
 * using this class (e. g. by creating a JFXPanel instance). It is also
 * recommended to call Platform.setImplicitExit(false) after initialization
 * to ensure that JavaFX platform keeps running. Don't forget to call
 * Platform.exit() when shutting down the application, to ensure that
 * the JavaFX threads don't prevent JVM exit.
 * 
 * To start JavaFX, perhaps I should open a JFrame and add the JFXPanel, call Platform.setImplicitExit(false)
 * and setVisible( false ) on the JFrame so it won't show up. If needed, I could add a Platform.exit() into the 
 * JFrame close method, but the routine already has this so maybe unnecessary.
 * 
 */

public class SynchronousJFXDirectoryChooser 
{
    private final Supplier<DirectoryChooser> directoryChooserFactory;
    
    /**
     * @param directoryChooserFactory the function used to construct new choosers
     */
    public SynchronousJFXDirectoryChooser( Supplier< DirectoryChooser > directoryChooserFactory ) 
    {
        this.directoryChooserFactory = directoryChooserFactory;
    }

    /**
     * Shows the DirectoryChooser dialog by calling the provided method.
     * 
     * Waits for one second for the dialog-showing task to start in the JavaFX
     * event thread, then throws an IllegalStateException if it didn't start.
     * 
     * @see #showDialog(java.util.function.Function, long, java.util.concurrent.TimeUnit) 
     * @param <T> the return type of the method, usually File or List&lt;File&gt;
     * @param method a function calling one of the dialog-showing methods
     * @return whatever the method returns
     */
    public < T > T showDialog( Function< DirectoryChooser, T > method )
    {
        return showDialog( method, 1, TimeUnit.SECONDS );
    }

    /**
     * Shows the DirectoryChooser dialog by calling the provided method. The dialog 
     * is created by the factory supplied to the constructor, then it is shown
     * by calling the provided method on it, then the result is returned.
     * <p>
     * Everything happens in the right threads thanks to
     * {@link SynchronousJFXCaller}. The task performed in the JavaFX thread
     * consists of two steps: construct a chooser using the provided factory
     * and invoke the provided method on it. Any exception thrown during these
     * steps will be rethrown in the calling thread, which shouldn't
     * normally happen unless the factory throws an unchecked exception.
     * </p>
     * <p>
     * If the calling thread is interrupted during either the wait for
     * the task to start or for its result, then null is returned and
     * the Thread interrupted status is set.
     * </p>
     * @param <T> return type (usually File or List&lt;File&gt;)
     * @param method a function that calls the desired FileChooser method
     * @param timeout time to wait for Platform.runLater() to <em>start</em>
     * the dialog-showing task (once started, it is allowed to run as long
     * as needed)
     * @param unit the time unit of the timeout argument
     * @return whatever the method returns
     * @throws IllegalStateException if Platform.runLater() fails to start
     * the dialog-showing task within the given timeout
     */
    
    public < T > T showDialog( Function< DirectoryChooser, T >
    						   method,
                               long timeout, 
                               TimeUnit unit
                             ) 
    {
        Callable< T > task = () -> 
        {
            DirectoryChooser chooser = directoryChooserFactory.get();
            return method.apply( chooser );
        };
        
        SynchronousJFXCaller<T> caller = new SynchronousJFXCaller<>( task );
        
        try 
        {
        	return caller.call( timeout, unit );
        } 
        catch ( RuntimeException | Error ex ) 
        {
        	throw ex;
        } 
        catch ( InterruptedException ex ) 
        {
        	Thread.currentThread().interrupt();
        	return null;
        } 
        catch ( Exception ex ) 
        {
        	throw new AssertionError("Got unexpected checked exception from"
                    + " SynchronousJFXCaller.call()", ex);
        }
    }

    /**
     * Shows a DirectoryChooser using DirectoryChooser.showSaveDialog().
     * 
     * @see #showDialog(java.util.function.Function, long, java.util.concurrent.TimeUnit) 
     * @return the return value of FileChooser.showSaveDialog()
     */
    public File showSaveDialog() 
    {
        return showDialog(chooser -> chooser.showDialog(null));
    }

 
    /**
     * Use this only for testing
     * 
     */
    
/*    public static void main( String[] args ) // Use for testing -- console output will be a string
    {
        @SuppressWarnings("unused") // Sergie seems to have put the following in to initialize JavaFX
    	javafx.embed.swing.JFXPanel dummy = new javafx.embed.swing.JFXPanel();
        
        Platform.setImplicitExit(false);
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
        	 
           System.out.println( file.getAbsolutePath() ); // This is the String we are after
            
            // this will throw an exception: NOTE: I (JM) commented this out to kill an illegal shutdown exception.
            //chooser.showDialog( ch -> ch.showDialog( null ), 1, TimeUnit.NANOSECONDS );
        }
        finally 
        {
             Platform.exit();
         }
    }
*/
}
