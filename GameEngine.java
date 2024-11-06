import javax.imageio.ImageIO;
import javax.swing.*;

import classes.Asteroid;
import classes.Entity;
import classes.MoveableEntity;
import classes.Projectile;
import classes.Ship;
import classes.Powerboost;
import interfaces.Vulnerable;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GameEngine extends JPanel implements KeyListener {
    private Ship player = new Ship(600, 600, 12, 150, "./assets/ships/player_ship.png", 300);
    private Set<Integer> activeKeys = new HashSet<>();
    private Random randomNumberGenerator;
    private ArrayList<Projectile> playerProjectiles;
    private ArrayList<Asteroid> asteroids;
    private ArrayList<Powerboost> powerboosts;

    private int intervalBetweenAsteroid = 200;
    private int intervalBetweenPlayerProjectile = 24;
    private int asteroidTimeLapsed = 0;
    private int playerProjectileCooldown = 0;
    private int destroyedAsteroid = 0;
    private int quota = 4;
    private int totalScore = 0;
    private double overallDamage = 1.0;

    int timeElapsed = 0;

    private BufferedImage playingImage;

    public GameEngine() {
        randomNumberGenerator = new Random();
        playerProjectiles = new ArrayList<Projectile>();
        asteroids = new ArrayList<Asteroid>();
        powerboosts = new ArrayList<Powerboost>();

        try {
            playingImage = ImageIO.read(new File("./assets/backgrounds/playing.jpg"));
        } catch (IOException e) {

        }

        setFocusable(true);
        setBackground(Color.GRAY);
        addKeyListener(this);
        thread.start();

    }

    Thread thread = new Thread() {
        @Override
        public void run() {

            while (true) {
                // System.out.println(Vulnerable.aliveVulnerableObjects.size());
                if (activeKeys.contains(KeyEvent.VK_A) && player.getX() - player.getSize() / 3 > 0) {
                    player.moveLeft();
                }
                if (activeKeys.contains(KeyEvent.VK_D) && player.getX() + player.getSize() / 3 < getWidth()) {
                    player.moveRight();
                }
                if (activeKeys.contains(KeyEvent.VK_W)) {
                    player.moveForward();
                }
                if (activeKeys.contains(KeyEvent.VK_S) && player.getY() + player.getSize() / 3 < getHeight()) {
                    player.moveBackward();
                }
                if (activeKeys.contains(KeyEvent.VK_SPACE)
                        && playerProjectileCooldown > intervalBetweenPlayerProjectile) {
                    playerProjectiles
                            .add(new Projectile(player.getX(), player.getY() - player.getSize() / 2, 5, 50,
                                    "./assets/projectiles/projectile3.png", (int) (66 * overallDamage), "player"));
                    playerProjectileCooldown = 0;
                }

                if (destroyedAsteroid == quota) {
                    destroyedAsteroid = 0;
                }

                // Spawn an asteroid every 200 tick
                if (asteroidTimeLapsed % intervalBetweenAsteroid == 0 && asteroidTimeLapsed != 0) {

                    int rand = randomNumberGenerator.nextInt((getWidth() - 45 - 45 + 1)) + 45;
                    int rand_index = randomNumberGenerator.nextInt(2) + 1;
                    int speed = 7 - (intervalBetweenAsteroid / 40);
                    if (asteroids.size() < 22) {
                        asteroids.add(new Asteroid(rand, 50, speed, 110,
                                "./assets/asteroids/asteroid" + rand_index + ".png", 300, 100));
                    }
                    intervalBetweenAsteroid -= 2;
                    if (intervalBetweenAsteroid < 2) {
                        intervalBetweenAsteroid = 2;
                    }
                    asteroidTimeLapsed = 0;

                }
                // Move forward asteroids and remove ones that are out of bound.
                if (player.getHealth() > 0) {

                    int asteroidsSize = asteroids.size();
                    for (int j = 0; j < asteroidsSize; j++) {
                        Asteroid currAsteroid = asteroids.get(j);
                        currAsteroid.moveBackward();

                        if (currAsteroid.getY() > getHeight()) {
                            asteroids.remove(j);
                            asteroidsSize--;
                            j--;
                            // If its health is equal to 0
                        } else if (!currAsteroid.isAlive()) {

                            asteroids.remove(j);
                            asteroidsSize--;
                            j--;
                            destroyedAsteroid++;
                            totalScore++;
                            if (destroyedAsteroid == quota) {
                                String randString = Powerboost.generateRandomBoost();
                                powerboosts.add(new Powerboost(currAsteroid.getX(), currAsteroid.getY(), 60,
                                        "./assets/powerboosts/" + randString + ".png", randString));
                            }

                        } else if (Entity.collides(currAsteroid, player)) {
                            currAsteroid.inflictDamage(player);
                            asteroids.remove(j);
                            asteroidsSize--;
                            j--;
                            destroyedAsteroid++;
                            totalScore++;
                            if (destroyedAsteroid == quota) {
                                String randString = Powerboost.generateRandomBoost();
                                powerboosts.add(new Powerboost(currAsteroid.getX(), currAsteroid.getY(), 60,
                                        "./assets/powerboosts/" + randString + ".png", randString));
                            }

                        }
                    }
                }
                int powerboostSize = powerboosts.size();
                for (int i = 0; i < powerboostSize; i++) {
                    Powerboost currPowerboost = powerboosts.get(i);

                    if (Entity.collides(currPowerboost, player)) {
                        if (currPowerboost.getBoostType() == "reducedCooldown") {
                            if (intervalBetweenPlayerProjectile < 3) {
                                intervalBetweenPlayerProjectile = 3;
                            } else {
                                intervalBetweenPlayerProjectile -= 1;
                            }
                        } else if (currPowerboost.getBoostType() == "medicine") {
                            player.restoreHealth(80);
                        } else if (currPowerboost.getBoostType() == "increasedDamage") {
                            overallDamage += 0.2;
                        }

                        powerboosts.remove(i);
                        powerboostSize--;
                        i--;

                    }

                }

                // Check first if projectile is out of bound, then check if the projectiles hit
                // the asteroids.
                int playerProjectileSize = playerProjectiles.size();
                for (int i = 0; i < playerProjectileSize; i++) {
                    Projectile currProjectile = playerProjectiles.get(i);
                    currProjectile.moveForward();
                    if (currProjectile.getY() < 0) {
                        playerProjectiles.remove(i);
                        playerProjectileSize--;
                        i--;

                    } else {
                        for (Asteroid e : asteroids) {
                            if (Entity.collides(e, currProjectile)) {

                                currProjectile.inflictDamage(e);
                                playerProjectiles.remove(i);
                                playerProjectileSize--;
                                i--;

                            }
                        }
                    }

                }

                timeElapsed++;
                asteroidTimeLapsed++;
                playerProjectileCooldown++;

                try {
                    thread.sleep(10);
                } catch (InterruptedException e) {

                }

                repaint();

            }

        };
    };

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!(player.getHealth() <= 0)) {
            g.drawImage(playingImage, 0, (int) (-60000 + timeElapsed >> 4),
                    null);

            g.drawImage(player.getImgInstance(), (int) (player.getX() - player.getSize() / 2),
                    (int) (player.getY() - player.getSize() / 2), null);

            g.setColor(Color.WHITE);

            // g.fillOval((int) player.getCenterX(), (int) player.getCenterY(), 512, 512);

            g.setColor(Color.RED);
            g.drawLine((int) player.getX(), (int) player.getY(), (int) (player.getX() + player.getDeltaX() * 9),
                    (int) (player.getY() + player.getDeltaY() * 9));

            for (Projectile projectile : playerProjectiles) {
                g.drawImage(projectile.getImgInstance(), (int) (projectile.getX() - projectile.getSize() / 2),
                        (int) (projectile.getY() - projectile.getSize() / 2), null);
            }

            for (Asteroid asteroid : asteroids) {

                double ratio = ((double) asteroid.getHealth() / (double) asteroid.getMaxHealth());

                if (ratio != 1) {
                    g.setColor(Color.RED);
                    g.drawRect(asteroid.getX() - asteroid.getSize() / 2 + 3,
                            asteroid.getY() - asteroid.getSize() / 2 - 27,
                            asteroid.getSize(), 20);
                    g.setColor(Color.RED);
                    g.fillRect(asteroid.getX() - asteroid.getSize() / 2 + 3,
                            asteroid.getY() - asteroid.getSize() / 2 - 27,
                            (int) (asteroid.getSize() * ratio), 20);
                }

                g.drawImage(asteroid.getImgInstance(), (int) (asteroid.getX() - asteroid.getSize() / 2),
                        (int) (asteroid.getY() - asteroid.getSize() / 2), null);
            }

            for (Powerboost powerboost : powerboosts) {
                g.drawImage(powerboost.getImgInstance(), (int) (powerboost.getX() - powerboost.getSize() / 2),
                        (int) (powerboost.getY() - powerboost.getSize() / 2), null);
            }
            double playerHealthRatio = (double) player.getHealth() / (double) player.getMaxHealth();

            g.setColor(Color.GREEN);
            g.drawRect(25, getHeight() - 60, (int) (player.getMaxHealth()), 20);
            g.setColor(Color.GREEN);
            g.fillRect(25, getHeight() - 60, (int) (player.getMaxHealth() * playerHealthRatio), 20);

            g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
            g.drawString("" + destroyedAsteroid + " / " + quota, 799, getHeight() - 35);
        } else {
            setBackground(Color.gray);
            g.setFont(new Font("TimesRoman", Font.PLAIN, 30));
            g.drawString("Your score:" + totalScore, 120, getHeight() / 2);

        }

    };

    @Override
    public void keyPressed(KeyEvent e) {
        activeKeys.add(e.getKeyCode());

        if (e.getKeyCode() == KeyEvent.VK_ENTER && player.getHealth() == 0) {
            playerProjectiles.clear();
            asteroids.clear();
            powerboosts.clear();
            intervalBetweenAsteroid = 200;
            intervalBetweenPlayerProjectile = 24;
            asteroidTimeLapsed = 0;
            playerProjectileCooldown = 0;
            destroyedAsteroid = 0;
            quota = 4;
            totalScore = 0;
            overallDamage = 1.0;
            player = new Ship(600, 600, 12, 150, "./assets/ships/player_ship.png", 300);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        activeKeys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

}
