import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.ArrayList;
/**
* Ellie Parobek
* ISTE 121 HW05
* Races class uses threads to have images race across the screen.
* Program not totally working as only the last racer is displayed.
*/
public class Races extends JFrame{
   private JPanel jpCenter = new JPanel();
   private boolean keepGoing = true;
   private ArrayList<Thread> threadList = new ArrayList<>();
     
   /*
   * main method creates GUI based on number of racers
   * @param String[] args - the number of racers
   */
   public static void main(String[] args){
      int num = 0;
      try{
         num = Integer.parseInt(args[2]);
      }
      catch(Exception e){
         // Default number of racers is 5 if none entered
         num = 5;
      }
      new Races(num);
   }
   
   /**
   * Constructor creates the GUI and racer threads
   * @param int num - the number of racers
   */
   public Races(int num){
      this.setTitle("Off to the Races - by Ellie Parobek");
      this.setSize(500, 32 * (num + 2));
      this.setLocationRelativeTo(null);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      // Create all racers and their threads
      for(int i = 0; i < num; i++){
         RaceThread racer = new RaceThread(i + 1, i * 32);
         this.add(racer);
         Thread t = new Thread(racer);
         threadList.add(t);
      }
      this.setVisible(true);
      // Start all threads
      for(int i = 0; i < threadList.size(); i++){
         threadList.get(i).start();
      }
   }
   
   /**
   * paintComponent that is supposed to draw the finish line but does not work
   * @param Graphics g - to draw the line
   */
   public void paintComponent(Graphics g){
      g.setColor(Color.black);
      g.drawLine(450,0, 450, 1000);
   }
   
   /**
   * RaceThread class which controls the threads
   */
   class RaceThread extends JPanel implements Runnable{
      private JLabel label;
      private JProgressBar progress;
      private String name;
      private Icon pic = null;
      private int x = 0;
      private int y = 0;
      private JLabel picLabel;
      
      /**
      * Constructor creates each runner image
      * @param int num - the name of the racer, int yPos - the y position of the racer
      */
      public RaceThread(int num, int yPos){
         this.name = "" + num;
         this.y = yPos;
         // Create image from races.gif
         pic = new ImageIcon("races.gif");
         picLabel = new JLabel("", pic, JLabel.CENTER);
      }
      
      /**
      * paintComponent draws each icon
      * @param Graphics g - to draw the racer icons
      */
      public void paintComponent(Graphics g){
         super.paintComponent(g);
         pic.paintIcon(this, g, x, y);
         // If the race is over, print who won
         if(keepGoing == false){
            g.drawString("Winner is: " + name, 0, y); 
            revalidate();
         }
      }
      
      /**
      * run method tells the threads what to do
      */
      public void run(){
         // Wait to start race
         try{
            Thread.sleep(1000);
         }
         catch(InterruptedException ie){
               System.out.println("Error: " + ie);
         }
         // Loop until someone reaches the end of the race or the racer finishes
         for(int i = 1; (i <= 180) && (keepGoing == true); i++){
            // Random distance to travel
            x += 0 + (int)(Math.random() * ((5 - 0) + 1));
            this.repaint();
            // Random interval to sleep
            try{
               Thread.sleep((int)(Math.random() * 100));
            }
            catch(InterruptedException ie){
               System.out.println("Error: " + ie);
            }
         }
         // Race is finished if loop is broken, tell other threads to stop
         keepGoing = false;
         this.repaint();
         revalidate();
      }
   }
}