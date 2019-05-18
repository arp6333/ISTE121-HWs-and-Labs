import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.ArrayList;

/**
* Ellie Parobek
* ISTE 121.01 HW #2
* FileListener class creates action listeners for the Editor class.
*/
public class FileListener implements ActionListener{
   
   private JTextArea jtaData = new JTextArea();
   private JFrame frame;
   
   private Scanner fIn = null;
   private String fileName = "";
   private String newFileName = "";
   private PrintWriter pw = null;
   
   /**
   * Constructor taking the text area and frame from Editor.
   */
   public FileListener(JTextArea data, JFrame theFrame){
      this.jtaData = data;
      this.frame = theFrame;
   }
   
   /**
   * actionPerformed for each menu item to call their respective methods
   */
   public void actionPerformed(ActionEvent ae){
       switch(ae.getActionCommand()){
         case "Exit":
            System.exit(0);
         case "New":
            doNew();
            break;
         case "Open":
            doOpen();
            break;
         case "Save":
            doSave();
            break;
         case "Save As":
            doSaveAs();
            break;
      }
   }
   
   /**
   * doOpen first clears the area, then puts the contents of a text file into the text area.
   */
   public void doOpen(){
      doNew();
      // Try to set up the scanner and catch if it fails.
      try{
         JFileChooser jfcDialog = new JFileChooser();
         FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
         jfcDialog.setFileFilter(filter);
         int returnValue = jfcDialog.showOpenDialog(jtaData);
         if(returnValue == JFileChooser.APPROVE_OPTION){
            fileName = jfcDialog.getSelectedFile().getName();
            fIn = new Scanner(new FileReader(jfcDialog.getSelectedFile()));
         }
      }
      catch(Exception e){
         JOptionPane.showMessageDialog(jtaData, "Cannot Open File: " + e, "Open Error", JOptionPane.ERROR_MESSAGE);
         fileName = "";
         return;
      }
      frame.setTitle("Editor: " + fileName);
      // Read each line from the file and add it to the text area.
      while(fIn.hasNextLine()){
         String line = fIn.nextLine();
         if(line.trim().length() > 0){
            jtaData.append(line + "\n");
         }
      }
   }
   
   /**
   * Save the text area to the file opened.
   */
   public void doSave(){
      // Check if the file has not been saved yet, and ask to use save as instead.
      if(fileName.equals("")){
         JOptionPane.showMessageDialog(jtaData, "No file name given.\nUse Save As", "No File Name", JOptionPane.ERROR_MESSAGE);
         return;
      }
      else{
         // Try to create print writer based on file name.
         try{
            pw = new PrintWriter(new FileOutputStream(new File(fileName))); 
         }
         catch(IOException e){
            JOptionPane.showMessageDialog(jtaData, "Unable to save file.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
         }
         // Write all data to the file.
         for(String line : jtaData.getText().split("\n")){
            pw.println(line + "\n");
         }
         pw.close();
      }
   }
   
   /**
   * doSaveAs to set a name for the file and save it.
   */
   public void doSaveAs(){
      // Open file chooser for saving.
      try{
         JFileChooser jfcDialog = new JFileChooser();
         FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
         jfcDialog.setFileFilter(filter);
         int returnValue = jfcDialog.showSaveDialog(jtaData);
         if(returnValue == JFileChooser.APPROVE_OPTION){
            newFileName = jfcDialog.getSelectedFile().getName();
         }
      }
      catch(Exception e){
         JOptionPane.showMessageDialog(jtaData, "Cannot Open File: " + e, "Open Error", JOptionPane.ERROR_MESSAGE);
         newFileName = "";
         return;
      }
      // Try to create the file based on set name.
      try{
         pw = new PrintWriter(new FileOutputStream(new File(newFileName + ".txt"), true)); 
      }
      catch(IOException e){
         JOptionPane.showMessageDialog(jtaData, "Unable to create file.", "Error", JOptionPane.ERROR_MESSAGE);
         return;
      }
      // Write to the file.
      for(String line : jtaData.getText().split("\n")){
         pw.println(line + "\n");
      }
      pw.close();
      frame.setTitle("Editor: " + newFileName + ".txt");
   }
   
   /**
   * doNew clears all variables.
   */
   public void doNew(){
      jtaData.setText("");
      fileName = "";
      newFileName = "";
      frame.setTitle("Editor");
      fIn = null;
   }
}