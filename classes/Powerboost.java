package classes;

import java.util.Random;

public class Powerboost extends Entity {
    static public String generateRandomBoost() {
        String[] list = { "reducedCooldown", "medicine", "increasedDamage", "increasedProjectileSpeed" };

        int rand = new Random().nextInt(list.length);

        return list[rand];

    }

    private String boostType;

    public Powerboost(int x, int y, int size, String url, String boostType) {
        super(x, y, size, url);
        this.boostType = boostType;
    }

    public String getBoostType() {
        return this.boostType;
    }

}
