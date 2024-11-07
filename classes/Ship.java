package classes;

import interfaces.Vulnerable;

public class Ship extends MoveableEntity implements Vulnerable {
    private int health;
    private int maxHealth;
    private int intervalBetweenProjectiles;
    private int projectileCooldown;
    private int baseProjectileSpeed;
    private String team;

    public Ship(int x, int y, double speed, double angle, int size, String url, int health,
            int intervalBetweenProjectiles,
            int baseProjectileSpeed, int projectileCooldown, String team) {
        super(x, y, speed, size, url, angle);
        this.maxHealth = health;
        this.health = health;
        this.intervalBetweenProjectiles = intervalBetweenProjectiles;
        this.baseProjectileSpeed = baseProjectileSpeed;
        this.projectileCooldown = projectileCooldown;
        this.team = team;
        aliveVulnerableObjects.add(this);
    }

    public int getIntervalBetweenProjectiles() {
        return this.intervalBetweenProjectiles;
    }

    public int getBaseProjectileSpeed() {
        return this.baseProjectileSpeed;
    }

    public int getProjectileCooldown() {
        return this.projectileCooldown;
    }

    public String getTeam() {
        return this.team;
    }

    public void incrementBaseProjectileSpeed() {
        if (baseProjectileSpeed < 13)
            this.baseProjectileSpeed++;
    }

    public void incrementProjectileCooldown() {
        this.projectileCooldown++;
    }

    public void resetProjectileCooldown() {
        this.projectileCooldown = 0;
    }

    public void decrementIntervalBetweenProjectiles() {
        if (this.intervalBetweenProjectiles - 1 > 5) {
            this.intervalBetweenProjectiles--;
        }

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
