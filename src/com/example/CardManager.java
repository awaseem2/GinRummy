package com.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardManager {

    /**
     * Adds the opponent's deadwood cards to the knocker's melds where applicable.
     *
     * @param knocker the player who called knock.
     * @param opponent the player who did not call knock.
     */
    public static void handleAppends(Player knocker, Player opponent) {
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


    /**
     * Returns all the cards that are not in melds.
     *
     * @param player the player whose deadwood cards we'd like to receive.
     * @return an ArrayList of Cards which contains all of the player's deadwood cards.
     */
    public static ArrayList<Card> getDeadwoodCards(Player player) {
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

    /**
     * The sum of the values of each deadwood card in a player' hand.
     *
     * @param deadwoodCards the cards that are not in any melds.
     * @return an int of the value of all the deadwood cards.
     */
    public static int deadwoodCount(ArrayList<Card> deadwoodCards) {
        int deadwoodCount = 0;
        for(Card card : deadwoodCards) {
            deadwoodCount += card.getPointValue();
        }

        return deadwoodCount;
    }
}
