package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LovesRanksPlayerStrategy implements PlayerStrategy {

    private List<Card> hand = new ArrayList<>();
    private List<Meld> melds = new ArrayList<>();

    /**
     * Called by the game engine for each player at the beginning of each round to receive and
     * process their initial hand dealt.
     *
     * @param hand The initial hand dealt to the player
     */
    @Override
    public void receiveInitialHand(List<Card> hand) {
        this.hand = hand;
    }

    /**
     * Called by the game engine to prompt the player on whether they want to take the top card
     * from the discard pile or from the deck.
     *
     * @param card The card on the top of the discard pile
     * @return whether the user takes the card on the discard pile
     */
    @Override
    public boolean willTakeTopDiscard(Card card) {
        for(Meld meld : melds) {
            if(meld.canAppendCard(card)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Called by the game engine to prompt the player to take their turn given a
     * dealt card (and returning their card they've chosen to discard).
     *
     * @param drawnCard The card the player was dealt
     * @return The card the player has chosen to discard
     */
    @Override
    public Card drawAndDiscard(Card drawnCard) {
        //If a card has no other card of the same rank, it is removed.
        Collections.sort(hand);
        ArrayList<Card> handCopy = new ArrayList<>(hand);
        handCopy.add(drawnCard);
        for(int i = 0; i < handCopy.size() - 1; i++) {
            if(!(handCopy.get(i).getRank().equals(handCopy.get(i + 1).getSuit()))) {
                return handCopy.get(i);
            }
        }

        return getHighestDeadwood(getDeadwoodCards());
    }

    /**
     * Called by the game engine to prompt the player is whether they would like to
     * knock.
     *
     * @return True if the player has decided to knock
     */
    @Override
    public boolean knock() {
        if(deadwoodCount(getDeadwoodCards()) <= 10) {
            return true;
        }
        return false;
    }

    /**
     * Called by the game engine when the opponent has finished their turn to provide the player
     * information on what the opponent just did in their turn.
     *
     * @param drewDiscard        Whether the opponent took from the discard
     * @param previousDiscardTop What the opponent could have drawn from the discard if they chose to
     * @param opponentDiscarded  The card that the opponent discarded
     */
    @Override
    public void opponentEndTurnFeedback(boolean drewDiscard, Card previousDiscardTop, Card opponentDiscarded) {

    }

    /**
     * Called by the game engine when the round has ended to provide this player strategy
     * information about their opponent's hand and selection of Melds at the end of the round.
     *
     * @param opponentHand  The opponent's hand at the end of the round
     * @param opponentMelds The opponent's Melds at the end of the round
     */
    @Override
    public void opponentEndRoundFeedback(List<Card> opponentHand, List<Meld> opponentMelds) {

    }

    /** Called by the game engine to allow access the player's current list of Melds.
     *
     * @return The player's list of melds.
     */
    @Override
    public List<Meld> getMelds() {
        findMelds();
        return melds;
    }

    /** Finds all the melds of sets and runs in the player's hand
     *  and appends them to the melds list. */
    private void findMelds() {
        ArrayList<Card> uncheckedHand = new ArrayList<>(hand);
        Collections.sort(uncheckedHand);
        melds.addAll(findSets(uncheckedHand));
        uncheckedHand = removeMelds(uncheckedHand);

        List<Card> diamonds = separateBySuit(uncheckedHand, "diamonds");
        melds.addAll(findRuns(diamonds));
        uncheckedHand = removeMelds(uncheckedHand);

        List<Card> hearts = separateBySuit(uncheckedHand, "hearts");
        melds.addAll(findRuns(hearts));
        uncheckedHand = removeMelds(uncheckedHand);

        List<Card> spades = separateBySuit(uncheckedHand, "spades");
        melds.addAll(findRuns(spades));
        uncheckedHand = removeMelds(uncheckedHand);

        List<Card> clubs = separateBySuit(uncheckedHand, "clubs");
        melds.addAll(findRuns(clubs));

    }

    /**
     * Identifies all of the sets in a player's hand.
     *
     * @param hand the player's cards.
     * @return A List of Meld for the sets found.
     */
    private List<Meld> findSets(List<Card> hand) {
        ArrayList<Meld> allSets = new ArrayList<>();

        int i = 0;
        while(i < hand.size() - 2) {
            Card currentCard = hand.get(i);
            Card[] possibleSet = {currentCard, hand.get(i + 1), hand.get(i + 2)};
            if(Meld.buildSetMeld(possibleSet) != null) {
                SetMeld setMeld = Meld.buildSetMeld(possibleSet);
                i += 3;
                while(i < hand.size()) {
                    if(setMeld.canAppendCard(hand.get(i))) {
                        setMeld.appendCard(hand.get(i));
                        i++;
                    } else {
                        break;
                    }
                }

                allSets.add(setMeld);
            } else {
                i++;
            }
        }

        return allSets;
    }

    /**
     * Removes the melds from the cards that are being checked.
     *
     * @param hand the cards that are being checked for melds.
     * @return an ArrayList of Card that contains only the cards that are not in melds.
     */
    private ArrayList<Card> removeMelds(ArrayList<Card> hand) {
        ArrayList<Card> newHand = new ArrayList<>(hand);

        for(Meld meld : melds) {
            for(Card card : meld.getCards()) {
                newHand.remove(card);
            }
        }

        return newHand;
    }

    /**
     * Provides a list of cards with the same specified suit.
     *
     * @param hand the cards needed to be filtered.
     * @param suit the desired suit to filter by.
     * @return A List of Card that contains cards in the hand but are of the same specified suit.
     */
    private List<Card> separateBySuit(List<Card> hand, String suit) {
        List<Card> filteredHand = new ArrayList<>();
        for(Card card : hand) {
            switch (suit) {
                case "diamonds":
                    if(card.getSuit().equals(Card.CardSuit.DIAMONDS)) {
                        filteredHand.add(card);
                    }
                    break;
                case "hearts":
                    if(card.getSuit().equals(Card.CardSuit.HEARTS)) {
                        filteredHand.add(card);
                    }
                    break;
                case "spades":
                    if(card.getSuit().equals(Card.CardSuit.SPADES)) {
                        filteredHand.add(card);
                    }
                    break;
                case "clubs":
                    if(card.getSuit().equals(Card.CardSuit.CLUBS)) {
                        filteredHand.add(card);
                    }
                    break;
            }
        }

        return filteredHand;
    }

    /**
     * Identifies the runs in a hand.
     *
     * @param hand the unchecked cards in a player's hand.
     * @return a List of Meld that contains the runs in the player's hand.
     */
    private List<Meld> findRuns(List<Card> hand) {
        ArrayList<Meld> allRuns = new ArrayList<>();

        int i = 0;
        while(i < hand.size() - 2) {
            Card currentCard = hand.get(i);
            Card[] possibleRun = {currentCard, hand.get(i + 1), hand.get(i + 2)};
            if(Meld.buildRunMeld(possibleRun) != null) {
                RunMeld runMeld = Meld.buildRunMeld(possibleRun);
                i += 3;
                while(i < hand.size()) {
                    if(runMeld.canAppendCard(hand.get(i))) {
                        runMeld.appendCard(hand.get(i));
                        i++;
                    } else {
                        break;
                    }
                }

                allRuns.add(runMeld);
            } else {
                i++;
            }
        }

        return allRuns;
    }

    /**
     * Called by the game engine to allow this player strategy to reset its internal state before
     * competing it against a new opponent.
     */
    @Override
    public void reset() {
        this.hand = null;
        this.melds = null;
    }

    /**
     * Parses the player's deadwood cards and finds the card with the highest value
     *
     * @param deadwoodCards the cards that are not in melds.
     * @return a Card that has the highest value of all the deadwood cards.
     */
    private Card getHighestDeadwood(ArrayList<Card> deadwoodCards) {
        Card highestDeadwood = deadwoodCards.get(0);

        for(Card card : deadwoodCards) {
            if(card.getPointValue() > highestDeadwood.getPointValue()) {
                highestDeadwood = card;
            }
        }

        return highestDeadwood;
    }

    /** Returns all the cards that are not in melds. */
    private ArrayList<Card> getDeadwoodCards() {
        ArrayList<Card> deadwoodCards = new ArrayList<>(hand);
        List<Meld> meldCards = melds;
        for(Meld meld : meldCards) {
            for(Card card : meld.getCards()) {
                if(deadwoodCards.contains(card)) {
                    deadwoodCards.remove(card);
                }
            }
        }

        return deadwoodCards;

    }

    /**
     * The sum of the values of each deadwood card in a player' hand.
     *
     * @param deadwoodCards the cards that are not in any melds.
     * @return an int of the value of all the deadwood cards.
     */
    private int deadwoodCount(ArrayList<Card> deadwoodCards) {
        int deadwoodCount = 0;
        for(Card card : deadwoodCards) {
            deadwoodCount += card.getPointValue();
        }

        return deadwoodCount;
    }
}
