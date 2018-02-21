package com.example;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class CardManagerTest {

    GameEngine gameEngine;
    ArrayList<Card> deck;
    ArrayList<Card> greatHand;
    ArrayList<Card> goodHand;
    ArrayList<Card> worseHand;

    @Before
    public void setUp() throws Exception {
        gameEngine = new GameEngine(new NormalPlayerStrategy(),
                new NormalPlayerStrategy(), 1);
        deck  = new ArrayList<>(Card.getAllCards());
        Collections.sort(deck);
        greatHand = new ArrayList<>(deck.subList(0, 7)); //4 Aces and 3 Twos
        goodHand = new ArrayList<>(deck.subList(1, 6)); //3 Aces and 2 Twos
        worseHand = new ArrayList<>();
        worseHand.add(deck.get(0));
        worseHand.add(deck.get(47)); //queen
        worseHand.add(deck.get(50)); //king
        worseHand.add(deck.get(51)); //king
    }

    @Test
    public void handleAppends() {
        gameEngine.getPlayerOne().setHand(goodHand);
        gameEngine.getPlayerTwo().setHand(worseHand);
        CardManager.handleAppends(gameEngine.getPlayerOne(), gameEngine.getPlayerTwo());
        assertTrue(gameEngine.getPlayerTwo().getHand().size() == 3);
    }

    @Test
    public void getDeadwoodCards() {
        gameEngine.getPlayerOne().setHand(worseHand);
        assertEquals(worseHand, CardManager.getDeadwoodCards(gameEngine.getPlayerOne()));
    }

    @Test
    public void deadwoodCount() {
        gameEngine.getPlayerOne().setHand(worseHand);
        assertEquals(CardManager.deadwoodCount(CardManager.getDeadwoodCards(
                gameEngine.getPlayerOne())), 30);
    }
}