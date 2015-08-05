/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package network;

/**
 *
 * @author Raffan
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingWorker;
import javax.swing.JLabel;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Animator extends SwingWorker< String, Object >
{
       public int count = 0; 
       private final JLabel resultJLabel; 
       private boolean continueAnimation = true;
       private final int ANIMATION_DELAY = 500; // millisecond delay      
       private javax.swing.Timer animationTimer;
       // constructor
       public Animator(  JLabel label )
       {
          resultJLabel = label;    
          resultJLabel.setVisible(true);
       } // end Animator constructor


       // code to run on the event dispatch thread when doInBackground returns
       protected void done()
       {
       } // end method done

       public void startAnimation()
        {
          if ( animationTimer == null ) 
          {          
             animationTimer = new javax.swing.Timer( ANIMATION_DELAY, new TimerHandler() );
             animationTimer.start(); // start Timer
          }
          else // animationTimer already exists, restart animation
          {
             if ( ! animationTimer.isRunning() )
                animationTimer.restart();
          } // end else
        } // end method startAnimation

        public void stopAnimation()
        {        
            animationTimer.stop();
            resultJLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("red.PNG")));

            //jLabel4.setEnabled(false);
        }
        private class TimerHandler implements ActionListener 
        {
          // respond to Timer's event
          public void actionPerformed( ActionEvent actionEvent )
          {        
             Icon icon1= new ImageIcon(getClass().getResource("green.png"));                 
             count++;
             if(count%2 == 0)
             {
                resultJLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("red.png")));
             }
             else
             {
                resultJLabel.setIcon(icon1);
             }
             
             //if(count >=30)
             if(continueAnimation != true)    
             {
                 stopAnimation();                 
             }
                 
          } // end method actionPerformed
        }

        @Override
       public String doInBackground()
       {
            startAnimation();
            return "";
       }
} // end class Animator

