package pl.edu.agh.kis.pz1;

import junit.framework.TestCase;

public class CardTest extends TestCase {



    public void testGetSuit() {
        assertEquals(10,10);
    }

    public void testTestGetRank() {
        assertEquals(new Card(Card.Rank.ACE, Card.Suit.KARO).getRank(),Card.Rank.ACE
        );
    }

    public void testTestGetSuit() {
        assertEquals(new Card(Card.Rank.FIVE, Card.Suit.KIER).getSuit(),Card.Suit.KIER);
    }

    public void testSuiToString() {
        assertEquals(new Card(Card.Rank.ACE, Card.Suit.KIER).suitToString(),"\u2665");
        assertEquals(new Card(Card.Rank.TWO, Card.Suit.PIK).suitToString(),"\u2660");
        assertEquals(new Card(Card.Rank.JACK, Card.Suit.KARO).suitToString(),"\u2666");
        assertEquals(new Card(Card.Rank.SEVEN, Card.Suit.TREFL).suitToString(),"\u2663");
    }
    public void testToString(){
        Card c=new Card(Card.Rank.ACE, Card.Suit.KIER);
        assertEquals(c.toString(),"ACE \u2665");
    }



    public void testTestEquals() {
        Card c1=new Card(Card.Rank.FIVE, Card.Suit.KARO);
        Card c2=new Card(Card.Rank.JACK, Card.Suit.PIK);
        Card c3=new Card(Card.Rank.TWO, Card.Suit.TREFL);
        Card c4=new Card(Card.Rank.JACK, Card.Suit.KIER);
        String a="Adas";
        assertTrue(c1.equals(c1));
        assertFalse(c1.equals(c2));
        assertFalse(c1.equals(a));
        assertFalse(c2.equals(null));
        assertFalse(c2.equals(c3));
        assertFalse(c2.equals(c4));
        assertFalse(c1.equals(c3));
        assertFalse(c1.equals(c4));
        assertFalse(c3.equals(c4));
    }


}