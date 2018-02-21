package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
     * in which case, the game ends. */
    public void runGame() {
        resetRound();
        pickFirstPlayer();

        if(playerTwo.isFirst()) {
            switchPlayers();
        }

        while(!gameFinished(playerOne.getPoints(), playerTwo.getPoints())) {
            Card desiredCard;
            Card undesiredCard;
            ArrayList<Card> newPlayerOneHand = new ArrayList<>(playerOne.getHand());
            boolean tookFromDiscardPile;

            if(playerOne.getPlayerStrategy().willTakeTopDiscard(
                    discardPile.get(discardPile.size() - 1))) {
                desiredCard = discardPile.get(discardPile.size() - 1);
                discardPile.remove(desiredCard);
                tookFromDiscardPile = true;
            } else {
                desiredCard = deck.get(0);
                tookFromDiscardPile = false;
            }

            undesiredCard = playerOne.getPlayerStrategy().drawAndDiscard(desiredCard);

            discardPile.add(undesiredCard);
            newPlayerOneHand.add(desiredCard);
            newPlayerOneHand.remove(undesiredCard);
            playerOne.setHand(newPlayerOneHand);
            playerTwo.getPlayerStrategy().opponentEndTurnFeedback(
                    tookFromDiscardPile, discardPile.get(discardPile.size() - 1), undesiredCard);

            if(playerOne.getPlayerStrategy().knock()) {
                handleKnock(playerOne, playerTwo);
            }

            ArrayList<Card> newPlayerTwoHand = new ArrayList<>(playerTwo.getHand());

            if(playerTwo.getPlayerStrategy().willTakeTopDiscard(
                    discardPile.get(discardPile.size() - 1))) {
                desiredCard = discardPile.get(discardPile.size() - 1);
                discardPile.remove(desiredCard);
                tookFromDiscardPile = true;
            } else {
                desiredCard = deck.get(0);
                tookFromDiscardPile = false;
            }

            undesiredCard = playerTwo.getPlayerStrategy().drawAndDiscard(desiredCard);

            discardPile.add(undesiredCard);
            newPlayerTwoHand.add(desiredCard);
            newPlayerTwoHand.remove(undesiredCard);
            playerTwo.setHand(newPlayerTwoHand);
            playerOne.getPlayerStrategy().opponentEndTurnFeedback(
                    tookFromDiscardPile, discardPile.get(discardPile.size() - 1), undesiredCard);

            if(playerTwo.getPlayerStrategy().knock()) {
                handleKnock(playerTwo, playerOne);
            }

        }

        if(playerOne.getPoints() >= 50) {
            playerOne.setWins(playerOne.getWins() + 1);
        } else {
            playerTwo.setWins(playerTwo.getWins() + 1);
        }


        if(playerTwo.isFirst()) {
            switchPlayers();
        }
    }

    /** Decides whether a game is finished based on if one player or the other
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
     * and initializing the player strategies based on that hand. */
    public void resetRound() {
        shuffleDeck();
        distributeCards();
        initializePlayerStrategies();
    }

    /** Shuffles the deck. */
    public void shuffleDeck() {
        deck = new ArrayList<>(Card.getAllCards());
        Collections.shuffle(deck);
    }

    /** Gives each player 10 cards each, in an alternating fashion. Then adds the first card on
     * the top of the deck to the discard pile. */
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

    /** Initializes each player's hand in player strategy. */
    public void initializePlayerStrategies() {
        playerOne.initializePlayerStrategy();
        playerTwo.initializePlayerStrategy();
    }

    /** Chooses the first player randomly by choosing between 1 and 2. If it's 1, player one goes
     * first. If it's 2, player two goes first. */
    public void pickFirstPlayer() {
        int temp = (Math.random() <= 0.5) ? 1 : 2;
        if(temp == 1) {
            playerOne.setIsFirst(true);
            playerTwo.setIsFirst(false);
        } else {
            playerTwo.setIsFirst(true);
            playerOne.setIsFirst(false);
        }
    }

    /** If player two is going first, the player objects are switched to make runGame() always
     * use 'PlayerOne' for the player who goes first. */
    public void switchPlayers() {
        Player temp = new Player(playerOne);
        playerOne = playerTwo;
        playerTwo = temp;
    }

    /** Awards points accordingly based on the deadwood count of each player.
     *
     * @param knocker the player who called knock.
     * @param opponent the player who did no call knock.
     */
    public void handleKnock(Player knocker, Player opponent) {
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

    /** Adds the opponent's deadwood cards to the knocker's melds where applicable.
     *
     * @param knocker the player who called knock.
     * @param opponent the player who did not call knock.
     */
    public void handleAppends(Player knocker, Player opponent) {
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

    /** Returns all the cards that are not in melds.
     *
     * @param player the player whose deadwood cards we'd like to receive.
     * @return an ArrayList of Cards which contains all of the player's deadwood cards.
     */
    public ArrayList<Card> getDeadwoodCards(Player player) {
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

    /** The sum of the values of each deadwood card in a player' hand.
     *
     * @param deadwoodCards the cards that are not in any melds.
     * @return an int of the value of all the deadwood cards.
     */
    public int deadwoodCount(ArrayList<Card> deadwoodCards) {
        int deadwoodCount = 0;
        for(Card card : deadwoodCards) {
            deadwoodCount += card.getPointValue();
        }

        return deadwoodCount;
    }

    /** Provides the amount of games a player has won.
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
