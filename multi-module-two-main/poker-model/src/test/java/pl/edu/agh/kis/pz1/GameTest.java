package pl.edu.agh.kis.pz1;

import junit.framework.TestCase;

public class GameTest extends TestCase {

    public void testAddPlayer() {
        Game game=new Game(2);
        game.addPlayer(new Player());

        game.addPlayer(new Player());
        assertEquals(game.numberOfPlayers,2);
        game.addPlayer(new Player());
        assertEquals(game.numberOfPlayers,2);
    }

    public void testGivePlayersCards() {
        Game game=new Game(1);
        Player player1=new Player();
        game.addPlayer(player1);
        game.givePlayersCards();
        assertEquals(5,player1.playerDeck.size());
    }



    public void testGetAnteFromPlayers() {
        Game game=new Game(2);
        Player player1=new Player();
        Player player2=new Player();
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.getAnteFromPlayers();
        assertEquals(200,player1.getBalance());
        assertEquals(200,player2.getBalance());

    }



    public void testGetPlayerStatus() {
        Game game=new Game(1);
        Player player1=new Player();
        game.addPlayer(player1);
        game.initializeParameters();
        String response=game.getPlayerStatus(player1);
        assertEquals(response,"Player "+player1.name+ " your founds are "+ player1.balance+"\n THE PRIZE TO WIN IS "+ game.sumToWin+"\n"+"currently no player is moving");
        game.nextMoveingPlayer();
        response=game.getPlayerStatus(player1);
        assertEquals(response,"Player "+player1.name+ " your founds are "+ player1.balance+"\n THE PRIZE TO WIN IS "+ game.sumToWin+"\n"+player1.getDeck()+"\nPlayer moving is "+game.playerMoving.name);
    }

    public void testInitializeParameters() {
        Game game=new Game(2);
        game.initializeParameters();
        assertEquals(false,game.gameOver);
        assertEquals(0,game.sumToWin);
    }


    public void testStartGame() {
        Game game=new Game(2);
        Player player1=new Player();
        Player player2=new Player();
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.startGame();
        assertEquals(game.currentBid,0);
        assertEquals(game.sumToWin,200);
        assertEquals(player1,game.playerMoving);
        assertEquals(player1.playerDeck.size(),5);
        assertEquals(player2.playerDeck.size(),5);
    }

    public void testAvilableMovesHelper() {
        Game game=new Game(2);
        String response=game.avilableMovesHelper(false);
        assertEquals("Avilable commands are: BID, REPLACE, PASS, CALL, STATUS\n" +
                " You can type HELP COMMAND_NAME to get help regarding the command use",response);
        response=game.avilableMovesHelper(true);
        assertEquals(response,"This is the replacing round, you can replace up to 5 cards using replace and passing the indexes, or you can type PASS to not replace any");

    }

    public void testHandleHelp() {
        Game game=new Game(2);
        String response= game.handleHelp("BID");
        assertEquals(response,"BID number -enter a bid you would like to make, remember it must be higher than the last bid, and you must have the founds to cover it\n\n");
        response=game.handleHelp("REPLACE");
        assertEquals(response,"REPLACE number [number] [number] [number] [number] -replace replaces your cards, you can enter from 1 to 5 card indexes, they correspond with the indexes youre given\n\n");
        response=game.handleHelp("PASS");
        assertEquals(response,"PASS -if you choose to pass in the round this command will make you do so\n\n");
        response=game.handleHelp("CALL");
        assertEquals(response,"CALL -you call to check the game, after it the winner will be decided, use it wisely\n\n");
        response=game.handleHelp("STATUS");
        assertEquals(response,"STATUS -returns a status of the current play including your tokens, the prize to win and your deck of cards\n\n");
    }

    public void testInterpretate() {
        Game game=new Game(2);
        Player player1=new Player();
        Player player2=new Player();
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeParameters();
        game.nextMoveingPlayer();
        String response=game.interpretate("BID 200",player1,false);
        assertEquals(response,"Its not your turn its Anonymus's turn");
        response=game.interpretate("BID 200",player2,true);
        assertEquals(response,"Its the replacing round you can only replace");
        response=game.interpretate("BID 200",player2,false);
        assertEquals(response,"Are you sure you want to raise your bid to 200");
        response=game.interpretate("no",player2,false);
        assertEquals(response,"ok choose your move again");
        response=game.interpretate("BID",player2,false);
        assertEquals(response,"you must enter a number");
        response=game.interpretate("ASDw wAd",player2,false);
        assertEquals(response,"Invalid command type HELP to get a quick manual help");
        response=game.interpretate("PASS",player2,false);
        assertEquals("Are you sure you want to pass?, there is no turning back",response);
        response=game.interpretate("yes",player2,false);
        assertEquals("Youre out of the game for now/ALLPlayer Anonymus has passedPlayers left in game 1/2Player moving is Anonymus",response);
        response=game.interpretate("CALL",player1,false);
        assertEquals("Are you sure you want to call? type yes if so or anything to exit",response);




    }

