package chap15;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class SimpleChatClient
{
    String username=null;
    JLabel label;
    JTextArea incoming;
    JTextField outgoing, tf_username;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;
    
    public void go() {
        JFrame frame = new JFrame("Ludicrously Simple Chat Client");
        JPanel mainPanel = new JPanel();
        incoming = new JTextArea(15, 50);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        tf_username = new JTextField(10);
        label = new JLabel();
        label.setText("Username : ");
        outgoing = new JTextField(20);
        JButton sendButton = new JButton("Send");
        JButton setUsernameButton = new JButton("Set");
        sendButton.addActionListener(new SendButtonListener());
        setUsernameButton.addActionListener(new SetUsernameButtonListener());
        mainPanel.add(label);
        mainPanel.add(tf_username);
        mainPanel.add(setUsernameButton);
        mainPanel.add(qScroller);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        setUpNetworking();
        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();
        
        frame.setSize(650, 500);
        frame.setVisible(true);
        
    }
    
    private void setUpNetworking() {
        try {
            sock = new Socket("127.0.0.1", 5000);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("networking established");
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }
    }
    
    public class SendButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            try {
                writer.println(username + " : " + outgoing.getText());
                writer.flush();
                
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }
    }
    
    public class SetUsernameButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            username = tf_username.getText();
            tf_username.setEditable(false);
        }
    }
    
    public static void main(String[] args) {
        new SimpleChatClient().go();
    }
    
    class IncomingReader implements Runnable {
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("client read " + message);
                    incoming.append(message + "\n");
                }
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
}

