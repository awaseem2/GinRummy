package com.example;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class LovesRanksPlayerStrategyTest {

    ArrayList<Card> deck;
    ArrayList<Card> goodHand;
    ArrayList<Card> badHand;
    ArrayList<Card> worseHand;
    Player player;

    @Before
    public void setUp() throws Exception {
        deck  = new ArrayList<>(Card.getAllCards());
        Collections.sort(deck);
        goodHand = new ArrayList<>(deck.subList(1, 6)); //4 Aces and 2 Twos
        badHand = new ArrayList<>(deck.subList(22, 25)); //2 Sixes and 1 Seven
        worseHand = new ArrayList<>();
        worseHand.add(deck.get(47)); //queen
        worseHand.add(deck.get(50)); //king
        worseHand.add(deck.get(51)); //king
        player = new Player(new LovesRanksPlayerStrategy());
    }

    @Test
    public void willTakeTopDiscard() {
        player.getPlayerStrategy().receiveInitialHand(goodHand);
        player.getPlayerStrategy().getMelds();
        Card goodCard = deck.get(0); //ace
        assertTrue(player.getPlayerStrategy().willTakeTopDiscard(goodCard));
    }

    @Test
    public void drawAndDiscard() {
        player.getPlayerStrategy().receiveInitialHand(worseHand);
        Card newCard = deck.get(50); //king
        assertEquals(player.getPlayerStrategy().drawAndDiscard(newCard), worseHand.get(0));
    }

    @Test
    public void knock() {
        player.getPlayerStrategy().receiveInitialHand(worseHand);
        assertFalse(player.getPlayerStrategy().knock());
    }

    @Test
    public void getMelds() {
        player.getPlayerStrategy().receiveInitialHand(worseHand);
        ArrayList<Card> emptyList = new ArrayList<>();
        assertEquals(emptyList, player.getPlayerStrategy().getMelds());
    }
}