import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
* Ellie Parobek ISTE-121.01 HW#3
* Class CsvIo which uses I/O to read and write CSV and dat files
*/
public class CsvIo extends JFrame implements ActionListener {
   private JTextField jtfFileName = new JTextField("", 15);
   private JTextField jtfRecordsIn = new JTextField("", 5);
   private JTextField jtfRecordsOut = new JTextField("", 5);
   
   private JButton jbAverage = new JButton("Average");
   private JButton jbOpen = new JButton("Open");
   
   private String fileName = "";
   private File fileObj = null;
   private FileInputStream fis = null;
   private DataInputStream dis = null;
   private FileOutputStream fos = null;
   private DataOutputStream dos = null;
   
   private JTextArea jtaData = new JTextArea();
   private Scanner fIn;
   private String line = "";
   private String[] toSplit;
   
   private ArrayList<String> first = new ArrayList<String>();
   private ArrayList<String> last = new ArrayList<String>();
   private ArrayList<Integer> day = new ArrayList<Integer>();
   private ArrayList<Integer> month = new ArrayList<Integer>();
   private ArrayList<Integer> year = new ArrayList<Integer>();
   private ArrayList<Integer> weight = new ArrayList<Integer>();
   private ArrayList<Double> height = new ArrayList<Double>();
   
   private int readIn = 0;
   private int readOut = 0;
   
   /**
   * Main method to create a new CsvIo.
   */
   public static void main(String[] args) {
      new CsvIo();
   }
   
   /**
   * Constructor for CsvIo creates all GUI elements.
   */
   public CsvIo(){
      this.setTitle("CSV I/O");
      this.setSize(375, 400);
      this.setLocationRelativeTo(null);
      this.setDefaultCloseOperation(EXIT_ON_CLOSE);
      this.setVisible(true);
      
      JPanel jpNorth = new JPanel(new FlowLayout());
      jpNorth.add(new JLabel("File Name", JLabel.RIGHT));
      jpNorth.add(jtfFileName);
      jtfFileName.setEditable(false);
      jpNorth.add(jbOpen);
      jbOpen.addActionListener(this);
      jpNorth.add(new JLabel("Records In:", JLabel.RIGHT));
      jpNorth.add(jtfRecordsIn);
      jpNorth.add(new JLabel("Records Out:", JLabel.RIGHT));
      jpNorth.add(jtfRecordsOut);
      jpNorth.setPreferredSize(new Dimension(200, 75));
      
      Font newFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);      
      jtaData.setFont(newFont);
      JScrollPane jspCenter = new JScrollPane(jtaData);
      
