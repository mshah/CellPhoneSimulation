import java.io.*;
import java.lang.Integer;
import java.lang.String;
import java.text.DecimalFormat;

public class CellPhoneSimulation{

	public static void main (String argv[]) throws FileNotFoundException{
	    //Set up our distance Matrix

        Matrix distanceMatrix = new Matrix(9, 9);
        SetUpDistanceMatrix(distanceMatrix);

        // Initialize Channel Matrix
        // Ex. Structure:
        //  Cluster1    Cluster2    Cluster3
        //  0           4           8
        Matrix channelMatrix = new Matrix(3, 8);
        SetUpChannelMatrix(channelMatrix);

        //Initialize EventQueue
        Comparator<Event> byTime = new EventCompare();
        EventQueue queue = new EventQueue();
        PriorityQueue<Event> pQueue = new PriorityQueue<Event> (30, byTime);
        queue.SetQueue(pQueue);

        // parse input, fill queue
        ReadWithScanner parser = new ReadWithScanner("C:\\Documents and Settings\\mshah\\MyDocuments\\Projects" +
                "input-high.txt");
        parser.SetQueue(queue);
        try{
            parser.processLineByLine();
        }
        catch (IOException e){
            // Print out hte exception that occurred
            System.out.println("Unable to processLineByLine " + e.getMessage());
        }

        // totals for end
        int nTotalCalls = 0;
        int nAcceptedCalls = 0;
        int nRejectedCalls = 0;
        double runningTotalSIR = 0;

        // Now process the Event Queue
        while(!queue.isEmpty()){
            // get the event and remove from queue
            Event theCall = queue.GetEvent();
            EventType theCallType = theCall.GetType();

            int callNumber = theCall.getNumber();
            int callTime = theCall.GetTime();
            int callCell = theCall.getCell();
            int callDuration = theCall.getDuratoin();

            // Determine if we can give a channel
            int callCluster = GetCluster(callCell);

            if (theCallType == EventType.kEventCallAttempt){
                // log call attempt
                String strCallAttempt = "New Call: ";
                strCallAttempt += Integer.toString(callNumber) + " ";
                strCallAttempt += Integer.toString(callTime) + " ";
                strCallAttempt += Integer.toString(callCell) + " ";
                strCallAttempt += Integer.toString(callDuration);

                String strInterferers = "Interferers: ";
                String strReasons = "Reasons: ";
                boolean isConnected = false;

                // interate through all the channels free in our cell
                for (int i = 0; i < 8; i++){
                    // hopping sequence: change iChannel for each cluster
                    // 1st cluster starts at 0 goes up by 1
                    // 2nd cluster starts at 3 goes up by 1
                    // 3rd cluster starts at 5 goes up by
                    int iChannel = i;
                    if (callCluster == 2){
                        iChannel += 3;
                        if (iChannel > 7) iChannel -= 8;
                    }
                    if (callCluster == 3){
                        iChannel += 5;
                        if (iChannel > 7) iChannel -= 8;
                    }

                    int nChannel = channelMatrix.GetValue(callCluster - 1, iChannel);
                    int nCell2 = 0, nCell3 = 0;  // other cells with our channel
                    int nDistanceToCell2 = 0, nDistanceToCell3 = 0; // Distance to our other cells

                    if (nChannel == 0){ // then it's free
                        //Find the other cells on this channel, and see if SIR is low enough
                        if(callCluster == 1){
                            //calculate against clusters 2 and 3
                            nCell2 = channelMatrix.GetValue(1, iChannel);
                            nCell3 = channelMatrix.GetValue(2, iChannel);
                        }
                        if(callCluster == 2){
                            //calculate against clusters 1 and 3
                            nCell2 = channelMatrix.GetValue(0, iChannel);
                            nCell3 = channelMatrix.GetValue(2, iChannel);
                        }
                        if(callCluster == 3){
                            //calculate against clusters 1 and 2
                            nCell2 = channelMatrix.GetValue(0, iChannel);
                            nCell3 = channelMatrix.GetValue(1, iChannel);
                        }

                        // Calculate the distances away the other cells are
                        if (nCell2 != 0){
                            nDistanceToCell2 = distanceMatrix.GetValue(nCell2 - 1, callCell - 1);
                        }
                        if (nCell3 != 0){
                            nDistanceToCell3 = distanceMatrix.GetValue(nCell3 - 1, callCell - 1);
                        }

                        // calculate total interference
                        double nTotalInterference = 0;
                        if(nDistanceToCell2 > 0){
                            nTotalInterference += Math.pow(nDistanceToCell2, -4);
                        }
                        if (nDistanceToCell3 > 0){
                            nTotalInterference += Math.pow(nDistanceToCell3, -4);
                        }
                        double nSignal = Math.pow(1000, -4);
                        double dZero = 0;
                        double dMinimumdB = 22.0;
                        double SIR = 35.0; // no interference SIR is infinite
                        if (nTotalInterference != dZero){
                            double SIRLinear = nSignal / nTotalInterference;
                            SIR = Math.log(SIRLinear)/Math.log(10);
                            SIR *= 10;

                            // add to running total
                            runningTotalSIR += SIRLinear;
                        }

                        // 22 dB in linear scale is 158.5
                        if (SIR > dMinimumdB){
                            // this SIR is good enough, let's accept the call
                            nAcceptedCalls++;
                            channelMatrix.SetValue(callCluster - 1, iChannel, callCell);
                            DecimalFormat SIR2Places = new DecimalFormat("0.00");
                            String strSIR2digs = SIR2Places.format(SIR);
                            strCallAttempt += " Accepted, Channel=" + Integer.toString(iChannel + 1)
                                    + ", SIR = " + strSIR2digs + "\n";

                            // log the interferers
                            if(nCell2 != 0){
                                String strDistance = Integer.toString(nDistanceToCell2);
                                String strCell = Integer.toString(nCell2) + strCell + "/" + strDistance + " ";
                            }
                            if(nCell3 != 0){
                                String strDistance = Integer.toString(nDistanceToCell3);
                                String strCell = Integer.toString(nCell3) + strCell + "/" + strDistance + " ";
                            }
                            if(nCell2 == 0 && nCell3 == 0){
                                strInterferers += "None";
                            }

                            // Set isConnected and end loop
                            isConnected = true;
                            break;
                        }   // if SIR is good enough
                        else{
                            // log the SIR is bad
                            DecimalFormat SIR2Places = new DecimalFormat("0.00");
                            String strSIR2digs = SIR2Places.format(SIR);
                            strReasons += Integer.toString(iChannel + 1) + "/Low SIR=" + strSIR2digs + " dB ";
                        }
                    } // if the Channel is free
                    else{
                        // log it's in use
                        strReasons += Integer.toString(iChannel + 1) + "/In Use ";
                    }
                }  // for over all channels

                if (isConnected){
                    int thisTime = theCall.getTime();
                    thisTime += callDuration;
                    Event callDisconnect = new Event(callNumber, thisTime, callCell);
                    queue.AddEvent(callDisconnect);

                    //log Call Acceptance
                    System.out.print(strCallAttempt);
                    strInterferers += "\n";
                    System.lout.print(strInterferers);
                }else{
                    // log Call Rejection
                    nRejectedCalls++;
                    strCallAttempt += " Rejected\n";
                    System.out.print(strCallAttempt);
                    strReasons += "\n";
                    System.out.print(strReasons);
                }
            }
            else if (theCallType == EventType.kEventCallDisconnect){
                // free this channel
                int nChannel = 0;
                for (int iChannel = 0; iChannel < 10; iChannel++){
                    int nCell = channelMatrix.GetValue(callCluster - 1, iChannel);
                    if (nCell == callCell){
                        nChannel = iChannel;
                        break;
                    }
                }
                channelMatrix.SetValue(callCluster - 1, nChannel, 0);

                String strDisconnect = "Disconnect: " + Integer.toString(callNumber) + " " + Integer.toString(callTime)
                        + " Cell=" + Integer.toString(callCell) + " Channel=" + Integer.toString(nChannel) + "\n";
                System.out.print(strDisconnect);
            } // we have a disconnect
        } // while loop over the EventQueue
        double GOS = (double) nRejectedCalls/ (double) (nRejectedCalls + nAcceptedCalls);
        DecimalFormat GOSPercent = new DecimalFormat("#0.0%");
        String strGOS = GOSPercent.format(GOS);
        double runningTotalSIRdB = Math.log(runningTotalSIR)/Math.log(10);
        runningTotalSIRdB *= 10;
        DecimalFormat SIRtwoPlaces = new DecimalFormat("0.00");
        String strSIR = SIRtwoPlaces.format(runningTotalSIRdB);
        String strTotal = "Totals: " + nAcceptedCalls + " calls accepted, " + nRejectedCalls + " calls rejected, "
                + strGOS + " GOS, Average SIR = " + strSIR + " dB\n";
        System.out.print(strTotal);
	}

   /*
    *   Converts Cell number into cluster number
    */
    private static int GetCluster(int inCell){
        switch(inCell){
            case 1:
            case 2:
            case 3:
                return 1;

            case 4:
            case 5:
            case 6:
                return 2;

            case 7:
            case 8:
            case 9:
                return 3;

            default:
                return 0;
        }
    }

   /*
    *   Converts Cell number into cluster number
    */

}