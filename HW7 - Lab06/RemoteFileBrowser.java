import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.text.DefaultCaret;

/**
* Lab 06 / HW 07 ISTE 121
* Ellie Parobek and JP Ramassini
* RemoteFileBrowser is the client side of RemoteFileServer for sending and receiving files.
*/
public class RemoteFileBrowser extends JFrame implements ActionListener{
   private JLabel jlServerIP = new JLabel("Server:");
   private JTextField jtfServerIP = new JTextField(20);
   private JButton jbConnect = new JButton("Connect");

   private JButton jbList = new JButton("List");
   private JButton jbChDir = new JButton("Ch Dir");
   private JButton jbUpload = new JButton("Upload");
   private JButton jbDownload = new JButton("Download");

   private JTextArea jtaLog = new JTextArea(10, 45);

   private PrintWriter pwt = null;
   private Scanner scn = null;

   public static final int SERVER_PORT = 9892;
   private Socket socket = null;
   
   /**
   * Main method to run GUI constructor.
   * @param String[] args - N/A
   */
   public static void main(String[] args){
      new RemoteFileBrowser();
   }
   
   /**
   * Constructor to create GUI.
   */
   public RemoteFileBrowser(){
      this.setTitle("Remote File Browser - Ellie Parobek and JP Ramassini");
      this.setSize(600, 300);
      this.setLocation(100, 100);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JPanel jpNorth = new JPanel();
      jpNorth.setLayout(new GridLayout(0,1));

      JPanel jpRow1 = new JPanel();
      jpRow1.add(jlServerIP);
      jpRow1.add(jtfServerIP);
      jpRow1.add(jbConnect);
      jpNorth.add(jpRow1);

      JPanel jpRow2 = new JPanel();
      jpRow2.add(jbList);
      jpRow2.add(jbChDir);
      jpRow2.add(jbUpload);
      jpRow2.add(jbDownload);
      
      // Disable buttons until client has connected.
      jbList.setEnabled(false);
      jbChDir.setEnabled(false);
      jbUpload.setEnabled(false);
      jbDownload.setEnabled(false);
      jpNorth.add(jpRow2);
      this.add(jpNorth, BorderLayout.NORTH);

      DefaultCaret caret = (DefaultCaret)jtaLog.getCaret();
      caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
      jtaLog.setLineWrap(true);
      jtaLog.setWrapStyleWord(true);

      JPanel jpCenter = new JPanel();
      jpCenter.add(new JScrollPane(jtaLog));
      this.add(jpCenter, BorderLayout.CENTER);

      jbConnect.addActionListener(this);
      jbList.addActionListener(this);
      jbChDir.addActionListener(this);
      jbUpload.addActionListener(this);
      jbDownload.addActionListener(this);

      this.setVisible(true);
   }
   
   /**
   * actionPerformed to process the action listener for each button.
   * @param ActionEvent ae - the button that has been pressed
   */
   public void actionPerformed(ActionEvent ae) {
      switch(ae.getActionCommand()){
         case "Connect":
            doConnect();
            break;
         case "Disconnect":
            doDisconnect();
            break;
         case "List":
            doList();
            break;
         case "Ch Dir":
            doChDir();
            break;
         case "Upload":
            doUpload();
            break;
         case "Download":
            doDownload();
            break;
      }
   }
   
   /**
   * doConnect connects the client with the server using sockets.
   */
   private void doConnect(){
      try{
         socket = new Socket(jtfServerIP.getText(), SERVER_PORT);
         scn = new Scanner(new InputStreamReader(socket.getInputStream()));
         pwt = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
      }
      catch(IOException ioe){
         jtaLog.append("IO Exception: " + ioe + "\n");
         return;
      }
      jtaLog.append("Connected!\n");
      jbConnect.setText("Disconnect");
      // Enable buttons now that client is connected.
      jbList.setEnabled(true);
      jbChDir.setEnabled(true);
      jbUpload.setEnabled(true);
      jbDownload.setEnabled(true);
   }
   
