package classes;

import interfaces.Vulnerable;

public class Ship extends MoveableEntity implements Vulnerable {
    private int health;
    private int maxHealth;

    public Ship(int x, int y, int speed, int size, String url, int health) {
        super(x, y, speed, size, url);
        this.maxHealth = health;
        this.health = health;
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
    public boolean isAlive() {
        return this.health > 0;
    }

    public void restoreHealth(int amount) {
        if (this.health + amount > this.maxHealth) {
            this.health = this.maxHealth;
        } else {
            this.health += amount;
        }
    }

}
