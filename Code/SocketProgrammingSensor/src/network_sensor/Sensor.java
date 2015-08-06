/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network_sensor;

/**
 *
 * @author Raffan
 */
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.SwingUtilities;

public class Sensor 
{   
   public ObjectOutputStream output; // output stream to server
   //public ObjectInputStream input; // input stream from server
   public String message = ""; // message from server
   public String server; // host server for this application
   public Socket sensor; // socket to communicate with server
   NewJFrame_sensor obj;


   // initialize server and set up GUI
   public Sensor(NewJFrame_sensor obj) throws IOException//( NewJFrame_sensor obj )
   {      
      server = "127.0.0.1"; // set server to which this sensor connects
      this.obj = obj;      
      
   } // end Sensor constructor
   // connect to server and process messages from server
   public void runSensor() 
   {
      try // connect to server, get streams, process connection
      {
         connectToServer(); // create a Socket to make connection
         getStreams(); // get the input and output streams
         //processConnection(); // process connection
      } // end try
      catch ( EOFException eofException ) 
      {
         displayMessage( "\nSensor terminated connection" );
      } // end catch
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // end catch
      /*finally 
      {
         closeConnection(); // close connection
      } // end finally*/
   } // end method runSensor*/

   // connect to server
   public void connectToServer() throws IOException
   {      
      displayMessage( "Attempting connection\n" );

      // create Socket to make connection to server
      sensor = new Socket( InetAddress.getByName( server ), 12345 );

      // display connection information
      displayMessage( "Connected to: " + 
         sensor.getInetAddress().getHostName() );
   } // end method connectToServer


   // get streams to send and receive data
   public void getStreams() throws IOException
   {
      // set up output stream for objects
      output = new ObjectOutputStream( sensor.getOutputStream() );      
      output.flush(); // flush output buffer to send header information

      displayMessage( "\nGot I/O streams\n" );
   } // end method getStreams


   // close streams and socket
   public void closeConnection() throws InterruptedException 
   {
      displayMessage( "\nClosing connection" );
      try 
      {
         sendData("TERMIANTE");         
         output.close(); // close output stream         
         sensor.close(); // close socket
      } // end try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // end catch
   } // end method closeConnection
   // send message to server
   public void sendData( String message )
   {
      try // send object to server
      {
         output.writeObject(message );
         output.flush(); // flush data to output
         displayMessage( "\nsensor>>> " + message );
      } // end try
      catch ( IOException ioException )
      {
         //displayArea.append( "\nError writing object" );
      } // end catch
   } // end method sendData

   // manipulates displayArea in the event-dispatch thread
   public void displayMessage( final String messageToDisplay )
   {
      SwingUtilities.invokeLater(
         new Runnable()
         {
            public void run() // updates displayArea
            {
               //displayArea.append( messageToDisplay );
                //System.out.println(messageToDisplay);
                obj.jTextArea1.append(messageToDisplay);
            } // end method run
         }  // end anonymous inner class
      ); // end call to SwingUtilities.invokeLater
   } // end method displayMessage

   // manipulates enterField in the event-dispatch thread
   
} // end class Sensor

/**************************************************************************
 * (C) Copyright 1992-2007 by Deitel & Associates, Inc. and               *
 * Pearson Education, Inc. All Rights Reserved.                           *
 *                                                                        *
 * DISCLAIMER: The authors and publisher of this book have used their     *
 * best efforts in preparing the book. These efforts include the          *
 * development, research, and testing of the theories and programs        *
 * to determine their effectiveness. The authors and publisher make       *
 * no warranty of any kind, expressed or implied, with regard to these    *
 * programs or to the documentation contained in these books. The authors *
 * and publisher shall not be liable in any event for incidental or       *
 * consequential damages in connection with, or arising out of, the       *
 * furnishing, performance, or use of these programs.                     *
 *************************************************************************/

