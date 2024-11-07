package classes;

import interfaces.Attackable;
import interfaces.Vulnerable;

public class Projectile extends MoveableEntity implements Attackable {
    private int attack;
    private String causedBy;

    public Projectile(int x, int y, double speed, double angle, int size, String url, int attack, String causedBy) {
        super(x, y, speed, size, url, angle);
        this.attack = attack;
        this.causedBy = causedBy;

    }

    @Override
    public boolean inflictDamage(Object obj) {
        if (obj instanceof Vulnerable) {
            if (obj instanceof Ship) {
                if (this.causedBy != ((Ship) obj).getTeam()) {
                    ((Vulnerable) obj).receiveDamage(this.attack);
                    return true;
                }
            }

            if (obj instanceof Asteroid && this.causedBy == "player") {
                ((Vulnerable) obj).receiveDamage(this.attack);
                return true;
            }

        }

        return false;
    }
}
