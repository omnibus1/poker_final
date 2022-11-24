package pl.edu.agh.kis.pz1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;




public class ServerConnection implements Runnable{
    private final SocketChannel serverSocket;


    private static final int BUFFER_SIZE = 1024;

    ServerConnection(SocketChannel socket) throws IOException {
        this.serverSocket=socket;


    }
    @Override
    public void run() {

        while(true){
            ByteBuffer serverResponse=ByteBuffer.allocate(BUFFER_SIZE);

            try {
                serverSocket.read(serverResponse);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String data = new String(serverResponse.array()).trim();
            if(data.length()>0){
                System.out.printf("%s\n",data);

            }



        }

    }
}