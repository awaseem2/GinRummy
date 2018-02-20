package com.example;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private PlayerStrategy playerStrategy;
    private ArrayList<Card> hand;
    private boolean isFirst;
    private int points;
    private int wins;

    public Player(PlayerStrategy playerStrategy) {
        this.playerStrategy = playerStrategy;
    }

    //for purposes of cloning a Player object
    public Player(Player player) {
        this.playerStrategy = player.getPlayerStrategy();
        this.hand = player.getHand();
        this.isFirst = player.isFirst;
        this.points = player.points;
        this.wins = player.wins;
    }

    public PlayerStrategy getPlayerStrategy() {
        return playerStrategy;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public List<Meld> getMelds() {
        return playerStrategy.getMelds();
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setIsFirst(boolean first) {
        isFirst = first;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void initializePlayerStrategy() {
        playerStrategy.receiveInitialHand(getHand());
    }
}
