/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Raffan
 */
public class ServerThread implements Runnable{
    
   public NewJFrame frame;
   private ObjectOutputStream output; // output stream to client
   private ObjectInputStream input; // input stream from client
   //private ServerSocket server; // server socket
   private Socket connection;
   private Thread thr;
   public String clientType = "";
   public int isConnected, actualEntry;
   
   public ServerThread(NewJFrame obj, Socket socket)
   {
        frame = obj;
        connection = socket;
        this.thr = new Thread(this);
	thr.start();
   }
    public void run() {
        try {
            getStreams();
            processConnection();
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeConnection();
        }
    }
    
    private void getStreams() throws IOException
    {
      // set up output stream for objects
      output = new ObjectOutputStream( connection.getOutputStream() );
      output.flush(); // flush output buffer to send header information

      // set up input stream for objects
      input = new ObjectInputStream( connection.getInputStream() );

      displayMessage( "\nGot I/O streams\n" );
   } // end method getStreams

   // process connection with client
   private void processConnection() throws IOException
   {
         String message = "Connection successful";
         displayMessage( message ); // send connection successful message

         try // read message and display it
         {
            message = ( String ) input.readObject(); // read new message
            displayMessage( "\n" + message ); // display message

            if(message.equals( "SUBSCRIBER" ))
            {
                clientType = "SUBSCRIBER";
                handleSubscriber();
            }            
            else if(message.equals( "SENSOR" ))
            {
                clientType = "SENSOR";
                handleSensor(); 
            }   
            else
            {
                closeConnection();
            }
         } // end try
         catch ( ClassNotFoundException classNotFoundException ) 
         {
            displayMessage( "\nUnknown object type received" );
         } // end catch

   } // end method processConnection

   
   private void handleSubscriber() throws IOException
   {       
       System.out.println("in subscriber");
       String entry="";       
       
       //System.out.println("in sensor");
       String message = "";
      
      do 
      { 
         try // read message and display it
         {
            message = ( String ) input.readObject(); // read new message            
            //displayMessage( "\n" + message ); // display message
            
            if(message.equals( "REGISTRY" ))
            {
                try // read message and display it
                 {
                    message = ( String ) input.readObject(); // read new message  
                    displayMessage( "\n" + message );
                 } // end try
                 catch ( ClassNotFoundException classNotFoundException ) 
                 {
                    displayMessage( "\nUnknown object type received" );
                 } // end catch
               
                entry = message;
                isConnected = registry(frame.subscriberList,frame.connectedSubscriberList, entry);
                
                if(isConnected >= 0)
                {
                    frame.refreshLists(frame.connectedSubscriberList, frame.Connected_Subscribers, 1);
                    
                }
                
                
            } 
            else if(message.equals( "SEND CHOICE" ))
            {
                send_choice();
            }
            else if(message.equals( "UPDATE CHOICE" ))
            {
                System.out.println("update received");
                try // read message and display it
                 {
                    message = ( String ) input.readObject(); // read new message  
                    displayMessage( "\n"+message );
                 } // end try
                 catch ( ClassNotFoundException classNotFoundException ) 
                 {
                    displayMessage( "\nUnknown object type received" );
                 }
                update_choice_list(message);
            }
            else
            {
                displayMessage("in else");     
                closeConnection();
                remove_registry(frame.connectedSubscriberList, entry);
                frame.refreshLists(frame.connectedSubscriberList, frame.Connected_Subscribers, 1);                
                isConnected =-1;
                break;
            
            }
         } // end try
         catch ( ClassNotFoundException classNotFoundException ) 
         {
            displayMessage( "\nUnknown object type received" );
         } // end catch

      } while ( true );
   
   }
   
  private void handleSensor() throws IOException
   {
       String entry="";
       JLabel label=null;
       Animator anim = null;
       
       System.out.println("in sensor");
       String message = "",location="",data="";
       
       
      do // process messages sent from client
      { 
         try // read message and display it
         {
            message = ( String ) input.readObject(); // read new message  
            System.out.println("in handle sensor");
            displayMessage( "\n" + message ); // display message
            
            if(message.equals( "DATA" ))
            {
                System.out.println("in Data......................");
                 try // read message and display it
                 {
                    message = ( String ) input.readObject(); // read new message  
                    displayMessage( "\n"+message );
                 } // end try
                 catch ( ClassNotFoundException classNotFoundException ) 
                 {
                    displayMessage( "\nUnknown object type received" );
                 } // end catch
                System.out.println("in Data......................after read "+isConnected);
                data = message;
                if(isConnected >=0)
                {
                    update_weather(frame.sensorData, message, actualEntry);
                    notify_subscribers(location,data);
                }
               
            }
            
            else if(message.equals( "REGISTRY" ))
            {
                try // read message and display it
                 {
                    message = ( String ) input.readObject(); // read new message  
                    displayMessage( "\n" + message );
                 } // end try
                 catch ( ClassNotFoundException classNotFoundException ) 
                 {
                    displayMessage( "\nUnknown object type received" );
                 } // end catch
               
                
                entry = message;
                isConnected = registry(frame.sensorList,frame.connectedSensorList, entry);
                
                if(isConnected >= 0)
                {
                    //geting the location of the sensor
                    StringTokenizer fields = new StringTokenizer(entry, " ");
                    location = fields.nextToken();
                    System.out.println("the location of the sensor is " + location);
                    
                    
                    frame.refreshLists(frame.connectedSensorList, frame.Connected_Sensors, 1);
                    
                    label = frame.sensorLabel.get(actualEntry);//getSensorLabel( isConnected,actualEntry);
                    anim = new Animator(label);
                    anim.execute();
                }
                
                
            } 
            else// if(message.equals( "TERMINATE" ))
            {                   
                closeConnection();
                remove_registry(frame.connectedSensorList, entry);
                frame.refreshLists(frame.connectedSensorList, frame.Connected_Sensors, 1);
                label.setIcon(new javax.swing.ImageIcon(getClass().getResource("red.PNG")));
                anim.stopAnimation();                
                isConnected =-1;
                break;
            }
            
         } // end try
         catch ( ClassNotFoundException classNotFoundException ) 
         {
            displayMessage( "\nUnknown object type received" );
         } // end catch

      } while(true);// ( !message.equals( "TERMINATE" ) );  
   }  
   private void closeConnection()
   {
      displayMessage( "\nTerminating connection\n" );      

      try 
      {
         output.close(); // close output stream
         input.close(); // close input stream
         connection.close(); // close socket
      } // end try
      catch ( IOException ioException ) 
      {
         ioException.printStackTrace();
      } // end catch
   } // end method closeConnection

