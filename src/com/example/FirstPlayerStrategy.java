package com.example;

import com.example.PlayerStrategy;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirstPlayerStrategy implements PlayerStrategy {
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
        return null;
    }

    /**
     * Called by the game engine to prompt the player is whether they would like to
     * knock.
     *
     * @return True if the player has decided to knock
     */
    @Override
    public boolean knock() {
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

    /**
     * Called by the game engine to allow access the player's current list of Melds.
     *
     * @return The player's list of melds.
     */
    @Override
    public List<Meld> getMelds() {
        Collections.sort(hand);

        return melds;
    }

    private void findMelds() {
        ArrayList<Card> uncheckedHand = new ArrayList<>(hand);
        Collections.sort(uncheckedHand);
        melds.addAll(findSets(uncheckedHand));
        uncheckedHand = removeMelds(uncheckedHand, melds);


    }

    private List<Meld> findSets(List<Card> hand) {
        ArrayList<Meld> allSets = new ArrayList<>();

        int i = 0;
        while(i < hand.size() - 3) {
            Card currentCard = hand.get(i);
            Card[] possibleSet = {currentCard, hand.get(i + 1), hand.get(i + 2)};
            if(Meld.buildSetMeld(possibleSet) != null) {
                SetMeld setMeld = Meld.buildSetMeld(possibleSet);
                i += 2;
                while(i < hand.size()) {
                    if(setMeld.canAppendCard(hand.get(i))) {
                        setMeld.appendCard(hand.get(i));
                    }
                    i++;
                }

                allSets.add(setMeld);
            } else {
                i++;
            }
        }

        return allSets;
    }

    private static ArrayList<Card> removeMelds(ArrayList<Card> hand, List<Meld> melds) {
        ArrayList<Card> newHand = new ArrayList<>(hand);

        for(Meld meld : melds) {
            for(Card card : meld.getCards()) {
                newHand.remove(card);
            }
        }

        return newHand;
    }

    private List<Meld> findRuns(List<Card> hand) {
        List<Card> diamonds = separateBySuit(hand, "diamonds");
        List<Card> hearts = separateBySuit(hand, "hearts");
        List<Card> spades = separateBySuit(hand, "spades");
        List<Card> clubs = separateBySuit(hand, "clubs");

        ArrayList<Card> uncheckedHand = new ArrayList<>(hand);
        Collections.sort(uncheckedHand);

        melds.addAll(findRuns(diamonds));
        
        uncheckedHand = removeMelds(uncheckedHand, melds);

        return null;
    }

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

    private List<Meld> fildMeldsPerSuit(List<Card> hand) {
        ArrayList<Meld> allRuns = new ArrayList<>();

        int i = 0;
        while(i < hand.size() - 3) {
            Card currentCard = hand.get(i);
            Card[] possibleRun = {currentCard, hand.get(i + 1), hand.get(i + 2)};
            if(Meld.buildRunMeld(possibleRun) != null) {
                RunMeld runMeld = Meld.buildRunMeld(possibleRun);
                i += 2;
                while(i < hand.size()) {
                    if(runMeld.canAppendCard(hand.get(i))) {
                        runMeld.appendCard(hand.get(i));
                    }
                    i++;
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

    }
}
