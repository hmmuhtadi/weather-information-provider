/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network_client;

/**
 *
 * @author Raffan
 */

// Client that reads and displays information sent from a Server.
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client implements Runnable// extends JFrame 
{
   public JTextField enterField; // enters information from user
   public JTextArea displayArea; // display information to user
   public ObjectOutputStream output; // output stream to server
   public ObjectInputStream input; // input stream from server
   public String message = ""; // message from server
   public String server; // host server for this application
   public Socket client; // socket to communicate with server
   NewJFrame_client obj;
   public Thread thr;
   public boolean stop = false;
   // initialize server and set up GUI
   public Client( NewJFrame_client obj )
   {
       System.out.println("in conturctir of cliebt");
      server = "127.0.0.1"; // set server to which this client connects
      this.obj = obj;
      this.thr = new Thread(this);
      thr.start();
   
   } // end Client constructor

   // connect to server and process messages from server
   public void run() {
        runClient();
   }
   
   public void runClient() 
   {
      try // connect to server, get streams, process connection
      {
         connectToServer(); // create a Socket to make connection
         getStreams(); // get the input and output streams
         processConnection(); // process connection
      } // end try
      catch ( EOFException eofException ) 
      {
         displayMessage( "\nClient terminated connection" );
      } // end catch
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // end catch
      finally 
      {
         closeConnection(); // close connection
      } // end finally
   } // end method runClient

   // connect to server
   public void connectToServer() throws IOException
   {      
      displayMessage( "Attempting connection\n" );

      // create Socket to make connection to server
      client = new Socket( InetAddress.getByName( server ), 12345 );

      // display connection information
      displayMessage( "Connected to: " +   client.getInetAddress().getHostName() );
   } // end method connectToServer

   // get streams to send and receive data
   public void getStreams() throws IOException
   {
      // set up output stream for objects
      output = new ObjectOutputStream( client.getOutputStream() );      
      output.flush(); // flush output buffer to send header information

      // set up input stream for objects
      input = new ObjectInputStream( client.getInputStream() );

      displayMessage( "\nGot I/O streams\n" );
   } // end method getStreams

   // process connection with server
   public void processConnection() throws IOException
   {
      do // process messages sent from server
      { 
         try // read message and display it
         {
             if(stop == true)
                 break;
             
            message = ( String ) input.readObject(); // read new message
            displayMessage( "\n" + message ); // display message
            
            if(message.equals("UPDATE"))
            {
                if(stop == true)
                 break;
                
                message = ( String ) input.readObject();
                displayMessage( "\n" + message ); // display message
                display_weather_update(message);              
                
            }
            else if(message.equals("CHOICE LIST"))
            {
                String myList="",allList="";
                
                if(stop == true)
                 break;
                
                message = ( String ) input.readObject();                
                myList = message;
                displayMessage( "\n" + message ); // display message
                
                message = ( String ) input.readObject();                
                allList = message;
                displayMessage( "\n" + message ); // display message
                display_choice_list(myList, allList);             
                
            }
            else if(message.equals("SENSOR UPDATE"))
            {
                String newlist="";
                
                if(stop == true)
                 break;
                
                message = ( String ) input.readObject();                
                newlist = message;
                displayMessage( "\n" + message ); // display message
                
                display_updated_sensor_list(newlist);               
            }
         } // end try
         catch ( ClassNotFoundException classNotFoundException ) 
         {
            displayMessage( "\nUnknown object type received" );
         } // end catch

      } while (true);
   } // end method processConnection

   // close streams and socket
   public void closeConnection() 
   {
      displayMessage( "\nClosing connection" );
      //setTextFieldEditable( false ); // disable enterField

      try 
      {
         sendData("TERMIANTE");
         output.close(); // close output stream
         input.close(); // close input stream
         client.close(); // close socket
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
         displayMessage( "\nSubscriber>>> " + message );
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
                //obj.jTextArea1.append(messageToDisplay);
                System.out.println(messageToDisplay);
            } // end method run
         }  // end anonymous inner class
      ); // end call to SwingUtilities.invokeLater
   } // end method displayMessage
   
   private void display_weather_update(String message)
   {
    
        StringTokenizer tokens = new StringTokenizer(message, " ");
                
        String location = tokens.nextToken();
        String temp = tokens.nextToken();
        String humid = tokens.nextToken();
        //String date = tokens.nextToken();
        
        Date date = new Date();
        obj.jTextArea2.append(date.toString());
        obj.jTextArea2.append("\n\tLocation: "+location);
        obj.jTextArea2.append("\n\t\tTemperature: "+temp);
        obj.jTextArea2.append("\n\t\tHumidity: "+humid+"\n\n\n");
   }

   synchronized private void display_choice_list(String list,String all)
   {
        DefaultListModel model = new DefaultListModel();
        DefaultListModel model2 = new DefaultListModel();
        
        obj.jList1_mylist.removeAll();
        obj.jList2_alllist.removeAll();
        
        
        if(list.contains("all"))
        {
            obj.jCheckBox1.setSelected(true);
        }
        else
        {
            System.out.println("in else of display choice");
            obj.jCheckBox1.setSelected(false);
            
            StringTokenizer token = new StringTokenizer(list," ");
        
            while(token.hasMoreTokens())
            {
                model.addElement(token.nextToken());            
            }
            obj.jList1_mylist.setModel(model);
                        
        }
               
        
        StringTokenizer token = new StringTokenizer(all," ");
        
        while(token.hasMoreTokens())
        {
            model2.addElement(token.nextToken());            
        }
        obj.jList2_alllist.setModel(model2);
        
   }
   // manipulates enterField in the event-dispatch thread
   synchronized private void display_updated_sensor_list(String list)
   {
        DefaultListModel model3 = new DefaultListModel();
        
        obj.jList2_alllist.removeAll();
        StringTokenizer token = new StringTokenizer(list," ");
        
        while(token.hasMoreTokens())
        {
            model3.addElement(token.nextToken());            
        }
        obj.jList2_alllist.setModel(model3);
        
        System.out.println("sensor update shown");
   }
   
   
} // end class Client
