import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
* Ellie Parobek
* ISTE 121.01 HW#4
* TimerFun class to use GUI and threading for date / time, rainbow display changing, and progress bars.
*/
public class TimerFun extends JFrame implements ActionListener{

   private JMenuBar jmbBar = new JMenuBar();
   private JMenu jmFile = new JMenu("File");
   private JMenu jmHelp = new JMenu("Help");
   private JMenuItem jmiAbout = new JMenuItem("About");
   private JMenuItem jmiExit = new JMenuItem("Exit");
   
   private JPanel jpCenter = new JPanel();
   private JPanel jpRed = new JPanel();
   private JPanel jpOrange = new JPanel();
   private JPanel jpYellow = new JPanel();
   private JPanel jpGreen = new JPanel();
   private JPanel jpBlue = new JPanel();
   private JPanel jpIndigo = new JPanel();
   private JPanel jpViolet = new JPanel();
   private boolean colorEnabled = true;
   private Timer timer;
   private Timer timerExit;
   private ArrayList<Color> colors = new ArrayList<>();
   private final String word = "words";
   private final String unabridged = "UnabridgedDictionary";
   
   private boolean keepGoing = true;
   // Have to specifiy this timer is a swing timer and not util like the other timers
   private javax.swing.Timer clock;
   private JLabel clockDisplay;
 
   /**
   * Main method to create the TimerFun GUI
   * @param String args[] - none
   */
   public static void main(String args[]){
      new TimerFun();
   }
   
   /**
   * TimerFun creates the GUI and everything inside of it
   */
   public TimerFun(){
      this.setTitle("Fun With Timers");
      this.setLocationRelativeTo(null);
      this.addWindowListener(new WindowAdapter(){
         // I couldn't get my timerExit to work for window closing so I used a sleep for the whole program
         public void windowClosing(WindowEvent e){
            try{
               TimeUnit.SECONDS.sleep(2);
            }
            catch(InterruptedException ie){
               System.out.println("Error: " + ie);
            }
            System.exit(0);
         }
      });
      this.setVisible(true);
      this.setSize(350, 350);
      this.setLayout(new GridLayout(0, 1));
      
      // Menu bar
      jmHelp.add(jmiAbout);   
      jmiAbout.addActionListener(this);
      jmFile.add(jmiExit);   
      jmiExit.addActionListener(this);
      jmbBar.add(jmFile);
      jmbBar.add(jmHelp);
      this.setJMenuBar(jmbBar);
      
      // Date / time display
      Calendar now = Calendar.getInstance();
      //DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
      DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");
      clockDisplay = new JLabel(dateFormat.format(now.getTime()));
      clockDisplay.setFont(new Font("Verdana", Font.BOLD, 20));
      clockDisplay.setForeground(Color.RED);
      this.add(clockDisplay);
            
      // Color panels
      jpRed.setOpaque(true);
      this.add(jpRed);
      jpOrange.setOpaque(true);
      this.add(jpOrange);
      jpYellow.setOpaque(true);
      this.add(jpYellow);
      jpGreen.setOpaque(true);
      this.add(jpGreen);
      jpBlue.setOpaque(true);
      this.add(jpBlue);
      jpIndigo.setOpaque(true);
      this.add(jpIndigo);
      jpViolet.setOpaque(true);
      this.add(jpViolet);
      // ArrayList of all the colors needed for the panels
      colors.add(Color.RED);
      colors.add(Color.ORANGE);
      colors.add(Color.YELLOW);
      colors.add(Color.GREEN);
      colors.add(Color.BLUE);
      colors.add(Color.decode("#4b0082"));
      colors.add(Color.decode("#ee82ee"));
      // Set inital panel colors
      jpRed.setBackground(colors.get(0));
      jpOrange.setBackground(colors.get(1));
      jpYellow.setBackground(colors.get(2));
      jpGreen.setBackground(colors.get(3));
      jpBlue.setBackground(colors.get(4));
      jpIndigo.setBackground(colors.get(5));
      jpViolet.setBackground(colors.get(6));
      // Create timer for color changing every 2 seconds
      timer = new Timer();
      timer.schedule(new Task(), 2*1000, 500);
      
      // Progress bars
      InnerProgress bar1 = new InnerProgress("Words", word);
      InnerProgress bar2 = new InnerProgress("Unabridged", unabridged);
      this.add(bar1);
      this.add(bar2);
      // Threads for the progress bars
      Thread thread1 = new Thread(bar1);
      Thread thread2 = new Thread(bar2);
      thread1.start();
      thread2.start();
      
      // Update date / time
      clockDisplay.setBounds(100, 100, 125, 125);
      // Being time
      clock = new javax.swing.Timer(1000, new ActionListener(){
         public void actionPerformed(ActionEvent e) {
            Calendar now = Calendar.getInstance();
            clockDisplay.setText(dateFormat.format(now.getTime()));
           }
      });
      clock.start();
      try{
         Thread.currentThread().join();
      }
      catch(InterruptedException ie){
         System.out.println("Error: " + ie);
      }
   }
   
