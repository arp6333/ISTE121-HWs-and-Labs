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
* RemoteFileServer is the server side of RemoteFileBrowser for sending and receiving files.
*/
public class RemoteFileServer extends JFrame{
   private JButton jbStart = new JButton("Start");
   private JTextArea jtaLog = new JTextArea(10, 45);

   private ServerSocket sSocket = null;
   public static final int SERVER_PORT = 9892;
   private ServerThread serverThread = null;
   
   /**
   * Main method to run GUI constructor.
   * @param String[] args - N/A
   */
   public static void main(String[] args){
      new RemoteFileServer();
   }
   
   /**
   * Constructor to create GUI.
   */
   public RemoteFileServer(){
      this.setTitle("Remote File Server - Ellie Parobek and JP Ramassini");
      this.setSize(600, 300);
      this.setLocation(800, 100);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JPanel jpNorth = new JPanel();
      jpNorth.setLayout(new FlowLayout(FlowLayout.RIGHT));
      jpNorth.add(jbStart);
      this.add(jpNorth, BorderLayout.NORTH);
      // Action listeners to start or stop server.
      jbStart.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent ae){
            switch(ae.getActionCommand()) {
               case "Start":
                  doStart();
                  break;
               case "Stop":
                  doStop();
                  break;
            }
         }
      } );

      DefaultCaret caret = (DefaultCaret)jtaLog.getCaret();
      caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
      jtaLog.setLineWrap(true);
      jtaLog.setWrapStyleWord(true);

      JPanel jpCenter = new JPanel();
      jpCenter.add(new JScrollPane(jtaLog));
      this.add(jpCenter, BorderLayout.CENTER);

      this.setVisible(true);
   }
   
   /**
   * doStart creates server thread to connect to a client.
   */
   public void doStart(){
      jbStart.setText("Stop");
      serverThread = new ServerThread();
      serverThread.start();
   }

   /**
   * doStop stops the server thread.
   */
   public void doStop(){
      jbStart.setText("Start");
      serverThread.stopServer();
   }
   
   /**
   * ServerThread creates and runs server threads.
   */
   class ServerThread extends Thread{
      /**
      * run tells the server threads what to do.
      */
      public void run(){
         // Try to create server socket.
         try{
            sSocket = new ServerSocket(SERVER_PORT);
         }
         catch(IOException ioe){
            jtaLog.append("IO Exception (Client): "+ ioe);
            return;
         }

         while(true){
            Socket cSocket = null;

            try{
               cSocket = sSocket.accept();
            }
            catch(IOException ioe){
               return;
            }
            // Create client thread to connect to client.
            ClientThread ct = new ClientThread(cSocket);
            ct.start();
         }
      }
      // stopServer closes the server sockets to stop the server threads.
      public void stopServer(){
         try{
            sSocket.close();
         }
         catch(Exception e){
            jtaLog.append("Exception (Client): " + e);
         }
      }
   }
   
   /**
   * ClientThread class creates and runs client threads.
   */
   class ClientThread extends Thread{
      private Socket cSocket;
      private String label = "";
      private String currentDir = System.getProperty("user.dir");
      
      /**
      * Constructor gets the client socket.
      */
      public ClientThread(Socket _cSocket){
         cSocket = _cSocket;
         label = cSocket.getInetAddress().getHostAddress() + ":" + cSocket.getPort() + " :: ";
      }
      
      /**
      * run tells the client threads what to do.
      */
      public void run(){
         Scanner scn = null;
         PrintWriter pwt = null;

         jtaLog.append("Connection: " + label + "\n");
         // Try to connect to client.
         try{
            scn = new Scanner(new InputStreamReader(cSocket.getInputStream()));
            pwt = new PrintWriter(new OutputStreamWriter(cSocket.getOutputStream()));
         }
         catch(IOException ioe){
            jtaLog.append(label + "IO Exception (ClientThread): "+ ioe + "\n");
            return;
         }
         
         // Listen for each button input from the client.
         while(scn.hasNextLine()){
            String message = scn.nextLine();
            jtaLog.append(label + "Received: " + message + "\n");
            switch(message){
               // doList sents the current working directory to the client.
               case "doList":
                  pwt.println(currentDir);
                  pwt.flush();
                  break;
               // doChDir changes the current working directory from clients requested path.
               case "doChDir":
                  pwt.println("OK");
                  pwt.flush();
                  currentDir += scn.nextLine();
                  pwt.println(currentDir);
                  pwt.flush();
                  break;
               // doUpload receives a file to save from the client.
               case "doUpload":
                  pwt.println(currentDir);
                  pwt.flush();
                  String fileName = scn.nextLine();
                  try{
                     // Get the file from the client. 
                     ObjectInputStream objInStream = new ObjectInputStream(cSocket.getInputStream());
                     File outFile = new File(fileName);
                     outFile.createNewFile();
                     ObjectOutputStream objOut = new ObjectOutputStream(new FileOutputStream(outFile));
                     // Write out file from the client.
                     File file = (File)objInStream.readObject();
                     objOut.writeObject(file);
                     jtaLog.append("Successfully wrote object out.\n");
                  } 
                  catch(Exception e){
                     jtaLog.append("Exception: " + e + "\n");
                  }
                  break;
               // doDownload sends requested file to the client.
               case "doDownload":
                  // Get requested file.
                  String requestedFileName = scn.nextLine();
                  // Check if file exists on the server.
                  if(new File(requestedFileName).exists()){
                     try{
                        pwt.println("File found");
                        pwt.flush();
                        // Write file out to client.
                        ObjectOutputStream objOut = new ObjectOutputStream(cSocket.getOutputStream());
                        File out = new File(requestedFileName);
                        objOut.writeObject(out);
                     } 
                     catch(Exception e){
                        pwt.println("Error sending file.");
                        pwt.flush();
                        System.out.println(e);
                     }
                  }
                  else{
                     pwt.println("Error sending file.");
                     pwt.flush();
                  }
                  break;
            }
         }
         // Disconnect from client.
         try{
            cSocket.close();
            scn.close();
            pwt.close();
         }
         catch(IOException ioe){
            jtaLog.append(label + "IO Exception (ClientThread): "+ ioe + "\n");
            return;
         }

         jtaLog.append(label + "Client disconnected\n");
      }
   }
}