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
* OrderServer is the server side of OrderClient for recieving order objects.
*/
public class OrderServer extends JFrame{
    private ServerSocket sSocket = null;
    public static final int SERVER_PORT = 9892;
    private ServerThread serverThread = null;

    private JButton jbStart = new JButton("Start");
    private JButton jbWriteToCSV = new JButton("Convert to CSV");
    private JTextArea jTextArea = new JTextArea("");

    private Vector<Order> ordersVector;

    /**
     * Main method to run GUI constructor.
     * @param //String[] args - N/A
     */
    public static void main(String[] args) {
        new OrderServer();
    }

    /**
     * Constructor to create GUI.
     */
    public OrderServer() {
        this.setTitle("Order Server - Ellie Parobek and JP Ramassini");
        this.setSize(300, 300);
        this.setLocation(800, 100);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ordersVector = new Vector<Order>();

        JPanel jpNorth = new JPanel();
        JPanel jpCenter = new JPanel();
        JPanel jpSouth = new JPanel();
        jpNorth.add(jbStart);
        jpCenter.add(jTextArea);
        jpSouth.add(jbWriteToCSV);
        this.add(jpNorth, BorderLayout.NORTH);
        this.add(jpSouth, BorderLayout.SOUTH);
        this.add(jpCenter, BorderLayout.CENTER);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent windowEvent){
                try {
                    jTextArea.append("Writing out objects...");
                    sSocket.close();
                    writeObjOut();
                    System.exit(0);
                } catch (Exception e){
                    
                }

            }
        });
        // Action listeners to start or stop server.
        jbStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                switch (ae.getActionCommand()) {
                    case "Start":
                        doStart();
                        break;
                    case "Stop":
                        doStop();
                        break;
                }
            }
        });

        jbWriteToCSV.addActionListener(e -> {
            convertToCSV();
        });

        this.setVisible(true);
    }

    /**
     * doStart creates server thread to connect to a client.
     */
    public void doStart(){
        jbStart.setText("Stop");
        jTextArea.append("Starting server on port " + SERVER_PORT + "\n");
        readObjIn();
        serverThread = new ServerThread();
        serverThread.start();
    }

    /**
     * doStop stops the server thread.
     */
    public void doStop(){
        jbStart.setText("Start");
        jTextArea.append("Server stopped.\n");
        serverThread.stopServer();
    }

    /**
     * Method to be run when window is closing
     */
    public void writeObjOut(){
        try{
            if(ordersVector.size() > 0){
                ObjectOutputStream ordersOut = new ObjectOutputStream(new FileOutputStream(new File("Orders.obj")));
                for (Order order : ordersVector) {
                    ordersOut.writeObject(order);
                }
                ordersOut.close();
            } 
            else{
                return;
            }
        } 
        catch(Exception e){
            System.out.println("Exception: " + e);
        }
    }
    
    /**
    * Read the Obj file for any existing entries.
    */
    public void readObjIn(){
        try{
            ObjectInputStream ordersIn = new ObjectInputStream(new FileInputStream("Orders.obj"));
            while(true){
                ordersVector.add((Order) ordersIn.readObject());
            }
        }
        catch(Exception e){
            // No file found, no objects to read.
            return;
        }
    }
   
    /**
    * Convert the data into a CSV file.
    */
    public void convertToCSV(){
        try {
            if(ordersVector.size() > 0) {
               FileWriter csvWriter = new FileWriter("orders.csv");
                csvWriter.write("\"Customer's name\",\"Customer's address\",\"Customer's email address\",Item_number,Quantity\n");
                csvWriter.flush();
                for(Order order : ordersVector){
                    String outString = "\"" + order.getCustName() + "\",\""+ order.getCustAddress() + "\",\"" +
                            order.getCustEmail() + "\"," + order.getItemNum() + "," + order.getQuantity()+"\n";
                    csvWriter.write(outString);
                    csvWriter.flush();
                }
                csvWriter.close();
            }
            else{
                jTextArea.append("There must be orders to convert.\n");
            }
        }
        catch(Exception e){
            System.out.println("Exception: " + e);
        }
    }

    /**
    * Class to manage the server threads.
    */
    class ServerThread extends Thread{
        /**
        * run tells the server threads what to do.
        */
        public void run(){
            try{
                sSocket = new ServerSocket(SERVER_PORT);
            } 
            catch(Exception e){
                System.out.println("IO Exception: " + e);
            }

            while(true){
                Socket cSocket = null;

                try{
                    cSocket = sSocket.accept();
                } 
                catch(IOException ioe){
                    return;
                }

                ClientThread ct = new ClientThread(cSocket);
                ct.start();
            }
        }
        
        /**
        * Close the server.
        */
        public void stopServer() {
            try {
                sSocket.close();
            } catch (Exception e) {
                System.out.println("Exception (Client): " + e);
            }
        }
    }
    
    /**
    * Class to manage the client threads.
    */ 
    class ClientThread extends Thread {
        private Socket cSocket;
        private String label = "";
        
        /**
        * Constructor to create thread.
        * @param Socket _cSocket - the socket to connect to.
        */
        public ClientThread(Socket _cSocket) {
            System.out.println("New client connected.");
            cSocket = _cSocket;
            label = cSocket.getInetAddress().getHostAddress() + ":" + cSocket.getPort() + "::";
        }
        
        /**
        * run tells the threads what to do.
        */
        public void run() {
            // Input Streams
            Scanner scn = null;
            ObjectInputStream objIn = null;

            // Output Streams
            PrintWriter pwt = null;
            try{
                pwt = new PrintWriter(new OutputStreamWriter(cSocket.getOutputStream()));
                scn = new Scanner(new InputStreamReader(cSocket.getInputStream()));

                while(scn.hasNextLine()){
                    String message = scn.nextLine();
                    System.out.println(message);
                    // Switch for each client request.
                    switch(message){
                        case "doOrder":
                            try{
                                objIn = new ObjectInputStream((cSocket.getInputStream()));
                                Order newOrder = (Order) objIn.readObject();
                                ordersVector.add(newOrder);
                                pwt.println("OK");
                                pwt.flush();
                                scn = new Scanner(new InputStreamReader(cSocket.getInputStream()));
                            } 
                            catch(Exception e){
                                System.out.println(e);
                            }
                            break;
                        case "doNum":
                            System.out.println("New Order.");
                            pwt.println("" + (ordersVector.size()));
                            pwt.flush();
                            break;
                        case "doDisconnect":
                            try{
                                cSocket.close();
                            } 
                            catch(Exception e){
                                System.out.println("Disconnect error");
                            }
                            break;

                    }
                }
            } 
            catch(Exception e){
                System.out.println("Exception: " + e);
            }
        }
    }
}