   /**
   * Inner class Task runs the timer for the color changing panels.
   */
   class Task extends TimerTask{
      /**
      *  Method run changes each panel based on the colors array
      */
      public void run(){
         // Stop changing colors if the progress bars are done
         if(colorEnabled == false){
            timer.cancel();
         }
         jpRed.setBackground(colors.get(0));
         jpOrange.setBackground(colors.get(1));
         jpYellow.setBackground(colors.get(2));
         jpGreen.setBackground(colors.get(3));
         jpBlue.setBackground(colors.get(4));
         jpIndigo.setBackground(colors.get(5));
         jpViolet.setBackground(colors.get(6));
         validate();
         // Rotate colors
         colors.add(colors.get(0));
         colors.remove(0);
      }
   }
   
   /**
   * actionPerformed performs an action based on the menu item selected 
   * @param ActionEvent ae - the action event for the menu item selected
   */
   public void actionPerformed(ActionEvent ae){
      String action = ae.getActionCommand();
      switch(action){
         case "Exit":
            // Timer to wait 2 seconds, then exit
            timerExit = new Timer();
            timerExit.schedule(new Exit(), 2*1000);
            break;
         case "About":
            JOptionPane.showMessageDialog(jmiAbout,"Fun With Timers and Threads\nBy Ellie Parobek");
      }
   }
   
   /**
   * Inner class Exit to exit the program after the timer says to (2 seconds)
   */
   class Exit extends TimerTask{
      /**
      *  Method run to exit program
      */
      public void run(){
         System.exit(0);
      }
   }
   
   /**
   * Inner class InnerProgress holds the run for the threads created in TimerFun
   */
   class InnerProgress extends JPanel implements Runnable{
   
      private JLabel label;
      private JProgressBar progress;
      private String name;
      private String file;
      
      /**
      * Constructor for InnerProgress to create progress bars and labels for progress bars
      * @param String basicName - the name of the file shorthand, String filename - the name of the file fully
      */
      public InnerProgress(String basicName, String fileName){
         this.name = basicName;
         this.file = fileName;
         progress = new JProgressBar(0, 100);
         label = new JLabel();
         this.add(label);
         this.add(progress);
         progress.setStringPainted(true);
         progress.setIndeterminate(true);
      }
      
      /**
      * Run shows the progress bars increasing as the files are read
      */
      public void run(){
         label.setText("Opening file...");
         progress.setValue(0);
         // 2 second wait to start reading file
         try{
            Thread.sleep(2000);
         }
         catch(InterruptedException ie){
            System.out.println("Error: " + ie);
         }
         label.setText(name + " progress:");
         // Read the file
         String f = "";
         String line = "";
         BufferedReader br = null;
         progress.setIndeterminate(false);
         // Try to start buffered reader based on file name
         try{
            br = new BufferedReader(new FileReader(file + ".txt"));
          }
          catch(IOException ie){
            System.out.println("Error: " + ie);
          }
          // Read each line in the file and add to string 'file' along with updating the progress bar 
          long length = new File(file + ".txt").length();
          double percentLength = 100.0 / length;
          long read = 0;
          try{
             while (((line = br.readLine()) != null) && (keepGoing == true)){
               f += line + "/n";
               // Not sure if this is correct but the reason it stops percent at 83 is because it does not count "/n" so i add 2 to the length to account for the supposed missed chars
               read += line.length() + 2;
               progress.setValue((int) Math.round(percentLength * read));
             }
             
          }
          catch(IOException ie){
            System.out.println("Error: " + ie);
          }
          // Close buffered reader and stop the other threads and the color changing
          try{
            br.close();
            keepGoing = false;
            colorEnabled = false;
          }
          catch(IOException ie){
            System.out.println("Error: " + ie);
          }

         // Wait 2 seconds, then change bar to say it is finished or not finished
         try{
            Thread.sleep(2000);
         }
         catch(InterruptedException ie){
            System.out.println("Error: " + ie);
         }
         if(progress.getValue() == 100){
            progress.setString("Finished file read");
         }
         else{
            progress.setString("Halted at " + progress.getValue() + "%");
         }
         progress.setIndeterminate(true);
      }
   }
}
