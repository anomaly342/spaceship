package classes;

import interfaces.Attackable;
import interfaces.Vulnerable;

public class Asteroid extends MoveableEntity implements Attackable, Vulnerable {
    private final int maxHealth;
    private int health;
    private int attack;

    public Asteroid(int x, int y, double speed, double angle, int size, String url, int maxHealth, int attack) {
        super(x, y, speed, size, url, angle);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attack = attack;
        aliveVulnerableObjects.add(this);
    }

    @Override
    public void receiveDamage(int amount) {

        if (health - amount < 0) {
            health = 0;
        } else {
            health -= amount;
        }

    }

    @Override
    public int getHealth() {
        return this.health;
    }

    @Override
    public int getMaxHealth() {
        return this.maxHealth;
    }

    @Override
    public boolean inflictDamage(Object obj) {
        if (obj instanceof Ship) {
            if (((Ship) obj).getTeam() == "player") {
                ((Vulnerable) obj).receiveDamage(attack);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isAlive() {
        return this.health > 0;
    }
}
