/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.jtk.polban.ac.id.localchat;

/**
 *
 * 
 */
import java.io.*;
import java.net.*;
import java.util.*;


public class Server
{
    ArrayList clientOutputStreams;
    
    /**
     * Class untuk menghandle cient
     */
    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;
        
        public ClientHandler(Socket clientSOcket) 
        {
            try {
                sock = clientSOcket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
                
            } catch (IOException ex) { ex.printStackTrace(); }
        }
        
        @Override
        public void run() 
        {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("read " + message);
                    tellEveryone(message);
                }
            } catch (IOException ex) { ex.printStackTrace(); }
        }
    }
    
    public static void main(String[] args) 
    {
        new Server().go();
    }
    
    public void go() 
    {
        clientOutputStreams = new ArrayList();
        try {
            ServerSocket serverSock = new ServerSocket(5000);
            while(true) 
            {
                Socket clientSocket = serverSock.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);
                
                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
                System.out.println("got a connection");
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }
    
    /**
     * Mengirimkan pesan ke Client
     * @param message 
     */
    public void tellEveryone(String message) 
    {
        Iterator it = clientOutputStreams.iterator();
        while (it.hasNext()) 
        {
            try {
                PrintWriter writer = (PrintWriter) it.next();
                writer.println(message);
                writer.flush();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
}
