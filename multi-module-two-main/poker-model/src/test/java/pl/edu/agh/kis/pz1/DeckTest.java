package pl.edu.agh.kis.pz1;

import junit.framework.TestCase;

public class DeckTest extends TestCase {

    public void testFillDeck() {
        Deck deck=new Deck();
        assertEquals(0,deck.getSize());
        deck.fillDeck();
        assertEquals(deck.getSize(),52);
    }

    public void testShuffle() {
        Deck sortedDeck=new Deck();
        sortedDeck.fillDeck();
        Deck unsortedDeck=new Deck();
        unsortedDeck.fillDeck();
        unsortedDeck.shuffle();
        assertNotSame(sortedDeck.drawCard(52),unsortedDeck.drawCard(52));
    }

    public void testSort() {
        Deck sortedDeck=new Deck();
        sortedDeck.fillDeck();
        assertEquals(new Card(Card.Rank.TWO, Card.Suit.KIER),sortedDeck.draw());
    }

    public void testDraw() {
        Deck deck =new Deck();
        deck.fillDeck();
        assertEquals(new Card(Card.Rank.TWO, Card.Suit.KIER),deck.draw());
    }

    public void testDrawCard() {
        Deck deck = new Deck();
        deck.fillDeck();
        assertEquals(new Card(Card.Rank.TWO, Card.Suit.KIER),deck.drawCard(5).get(0));
    }

    public void testGetSize() {
        Deck deck=new Deck();
        assertEquals(0,deck.getSize());
        deck.fillDeck();
        assertEquals(52,deck.getSize());
        deck.drawCard(16);
        assertEquals(36,deck.getSize());
    }
}