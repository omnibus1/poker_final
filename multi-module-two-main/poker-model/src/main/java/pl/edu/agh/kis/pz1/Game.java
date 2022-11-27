package pl.edu.agh.kis.pz1;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import pl.edu.agh.kis.pz1.Player;
public class Game {
    int numberOfPlayers;
    int i=0;
    int winningBid;
    Player playerMoving;
    int ante;
    int playerInitialBalance=600;
    Deck dealerDeck;
    public int currentBid;
    public int sumToWin;
    ArrayList<Player> Players=new ArrayList<>();
    public Scanner scanner=new Scanner(System.in);
    boolean gameOver;
    int playersWantingToPlay;
    int playersLeftInGame=0;
    Player winner;
    ArrayList<FiveCardPokerHand> standings;
    Game(int numberOfPlayers){
        this.numberOfPlayers=numberOfPlayers;
        System.out.println("Creating the deck");
        dealerDeck=new Deck();
        dealerDeck.fillDeck();
        this.playersWantingToPlay=numberOfPlayers;
    }
    void addPlayer(Player player){
        if(Players.size()<numberOfPlayers){
            Players.add(player);
            player.inGame=true;
            System.out.println("Added Player "+player.name+ " to the game "+Integer.toString(Players.size())+"/"+Integer.toString(numberOfPlayers));
        }
        else{
            System.out.println("Already max players");
        }
    }
    void givePlayersCards(){
        System.out.println("shuffling the deck");
        dealerDeck.shuffle();
        for(Player player:Players){
            System.out.println("Player "+player.name+" is drawing cards");
            player.drawFromDeck(dealerDeck);
        }
    }


    void choseAnte(){
        int tmp=0;
        for(Player player:Players){
            System.out.println(player.name + " enter the ante you would like to have");
            tmp+=Integer.parseInt(scanner.nextLine());

        }
        this.ante=Math.round(tmp/Players.size());
        System.out.println("This games ante is "+this.ante);

    }
    void getAnteFromPlayers(){
        System.out.println("Ante "+ante);
        for(Player player:Players){
            System.out.print("Before "+player.name+" "+ player.balance);
            this.sumToWin+=this.ante;
            player.balance-=ante;
            System.out.println("Balance After Ante"+player.balance);
        }
    }
//    boolean checkIfGameOver(){
//        if (gameOver==true)return gameOver;
//        if(Players.size()==1){
//            gameOver=true;
//            return true;
//        }
//        gameOver=false;
//        return false;
//    }
    public void printPlayerStatus(Player player){
        System.out.println("Player "+player.name+ " your founds are "+ player.balance+"\n THE PRIZE TO WIN IS "+ sumToWin);
        player.printDeck();
    }

    String getPlayerStatus(Player player){
        return "Player "+player.name+ " your founds are "+ player.balance+"\n THE PRIZE TO WIN IS "+ sumToWin+"\n"+player.getDeck()+"\nPlayer moving is "+playerMoving.name;
    }

    void initializeParameters(){
        gameOver=false;
        sumToWin=0;
        System.out.println("SET sum to win to: "+sumToWin);
        this.dealerDeck=new Deck();
        dealerDeck.fillDeck();
        dealerDeck.shuffle();
        System.out.println("created a new deck and shuffled it");
        currentBid=0;
        System.out.println("SET current bid to "+currentBid);
        for(Player player:Players){
            player.inGame=true;
            player.hasMoved=false;
            playersLeftInGame+=1;
        }

    }
    void play(){
        if(Players.size()==numberOfPlayers) {
            while (true) {
                if (playersWantingToPlay == numberOfPlayers) {
                    this.startGame();
                }
                for (Player player : Players) {
                    System.out.println("Do you want to play again yes for yes else no");
                    String response = scanner.nextLine();
                    if (response.startsWith("yes")) playersWantingToPlay += 1;

                }
                if (playersWantingToPlay != numberOfPlayers) {
                    System.out.println("ending the game");
                    break;
                }
            }
        }
        else{
            System.out.println("Not enough players");
        }


    }

    public void startGame(){
        initializeParameters();

//        choseAnte();
        this.ante=100;
        getAnteFromPlayers();
        givePlayersCards();
        for(Player player:Players){
            printPlayerStatus(player);
        }
        playerMoving=Players.get(i%numberOfPlayers);
    }


