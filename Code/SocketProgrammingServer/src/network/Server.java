/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

/**
 *BeansBeans
 * @author Raffan
 */
// Fig. 24.5: Server.java
// Set up a server that will receive a connection from a client, send 
// a string to the client, and close the connection.
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.SwingUtilities;

public class Server implements Runnable{
  //private JTextField enterField; // inputs message from user
   //private JTextArea displayArea; // display information to user
   public NewJFrame frame;
   private ObjectOutputStream output; // output stream to client
   private ObjectInputStream input; // input stream from client
   private ServerSocket server; // server socket
   private Socket connection; // connection to client
   private int counter = 1; // counter of number of connections
   private Thread thr;
   public boolean stop = false;

   // set up GUI
   public Server(NewJFrame obj)
   {
      //super( "Server" );   
       System.out.println("server is starting..");
       frame = obj;
       this.thr = new Thread(this);
       thr.start();

   } // end Server constructor
   
    public void run() {
        runServer();
    }

   // set up and run server 
   public void runServer()
   {
      try // set up server to receive connections; process connections
      {
         server = new ServerSocket( 12345, 100 ); // create ServerSocket

         while(stop == false)//( true ) 
         {
             System.out.println("server is listening..");
            try 
            {
                connection = server.accept();
                new ServerThread(frame,connection);               
            } // end try
            catch ( EOFException eofException ) 
            {
               //displayMessage( "\nServer terminated connection" );                
            } // end catch              
         } // end while
         //server.close();
         System.out.println("server is now goint to close.");
         closeConnection();
         
      } // end try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // end catch
   } // end method runServer

   // wait for connection to arrive, then display connection info
   private void waitForConnection() throws IOException
   {
      //displayMessage( "Waiting for connection\n" );
      connection = server.accept(); // allow server to accept connection            
      //displayMessage( "Connection " + counter + " received from: " + connection.getInetAddress().getHostName() );
   } // end method waitForConnection
   

   private void closeConnection() throws IOException 
   {
       System.out.println("server is closing..");
       server.close();
   } // end method closeConnection
  
   private void displayMessage( final String messageToDisplay )
   {
      SwingUtilities.invokeLater(
         new Runnable() 
         {
            public void run() // updates displayArea
            {
               System.out.println("terminating the server...");
            } // end method run
         } // end anonymous inner class
      ); // end call to SwingUtilities.invokeLater
   } // end method displayMessage
} // end class Server

