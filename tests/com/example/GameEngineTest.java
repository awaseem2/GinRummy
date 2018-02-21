package com.example;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;

public class GameEngineTest {

    GameEngine gameEngine;
    ArrayList<Card> deck;
    ArrayList<Card> goodHand;
    ArrayList<Card> worseHand;

    @Before
    public void setUp() throws Exception {
        gameEngine = new GameEngine(new NormalPlayerStrategy(),
                new NormalPlayerStrategy(), 1);
        deck  = new ArrayList<>(Card.getAllCards());
        Collections.sort(deck);
        goodHand = new ArrayList<>(deck.subList(0, 6)); //4 Aces and 2 Twos
        worseHand = new ArrayList<>();
        worseHand.add(deck.get(47));
        worseHand.add(deck.get(50));
        worseHand.add(deck.get(51));
    }

    @Test
    public void gameFinished() {
        gameEngine.getPlayerOne().setPoints(5);
        gameEngine.getPlayerTwo().setPoints(50);
        assertTrue(gameEngine.gameFinished(gameEngine.getPlayerOne().getPoints(),
                gameEngine.getPlayerTwo().getPoints()));

        gameEngine.getPlayerOne().setPoints(5);
        gameEngine.getPlayerTwo().setPoints(45);
        assertFalse(gameEngine.gameFinished(gameEngine.getPlayerOne().getPoints(),
                gameEngine.getPlayerTwo().getPoints()));
    }

    @Test
    public void distributeCards() {
        gameEngine.shuffleDeck();
        gameEngine.distributeCards();
        gameEngine.initializePlayerStrategies();
        assertTrue(gameEngine.getPlayerOne().getHand().size() == 10);
        assertTrue(gameEngine.getPlayerTwo().getHand().size() == 10);
        assertFalse(gameEngine.getPlayerOne().getHand().equals(gameEngine.getPlayerTwo().getHand()));
    }

    @Test
    public void getDeadwoodCards() {
        gameEngine.getPlayerOne().setHand(worseHand);
        assertEquals(worseHand, gameEngine.getDeadwoodCards(gameEngine.getPlayerOne()));
    }

    @Test
    public void deadwoodCount() {
        gameEngine.getPlayerOne().setHand(worseHand);
        assertEquals(gameEngine.deadwoodCount(gameEngine.getDeadwoodCards(
                gameEngine.getPlayerOne())), 30);
    }

    @Test
    public void playerWins() {
        gameEngine.getPlayerOne().setPoints(50);
        gameEngine.runGame();
        assertEquals(1, gameEngine.playerWins(gameEngine.getPlayerOne()));
    }

    @Test
    public void getPlayerOne() {
        NormalPlayerStrategy testStrategyOne = new NormalPlayerStrategy();
        NormalPlayerStrategy testStrategyTwo = new NormalPlayerStrategy();
        GameEngine playerGetTest = new GameEngine(testStrategyOne,
                testStrategyTwo, 1);
        assertEquals(playerGetTest.getPlayerOne().getPlayerStrategy(), testStrategyOne);
    }

    @Test
    public void getPlayerTwo() {
        NormalPlayerStrategy testStrategyOne = new NormalPlayerStrategy();
        NormalPlayerStrategy testStrategyTwo = new NormalPlayerStrategy();
        GameEngine playerGetTest = new GameEngine(testStrategyOne,
                testStrategyTwo, 1);
        assertEquals(playerGetTest.getPlayerTwo().getPlayerStrategy(), testStrategyTwo);
    }
}