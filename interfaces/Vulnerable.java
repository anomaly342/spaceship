package interfaces;

import java.util.ArrayList;

public interface Vulnerable {
    public static ArrayList<Vulnerable> aliveVulnerableObjects = new ArrayList<Vulnerable>();

    public void receiveDamage(int amount);

    public int getHealth();

    public int getMaxHealth();

    public boolean isAlive();
}