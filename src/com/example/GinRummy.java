package com.example;

public class GinRummy {
    public static void main(String[] args){
        GameEngine gameEngine = new GameEngine(new NormalPlayerStrategy(),
                new StubbornPlayerStrategy(), 1);
        gameEngine.runCompetition();
        System.out.println("Player one won: " + gameEngine.playerWins(gameEngine.getPlayerOne()));
        System.out.println("Player two won: " + gameEngine.playerWins(gameEngine.getPlayerTwo()) );

        gameEngine = new GameEngine(new NormalPlayerStrategy(),
                new LovesRanksPlayerStrategy(), 1);
        gameEngine.runCompetition();
        System.out.println("Player one won: " + gameEngine.playerWins(gameEngine.getPlayerOne()));
        System.out.println("Player two won: " + gameEngine.playerWins(gameEngine.getPlayerTwo()) );

        gameEngine = new GameEngine(new LovesRanksPlayerStrategy(),
                new StubbornPlayerStrategy(), 1);
        gameEngine.runCompetition();
        System.out.println("Player one won: " + gameEngine.playerWins(gameEngine.getPlayerOne()));
        System.out.println("Player two won: " + gameEngine.playerWins(gameEngine.getPlayerTwo()) );
    }

}
