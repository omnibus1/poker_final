package pl.edu.agh.kis.pz1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Server{
    private static final int BUFFER_SIZE = 1024;
    private static Selector selector = null;
    public static Map<SocketChannel,String> connectedUsers=new HashMap<>();
    static int usersCount=0;

    public static void main(String[] args) {
        System.out.println("starting server");
        try {
            InetAddress hostIP = InetAddress.getLocalHost();
            int port = 9999;

            System.out.printf("listening to connections on %s:%d...%n",
                    hostIP.getHostAddress(), port);
            selector = Selector.open();
            ServerSocketChannel mySocket = ServerSocketChannel.open();
            ServerSocket serverSocket = mySocket.socket();
            InetSocketAddress address = new InetSocketAddress(hostIP, port);
            serverSocket.bind(address);

            mySocket.configureBlocking(false);
            int ops = mySocket.validOps();
            mySocket.register(selector, ops, null);
            while (true) {

                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> i = selectedKeys.iterator();

                while (i.hasNext()) {
                    SelectionKey key = i.next();


                    if (key.isAcceptable()) {
                        processAcceptEvent(mySocket, key);
                    } else if (key.isReadable()) {
                        processReadEvent(key);

                    }
                    i.remove();
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processAcceptEvent(ServerSocketChannel mySocket,
                                           SelectionKey key) throws IOException {


        usersCount+=1;



        // Accept the connection and make it non-blocking
        SocketChannel myClient = mySocket.accept();
        myClient.configureBlocking(false);
        connectedUsers.put(myClient,"User"+Integer.toString(usersCount));
        System.out.println("client accepted:"+connectedUsers.get(myClient));
        // Register interest in reading this channel
        myClient.register(selector, SelectionKey.OP_READ);
        serverToAll(connectedUsers.get(myClient)+"has joined");
    }

    private static void processReadEvent(SelectionKey key)
            throws IOException {
        System.out.println("inside read");

        // create a ServerSocketChannel to read the request
        SocketChannel myClient = (SocketChannel) key.channel();

        // Set up out 1k buffer to read data into
        ByteBuffer clientResponse = ByteBuffer.allocate(BUFFER_SIZE);
        myClient.read(clientResponse);
        String data = new String(clientResponse.array()).trim();
        if (data.length() > 0) {
            System.out.printf("message recived from: %s %s%n",connectedUsers.get(key.channel()), data);
            if(data.startsWith("/all")){
                sendToAllUsersFromUser(data.substring(4),myClient);
            }
            if (data.equalsIgnoreCase("*exit*")) {
                myClient.close();
                System.out.println("closing server");
            }
        }


    }
    private static void sendToAllUsersFromUser(String message,SocketChannel messageAuthor){



        String finalMessage =connectedUsers.get(messageAuthor)+"has said: "+ message;
        connectedUsers.forEach((x, value)-> {
            try {

                ByteBuffer serverResponse= ByteBuffer.allocate(BUFFER_SIZE);
                serverResponse.put(finalMessage.getBytes());
                serverResponse.flip();
                x.write(serverResponse);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
    private static void serverToAll(String message){
        connectedUsers.forEach((x,value)->{
            try{
                ByteBuffer serverResponse= ByteBuffer.allocate(BUFFER_SIZE);
                serverResponse.put(message.getBytes());
                serverResponse.flip();
                x.write(serverResponse);
            }
            catch(IOException e){
                throw new RuntimeException(e);
            }
        });
    }


}
