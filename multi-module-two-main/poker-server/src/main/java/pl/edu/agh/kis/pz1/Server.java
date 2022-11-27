package pl.edu.agh.kis.pz1;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Server{
    private static final int BUFFER_SIZE = 1024;
    private static Selector selector = null;
    public static Map<SocketChannel,Player> connectedUsers=new HashMap<>();
    static int usersCount=0;
    static int numOfPlayersForGame;
    static InetAddress hostIP;
    static int port;
    static boolean exitRead=false;
    static ServerSocketChannel mySocket;
    static ServerSocket serverSocket;
    static InetSocketAddress address;

    static boolean askForStart=false;
    static int readyUsers=0;
    static boolean inGame;
    static Game game;
    static boolean firstRound;
    static int replaced=0;
    static ArrayList<Player>performedAction=new ArrayList<>();
    static boolean askForRematch=false;
    static int readyUsersForRematch=0;




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
                if(exitRead)break;

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
    static void prepareGame() throws InterruptedException {
        game=new Game(numOfPlayersForGame);
        for(Player player:connectedUsers.values()){
            game.addPlayer(player);
        }
        game.startGame();
        for(Player player: game.Players){
            sendToPlayer(player,game.getPlayerStatus(player));


        }
        serverToAll(game.avilableMovesHelper(true));
        firstRound=true;
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
        if(userResponse.equals("status")){
            sendToPlayer(player,game.getPlayerStatus(player));
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


            if(userResponse.startsWith("yes")&& !performedAction.contains(player)){
                readyUsers+=1;
                serverToAll(player.name+ " is ready "+readyUsers+"/"+numOfPlayersForGame);
                performedAction.add(player);

            }
            if(readyUsers==numOfPlayersForGame){
                performedAction.clear();
                serverToAll("All users ready starting the game...");
                askForStart=false;
                readyUsers=0;

                prepareGame();
                serverToAll("Player starting is "+game.playerMoving.name);
            }
            return;
        }
        if(firstRound){
            System.out.println("first round handler");
            String gameResponse=game.interpretate(userResponse,player,true);
            serverInterprate(gameResponse,player);
            if(player.hasReplaced && !performedAction.contains(player)){
                replaced+=1;
                performedAction.add(player);
            }
            if(replaced==numOfPlayersForGame){
                serverToAll("Replacing part ended its time to play\n Avilable moves are CALL, BID, PASS");
                firstRound=false;
                inGame=true;
               performedAction.clear();
            }
            return;
        }
        if(inGame){
            System.out.println("ingame handler");
            String gameResponse=game.interpretate(userResponse,player,false);
            serverInterprate(gameResponse,player);
            if(game.gameOver){
                serverToAll("--------------------------------------------------");
                serverToAll("--------------------------------------------------");
                serverToAll("*****************Game is over*********************");
                serverToAll("The winner is "+game.winner.name);
                serverToAll("Winning is "+game.sumToWin);
                serverToAll("*****************Game is over*********************");
                serverToAll("--------------------------------------------------");
                serverToAll("--------------------------------------------------");
                inGame=false;
                askForStart=true;
                TimeUnit.SECONDS.sleep(1);
                serverToAll("Voting for another round starts now\n");
                serverToAll("type yes if you want to play");
                return;
            }
            System.out.println("asd");
            if(askForRematch){
                System.out.println("in ask for start");


                if(userResponse.startsWith("yes")&& !performedAction.contains(player)){
                    readyUsersForRematch+=1;
                    serverToAll(player.name+ " is ready "+readyUsersForRematch+"/"+numOfPlayersForGame);
                    performedAction.add(player);

                }
                if(readyUsersForRematch==numOfPlayersForGame){
                    readyUsersForRematch=0;
                    performedAction.clear();
                    serverToAll("All users ready starting the game...");
                    askForStart=true;

                }

            }


        }





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



}
