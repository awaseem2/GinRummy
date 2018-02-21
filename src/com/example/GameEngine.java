package com.example;

import java.util.ArrayList;
import java.util.Collections;

public class GameEngine {
    private Player playerOne;
    private Player playerTwo;
    private int gamesPerCompetition;
    private ArrayList<Card> deck = new ArrayList<>();
    private ArrayList<Card> discardPile = new ArrayList<>();

    public GameEngine(PlayerStrategy firstPlayerStrategy, PlayerStrategy secondPlayerStrategy,
                      int gamesPerCompetition) {
        this.gamesPerCompetition = gamesPerCompetition;
        playerOne = new Player(firstPlayerStrategy);
        playerTwo = new Player(secondPlayerStrategy);
    }

    /** Starts a new game until the competition is over. */
    public void runCompetition() {
        int gamesCompleted = 0;

        while(gamesCompleted < gamesPerCompetition) {
            playerOne.setPoints(0);
            playerTwo.setPoints(0);
            runGame();

            gamesCompleted++;
        }
    }

    /** Starts new rounds until one of the players gets to 50 points,
     *  in which case, the game ends. */
    public void runGame() {
        resetRound();
        GameEngineUtilities.pickFirstPlayer(playerOne, playerTwo);

        if(playerTwo.isFirst()) {
            GameEngineUtilities.switchPlayers(playerOne, playerTwo);
        }

        while(!gameFinished(playerOne.getPoints(), playerTwo.getPoints())) {
            handleTakingCard(playerOne, playerTwo);
            handleTakingCard(playerTwo, playerOne);
        }

        if(playerOne.getPoints() >= 50) {
            playerOne.setWins(playerOne.getWins() + 1);
        } else {
            playerTwo.setWins(playerTwo.getWins() + 1);
        }


        if(playerTwo.isFirst()) {
            GameEngineUtilities.switchPlayers(playerOne, playerTwo);
        }
    }

    private void handleTakingCard(Player player, Player opponent) {
        Card desiredCard;
        Card undesiredCard;
        ArrayList<Card> newPlayerHand = new ArrayList<>(player.getHand());
        boolean tookFromDiscardPile;

        if(player.getPlayerStrategy().willTakeTopDiscard(
                discardPile.get(discardPile.size() - 1))) {
            desiredCard = discardPile.get(discardPile.size() - 1);
            discardPile.remove(desiredCard);
            tookFromDiscardPile = true;
        } else {
            desiredCard = deck.get(0);
            tookFromDiscardPile = false;
        }

        undesiredCard = player.getPlayerStrategy().drawAndDiscard(desiredCard);

        discardPile.add(undesiredCard);
        newPlayerHand.add(desiredCard);
        newPlayerHand.remove(undesiredCard);
        player.setHand(newPlayerHand);
        opponent.getPlayerStrategy().opponentEndTurnFeedback(
                tookFromDiscardPile, discardPile.get(discardPile.size() - 1), undesiredCard);

        if(playerOne.getPlayerStrategy().knock()) {
            handleKnock(playerOne, playerTwo);
        }
    }

    /**
     * Decides whether a game is finished based on if one player or the other
     * has gotten 50 or more points
     *
     * @param playerOnePoints the amount of points the first player has.
     * @param playerTwoPoints the amount of points the second player has.
     * @return a boolean value for whether the current game is over.
     */
    public boolean gameFinished(int playerOnePoints, int playerTwoPoints) {
        return (playerOnePoints >= 50 || playerTwoPoints >= 50);
    }

    /** Initializes a new round by shuffling the deck, distributing a new hand to each player,
     *  and initializing the player strategies based on that hand. */
    public void resetRound() {
        shuffleDeck();
        distributeCards();

        GameEngineUtilities.initializePlayerStrategies(playerOne, playerTwo);
    }

    /** Shuffles the deck. */
    public void shuffleDeck() {
        deck = new ArrayList<>(Card.getAllCards());
        Collections.shuffle(deck);
    }

    /** Gives each player 10 cards each, in an alternating fashion. Then adds the first card on
     *  the top of the deck to the discard pile. */
    public void distributeCards() {
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

    /**
     * Awards points accordingly based on the deadwood count of each player.
     *
     * @param knocker the player who called knock.
     * @param opponent the player who did no call knock.
     */
    public void handleKnock(Player knocker, Player opponent) {
        int knockerDeadwoodCount = CardManager.deadwoodCount(
                CardManager.getDeadwoodCards(knocker));
        int opponentDeadwoodCount;
        int differenceInDeadwood;

        if(knockerDeadwoodCount == 0) {
            opponentDeadwoodCount = CardManager.deadwoodCount(
                    CardManager.getDeadwoodCards(opponent));
            differenceInDeadwood = Math.abs(knockerDeadwoodCount - opponentDeadwoodCount);
            knocker.setPoints(knocker.getPoints() + 25 + differenceInDeadwood);
            return;
        }

        CardManager.handleAppends(knocker, opponent);

        if(knockerDeadwoodCount > 0 && knockerDeadwoodCount <= 10) {
            opponentDeadwoodCount = CardManager.deadwoodCount(
                    CardManager.getDeadwoodCards(opponent));
            differenceInDeadwood = knockerDeadwoodCount - opponentDeadwoodCount;
            if(differenceInDeadwood >= 0) {
                knocker.setPoints(knocker.getPoints() + differenceInDeadwood);
            } else {
                opponent.setPoints(opponent.getPoints() + Math.abs(differenceInDeadwood));
            }

        }

        resetRound();

    }

    /**
     * Provides the amount of games a player has won.
     *
     * @param player the player whose win count we'd like.
     * @return an int of the number of games won.
     */
    public int playerWins(Player player) {
        return player.getWins();
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }
}