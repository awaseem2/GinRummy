package com.example;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class StubbornPlayerStrategyTest {

    ArrayList<Card> deck;
    ArrayList<Card> greatHand;
    ArrayList<Card> goodHand;
    ArrayList<Card> badHand;
    ArrayList<Card> worseHand;
    Player player;

    @Before
    public void setUp() throws Exception {
        deck  = new ArrayList<>(Card.getAllCards());
        Collections.sort(deck);
        greatHand = new ArrayList<>(deck.subList(0, 7)); //4 Aces and 3 Twos
        goodHand = new ArrayList<>(deck.subList(1, 6)); //3 Aces and 2 Twos
        badHand = new ArrayList<>(deck.subList(22, 25)); //2 Sixes and 1 Seven
        worseHand = new ArrayList<>();
        worseHand.add(deck.get(47));
        worseHand.add(deck.get(50));
        worseHand.add(deck.get(51));
        player = new Player(new StubbornPlayerStrategy());
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
        player.getPlayerStrategy().receiveInitialHand(badHand);
        assertEquals(badHand.get(2), player.getPlayerStrategy().drawAndDiscard(deck.get(0)));
    }

    @Test
    public void knock() {
        player.getPlayerStrategy().receiveInitialHand(greatHand);
        player.getPlayerStrategy().getMelds();
        assertTrue(player.getPlayerStrategy().knock());

    }

    @Test
    public void getMelds() {
        player.getPlayerStrategy().receiveInitialHand(greatHand);
        assertEquals(deck.subList(0,4), player.getPlayerStrategy().getMelds().get(0).getCards());
    }
}