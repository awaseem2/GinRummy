package com.example;

public class GinRummy {
    public static void main(String[] args){
        GameEngine gameEngine = new GameEngine(new FirstPlayerStrategy(),
                new FirstPlayerStrategy(), 1);
        gameEngine.runCompetition();
        System.out.println("Player one won: " + gameEngine.playerWins(gameEngine.getPlayerOne()));
        System.out.println("Player two won: " + gameEngine.playerWins(gameEngine.getPlayerTwo()) );
    }

}