   // send message to client
   public void sendData( String message )
   {
      try // send object to client
      {
         output.writeObject( message );
         output.flush(); // flush output to client
         displayMessage( "\nSERVER>>> " + message );
      } // end try
      catch ( IOException ioException ) 
      {
         //displayArea.append( "\nError writing object" );
      } // end catch
   } // end method sendData
   
   private void displayMessage( final String messageToDisplay )
   {
      SwingUtilities.invokeLater(
         new Runnable() 
         {
            public void run() // updates displayArea
            {
               //displayArea.append( messageToDisplay ); // append message
                System.out.println(messageToDisplay);
            } // end method run
         } // end anonymous inner class
      ); // end call to SwingUtilities.invokeLater
   } // end method displayMessage

   synchronized public void update_choice_list(String choice)
   {
       frame.choiceList.remove(actualEntry);
       frame.choiceList.add(actualEntry,choice);      
       frame.refreshSubscriberAndchoice();
   }
   
   synchronized public int registry(ArrayList<String> all, ArrayList<String> connected,String entry)
   {
       System.out.println("in registry :entry is "+entry);
        
       if(all.contains(entry))
        {
            connected.add(entry);
            System.out.println("The sensor is now connected");
            //frame.refreshLists(connected, frame.Connected_Sensors, 1);
            //return true;
            
            actualEntry = all.indexOf(entry);            
            System.out.println("actual entry is "+ actualEntry);
            
            if(clientType.equals("SUBSCRIBER"))
            {
                frame.subscriberHandlerThread.add(this);
            }
            return connected.indexOf(entry);
        }
        
        System.out.println("The sensor is not a valid sensor");
        return -1;
       
   }
   
   synchronized public void remove_registry(ArrayList<String> connected,String entry)
   {
   
        if(connected.contains(entry))
        {
            connected.remove(entry);
            System.out.println("The sensor is now connected");
            //frame.refreshLists(connected, frame.Connected_Sensors, 1);            
        }
        
        System.out.println("The sensor is not a valid sensor");
   }
   synchronized public void update_weather(ArrayList<String> list,String data,int index)
   {
       list.remove(index);
       list.add(index, data);      
   }
   synchronized public void notify_subscribers(String location, String data)
   {
      // System.out.println("the size of connected subscriber is  :"+frame.connectedSubscriberList.size());
        for(int i=0;i<frame.connectedSubscriberList.size();i++)
        {
            int index =  frame.subscriberList.indexOf(frame.connectedSubscriberList.get(i));
            //System.out.println("the index is :"+index);
            if(frame.choiceList.get(index).contains(location) || frame.choiceList.get(index).contains("all"))
            {
                frame.subscriberHandlerThread.get(i).sendData("UPDATE");                
                frame.subscriberHandlerThread.get(i).sendData(location+" "+data+" ");
            }
        }
   }
   
   synchronized public void send_choice()
   {
       sendData("CHOICE LIST");  
       
       String allChoices = "";
       
       for(int i=0;i<frame.sensorLocation.size();i++)
       {
            allChoices += frame.sensorLocation.get(i);
            allChoices +=" ";
       }
       sendData(frame.choiceList.get(actualEntry));    
       sendData(allChoices.trim());  
       
   }  
   
   synchronized public void send_sensor_update_notification()
   {
       sendData("SENSOR UPDATE");  
       
       String allChoices = "";
       
       for(int i=0;i<frame.sensorLocation.size();i++)
       {
            allChoices += frame.sensorLocation.get(i);
            allChoices +=" ";
       }
       //sendData(frame.choiceList.get(actualEntry));    
       sendData(allChoices.trim());  
       
       System.out.println("sensor update notification is sent");
       
   }   
   
}
