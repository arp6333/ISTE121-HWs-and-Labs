import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.text.DefaultCaret;

/**
* Lab 07 / HW 08 ISTE 121
* Ellie Parobek and JP Ramassini
* OrderClient is the client side of OrderServer for sending Order objects.
*/
public class OrderClient extends JFrame implements ActionListener{
   private JLabel jlServerIP = new JLabel("Server:");
   private JTextField jtfServerIP = new JTextField(20);
   private JButton jbConnect = new JButton("Connect");
   
   private JLabel name = new JLabel("Name:", SwingConstants.RIGHT);
   private JLabel street = new JLabel("Street:", SwingConstants.RIGHT);
   private JLabel city = new JLabel("City:", SwingConstants.RIGHT);
   private JLabel state = new JLabel("State:", SwingConstants.RIGHT);
   private JLabel zip = new JLabel("Zip:", SwingConstants.RIGHT);
   private JLabel email = new JLabel("Email:", SwingConstants.RIGHT);
   private JLabel itemNum = new JLabel("Item Number:", SwingConstants.RIGHT);
   private JLabel quantity = new JLabel("Quantity:", SwingConstants.RIGHT);
   
   private JTextField tname = new JTextField("", 25);
   private JTextField tstreet = new JTextField("", 25);
   private JTextField tcity = new JTextField("", 25);
   private JTextField tstate = new JTextField("", 25);
   private JTextField tzip = new JTextField("", 25);
   private JTextField temail = new JTextField("", 25);
   private JTextField titemNum = new JTextField("", 25);
   private JTextField tquantity = new JTextField("", 25);

   private JButton jbNew = new JButton("New Order");
   private JButton jbNum = new JButton("Number of Current Orders");
   private JButton jbExit = new JButton("Disconnect");

   private JTextArea jtaLog = new JTextArea(10, 45);

   private PrintWriter pwt = null;
   private Scanner scn = null;
   private ObjectOutputStream out = null;

   public static final int SERVER_PORT = 9892;
   private Socket socket = null;
   
   /**
   * Main method to run GUI constructor.
   * @param String[] args - N/A
   */
   public static void main(String[] args){
      new OrderClient();
   }
   
   /**
   * Constructor to create GUI.
   */
   public OrderClient(){
      this.setTitle("Order Client - Ellie Parobek and JP Ramassini");
      this.setSize(450, 300);
      this.setLocation(100, 100);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      this.addWindowListener(new java.awt.event.WindowAdapter(){
      public void windowClosing(java.awt.event.WindowEvent windowEvent){
         pwt.println("doDisconnect");
         pwt.flush();
         System.exit(0);
      }});

      JPanel jpNorth = new JPanel();
      jpNorth.setLayout(new GridLayout(0,1));

      JPanel jpRow1 = new JPanel();
      jpRow1.add(jlServerIP);
      jpRow1.add(jtfServerIP);
      jpRow1.add(jbConnect);
      jpNorth.add(jpRow1);
      
      JPanel jpRow2 = new JPanel();
      jpRow2.add(jbNew);
      jpRow2.add(jbNum);
      jpRow2.add(jbExit);
      
      // Disable buttons until client has connected.
      jbNew.setEnabled(false);
      jbNum.setEnabled(false);
      jbExit.setEnabled(false);
      jpNorth.add(jpRow2);
      this.add(jpNorth, BorderLayout.NORTH);

      DefaultCaret caret = (DefaultCaret)jtaLog.getCaret();
      caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
      jtaLog.setLineWrap(true);
      jtaLog.setWrapStyleWord(true);

      JPanel jpCenter = new JPanel();
      jpCenter.setLayout(new GridLayout(0,2));
      jpCenter.add(name);
      jpCenter.add(tname);
      jpCenter.add(street);
      jpCenter.add(tstreet);
      jpCenter.add(city);
      jpCenter.add(tcity);
      jpCenter.add(state);
      jpCenter.add(tstate);
      jpCenter.add(zip);
      jpCenter.add(tzip);
      jpCenter.add(email);
      jpCenter.add(temail);
      jpCenter.add(itemNum);
      jpCenter.add(titemNum);
      jpCenter.add(quantity);
      jpCenter.add(tquantity);
      this.add(jpCenter, BorderLayout.CENTER);

      jbConnect.addActionListener(this);
      jbNew.addActionListener(this);
      jbNum.addActionListener(this);
      jbExit.addActionListener(this);

      this.setVisible(true);
   }
   
   /**
   * actionPerformed to process the action listener for each button.
   * @param ActionEvent ae - the button that has been pressed
   */
   public void actionPerformed(ActionEvent ae) {
      switch(ae.getActionCommand()){
         case "New Order":
            doOrder();
            break;
         case "Number of Current Orders":
            doNum();
            break;
         case "Disconnect":
            doDisconnect();
            break;
         case "Connect":
            doConnect();
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
      jbNew.setEnabled(true);
      jbNum.setEnabled(true);
      jbExit.setEnabled(true);
   }
   
   /**
   * doDisconnect disconnects the client from the server.
   */
   private void doDisconnect(){
      pwt.println("doDisconnect");
      pwt.flush();
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
      jbNew.setEnabled(false);
      jbNum.setEnabled(false);
      jbExit.setEnabled(false);
   }
   
   /**
   * doOrder creates a new order object.
   */
   private void doOrder(){
      pwt.println("doOrder");
      pwt.flush();
      
      jbNew.setEnabled(false);
      
      try{
         out = new ObjectOutputStream(socket.getOutputStream());
      }
      catch(IOException ioe){
         JOptionPane.showMessageDialog(null, "Cannot Create ObjectOutputStream: " + ioe + "\n", "File Error", JOptionPane.ERROR_MESSAGE);
         return;
      }
      // Create order object based on text fields
      Order order = null;
      try{
         order = new Order(tname.getText(), tstreet.getText() + tcity.getText() + tstate.getText() + tzip.getText(), temail.getText(), Integer.parseInt(titemNum.getText()), Integer.parseInt(tquantity.getText()), 01L);    
      }
      catch(Exception e){
         JOptionPane.showMessageDialog(null, "Text fields are not completed correctly: " + e + "\n", "Text Error", JOptionPane.ERROR_MESSAGE);
         return;
      }
      try{
         // Write order to the server.
         out.writeObject(order);
         out.flush();
      }
      catch(IOException ioe){
         JOptionPane.showMessageDialog(null, "Cannot Send File: " + ioe + "\n", "File Error", JOptionPane.ERROR_MESSAGE);
         return;
      }
      // Wait for server to say it is all good.
      String reply = scn.nextLine();
      if(reply.equals("OK")){
         JOptionPane.showMessageDialog(null, "Order recieved!");
         jbNew.setEnabled(true);
         return;
      }    
   }
   
   /**
   * doNum lists the number of orders currently in the list.
   */
   private void doNum(){
      pwt.println("doNum");
      pwt.flush();
      String reply = scn.nextLine();
      JOptionPane.showMessageDialog(null, "Number of current orders: " + reply);
   }
}