package pl.edu.agh.kis.pz1;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Game {
    int numberOfPlayers;
    int i=0;
    int winningBid;
    Player playerMoving;
    int ante=100;
    int playerInitialBalance=600;
    Deck dealerDeck;
    int currentBid;
    int sumToWin;
    ArrayList<Player> Players=new ArrayList<>();
    public Scanner scanner=new Scanner(System.in);
    boolean gameOver;
    int playersWantingToPlay;
    int playersLeftInGame=0;
    Player winner;
    ArrayList<FiveCardPokerHand> standings;
    Game(int numberOfPlayers){
        this.numberOfPlayers=numberOfPlayers;
        logger("Creating the deck");
        dealerDeck=new Deck();
        dealerDeck.fillDeck();
        this.playersWantingToPlay=numberOfPlayers;
    }
    void addPlayer(Player player){
        if(Players.size()<numberOfPlayers){
            Players.add(player);
            player.inGame=true;
            logger("Added "+player.name+ " to the game "+Integer.toString(Players.size())+"/"+Integer.toString(numberOfPlayers));
        }
        else{
            logger("Already max players");
        }
    }
    void givePlayersCards(){
        logger("shuffling the deck");
        dealerDeck.shuffle();
        for(Player player:Players){

            player.drawFromDeck(dealerDeck);
        }
    }



    void getAnteFromPlayers(){
        logger("Ante "+ante);
        for(Player player:Players){
            logger("Before "+player.name+" "+ player.balance);
            this.sumToWin+=this.ante;
            player.balance-=ante;
            logger("Balance After Ante"+player.balance);
        }
    }



    String getPlayerStatus(Player player){
        if(playerMoving!=null){
        return playerPrefix(player)+ " your founds are "+ player.balance+"\n THE PRIZE TO WIN IS "+ sumToWin+"\n"+player.getDeck()+"\n"+newPlayerMovingString();
    }
        else{
            return playerPrefix(player)+ " your founds are "+ player.balance+"\n THE PRIZE TO WIN IS "+ sumToWin+"\n"+"currently no player is moving";
        }
    }

    void initializeParameters(){
        gameOver=false;
        sumToWin=0;
        logger("SET sum to win to: "+sumToWin);
        this.dealerDeck=new Deck();
        dealerDeck.fillDeck();
        dealerDeck.shuffle();
        logger("created a new deck and shuffled it");
        currentBid=0;
        logger("SET current bid to "+currentBid);
        for(Player player:Players){
            player.inGame=true;
            player.hasMoved=false;
            playersLeftInGame+=1;
        }

    }

    public void startGame(){
        initializeParameters();



        getAnteFromPlayers();
        givePlayersCards();

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
        if(userInput.contains("STATUS")){
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
            logger("xxx");
            if(playerInput.startsWith("YES")){
                logger("yyy");
                return makeMove(player);
            }

            player.chosenMove="";
            return "ok choose your move again";

        }
        if(playerInput.startsWith("PASS")&&!replacingRound){
            return handlePass(player);
        }
        if(playerInput.startsWith("CALL")&&!replacingRound){
            logger("inside call");
            return handleCall(player);
        }

        if((playerInput.startsWith("REPLACE")||playerInput.startsWith("PASS"))){
            if(replacingRound){
                logger("in replace interprate");
                return handleReplace(playerInput,player);
            }
            return "you cannot replace in this round,"+avilableMovesHelper(false);



        }
        if(playerInput.startsWith("BID")){
            if(!replacingRound) {
                return handleBid(playerInput, player);
            }

            return "Its the replacing round you can only replace";

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
    String handleReplace(String playerInput,Player player) {
        if (playerInput.startsWith("PASS")) {
            player.chosenMove="RPASS";
            return "Are you sure you dont want to replace any cards, you cannot replace them later";

        }
        String tmp = playerInput;
        if (playerInput.strip().length() == 7) {
            return "You must enter a number/numbers after replace";
        }

        String response = "";
        playerInput = playerInput.substring(7);

        playerInput = playerInput.strip();


        String[] splited = playerInput.split(" ");
        ArrayList<Integer> indexes = new ArrayList<>();

        try{
            for (String s : splited) {
                int index = Integer.parseInt(s);
                if (index >= 0 && index <= 4 && !indexes.contains(index)) {
                    indexes.add(index);
                }
            }
        }
        catch (Exception e){
            return "You have entered invalid text, enter SPACE SEPERATED NUMBERS";
        }

        response+="Are you sure you want to replace these cards:";

        for(int idx:indexes){
            response+=player.playerDeck.get(idx)+"; ";
        }

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
        if(player.chosenMove.startsWith("RPASS")){
            return handleReplacePass(player);
        }
        return "";
    }
    String handleReplacePass(Player player){
        String response="Player "+player.name+" didin't replace any cards\n";
        player.hasReplaced=true;
        player.hasMoved=true;
        nextMoveingPlayer();
        response+="/ALLPlayer"+player.name+" didnt replace any cards\n";
        response += newPlayerMovingString();
        return response;
    }
    public String handleMadePass(Player player){
        String response="Youre out of the game for now";
        player.inGame=false;


        response+="/ALLPlayer "+player.name+" has passed";
        playersLeftInGame-=1;
        response+="Players left in game "+playersLeftInGame+"/"+numberOfPlayers;
        player.hasMoved=true;
        nextMoveingPlayer();
        response += newPlayerMovingString();
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

            for (String s : splited) {
                int index = Integer.parseInt(s);
                if (index >= 0 && index <= 4 && !indexes.contains(index)) {
                    indexes.add(index);
//
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
        logger("Replace handled");
        player.chosenMove = "";
        nextMoveingPlayer();
        response += newPlayerMovingString();
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
                logger("bidding performed");
                player.chosenMove = "";
                nextMoveingPlayer();
                response += newPlayerMovingString();

            }
            else{
                response+="you dont have enough founds for that your founds are "+player.getBalance();
            }
        }
        else{
            response+="you must enter a bid thats higher than the current one";
            player.chosenMove = "";
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
        response+="/ALLPLayer "+player.name+" has called, Picking the winner...";
        ArrayList<FiveCardPokerHand> handsToCompare=new ArrayList<>();
            player.hasMoved=true;
            for(Player playersInGame:Players){
                handsToCompare.add(new FiveCardPokerHand.Builder().addCards(playersInGame.playerDeck).addName(playersInGame.name).build());
                logger(playersInGame.name+"-----added");

            }
            this.gameOver=true;
            Collections.sort(handsToCompare);
            String winnerName=handsToCompare.get(handsToCompare.size()-1).name;
            for(Player p:Players){
                if(p.name.equals(winnerName)){
                    winner=p;
                    winner.balance+=sumToWin;
                }
            }
            return response;

    }
    void setCurrentBid(int bidAmount){
        if(bidAmount>0){
            this.currentBid=bidAmount;
        }


    }
    int getCurrentBid(){
        return this.currentBid;
    }
    static void logger(String message){
        System.out.println(message);
    }
    String newPlayerMovingString(){
        return "Player moving is "+playerMoving.name;
    }
    String playerPrefix(Player player){
        return "Player "+player.name;
    }


}

