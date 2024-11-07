package classes;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Entity {
    private String imgURL;
    private int size;
    protected int x;
    protected int y;
    private BufferedImage imgInstance;

    public static boolean collides(Entity a, Entity b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        double radiusA = a.getSize() / 3;
        double radiusB = b.getSize() / 3;
        double radiusSum = radiusA + radiusB;

        return distance <= radiusSum;
    }

    public Entity(int x, int y, int size, String url) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.imgURL = url;
        try {
            this.imgInstance = ImageIO.read(new File(this.imgURL));
            this.imgInstance = resizeImage(this.imgInstance, (int) size, (int) size);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public double getCenterX() {
        return this.x - (size / 2);
    }

    public double getCenterY() {
        return this.y - (size / 2);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getSize() {
        return this.size;
    }

    public String getURL() {
        return this.imgURL;
    }

    public BufferedImage getImgInstance() {
        return this.imgInstance;
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, width, height, null);
        g2d.dispose();
        return resizedImage;
    }
}
