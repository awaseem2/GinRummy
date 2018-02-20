package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameEngine {
    private static Player playerOne;
    private static Player playerTwo;
    private int gamesPerCompetition;
    private static ArrayList<Card> deck = new ArrayList<>();
    private static ArrayList<Card> discardPile = new ArrayList<>();

    public GameEngine(PlayerStrategy firstPlayerStrategy, PlayerStrategy secondPlayerStrategy,
                      int gamesPerCompetition) {
        this.gamesPerCompetition = gamesPerCompetition;
        playerOne = new Player(firstPlayerStrategy);
        playerTwo = new Player(secondPlayerStrategy);
    }

    public void runCompetition() {
        int gamesCompleted = 0;

        while(gamesCompleted < gamesPerCompetition) {
            playerOne.setPoints(0);
            playerTwo.setPoints(0);
            runGame();
            gamesCompleted++;

        }
    }

    private static void runGame() {
        resetRound();
        while(!gameFinished(playerOne.getPoints(), playerTwo.getPoints())) {
            //resetRound(); should be called when a player knocks
            pickFirstPlayer();

            if(playerTwo.isFirst()) {
                switchPlayers();
            }

            Card desiredCard;
            ArrayList<Card> newPlayerOneHand = playerOne.getHand();

            if(playerOne.getPlayerStrategy().willTakeTopDiscard(
                    discardPile.get(discardPile.size() - 1))) {
                desiredCard = discardPile.get(discardPile.size() - 1);
            } else {
                desiredCard = deck.get(0);
            }

            newPlayerOneHand.add(desiredCard);
            newPlayerOneHand.remove(playerOne.getPlayerStrategy().drawAndDiscard(desiredCard));
            playerOne.setHand(newPlayerOneHand);

            discardPile.remove(desiredCard);

            if(playerOne.getPlayerStrategy().knock()) {
                handleKnock(playerOne, playerTwo);
            }

            ArrayList<Card> newPlayerTwoHand = playerTwo.getHand();

            if(playerTwo.getPlayerStrategy().willTakeTopDiscard(
                    discardPile.get(discardPile.size() - 1))) {
                desiredCard = discardPile.get(discardPile.size() - 1);
            } else {
                desiredCard = deck.get(0);
            }

            newPlayerTwoHand.add(desiredCard);
            newPlayerTwoHand.remove(playerTwo.getPlayerStrategy().drawAndDiscard(desiredCard));
            playerTwo.setHand(newPlayerTwoHand);

            discardPile.remove(desiredCard);

            if(playerTwo.getPlayerStrategy().knock()) {
                handleKnock(playerTwo, playerOne);
            }

            if(playerTwo.isFirst()) {
                switchPlayers();
            }

        }
    }

    private static boolean gameFinished(int playerOnePoints, int playerTwoPoints) {
        return (playerOnePoints >= 50 || playerTwoPoints >= 50);
    }

    private static void resetRound() {
        shuffleDeck();
        distributeCards();
        initializePlayerStrategies();
    }

    private static void shuffleDeck() {
        deck = new ArrayList<>(Card.getAllCards());
        Collections.shuffle(deck);
    }

    private static void distributeCards() {
        ArrayList<Card> playerOneHand = new ArrayList<>();
        ArrayList<Card> playerTwoHand = new ArrayList<>();
        discardPile.clear();

        for(int i = 0; i < 10; i++) {
            playerOneHand.add(deck.get(i));
            deck.remove(i);
            playerTwoHand.add(deck.get(i));
            deck.remove(i);
        }

        playerOne.setHand(playerOneHand);
        playerTwo.setHand(playerTwoHand);
        discardPile.add(deck.get(0));
        deck.remove(0);
    }

    private static void initializePlayerStrategies() {
        playerOne.initializePlayerStrategy();
        playerTwo.initializePlayerStrategy();
    }

    private static void pickFirstPlayer() {
        int temp = (Math.random() <= 0.5) ? 1 : 2;
        if(temp == 1) {
            playerOne.setIsFirst(true);
            playerTwo.setIsFirst(false);
        } else {
            playerTwo.setIsFirst(true);
            playerOne.setIsFirst(false);
        }
    }

    private static void switchPlayers() {
        Player temp = new Player(playerOne);
        playerOne = playerTwo;
        playerTwo = temp;
    }

    private static void handleKnock(Player knocker, Player opponent) {
        int knockerDeadwoodCount = deadwoodCount(getDeadwoodCards(knocker));
        int opponentDeadwoodCount;
        int differenceInDeadwood;

        if(knockerDeadwoodCount == 0) {
            opponentDeadwoodCount = deadwoodCount(getDeadwoodCards(opponent));
            differenceInDeadwood = Math.abs(knockerDeadwoodCount - opponentDeadwoodCount);
            knocker.setPoints(knocker.getPoints() + 25 + differenceInDeadwood);
            return;
        }

        handleAppends(knocker, opponent);

        if(knockerDeadwoodCount > 0 && knockerDeadwoodCount <= 10) {
            opponentDeadwoodCount = deadwoodCount(getDeadwoodCards(opponent));
            differenceInDeadwood = knockerDeadwoodCount - opponentDeadwoodCount;
            if(differenceInDeadwood >= 0) {
                knocker.setPoints(knocker.getPoints() + differenceInDeadwood);
            } else {
                opponent.setPoints(opponent.getPoints() + Math.abs(differenceInDeadwood));
            }

        }

        resetRound();

    }

    private static void handleAppends(Player knocker, Player opponent) {
        ArrayList<Card> opponentDeadwoodCards = getDeadwoodCards(knocker);
        ArrayList<Card> newHand = new ArrayList<>(opponent.getHand());

        for(Meld meld : knocker.getMelds()) {
            for(Card card : opponentDeadwoodCards) {
                if(meld.canAppendCard(card)) {
                    meld.appendCard(card);
                    newHand.remove(card);
                }
            }
        }

        opponent.setHand(newHand);
    }

    private static ArrayList<Card> getDeadwoodCards(Player player) {
        ArrayList<Card> deadwoodCards = new ArrayList<>(player.getHand());
        List<Meld> meldCards = player.getMelds();
        for(Meld meld : meldCards) {
            for(Card card : meld.getCards()) {
                if(deadwoodCards.contains(card)) {
                    deadwoodCards.remove(card);
                }
            }
        }

        return deadwoodCards;

    }

    private static int deadwoodCount(ArrayList<Card> deadwoodCards) {
        int deadwoodCount = 0;
        for(Card card : deadwoodCards) {
            deadwoodCount += card.getPointValue();
        }

        return deadwoodCount;
    }




}
