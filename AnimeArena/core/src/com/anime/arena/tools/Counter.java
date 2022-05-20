package com.anime.arena.tools;

public class Counter {
    private int counter;
    public Counter() {
        this.counter = 0;
    }

    public void increment() {
        counter++;
    }

    public void increment(int number) {
        counter += number;
    }

    public int getCounter() {
        return counter;
    }
}
