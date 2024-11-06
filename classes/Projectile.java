package classes;

import interfaces.Attackable;
import interfaces.Vulnerable;

public class Projectile extends MoveableEntity implements Attackable {
    private int attack;
    private String causedBy;

    public Projectile(int x, int y, int speed, int size, String url, int attack, String causedBy) {
        super(x, y, speed, size, url);
        this.attack = attack;

    }

    @Override
    public void inflictDamage(Object obj) {
        if (obj instanceof Vulnerable && !(obj instanceof Ship)) {

            ((Vulnerable) obj).receiveDamage(this.attack);
        }
    }

}
