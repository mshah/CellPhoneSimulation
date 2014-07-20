import java.io.*;

import com.navis.entitygeneratorapp.datatypes.Integer;

public class ReadWithScanner{

   /*
    * @param aFileName full name of an existing, readable file
    */
    public ReadWithScanner(String aFileName){
        fFile = new File(aFileName);
    }

    // template method that calls @link #processLine(String)
    public final void processLineByLine() throws FileNotFoundException{
        Scanner scanner = new Scanner(fFile);
        try{
            // first use a scanner to get each line
            while( scanner.hasNextLine()){
                processLine(scanner.nextLine());
            }
            finally{
                // ensure the underlying stream is always closed
                scanner.close();
            }
        }
    }

   /*
    * Out file looks like this:
    * Number Time    Cell    Duration
    * 1  1   1   25
    */
    public void processLine(String aLine){
        // use a second scanner to parse the content of each line
        Scanner scanner = new Scanner(aLine);
        scanner.useDelimiter("\t");

        String header = "Number\tTime\tCell\tDuration";
        if(scanner.hasNext()){
            // only after the first line
            if(!aLine.equals(header)){
                String number = scanner.next();
                int nNumber = Integer.parseInt(number);
                String time = scanner.next();
                int nTime = Integer.parseInt(time);
                String cell = scanner.next();
                int nCell = Integer.parseInt(cell);
                String duration = scanner.next();
                int nDuration = Integer.parseInt(duration);
                Event callAttempt = new Event(nNumber, nTime, nCell, nDuration);

                fQueue.AddEvent(callAttempt);
            }
        }else{
            log("Empty of invalid line. Unable to process.");
        }
        // no need for finally here, since String is source
        scanner.close();
    }

    // accessors
    public void SetQueue(EventQueue inQueue){
        fQueue = inQueue;
    }

    // private
    private final File fFile;
    private EventQueue fQueue;

    private static void log(Object aObject){
        System.out.println(String.valueOf(aObject));
    }
}