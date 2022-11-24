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
import java.util.concurrent.TimeUnit;

public class Server{
    private static final int BUFFER_SIZE = 1024;
    private static Selector selector = null;
    public static Map<SocketChannel,Player> connectedUsers=new HashMap<>();
    static int usersCount=0;
    static int numOfPlayersForGame;
    static InetAddress hostIP;
    static int port;
    static ServerSocketChannel mySocket;
    static ServerSocket serverSocket;
    static InetSocketAddress address;
    static int usersReadyForGame=0;
    static boolean askForStart=false;
    static int readyUsers=0;
    static boolean inGame;
    static Game game;
    static boolean firstRound;
    static int replaced=0;




    public static void main(String[] args) {
        System.out.println("starting server");

        try {
            hostIP = InetAddress.getLocalHost();
            port = 9999;
            numOfPlayersForGame=Integer.parseInt(args[0]);
            System.out.println(numOfPlayersForGame);
            System.out.printf("listening to connections on %s:%d...%n",
                    hostIP.getHostAddress(), port);
            selector = Selector.open();
            mySocket = ServerSocketChannel.open();
            serverSocket = mySocket.socket();
            address = new InetSocketAddress(hostIP, port);
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
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static void processAcceptEvent(ServerSocketChannel mySocket,
                                           SelectionKey key) throws IOException, InterruptedException {


        usersCount+=1;



        // Accept the connection and make it non-blocking
        SocketChannel myClient = mySocket.accept();
        myClient.configureBlocking(false);
        connectedUsers.put(myClient,new Player("User"+Integer.toString(usersCount),myClient));
        System.out.println("client accepted:"+connectedUsers.get(myClient).name);
        // Register interest in reading this channel
        myClient.register(selector, SelectionKey.OP_READ);
        serverToAll("User "+connectedUsers.get(myClient).name+" has joined "+ usersCount+"/"+numOfPlayersForGame);
        if(usersCount==numOfPlayersForGame){
            askForStart=true;
            serverToAll("Type Yes to start the game");

        }

    }
    static void prepareGame(){
        game=new Game(numOfPlayersForGame);
        for(Player player:connectedUsers.values()){
            game.addPlayer(player);
        }
        game.startGame();
        for(Player player: game.Players){
            sendToPlayer(player,game.getPlayerStatus(player));
            sendToPlayer(player,game.avilableMovesHelper());
            sendToPlayer(player,"Only in this round you can replace your cards");
            firstRound=true;
        }
    }




    private static void processReadEvent(SelectionKey key)
            throws IOException, InterruptedException {

        String userResponse=getMessage(key).toLowerCase();
        SocketChannel myClient = (SocketChannel) key.channel();
        Player player=connectedUsers.get(key.channel());
        if(userResponse.toLowerCase().startsWith("help")){
            System.out.println("inside help");
            sendToPlayer(player,game.handleHelp(userResponse));
            return;

        }
        if (userResponse.length() > 0) {
            System.out.printf("message recived from: %s %s\n", connectedUsers.get(key.channel()), userResponse);
            if (userResponse.startsWith("/all")) {
                sendToAllUsersFromUser(userResponse.substring(4), myClient);
                return;
            }
        }
        if(askForStart){
            System.out.println("in ask for start");


            if(userResponse.startsWith("yes")&& !player.wantsToStart){
                readyUsers+=1;
                serverToAll(player.name+ " is ready "+readyUsers+"/"+numOfPlayersForGame);
                player.wantsToStart=true;

            }
            if(readyUsers==numOfPlayersForGame){
                serverToAll("All users ready starting the game...");
                askForStart=false;

                prepareGame();
                serverToAll("Player starting is "+game.playerMoving.name);
            }
            return;
        }
        if(firstRound){
            System.out.println("first round handler");
            String gameResponse=game.interpretate(userResponse,player,true);
            serverInterprate(gameResponse,player);
            if(player.hasReplaced){
                replaced+=1;
            }
            if(replaced==numOfPlayersForGame){
                serverToAll("Replacing part ended its time to play");
                firstRound=false;
                inGame=true;
            }
            return;
        }
        if(inGame){
            System.out.println("ingame handler");
            String gameResponse=game.interpretate(userResponse,player,false);
            serverInterprate(gameResponse,player);


        }

        // create a ServerSocketChannel to read the request

//        if (data.length() > 0) {
//            System.out.printf("message recived from: %s %s\n",connectedUsers.get(key.channel()), data);
//            if(data.startsWith("/all")){
//                sendToAllUsersFromUser(data.substring(4),myClient);
//
//            }
//            if (data.equalsIgnoreCase("*exit*")) {
//                myClient.close();
//                System.out.println("closing server");
//            }
//        }



    }
    static String getMessage(SelectionKey key) throws IOException {
        System.out.println("inside get message");
        SocketChannel myClient = (SocketChannel) key.channel();

        // Set up out 1k buffer to read data into
        ByteBuffer clientResponse = ByteBuffer.allocate(BUFFER_SIZE);
        myClient.read(clientResponse);
        String data = new String(clientResponse.array()).trim();
        if (data.length() > 0) {
            System.out.println(data);
            return data;}
        return "";




    }
    private static void sendToAllUsersFromUser(String message,SocketChannel messageAuthor){


        System.out.println("MESSAGE:"+message);
        String finalMessage =connectedUsers.get(messageAuthor).name+"has said: "+ message;
        System.out.println(finalMessage);
        connectedUsers.forEach((x, value)-> {
            try {
                ByteBuffer serverResponse = ByteBuffer.allocate(BUFFER_SIZE);
                serverResponse.clear();
                serverResponse.put(finalMessage.getBytes());
                serverResponse.flip();
                x.write(serverResponse);
                serverResponse.clear();


            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
    private static void serverToAll(String message) throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        connectedUsers.forEach((x,value)->{
            try{
                ByteBuffer serverResponse= ByteBuffer.allocate(BUFFER_SIZE);

                serverResponse.put(message.getBytes());
                serverResponse.flip();
                x.write(serverResponse);
                serverResponse.clear();
            }
            catch(IOException e){
                throw new RuntimeException(e);
            }
        });
    }
    static void sendToPlayer(Player player,String message){
        try{
            ByteBuffer serverResponse= ByteBuffer.allocate(BUFFER_SIZE);

            serverResponse.put(message.getBytes());
            serverResponse.flip();
            player.socket.write(serverResponse);
            serverResponse.clear();
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }

    }
    public static void serverInterprate(String gameResponse,Player player) throws InterruptedException {
        gameResponse=gameResponse.toUpperCase();
        System.out.println("inside serverInterprate");
        int messageSplit=gameResponse.indexOf("/ALL");
        if(messageSplit==-1){
            sendToPlayer(player,gameResponse);
        }
        else{
            String messageToPlayer=gameResponse.substring(0,messageSplit);
            String messageToAll=gameResponse.substring(messageSplit+4);
            sendToPlayer(player,messageToPlayer);
            serverToAll(messageToAll);
        }
    }

    class ReadFromSockets{

    }


}
