package com.example;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class FirstPlayerStrategyTest {

    ArrayList<Card> deck;
    ArrayList<Card> hand;
    Player player;

    @Before
    public void setUp() throws Exception {
        deck  = new ArrayList<>(Card.getAllCards());
        Collections.sort(deck);
        hand = new ArrayList<>(deck.subList(0, 6));
        player = new Player(new FirstPlayerStrategy());
    }


    @Test
    public void willTakeTopDiscard() {
    }

    @Test
    public void drawAndDiscard() {
    }

    @Test
    public void knock() {
    }

    @Test
    public void getMelds() {
        player.getPlayerStrategy().receiveInitialHand(hand);
        assertEquals(deck.subList(0,4), player.getPlayerStrategy().getMelds().get(0).getCards());
    }

    @Test
    public void deadwoodCount() {
    }
}