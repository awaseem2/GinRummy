package com.example;

import com.example.Player;

public class GameEngineUtilities {

    /** Initializes each player's hand in player strategy. */
    public static void initializePlayerStrategies(Player playerOne, Player playerTwo) {
        playerOne.initializePlayerStrategy();
        playerTwo.initializePlayerStrategy();
    }

    /** Chooses the first player randomly by choosing between 1 and 2. If it's 1, player one goes
     *  first. If it's 2, player two goes first. */
    public static void pickFirstPlayer(Player playerOne, Player playerTwo) {
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
     *  use 'PlayerOne' for the player who goes first. */
    public static void switchPlayers(Player playerOne, Player playerTwo) {
        Player temp = new Player(playerOne);
        playerOne = playerTwo;
        playerTwo = temp;
    }
}
