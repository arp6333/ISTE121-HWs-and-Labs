import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

/**
* Ellie Parobek
* ISTE 121.01 HW #2
* Editor class which allows a user to choose a file, load it, modify it, and save it to the original file or a new file.
*/
public class Editor extends JFrame implements ActionListener{
   private JPanel jpCenter = new JPanel();
   
   private JButton jbPlus = new JButton("+");
   private JButton jbMinus = new JButton("-");
   private JButton jbReset = new JButton("Reset");
   private JButton jbQuit = new JButton("Quit");
   
   private JMenuBar jmbBar = new JMenuBar();
   private JMenu jmFile = new JMenu("File");
   private JMenu jmTools = new JMenu("Tools");
   private JMenu jmHelp = new JMenu("Help");
   private JMenuItem jmiNew = new JMenuItem("New");
   private JMenuItem jmiOpen = new JMenuItem("Open");
   private JMenuItem jmiSave = new JMenuItem("Save");
   private JMenuItem jmiSaveAs = new JMenuItem("Save As");
   private JMenuItem jmiExit = new JMenuItem("Exit");
   private JMenuItem jmiWordCount = new JMenuItem("Word Count");
   private JMenuItem jmiAbout = new JMenuItem("About Editor");
   private JTextArea jtaData = new JTextArea(10, 20);
   private int count = 0;
   
   /**
   * Main method to run Editor.
   */
   public static void main(String args[]){
      new Editor();
   }
   
   /**
   * Constructor which creates the GUI and action listeners.
   */
   public Editor(){
      this.setTitle("Editor");
      this.setSize(500, 200);
      this.setLocationRelativeTo(null);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setVisible(true);
      this.setLayout(new BorderLayout());
      // FileListener class action listener.
      ActionListener actionListener = new FileListener(jtaData, this);
      // Create action listeners for FileListener class
      jmFile.add(jmiNew);   
      jmiNew.addActionListener(actionListener);
      jmFile.add(jmiOpen);   
      jmiOpen.addActionListener(actionListener);
      jmFile.add(jmiSave);   
      jmiSave.addActionListener(actionListener);
      jmFile.add(jmiSaveAs);   
      jmiSaveAs.addActionListener(actionListener);
      jmFile.add(jmiExit);   
      jmiExit.addActionListener(actionListener);
      // Anonymous inner class action listeners.
      jmTools.add(jmiWordCount);
      jmiWordCount.addActionListener(new ActionListener(){
                  public void actionPerformed(ActionEvent ae){
                     doWordCount();
                  }});
      
      jmHelp.add(jmiAbout);
      jmiAbout.addActionListener(new ActionListener(){
                  public void actionPerformed(ActionEvent ae){ 
                     JOptionPane.showMessageDialog(jmiAbout, "Written By: Ellie Parobek\n01/31/18", "About", JOptionPane.INFORMATION_MESSAGE);
                  }});

      jmbBar.add(jmFile);
      jmbBar.add(jmTools);
      jmbBar.add(jmHelp);
      this.setJMenuBar(jmbBar);
      // Text area style
      Font newFont = new Font("Courier", Font.PLAIN, 16);      
      jtaData.setFont(newFont);
      jtaData.setLineWrap(true);
      jtaData.setWrapStyleWord(true);
      JScrollPane jspCenter = new JScrollPane(jtaData);
      
      this.add(jspCenter, BorderLayout.CENTER);
   }
   
   /**
   * actionPerformed does nothing since the action listeners are either in FileListener or are anonymous inner classes
   */
   public void actionPerformed(ActionEvent ae){
   
   }
   
   /**
   * doWordCount counts the number of words in the text area.
   */
   public void doWordCount(){
      // If the text area is empty, then count is 0.
      if(jtaData.getText().equals("")){
         count = 0;
      }
      // Otherwise, put each word into an array and the length of the array is count.
      else{
         String allLines = jtaData.getText();
         String[] text = allLines.split("\\s+");
         count = text.length;
      }
      JOptionPane.showMessageDialog(jmiAbout, count + " words", "Word Count", JOptionPane.INFORMATION_MESSAGE); 
   }
   
}