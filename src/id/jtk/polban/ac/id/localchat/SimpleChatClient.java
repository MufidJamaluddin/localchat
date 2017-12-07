/*Run Server terlebih dahulu lalu run client*/

package id.jtk.polban.ac.id.localchat;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SimpleChatClient
{
    String username=null;
    JLabel label;
    JTextArea incoming;
    JTextField outgoing, tf_username;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;
    ArrayList<String> history;
    
    /**
     * Kosntruktor
     */
    public SimpleChatClient()
    {
        this.history = new ArrayList<>();
    }
    
    /**
     * Menu
     */
    public void go() 
    {
        JFrame frame = new JFrame("Chat Client");
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
        outgoing = new JTextField(30);
        JButton sendButton = new JButton("Send");
        JButton setUsernameButton = new JButton("Set");
        JButton saveHistoryButton = new JButton("Save Chat History");
        JButton openHistoryButton = new JButton("Open Chat History");
        sendButton.addActionListener(new SendButtonListener());
        setUsernameButton.addActionListener(new SetUsernameButtonListener());
        saveHistoryButton.addActionListener(new SaveHistoryButtonListener());
        openHistoryButton.addActionListener(new OpenHistoryButtonListener());
        mainPanel.add(label);
        mainPanel.add(tf_username);
        mainPanel.add(setUsernameButton);
        mainPanel.add(qScroller);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);
        mainPanel.add(saveHistoryButton);
        mainPanel.add(openHistoryButton);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        setUpNetworking();
        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();
        
        frame.setSize(650, 400);
        frame.setVisible(true);
        
    }
    
    /**
     * Set Jaringan
     */
    private void setUpNetworking() 
    {
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
    
    /**
     * Aksi Tombol Send
     */
    public class SendButtonListener implements ActionListener 
    {
        @Override
        public void actionPerformed(ActionEvent ev) 
        {
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
    
    /**
     * 
     */
    public class SetUsernameButtonListener implements ActionListener 
    {
        @Override
        public void actionPerformed(ActionEvent ev) 
        {
            username = tf_username.getText();
            tf_username.setEditable(false);
        }
    }
    
    public class SaveHistoryButtonListener implements ActionListener 
    {
        @Override
        public void actionPerformed(ActionEvent ev) 
        {
            //file history ada di folder project dengan nama chathistory.txt
            try(FileWriter fw = new FileWriter("chathistory.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw))
            {
                history.forEach(out::println);
                out.close();
                incoming.append(">>>>chat saved!\n");
            } catch (IOException e) {
                
            }
        }
    }
    
    public class OpenHistoryButtonListener implements ActionListener 
    {
        @Override
        public void actionPerformed(ActionEvent ev) 
        {
            try(BufferedReader br = new BufferedReader(new FileReader("chathistory.txt"))) {
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                String everything = sb.toString();
                incoming.append(everything + ">>>>chat history !\n");
            }catch (IOException e) {
                
            }
        }
    }
    
    public static void main(String[] args) 
    {
        new SimpleChatClient().go();
    }
    
    
    
    /**
     * Thread buat membaca message dari server
     */
    class IncomingReader implements Runnable 
    {
        @Override
        public void run() 
        {
            String message;
            try 
            {
                while ((message = reader.readLine()) != null) 
                {
                    System.out.println("client read " + message);
                    history.add(message);
                    incoming.append(message + "\n");
                }
            } 
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }
}