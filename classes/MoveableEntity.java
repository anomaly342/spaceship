package classes;

import java.awt.geom.AffineTransform;

public class MoveableEntity extends Entity {
    private final int view_distance = 50;
    private double deltaX;
    private double deltaY;
    private double angle;
    private double speed;

    public MoveableEntity(int x, int y, double speed, int size, String url, double angle) {
        super(x, y, size, url);
        this.speed = speed;
        this.angle = angle;
        updateDelta();
    }

    public double getDeltaX() {
        return this.deltaX;
    }

    public double getDeltaY() {
        return this.deltaY;
    }

    public void moveLeft() {

        this.x -= speed;
    }

    public void moveRight() {
        this.x = (int) (this.x + speed);
    }

    public void moveUp() {

        this.y -= speed;

    }

    public void moveDown() {
        this.y += speed;
    }

    public void moveForward() {

        this.x = (int) (this.x + this.deltaX * speed);
        this.y = (int) (this.y + this.deltaY * speed);
    }

    public double getViewDistance() {
        return this.view_distance;
    }

    private void updateDelta() {

        this.deltaX = Math.cos(this.angle);
        this.deltaY = -Math.sin(this.angle);
    }

    // public double getAngle() {
    // return this.angle;
    // }

    public double getSpeed() {
        return this.speed;
    }

    public AffineTransform getTransform() {
        AffineTransform transform = new AffineTransform();

        transform.translate(this.getX(), this.getY());
        transform.rotate(Math.PI);
        transform.translate(this.getX(), this.getY());
        return transform;
    }

}
