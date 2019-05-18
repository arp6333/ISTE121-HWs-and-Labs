import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
* Ellie Parobek
* ISTE 121.01 HW #1
* Orders class to calculate and store information about orders using GUI.
*/
public class Orders extends JFrame implements ActionListener{
   // Create JPanels for each section of the GUI.
   private JPanel jpNorth = new JPanel();
   private JPanel jpEast = new JPanel();
   private JPanel jpSouth = new JPanel();
   private JPanel jpWest = new JPanel();
   private JPanel jpCenter = new JPanel(new GridLayout(0, 1));
   
   // Create the 4 text fields for the GUI.
   private JTextField jtfName = new JTextField("");
   private JTextField jtfNum = new JTextField("");
   private JTextField jtfCost = new JTextField("");
   private JTextField jtfAmount = new JTextField("");
   
   // Create the buttons for the GUI.
   private JButton jbCalc = new JButton("Calculate");
   private JButton jbSave = new JButton("Save");
   private JButton jbClear = new JButton("Clear");
   private JButton jbExit = new JButton("Exit");
   private JButton jbLoad = new JButton("Load");
   private JButton jbPrev = new JButton("<Prev");
   private JButton jbNext = new JButton("Next>");
   
   private PrintWriter pw;
   // ArrayList to store file data.
   private ArrayList<String> list = new ArrayList<>();
   // Postion to store posiiton in file.
   private int position = -1;
   private Scanner fIn;
   // Count for number of orders read in a file.
   private int count = 0;
   private String ln = "";
   
   /**
   * Main method to run the Orders constructor.
   */
   public static void main(String[] args) {
      new Orders();
   }
   
   /**
   * Orders constructor which creates and displays the GUI.
   */
   public Orders(){
      // Create the initial GUI
      this.setTitle("Ellie Parobek's Item Orders Calculator");
      this.setSize(500, 250);
      this.setLocationRelativeTo(null);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setVisible(true);
      this.setLayout(new BorderLayout());
      
      // Add all text fields with their respective labels in the center of the GUI.
      jpCenter.setLayout(new GridLayout(0,2));
      
      jpCenter.add(new JLabel("Item Name:", SwingConstants.RIGHT));
      jpCenter.add(jtfName);
      
      jpCenter.add(new JLabel("Number of:", SwingConstants.RIGHT));
      jpCenter.add(jtfNum);
      
      jpCenter.add(new JLabel("Cost:", SwingConstants.RIGHT));
      jpCenter.add(jtfCost);
      
      jpCenter.add(new JLabel("Amount owed:", SwingConstants.RIGHT));
      jpCenter.add(jtfAmount);
      // Amount is not enabled.
      jtfAmount.setEnabled(false);
      
      // Create action listeners for all buttons.
      jbCalc.addActionListener(this);
      jbSave.addActionListener(this);
      jbClear.addActionListener(this);
      jbExit.addActionListener(this);
      jbLoad.addActionListener(this);
      jbPrev.addActionListener(this);
      jbNext.addActionListener(this);
      
      // Add all buttons in the south of the GUI along with tooltips for each button.
      jpSouth.setLayout(new GridLayout(2, 0));
      jpSouth.add(jbCalc);
      jbCalc.setToolTipText("Calculate order.");
      jpSouth.add(jbSave);
      jbSave.setToolTipText("Calculate and save order to file \"121HW1.csv\".");
      jpSouth.add(jbClear);
      jbClear.setToolTipText("Clear all the text fields.");
      jpSouth.add(jbExit);
      jbExit.setToolTipText("Exit the program without saving.");
      jpSouth.add(jbLoad);
      jbLoad.setToolTipText("Load a file for reading.");
      // Set prev and next button enabled to false since a file has not been choosen yet.
      jpSouth.add(jbPrev);
      jbPrev.setEnabled(false);
      jbPrev.setToolTipText("Read the previous item in the file.");
      jpSouth.add(jbNext);
      jbNext.setEnabled(false);
      jbNext.setToolTipText("Read the next item in the file.");
      
      // Add all JPanels to the GUI in their respective sections.
      this.add(jpNorth, BorderLayout.NORTH);
      this.add(jpEast, BorderLayout.EAST);
      this.add(jpSouth, BorderLayout.SOUTH);
      this.add(jpWest, BorderLayout.WEST);
      this.add(jpCenter, BorderLayout.CENTER);
   }
   
   /**
   * actionPerformed reads the button being clicked and performs the action required of that button.
   * @param: ActionEvent ae - the action event telling us which button is clicked
   */
   public void actionPerformed(ActionEvent ae){
      // Get the button that was clicked from the action event.
      String action = ae.getActionCommand();
      // Switch using action to perform the correct action.
      switch(action){
         // Exits the program.
         case "Exit":
            System.exit(0);
            break;
         // Calls Calculate method.
         case "Calculate":
            Calculate();
            break;
         // Calls doClear method.
         case "Clear":
            doClear();
            break;
         // Calls Calculate then Save methods.
         case "Save":
            Calculate();
            Save();
            break;
         // Calls Load method.
         case "Load":
            Load();
            break;
         // Calls doNext method.
         case "Next>":
            doNext();
            break;
         // Calls doPrev method.
         case "<Prev":
            doPrev();
            break;
      }
   }
   