    public String avilableMovesHelper(boolean isReplacingRound) {
        if(isReplacingRound){
            return"This is the replacing round, you can replace up to 5 cards using replace and passing the indexes, or you can type PASS to not replace any";
        }
        return "Avilable commands are: BID, REPLACE, PASS, CALL, STATUS\n You can type HELP COMMAND_NAME to get help regarding the command use";
    }
    public String handleHelp(String userInput){
        userInput=userInput.toUpperCase();
        String response="";
        if(userInput.contains("BID")){
            response+="BID number -enter a bid you would like to make, remember it must be higher than the last bid, and you must have the founds to cover it\n\n";
        }
        if(userInput.contains("PASS")){
            response+="PASS -if you choose to pass in the round this command will make you do so\n\n";
        }
        if(userInput.contains("REPLACE")){
            response+="REPLACE number [number] [number] [number] [number] -replace replaces your cards, you can enter from 1 to 5 card indexes, they correspond with the indexes youre given\n\n";
        }
        if(userInput.contains("CALL")){
            response+="CALL -you call to check the game, after it the winner will be decided, use it wisely\n\n";
        }
        if(response.contains("STATUS")){
            response+="STATUS -returns a status of the current play including your tokens, the prize to win and your deck of cards\n\n";
        }
        return response;

    }

