import javax.imageio.ImageIO;
import javax.swing.*;

import classes.Asteroid;
import classes.Entity;
import classes.Projectile;
import classes.Ship;
import classes.Powerboost;

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
    private Ship player = new Ship(600, 600, 12, Math.PI / 2, 150, "./assets/ships/player_ship.png", 300, 24, 5, 0,
            "player");
    private Set<Integer> activeKeys = new HashSet<>();
    private Random randomNumberGenerator;

    private ArrayList<Projectile> projectiles;
    private ArrayList<Asteroid> asteroids;
    private ArrayList<Powerboost> powerboosts;
    private ArrayList<Ship> enemyShips;

    private int intervalBetweenAsteroid = 225;
    private int asteroidTimeLapsed = 0;
    private int intervalBetweenEnemeyShips = 750;
    private int enemyShipTimeLapsed = 0;
    private int destroyedAsteroid = 0;
    private int quota = 3;
    private int totalScore = 0;
    private double overallDamage = 1.0;

    int timeElapsed = 0;

    private BufferedImage playingImage;

    public GameEngine() {
        randomNumberGenerator = new Random();
        projectiles = new ArrayList<Projectile>();
        asteroids = new ArrayList<Asteroid>();
        powerboosts = new ArrayList<Powerboost>();
        enemyShips = new ArrayList<Ship>();

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
                    player.moveDown();
                }
                if (activeKeys.contains(KeyEvent.VK_SPACE)
                        && player.getIntervalBetweenProjectiles() <= player.getProjectileCooldown()) {
                    projectiles
                            .add(new Projectile(player.getX(), player.getY() - player.getSize() / 2,
                                    player.getBaseProjectileSpeed(), Math.PI / 2, 50,
                                    "./assets/projectiles/projectile3.png", (int) (66 * overallDamage), "player"));
                    player.resetProjectileCooldown();

                }

                if (player.getHealth() > 0) {
                    // Spawn a ship every specified tick
                    if (enemyShipTimeLapsed % intervalBetweenEnemeyShips == 0 && enemyShipTimeLapsed != 0) {

                        int rand = randomNumberGenerator.nextInt((getWidth() - 45 - 45 + 1)) + 45;

                        if (enemyShips.size() < 10) {

                            enemyShips.add(
                                    new Ship(rand, 0, 1, Math.PI / 2, 90, "./assets/ships/enemy_ship.png",
                                            250, 37, 5, 0,
                                            "enemy"));
                        }

                        intervalBetweenEnemeyShips -= 8;
                        if (intervalBetweenEnemeyShips < 40) {
                            intervalBetweenEnemeyShips = 40;
                        }
                        enemyShipTimeLapsed = 0;

                    }

                    // Spawn an asteroid every specified tick
                    if (asteroidTimeLapsed % intervalBetweenAsteroid == 0 && asteroidTimeLapsed != 0) {

                        int rand = randomNumberGenerator.nextInt((getWidth() - 45 - 45 + 1)) + 45;
                        int rand_index = randomNumberGenerator.nextInt(2) + 1;
                        int speed = 14 - (intervalBetweenAsteroid / 20);
                        if (asteroids.size() < 16) {
                            asteroids.add(new Asteroid(rand, 50, speed, 90.0, 110,
                                    "./assets/asteroids/asteroid" + rand_index + ".png", 230, 100));
                        }
                        intervalBetweenAsteroid -= 1;
                        if (intervalBetweenAsteroid < 12) {
                            intervalBetweenAsteroid = 12;
                        }
                        asteroidTimeLapsed = 0;

                    }

                    // Check enemy ship is out of health or colides with player ship
                    // If neiher of above applies, then make it shoot out projectiles
                    int enemyShipSize = enemyShips.size();
                    for (int j = 0; j < enemyShipSize; j++) {
                        Ship currEnemyShip = enemyShips.get(j);
                        currEnemyShip.moveDown();
                        if (!currEnemyShip.isAlive()) {
                            enemyShips.remove(j);
                            enemyShipSize--;
                            j--;
                            destroyedAsteroid += 2;
                            totalScore += 5;

                        } else if (Entity.collides(currEnemyShip, player)) {
                            player.receiveDamage(150);
                            enemyShips.remove(j);
                            enemyShipSize--;
                            j--;
                            destroyedAsteroid += 2;
                            totalScore += 5;

                        } else {
                            if (currEnemyShip.getIntervalBetweenProjectiles() <= currEnemyShip
                                    .getProjectileCooldown()) {
                                double deltaX = player.getX() - currEnemyShip.getX();
                                double deltaY = (currEnemyShip.getY() - player.getY());
                                double angle = Math.atan2(deltaY, deltaX);

                                System.out.println(angle);
                                projectiles
                                        .add(new Projectile(currEnemyShip.getX(),
                                                currEnemyShip.getY() + currEnemyShip.getSize() / 2,
                                                currEnemyShip.getBaseProjectileSpeed(), angle, 50,
                                                "./assets/projectiles/projectile2.png", 40,
                                                "enemy"));
                                currEnemyShip.resetProjectileCooldown();
                            } else {
                                currEnemyShip.incrementProjectileCooldown();
                            }

                        }

                        if (destroyedAsteroid >= quota) {
                            String randString = Powerboost.generateRandomBoost();
                            powerboosts.add(new Powerboost(currEnemyShip.getX(), currEnemyShip.getY(), 60,
                                    "./assets/powerboosts/" + randString + ".png", randString));
                            destroyedAsteroid -= quota;
                        }

                    }
                    // Move forward asteroids and remove ones that are out of bound or out of
                    // health.
                    int asteroidsSize = asteroids.size();
                    for (int j = 0; j < asteroidsSize; j++) {
                        Asteroid currAsteroid = asteroids.get(j);
                        currAsteroid.moveDown();

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

                        } else if (Entity.collides(currAsteroid, player)) {
                            currAsteroid.inflictDamage(player);
                            asteroids.remove(j);
                            asteroidsSize--;
                            j--;
                            destroyedAsteroid++;
                            totalScore++;

                        }

                        if (destroyedAsteroid >= quota) {
                            String randString = Powerboost.generateRandomBoost();
                            powerboosts.add(new Powerboost(currAsteroid.getX(), currAsteroid.getY(), 60,
                                    "./assets/powerboosts/" + randString + ".png", randString));
                            destroyedAsteroid -= quota;
                        }
                    }
                }

                int powerboostSize = powerboosts.size();
                for (int i = 0; i < powerboostSize; i++) {
                    Powerboost currPowerboost = powerboosts.get(i);

                    if (Entity.collides(currPowerboost, player)) {
                        if (currPowerboost.getBoostType() == "reducedCooldown") {
                            if (player.getIntervalBetweenProjectiles() >= 3) {
                                player.decrementIntervalBetweenProjectiles();
                            }

                        } else if (currPowerboost.getBoostType() == "medicine") {
                            player.restoreHealth(80);
                        } else if (currPowerboost.getBoostType() == "increasedDamage") {
                            overallDamage += 0.2;
                        } else if (currPowerboost.getBoostType() == "increaseBaseProjectileSpeed") {
                            player.incrementBaseProjectileSpeed();
                        }

                        powerboosts.remove(i);
                        powerboostSize--;
                        i--;

                    }

                }

                // Check first if projectile is out of bound, then check if the projectiles hit
                // the asteroids.
                int projectilesize = projectiles.size();
                for (int i = 0; i < projectilesize; i++) {
                    Projectile currProjectile = projectiles.get(i);
                    currProjectile.moveForward();
                    if (currProjectile.getY() < 0) {
                        projectiles.remove(i);
                        projectilesize--;
                        i--;

                    } else {
                        for (Asteroid e : asteroids) {
                            if (Entity.collides(e, currProjectile)) {
                                if (currProjectile.inflictDamage(e)) {
                                    projectiles.remove(i);
                                    projectilesize--;
                                    i--;
                                }

                            }
                        }

                        for (Ship e : enemyShips) {
                            if (Entity.collides(e, currProjectile)) {
                                if (currProjectile.inflictDamage(e)) {
                                    projectiles.remove(i);
                                    projectilesize--;
                                    i--;
                                }

                            }
                        }

                        if (Entity.collides(currProjectile, player)) {
                            if (currProjectile.inflictDamage(player)) {
                                projectiles.remove(i);
                                projectilesize--;
                                i--;
                            }
                        }
                    }

                }

                timeElapsed++;
                asteroidTimeLapsed++;
                enemyShipTimeLapsed++;
                player.incrementProjectileCooldown();

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

            for (Projectile projectile : projectiles) {
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

            for (Ship ship : enemyShips) {

                double ratio = ((double) ship.getHealth() / (double) ship.getMaxHealth());

                if (ratio != 1) {
                    g.setColor(Color.RED);
                    g.drawRect(ship.getX() - ship.getSize() / 2 + 3,
                            ship.getY() - ship.getSize() / 2 - 27,
                            ship.getSize(), 20);
                    g.setColor(Color.RED);
                    g.fillRect(ship.getX() - ship.getSize() / 2 + 3,
                            ship.getY() - ship.getSize() / 2 - 27,
                            (int) (ship.getSize() * ratio), 20);
                }

                g.drawImage(ship.getImgInstance(), (int) (ship.getX() - ship.getSize() / 2),
                        (int) (ship.getY() - ship.getSize() / 2), null);
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

    public void getPowerboost(String type) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        activeKeys.add(e.getKeyCode());

        if (e.getKeyCode() == KeyEvent.VK_ENTER && player.getHealth() == 0) {
            enemyShips.clear();
            projectiles.clear();
            asteroids.clear();
            powerboosts.clear();
            intervalBetweenAsteroid = 200;
            asteroidTimeLapsed = 0;
            intervalBetweenEnemeyShips = 750;
            enemyShipTimeLapsed = 0;
            destroyedAsteroid = 0;
            quota = 3;
            totalScore = 0;
            overallDamage = 1.0;
            timeElapsed = 0;
            player = new Ship(600, 600, 12, Math.PI / 2, 150, "./assets/ships/player_ship.png", 300, 24, 5, 0,
                    "player");
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
