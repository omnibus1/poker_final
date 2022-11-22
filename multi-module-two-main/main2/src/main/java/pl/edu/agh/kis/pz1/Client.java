package pl.edu.agh.kis.pz1;

import pl.edu.agh.kis.pz1.util.TextUtils;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    private static final int BUFFER_SIZE = 1024;


    public static void main(String[] args) {

        System.out.println("Starting MySelectorClientExample...");
        try {
            int port = 9999;
            InetAddress hostIP = InetAddress.getLocalHost();
            InetSocketAddress myAddress =
                    new InetSocketAddress(hostIP, port);
            SocketChannel myClient = SocketChannel.open(myAddress);

            BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println(String.format("Trying to connect to %s:%d...",
                    myAddress.getHostName(), myAddress.getPort()));
            ServerConnection server=new ServerConnection(myClient);
            new Thread(server).start();
            while(true){
                String inputString=keyboardReader.readLine();
                ByteBuffer myBuffer=ByteBuffer.allocate(BUFFER_SIZE);
                myBuffer.put(inputString.getBytes());
                myBuffer.flip();
                myClient.write(myBuffer);
                if(inputString.equals("exit"))break;
            }


            System.out.println("closing connection");
            myClient.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }


}