   /**
   * doDisconnect disconnects the client from the server.
   */
   private void doDisconnect(){
      try{
         socket.close();
         scn.close();
         pwt.close();
      }
      catch(IOException ioe) {
         jtaLog.append("IO Exception: " + ioe + "\n");
         return;
      }
      jbConnect.setText("Connect");
      // Diable all buttons again.
      jbList.setEnabled(false);
      jbChDir.setEnabled(false);
      jbUpload.setEnabled(false);
      jbDownload.setEnabled(false);
   }

   /**
   * doList lists all of the files and directories in the current working directory.
   */
   private void doList(){
      pwt.println("doList");
      pwt.flush();
      String reply = scn.nextLine();
      // Print current working directory.
      jtaLog.append("Listing of: " + reply + "\n");
      File folder = new File(reply);
      File[] listOfFiles = folder.listFiles();
      // Print out each file in the working directory.
      for(int i = 0; i < listOfFiles.length; i++){
         if(listOfFiles[i].isFile()){
            jtaLog.append("File: " + listOfFiles[i].getName() + "\n");
         }
         else if(listOfFiles[i].isDirectory()){
            jtaLog.append("Directory: " + listOfFiles[i].getName() + "\n");
         }
      }
      jtaLog.append("***********************************************\n");
   }

   /**
   * doChDir changes the current working directory to a selected directory.
   */
   private void doChDir(){
      // Get path to change to.
      String s = (String)JOptionPane.showInputDialog(this, "Enter remote directory name:");
      if(s == null){
          return;
      }
      pwt.println("doChDir");
      pwt.flush();
      while(true){
         String reply = scn.nextLine();
         // Wait for server to say it is done.
         if(reply.equals("OK")){
            pwt.println(s);
            pwt.flush();
            break;
         }
      }
      String reply = scn.nextLine();
      jtaLog.append("Changed directory to: " + reply + "\n");
   }
   
   /**
   * doUpload uploads a file from client to the server.
   */
   private void doUpload(){
      pwt.println("doUpload");
      pwt.flush();
      String directory = scn.nextLine();
      String fileName = (String)JOptionPane.showInputDialog(this, "Enter remote file name:");
      pwt.println(fileName);
      pwt.flush();
      File file = null;
      try{
         // Get the file to upload.
         JFileChooser jfcDialog = new JFileChooser(directory);
         int returnValue = jfcDialog.showOpenDialog(this);
         if(returnValue == JFileChooser.APPROVE_OPTION){
            file = jfcDialog.getSelectedFile();
         }
      }
      catch(Exception e){
         JOptionPane.showMessageDialog(null, "Cannot Open File: " + e + "\n", "Open Error", JOptionPane.ERROR_MESSAGE);
         return;
      }
      
      try{
         // Write file to server.
         ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
         out.writeObject(file);
         out.flush();
      }
      catch(IOException ioe){
         JOptionPane.showMessageDialog(null, "Cannot Send File: " + ioe + "\n", "File Error", JOptionPane.ERROR_MESSAGE);
         return;
      }

      jtaLog.append("File " + file.getName() + " uploaded.\n");
   }

   /**
   * doDownload downloads a file from the server to the client.
   */
   private void doDownload(){
      pwt.println("doDownload");
      pwt.flush();
      // Get the name of the file to download.
      String fileName = (String)JOptionPane.showInputDialog(this, "Enter remote file name:");
      pwt.println(fileName);
      pwt.flush();
      File file = null;

      try{
         if(scn.nextLine().equals("Error sending file.")){
            jtaLog.append("Error: File not found on server.\n");
            return;
         }
         else{
            // File chooser to specify where to save file.
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Specify where to save the file.");
            int chose = chooser.showSaveDialog(null);
            if(chose == JFileChooser.APPROVE_OPTION){
               try{ 
                  // Write file to selected save place.
                  ObjectInputStream objInStream = new ObjectInputStream(socket.getInputStream());
                  ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(chooser.getSelectedFile()));
                  file = (File)objInStream.readObject();
                  objOut.writeObject(file);
               } 
               catch (Exception e){
                  jtaLog.append("Error: " + e + "\n");
               }
            }
            else{
               jtaLog.append("Error selecting file to save to.");
               return;
            }
         }
      }
      catch(Exception e){
         jtaLog.append("Error: " + e + "\n");
      }
      
      jtaLog.append("File " + file.getName() + " successfully uploaded.\n");
   }
}