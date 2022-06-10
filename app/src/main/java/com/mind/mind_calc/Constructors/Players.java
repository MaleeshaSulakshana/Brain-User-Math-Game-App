package com.mind.mind_calc.Constructors;

public class Players implements Comparable<Players> {

    int level;
    String PlayerName;

    public Players(int level, String playerName) {
        this.level = level;
        PlayerName = playerName;
    }

    @Override
    public int compareTo(Players f)
    {
        if (level > f.level) {
            return 1;
        } else if (level < f.level) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return this.PlayerName;
    }

}