    public String interpretate(String playerInput,Player player,boolean replacingRound){
        playerInput=playerInput.toUpperCase();
        if(player!=playerMoving){
            return "Its not your turn its "+playerMoving.name +"'s turn";
        }

        if(!player.chosenMove.equals("")){
            System.out.println("xxx");
            if(playerInput.startsWith("YES")){
                System.out.println("yyy");
                return makeMove(player);
            }
            else{
                player.chosenMove="";
                return "ok choose your move again";
            }
        }
        if(playerInput.startsWith("PASS")&&!replacingRound){
            return handlePass(player);
        }
        if(playerInput.startsWith("CALL")&&!replacingRound){
            System.out.println("inside call");
            return handleCall(player);
        }

        if((playerInput.startsWith("REPLACE")||playerInput.startsWith("PASS"))){
            if(replacingRound){
                System.out.println("in replace interprate");
                return handleReplace(playerInput,player);
            }
            else{
                return "you cannot replace in this round,"+avilableMovesHelper(false);
            }


        }
        if(playerInput.startsWith("BID")){
            if(!replacingRound) {
                return handleBid(playerInput, player);
            }
            else{
                return "Its the replacing round you can only replace";
            }
        }
        return "Invalid command type HELP to get a quick manual help";
    }
    public String handlePass(Player player){
        player.chosenMove="PASS";
        return "Are you sure you want to pass?, there is no turning back";

    }
    public String handleCall(Player player){
        player.chosenMove="CALL";
        return "Are you sure you want to call? type yes if so or anything to exit";

    }
    public String handleBid(String playerInput,Player player){
        if(playerInput.strip().length()==3){
            return "you must enter a number";
        }
        try {
            int playerBid = Integer.parseInt(playerInput.substring(3).strip());
            player.chosenMove = playerInput;
            return "Are you sure you want to raise your bid to " + playerBid;
        } catch (Exception e){
            return "you have entered a invalid text";
        }


    }
    String handleReplace(String playerInput,Player player){
        if(playerInput.startsWith("PASS")){
            String response="Player "+player.name+" didin't replace any cards\n";
            player.hasReplaced=true;
            player.hasMoved=true;
            nextMoveingPlayer();
            response+="/ALLPlayer"+player.name+" didnt replace any cards\n";
            response += "Player moving is " + playerMoving.name;
            return response;

        }
        String tmp=playerInput;
        if(playerInput.strip().length()==7){
            return "you must enter a number/numbers after replace";
        }
        System.out.println("1");
        String response="";
        playerInput=playerInput.substring(7);
        System.out.println("2");
        playerInput=playerInput.strip();
        System.out.println("3");

        String[] splited = playerInput.split(" ");
        ArrayList<Integer> indexes=new ArrayList<>();
        System.out.println("4");
        for(String s:splited) {
            int index = Integer.parseInt(s);
            if (index >= 0 && index <= 4 && !indexes.contains(index)) {
                indexes.add(index);
            }
        }
        System.out.println("-------");
        response+="Are you sure you want to replace these cards:";
        for(int i:indexes){
            response+=player.playerDeck.get(i)+"---";
        }
        System.out.println("======");
        player.chosenMove=tmp;
        return response;
    }
    String makeMove(Player player){
        if(player.chosenMove.startsWith("BID")){
            return handlePlayerBid(player);
        }
        if(player.chosenMove.startsWith("REPLACE")){
            return handleReplace(player);
        }
        if(player.chosenMove.startsWith("PASS")){
            return handleMadePass(player);
        }
        if(player.chosenMove.startsWith("CALL")){
            return playerMakeCall(player);
        }
        return "";
    }
    public String handleMadePass(Player player){
        String response="Youre out of the game for now";
        player.inGame=false;


        response+="/ALLPlayer "+player.name+" has passed";
        playersLeftInGame-=1;
        response+="Players left in game "+playersLeftInGame+"/"+numberOfPlayers;
        player.hasMoved=true;
        nextMoveingPlayer();
        response += "Player moving is " + playerMoving.name;
        if(chekcIfGameOver()){
            gameOver=true;
        }
        return response;

    }
    boolean chekcIfGameOver(){
        if(playersLeftInGame==1){
            winner=playerMoving;
            winner.balance+=sumToWin;
            return true;
        }
        return false;
    }
    String handleReplace(Player player){
        String response="";
        String playerInput=player.chosenMove.substring(7);
        playerInput=playerInput.strip();

        String[] splited = playerInput.split(" ");
        ArrayList<Integer> indexes=new ArrayList<>();
        for(String s:splited) {
            int index = Integer.parseInt(s);
            if (index >= 0 && index <= 4 && !indexes.contains(index)) {
                indexes.add(index);
                response+=player.playerDeck.get(index);
            }
        }
        for(int index:indexes){
            Card old=player.playerDeck.get(index);
            Card newCard=dealerDeck.draw();
            player.playerDeck.set(index,newCard);
            response+=("Replaced "+ old+" for "+newCard+" at index "+Integer.toString(index)+" - ");

        }
        response+="/ALL Player "+player.name+" replaced "+indexes.size()+" cards\n";
        player.hasReplaced=true;
        player.hasMoved=true;
        System.out.println("Replace handled");
        player.chosenMove = "";
        nextMoveingPlayer();
        response += "Player moving is " + playerMoving.name;
        return response;
    }
    String handlePlayerBid(Player player) {
        int playerBid = Integer.parseInt(player.chosenMove.substring(3).strip());
        String response = "";
        if (playerBid >= currentBid) {
            if (player.balance - playerBid >= 0) {
                currentBid = playerBid;
                sumToWin += currentBid;

                player.balance -= currentBid;


                player.hasMoved = true;
                response +="/PRIVATE"+ player.name + "your balance after bidding is " + player.balance + "\n";

                response += ("/ALLBid: +" + playerBid + " by " + player.name + "\n");
                response += "There is " + sumToWin + " tokens to win\n";
                System.out.println("bidding performed");
                player.chosenMove = "";
                nextMoveingPlayer();
                response += "Player moving is " + playerMoving.name;

            }
        }
        return response;
    }
    void nextMoveingPlayer(){
        i+=1;
        while(!Players.get(i%numberOfPlayers).inGame){
            i++;
        }
        playerMoving=Players.get(i%numberOfPlayers);

    }
    String playerMakeCall(Player player){
        String response="";
        response+="You have decided to call";
        response+="/ALLPLayer"+player.name+"has called"+"Picking the winner";
        ArrayList<FiveCardPokerHand> handsToCompare=new ArrayList<>();
            player.hasMoved=true;
            for(Player playersInGame:Players){
                handsToCompare.add(new FiveCardPokerHand.Builder().addCards(playersInGame.playerDeck).addName(playersInGame.name).build());
                System.out.println(playersInGame.name+"-----added");

            }
            this.gameOver=true;
            Collections.sort(handsToCompare);
            String winnerName=handsToCompare.get(handsToCompare.size()-1).name;
            for(Player p:Players){
                if(p.name.equals(winnerName)){
                    winner=p;
                }
            }
            return response;

    }


}

