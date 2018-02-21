package com.example;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class NormalPlayerStrategyTest {

    ArrayList<Card> deck;
    ArrayList<Card> goodHand;
    ArrayList<Card> badHand;
    ArrayList<Card> worseHand;
    Player player;

    @Before
    public void setUp() throws Exception {
        deck  = new ArrayList<>(Card.getAllCards());
        Collections.sort(deck);
        goodHand = new ArrayList<>(deck.subList(0, 6)); //4 Aces and 2 Twos
        badHand = new ArrayList<>(deck.subList(22, 25)); //2 Sixes and 1 Seven
        worseHand = new ArrayList<>();
        worseHand.add(deck.get(47));
        worseHand.add(deck.get(50));
        worseHand.add(deck.get(51));
        player = new Player(new NormalPlayerStrategy());
    }


    @Test
    public void willTakeTopDiscard() {
        Card topOfDiscardPile = deck.get(0); //Ace
        player.getPlayerStrategy().receiveInitialHand(badHand);
        assertTrue(player.getPlayerStrategy().willTakeTopDiscard(topOfDiscardPile));

        topOfDiscardPile = deck.get(40); //Jack
        player.getPlayerStrategy().receiveInitialHand(badHand);
        assertFalse(player.getPlayerStrategy().willTakeTopDiscard(topOfDiscardPile));
    }

    @Test
    public void drawAndDiscard() {
        player.getPlayerStrategy().receiveInitialHand(badHand);
        assertEquals(badHand.get(2), player.getPlayerStrategy().drawAndDiscard(deck.get(0)));
    }

    @Test
    public void knock() {
        player.getPlayerStrategy().receiveInitialHand(worseHand);
        assertFalse(player.getPlayerStrategy().knock());
    }

    @Test
    public void getMelds() {
        player.getPlayerStrategy().receiveInitialHand(goodHand);
        assertEquals(deck.subList(0,4), player.getPlayerStrategy().getMelds().get(0).getCards());
    }

}