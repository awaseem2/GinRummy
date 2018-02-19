package com.example;

import java.util.ArrayList;
import java.util.Collections;

public class GameEngine {

    private static PlayerStrategy firstPlayerStrategy;
    private static PlayerStrategy secondPlayerStrategy;

    private int gamesPerCompetition;
    private int playerOneWins;
    private int playerTwoWins;

    private static ArrayList<Card> deck = new ArrayList<>();
    private static ArrayList<Card> playerOneHand = new ArrayList<>();
    private static ArrayList<Card> playerTwoHand = new ArrayList<>();
    private static ArrayList<Card> discardPile = new ArrayList<>();

    public GameEngine(PlayerStrategy firstPlayerStrategy, PlayerStrategy secondPlayerStrategy,
                      int gamesPerCompetition) {
        this.firstPlayerStrategy = firstPlayerStrategy;
        this.secondPlayerStrategy = secondPlayerStrategy;
        this.gamesPerCompetition = gamesPerCompetition;
    }

    public void runCompetition() {
        int gamesCompleted = 0;

        while(gamesCompleted < gamesPerCompetition) {
            runGame();
            gamesCompleted++;

        }
    }

    private static void runGame() {
        int playerOnePoints = 0;
        int playerTwoPoints = 0;


        while(!roundFinished(playerOnePoints, playerTwoPoints)) {
            resetRound(); //should not be here
            //!! need to set first player randomly

            Card desiredCard;

            if(firstPlayerStrategy.willTakeTopDiscard(discardPile.get(discardPile.size() - 1))) {
                desiredCard = discardPile.get(discardPile.size() - 1);
                playerOneHand.add(desiredCard);
                discardPile.remove(desiredCard);
                playerOneHand.remove(firstPlayerStrategy.drawAndDiscard(desiredCard));
            } else {
                desiredCard = deck.get(0);
                playerOneHand.add(desiredCard);
                discardPile.remove(desiredCard);
                playerOneHand.remove(firstPlayerStrategy.drawAndDiscard(desiredCard));
            }



            if(secondPlayerStrategy.willTakeTopDiscard(discardPile.get(discardPile.size() - 1))) {
                desiredCard = discardPile.get(discardPile.size() - 1);
                playerTwoHand.add(desiredCard);
                discardPile.remove(desiredCard);
                playerTwoHand.remove(firstPlayerStrategy.drawAndDiscard(desiredCard));
            } else {
                desiredCard = deck.get(0);
                playerTwoHand.add(desiredCard);
                discardPile.remove(desiredCard);
                playerTwoHand.remove(firstPlayerStrategy.drawAndDiscard(desiredCard));
            }

        }
    }

    //literally doesn't make sense how is this incrementing the points of each player??
    //also i'm pretty sure this is creating a new playerstrategy instance?? idk
    private static void handleKnock(PlayerStrategy knocker, int knockerPoints,
                                    PlayerStrategy opponent, int opponentPoints) {
        int knockerDeadwoodCount = 10 - knocker.getMelds().size();
        int opponentDeadwoodCount = 10 - opponent.getMelds().size();
        int knckerDeadwoodPoints;
        int opponentDeadwoodPoints;
        if(knockerDeadwoodCount == 0) {
           // opponentPoints =
        }

    }

    private static void resetRound() {
        shuffleDeck();
        distributeCards();
        initializePlayerStrategies();
    }

    public int getPlayerOneWins() {
        return playerOneWins;
    }

    public int getPlayerTwoWins() {
        return playerTwoWins;
    }

    private static boolean roundFinished(int playerOnePoints, int playerTwoPoints) {
        return (playerOnePoints >= 50 || playerTwoPoints >= 50);
    }

    private static void shuffleDeck() {
        deck = new ArrayList<>(Card.getAllCards());
        Collections.shuffle(deck);
    }

    private static void distributeCards() {
        for(int i = 0; i < 10; i++) {
            playerOneHand.add(deck.get(i));
            deck.remove(i);
            playerTwoHand.add(deck.get(i));
            deck.remove(i);
        }

        discardPile.add(deck.get(0));
        deck.remove(0);
    }

    private static void initializePlayerStrategies() {
        firstPlayerStrategy.receiveInitialHand(playerOneHand);
        secondPlayerStrategy.receiveInitialHand(playerTwoHand);
    }


}
