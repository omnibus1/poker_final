package pl.edu.agh.kis.pz1;

import junit.framework.TestCase;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class PlayerTest extends TestCase {

    public void testPrintDeck() {
    }

    public void testGetDeck() {

    }

    public void testAddBalance() {
        Player player=new Player();
        player.addBalance(200);
        assertEquals(500,player.getBalance());
        player.addBalance(-200);
        assertEquals(500,player.getBalance());
    }

    public void testSetBalance() {
        Player player=new Player();
        player.setBalance(500);
        assertEquals(500,player.getBalance());
        player.setBalance(-100);
        assertEquals(500,player.getBalance());
    }

    public void testPrintCards() {
        Player player = new Player();
        Deck deck=new Deck();
        deck.fillDeck();
        player.drawFromDeck(deck);

    }

    public void testDrawFromDeck() {
        Player player=new Player("Adam",15);
        Deck deck=new Deck();
        deck.fillDeck();
        player.drawFromDeck(deck);
        assertEquals(5,player.playerDeck.size());

    }
    public void testGetChosenMove(){
        Player player=new Player();
        player.setChosenMove("WWW");
        assertEquals("WWW",player.getChosenMove());
    }
    public void testSetChosenMove() throws IOException {

        Player player=new Player();
        player.setChosenMove("WEQ");
        player.setChosenMove("PPPPP");
        assertEquals("PPPPP",player.getChosenMove());

    }
}