    public void testHandlePass() {
        Game game=new Game(2);
        Player player1=new Player();
        Player player2=new Player();
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeParameters();
        game.nextMoveingPlayer();
        String response=game.handlePass(player2);
        assertEquals(response,"Are you sure you want to pass?, there is no turning back");
        game.interpretate("no",player2,false);
        response=game.interpretate("PASS",player2,true);
        assertEquals("Are you sure you dont want to replace any cards, you cannot replace them later",response);
    }

    public void testHandleCall() {
        Game game=new Game(2);
        Player player1=new Player();
        Player player2=new Player();
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeParameters();
        game.nextMoveingPlayer();
        String response=game.handleCall(player2);
        assertEquals(response,"Are you sure you want to call? type yes if so or anything to exit");
    }


    public void testHandleReplace() {
        Game game=new Game(2);
        Player player1=new Player();
        Player player2=new Player();
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.startGame();
        String response=game.interpretate("REPLACE 0",player1,false);
        assertEquals("you cannot replace in this round,"+game.avilableMovesHelper(false),response);
        response=game.interpretate("REPLACE 0",player1,true);
        assertEquals("Are you sure you want to replace these cards:"+player1.playerDeck.get(0)+"; ",response);
        response=game.interpretate("no",player1,false);
        assertEquals("ok choose your move again",response);
        response=game.interpretate("REPLACE 0 1",player1,true);
        assertEquals("Are you sure you want to replace these cards:"+player1.playerDeck.get(0)+"; " +player1.playerDeck.get(1)+"; ",response);
        Card card1=player1.playerDeck.get(0);
        Card card2=player1.playerDeck.get(1);
        response=game.interpretate("yes",player1,true);
        Card card3=player1.playerDeck.get(0);
        Card card4=player1.playerDeck.get(1);
        assertEquals("Replaced "+ card1+ " for "+card3 +" at index 0 - Replaced "+card2+" for "+ card4+" at index 1 - /ALL Player Anonymus replaced 2 cards\n" +
                "Player moving is Anonymus",response);
        response=game.interpretate("REPLACE S WE",player2,true);
        assertEquals("You have entered invalid text, enter SPACE SEPERATED NUMBERS",response);
        response=game.interpretate("PASS",player2,true);
        assertEquals("Are you sure you dont want to replace any cards, you cannot replace them later",response);
        response=game.interpretate("yes",player2,true);
        assertEquals("Player Anonymus didin't replace any cards\n" +
                "/ALLPlayerAnonymus didnt replace any cards\n" +
                "Player moving is Anonymus",response);

    }



    public void testHandlePlayerBid() {
        Game game=new Game(2);
        Player player1=new Player();
        Player player2=new Player();
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.initializeParameters();
        game.nextMoveingPlayer();
        String response=game.interpretate("BID 200",player1,false);
        assertEquals(response,"Its not your turn its Anonymus's turn");
        response=game.interpretate("BID 200",player2,true);
        assertEquals(response,"Its the replacing round you can only replace");
        response=game.interpretate("BID 200",player2,false);
        assertEquals(response,"Are you sure you want to raise your bid to 200");
        response=game.interpretate("yes",player2,false);
        assertEquals(player2.getBalance(),100);
        assertEquals(game.sumToWin,200);
        response=game.interpretate("BID @#$",player1,false);
        assertEquals(response,"you have entered a invalid text");
        response=game.interpretate("BID 10",player1,false);
        response=game.interpretate("yes",player1,false);
        assertEquals(response,"you must enter a bid thats higher than the current one");
        response=game.interpretate("BID 1000",player1,false);
        assertEquals(response,"Are you sure you want to raise your bid to 1000");
        response=game.interpretate("yes",player1,false);
        assertEquals(response,"you dont have enough founds for that your founds are 300");
    }

    public void testNextMoveingPlayer() {
        Game game1=new Game(2);
        Player player1=new Player();
        Player player2=new Player();
        game1.addPlayer(player1);
        game1.addPlayer(player2);
        game1.initializeParameters();
        game1.nextMoveingPlayer();
        assertEquals(game1.playerMoving,player2);
        Game game2=new Game(3);
        Player player21=new Player();
        Player player22=new Player();
        Player player23=new Player();
        game2.addPlayer(player21);
        game2.addPlayer(player22);
        game2.addPlayer(player23);
        game2.initializeParameters();
        game2.nextMoveingPlayer();
        assertEquals(player22,game2.playerMoving);
        player23.inGame=false;
        game2.nextMoveingPlayer();
        assertEquals(player21,game2.playerMoving);


    }

    public void testPlayerMakeCall() {
        Game game=new Game(2);
        Player player1=new Player();
        Player player2=new Player();
        game.addPlayer(player1);
        game.addPlayer(player2);
        game.startGame();
        String response=game.interpretate("CALL",player2,true);
        assertEquals("Its not your turn its Anonymus's turn",response);
        response=game.interpretate("CALL",player1,false);
        assertEquals("Are you sure you want to call? type yes if so or anything to exit",response);
        response= game.interpretate("yes",player1,false);
        assertEquals("You have decided to call/ALLPLayer Anonymus has called, Picking the winner...",response);
        assertEquals(true,game.gameOver);
        assertNotNull(game.winner);
    }
    public void testGetCurrentBid(){
        Game game=new Game(2);
        game.setCurrentBid(200);
        assertEquals(200,game.getCurrentBid());
    }
}