      this.add(jpNorth, BorderLayout.NORTH);
      this.add(jspCenter, BorderLayout.CENTER);
   }
   
   /**
   * actionPerformed for the open button.
   */
   public void actionPerformed(ActionEvent ae) {
      switch(ae.getActionCommand()){
         case "Open":
            doOpen();
            break;
      }
   }
   
    public void doOpen(){
      doClose();
      // File chooser for CSV file and to create scanner.
      try{
         JFileChooser jfcDialog = new JFileChooser();
         FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
         jfcDialog.setFileFilter(filter);
         int returnValue = jfcDialog.showOpenDialog(this);
         if(returnValue == JFileChooser.APPROVE_OPTION){
            jtfFileName.setText(jfcDialog.getSelectedFile().getName());
            fileName = jtfFileName.getText();
            fIn = new Scanner(new FileReader(jfcDialog.getSelectedFile()));
         }
      }
      catch(Exception e){
         JOptionPane.showMessageDialog(null, "Cannot Open File: " + e, "Open Error", JOptionPane.ERROR_MESSAGE);
         jtfFileName.setText("");
         return;
      }
      this.setTitle(fileName);
      
      // Read the data.
      readData();
      
      // Write the data.
      writeData();
      
      // Set the records read in and out.
      jtfRecordsIn.setText("" + readIn);
      jtfRecordsOut.setText("" + readOut);
   }
   
   /**
   * writeData writes all data to a .dat file of the same name.
   */
   public void writeData(){
      try{
         fileObj = new File(fileName.substring(0, fileName.length() - 4) + ".dat");
         fos = new FileOutputStream(fileObj, true);
         dos = new DataOutputStream(fos);
      }
      catch(Exception e){
         JOptionPane.showMessageDialog(this, "Exception: " + e, "Cannot open file", JOptionPane.ERROR_MESSAGE);
         return;
      }
      
      try{
         // Write each element to the file.
         String heading = String.format("%-20s %10s %5s %5s \n","First & Last Name", "Birthdate", "Weight", "Height");
         jtaData.append(heading);
         for(int i = 0; i < first.size(); i++){
            dos.writeUTF(first.get(i));
            dos.writeUTF(last.get(i));
            dos.writeInt(day.get(i));
            dos.writeInt(month.get(i));
            dos.writeInt(year.get(i));
            dos.writeInt(weight.get(i));
            dos.writeDouble(height.get(i));
            readOut++;
            // Write to text area, formatted.
            String l = String.format("%-20s %10s %5s %5s", first.get(i) + " " + last.get(i), day.get(i) + "/" + month.get(i) + "/" + year.get(i), weight.get(i) +"", height.get(i) + "");
            jtaData.append(l + "\n");
         }
      }
      catch(Exception e){
         JOptionPane.showMessageDialog(this, "Exception: " + e, "Cannot write to file", JOptionPane.ERROR_MESSAGE);
         return;
      }
   }
   
   /**
   * readData reads the data from the selected file.
   */
   public void readData(){
      // Read each line and remove white space.
      while(fIn.hasNext()){
         line += fIn.nextLine();
         line = line.replaceAll("\\s+","");
      }
      // Split by commas, then add to arraylist to not use an array.
      String[] split = line.split(",");
      ArrayList<String> info  = new ArrayList<String>(Arrays.asList(split));
      // Remove headings
      for(int i = 0; i < 6; i++){
         info.remove(0);
      }
      info.set(0, info.get(0).substring(6));
      String weights;
      String lasts;
      String[] toSplit;
      // Since the height and first name aren't separated by a comma, have to separate ourself then re-add to the arraylist.
      for(int i = 6; i < info.size(); i += 7){
         // Split after number or if null
         if(info.get(i).contains("null")){
            weights = info.get(i).substring(0, 4);
            lasts = info.get(i).substring(4);
         }
         else{
            // Split by number
            toSplit = info.get(i).split("(?<=\\d)(?=[a-zA-Z])");
            weights = toSplit[0];
            if(toSplit.length == 2){
               lasts = toSplit[1];
            }
            else{
               lasts = "";
            }   
         }
         info.remove(i);
         info.add(i, lasts);
         info.add(i, weights);
      }
      // Remove last element bc it will just be white space.
      info.remove(info.size()-1);
      // Add all info to respective arraylists. Check if the information is null. I know this is probably super inefficient but oh well.
      try{
         for(int i = 0; i < info.size(); i++){
            readIn++;
            int start = i;
            if(info.get(i).equals("null")){
               ArrayList<String> range = new ArrayList<String>(info.subList(start, i + 7));
               jtaData.append("Offending item is first name\nError found with " + range + "\n\n");
               i += 6;
               continue;
            }
            i++;
            if(info.get(i).equals("null")){
               ArrayList<String> range = new ArrayList<String>(info.subList(start, i + 7));
               jtaData.append("Offending item is last name\nError found with " + range + "\n\n");
               i += 5;
               continue;
            }
            i++;
            if(info.get(i).equals("null")){
               ArrayList<String> range = new ArrayList<String>(info.subList(start, i + 7));
               jtaData.append("Offending item is day\nError found with " + range + "\n\n");
               i += 4;
               continue;
            }
            i++;
            if(info.get(i).equals("null")){
               ArrayList<String> range = new ArrayList<String>(info.subList(start, i + 7));
               jtaData.append("Offending item is month\nError found with " + range + "\n\n");
               i += 3;
               continue;
            }
            i++;
            if(info.get(i).equals("null")){
               ArrayList<String> range = new ArrayList<String>(info.subList(start, i + 7));
               jtaData.append("Offending item is year\nError found with " + range + "\n\n");
               i += 2;
               continue;
            }
            i++;
            if(info.get(i).equals("null")){
               ArrayList<String> range = new ArrayList<String>(info.subList(start, i + 7));
               jtaData.append("Offending item is weight\nError found with " + range + "\n\n");
               i += 1;
               continue;
            }
            i++;
            if(info.get(i).equals("null")){
               ArrayList<String> range = new ArrayList<String>(info.subList(start, i + 7));
               jtaData.append("Offending item is height\nError found with " + range + "\n\n");
               continue;
            }
            i -= 6;
            first.add(info.get(i));
            i++;
            last.add(info.get(i));
            i++;
            day.add(Integer.parseInt(info.get(i)));
            i++;
            month.add(Integer.parseInt(info.get(i)));
            i++;
            year.add(Integer.parseInt(info.get(i)));
            i++;
            weight.add(Integer.parseInt(info.get(i)));
            i++;
            height.add(Double.parseDouble(info.get(i)));
         }
      }
      catch(Exception e){
         JOptionPane.showMessageDialog(this, "Exception: " + e, "Error reading file", JOptionPane.ERROR_MESSAGE);
         return;
      }
   }
   
   /**
   * doClose resets variables.
   */
   public void doClose(){
      jtfFileName.setText("");
      jtaData.setText("");
      this.setTitle("CSV I/O");
      line = "";
      readOut = 0;
      readIn = 0;
   }
}