   /**
   * Calculate calculates the orders amount using the number of items and cost entered.
   */
   public void Calculate(){
      // Calculate as a double the number of items multiplied by the cost of an item to get the amount.
      double calcI = (Double.parseDouble(jtfNum.getText())) * (Double.parseDouble(jtfCost.getText()));
      // Format the double calculated into a 2 decimal string.
      String calcS = String.format("%.2f", calcI);
      // Set the formated string as the text field amount.
      jtfAmount.setText(calcS);
   }
   /**
   * doClear clears all text fields.
   */
   public void doClear(){
      // Clear all the text fields.
      jtfName.setText(null);
      jtfNum.setText(null);
      jtfCost.setText(null);
      jtfAmount.setText(null);
   }
   
   /**
   * Load allows a file to be chosen to read. 
   */
   public void Load(){
      // Resets all counters and list for if more than 1 file is read before exit.
      count = 0;
      ln = "";
      list.clear();
      position = -1;
      // Try to use the file chooser GUI to have a file be chosen.
      try{
         JFileChooser jfcDialog = new JFileChooser();
         // Only allow text files to be chosen.
         FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
         jfcDialog.setFileFilter(filter);
         int returnValue = jfcDialog.showOpenDialog(this);
         if(returnValue == JFileChooser.APPROVE_OPTION){
            // Read the file with scanner fIn.
            fIn = new Scanner(new FileReader(jfcDialog.getSelectedFile()));
         }
      }
      // Catch an exception with the file unable to be opened.
      catch(Exception e){
         JOptionPane.showMessageDialog(null, "Cannot Open File: " + e, "Open Error", JOptionPane.ERROR_MESSAGE);
         return;
      }
      // Place all lines in the file into an ArrayList of Strings until end of file has been reached.
      while(fIn.hasNext()){
         ln = fIn.nextLine();
         list.add(ln);
         // Counter of each line being read.
         count++;
      }
      // If counter is 0 (no lines read), file is missing or empty so do nothing with the file.
      if(count == 0){
         JOptionPane.showMessageDialog(jbLoad, "Missing file or no data to read.");
         return;
      }
      // Close scanner.
      fIn.close();
      // Try to read the first line of the file.
      try{
         doNext();
      }
      // Catch if the first line is giberish and/or does not relate to the lab and do nothing with the file if so.
      catch(Exception e){
         JOptionPane.showMessageDialog(jbLoad, "Unable to properly read file.", "Error", JOptionPane.ERROR_MESSAGE);
         doClear();
         return;
      }
      // Enable doNext but disable doPrev since it is the first item of the file.
      jbNext.setEnabled(true);
      jbPrev.setEnabled(false);
      // Display the number of orders read.
      JOptionPane.showMessageDialog(jbLoad, "Number of orders read: " + count);
   }
   
   /**
   * doNext reads the next order in the file chosen.
   */
   public void doNext(){
      // Add one to position in the file.
      position += 1;
      // Check if at the end of the file and disable doNext if so.
      if(position >= list.size()){
         JOptionPane.showMessageDialog(jbNext, "End of file, no more data next.");
         position -= 1;
         jbNext.setEnabled(false);
         return;
      }
      // Split the line at position based on ",".
      String[] info = list.get(position).split(",");
      // Set each text field to the proper object.
      jtfName.setText("" + info[0]);
      jtfNum.setText("" + info[1]);
      jtfCost.setText("" + info[2]);
      jtfAmount.setText("" + info[3]);
      // Enable setPrev since there is now a previous order to read.
      jbPrev.setEnabled(true);
   }
   
   /**
   * doNext reads the previous order in the file chosen.
   */
   public void doPrev(){
      // Subtract one to position in the file.
      position -= 1;
      // Check if at the beginning of the file and disable doPrev if so.
      if(position < 0){
         JOptionPane.showMessageDialog(jbPrev, "Beginning of file, no more data previous.");
         position += 1;
         jbPrev.setEnabled(false);
         return;
      }
      // Split the line at position based on ",".
      String[] info = list.get(position).split(",");
      // Set each text field to the proper object.
      jtfName.setText("" + info[0]);
      jtfNum.setText("" + info[1]);
      jtfCost.setText("" + info[2]);
      jtfAmount.setText("" + info[3]); 
      // Enable setNext since there is now a next order to read.
      jbNext.setEnabled(true);
   }

   /**
   * Save saves the order entered into a text file "121HW1.csv".
   */
   public void Save(){
      // Try to create print writer for file 121HW1.csv.
      try{
         pw = new PrintWriter(new FileOutputStream(new File("121HW1.csv"), true)); 
      }
      // Catch error in creating file.
      catch(IOException e){
         JOptionPane.showMessageDialog(jbSave, "Unable to create file.", "Error", JOptionPane.ERROR_MESSAGE);
      }
      // Write to file the name, number, cost, and amount formated by commas.
      pw.println("\""+ jtfName.getText() + "\"," + jtfNum.getText() + "," + jtfCost.getText() + "," + jtfAmount.getText());
      // Close the print writer.
      pw.close();
   }
}

