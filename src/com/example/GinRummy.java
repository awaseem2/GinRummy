package com.example;

public class GinRummy {
    public static void main(String[] args){
        GameEngine gameEngine = new GameEngine(new FirstPlayerStrategy(),
                new SecondPlayerStrategy(), 1000);
        gameEngine.runCompetition();
    }

}
