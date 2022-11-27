package pl.edu.agh.kis.pz1;

import junit.framework.TestCase;

public class DeckTest extends TestCase {


    public void testDraw(){
        Deck deck=new Deck();
        deck.fillDeck();
        assertEquals(deck.drawCard(1).get(0),new Card(Card.Rank.TWO, Card.Suit.KIER));
    }



}