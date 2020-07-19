package com.javarush.games.spaceinvaders;

import com.javarush.engine.cell.*;
import com.javarush.games.spaceinvaders.gameobjects.Bullet;
import com.javarush.games.spaceinvaders.gameobjects.EnemyFleet;
import com.javarush.games.spaceinvaders.gameobjects.PlayerShip;
import com.javarush.games.spaceinvaders.gameobjects.Star;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SpaceInvadersGame extends Game {

    public static final int WIDTH = 64;
    public static final int HEIGHT = 64;
    private List<Star> stars;
    private EnemyFleet enemyFleet;
    public static final int COMPLEXITY = 5;
    private List<Bullet> enemyBullets;
    private PlayerShip playerShip;
    private boolean isGameStopped;
    private int animationsCount;
    private List<Bullet> playerBullets;
    private static final int PLAYER_BULLETS_MAX = 1;
    private int score;

    @Override
    public void initialize() {
        setScreenSize(WIDTH, HEIGHT);
        createGame();
    }

    private void createGame() {
        createStars();
        enemyFleet = new EnemyFleet();
        enemyBullets = new ArrayList<Bullet>();
        playerShip = new PlayerShip();
        isGameStopped = false;
        animationsCount = 0;
        playerBullets = new ArrayList<Bullet>();
        drawScene();
        setTurnTimer(40);
        score = 0;
    }

    private void drawScene() {
        drawField();
        enemyFleet.draw(this);
        for (int i = 0; i < enemyBullets.size(); i++) {
            enemyBullets.get(i).draw(this);
        }
        playerShip.draw(this);

        for (int k = 0; k < playerBullets.size(); k++) {
            playerBullets.get(k).draw(this);
        }

    }

    private void drawField() {
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                setCellValueEx(i, j, Color.BLACK, "");
            }
        }
        for (Star star : stars) {
            star.draw(this);
        }
    }

    private void createStars() {
        stars = new ArrayList<Star>();
        for (int i = 0; i < 8; i++)
            stars.add(new Star(
                    getRandomNumber(WIDTH), getRandomNumber(HEIGHT)));
    }

    @Override
    public void onTurn(int step) {
        moveSpaceObjects();
        check();
        Bullet fire = enemyFleet.fire(this);
        if (fire != null) {
            enemyBullets.add(fire);
        }
        setScore(score);
        drawScene();
    }

    private void moveSpaceObjects() {
        enemyFleet.move();
        for (int i = 0; i < enemyBullets.size(); i++) {
            enemyBullets.get(i).move();
        }
        playerShip.move();

        for (int k = 0; k < playerBullets.size(); k++) {
            playerBullets.get(k).move();
        }
    }

    private void removeDeadBullets() {
        for (int i = enemyBullets.size() - 1; i >= 0; i--) {
            //if (!enemyBullets.get(i).isAlive || enemyBullets.get(i).y >= HEIGHT - 1 || enemyBullets.get(i).y < 0) {
            if (!enemyBullets.get(i).isAlive || enemyBullets.get(i).y >= HEIGHT - 1) {
                enemyBullets.remove(i);
            }
        }

        for (int i = playerBullets.size() - 1; i >= 0; i--) {
            if (!playerBullets.get(i).isAlive || (playerBullets.get(i).y + playerBullets.get(i).height) < 0) {
                playerBullets.remove(i);
                //playerBullets.clear();
            }
        }
    }

    private void check() {
        playerShip.verifyHit(enemyBullets);
        score += enemyFleet.verifyHit(playerBullets);
        enemyFleet.verifyHit(playerBullets);
        enemyFleet.deleteHiddenShips();
        removeDeadBullets();
        if (!playerShip.isAlive) {
            stopGameWithDelay();
        }
        if(enemyFleet.getBottomBorder() >= playerShip.y) {
            playerShip.kill();
        } if(enemyFleet.getShipsCount() == 0) {
            playerShip.win();
            stopGameWithDelay();
        }
    }

    private void stopGame(boolean isWin) {
        isGameStopped = true;
        stopTurnTimer();
        if (isWin == true) {
            showMessageDialog(Color.BLACK, "You win!", Color.GREEN, 100);
        } else {
            showMessageDialog(Color.BLACK, "Game over!", Color.RED, 100);
        }
    }

    private void stopGameWithDelay() {
        animationsCount++;
        if (animationsCount >= 10) {
            stopGame(playerShip.isAlive);
        }
    }

    @Override
    public void onKeyPress(Key key) {
        if (key == Key.SPACE && isGameStopped == true) {
            createGame();
        }
        if (key == Key.LEFT) {
            playerShip.setDirection(Direction.LEFT);
        }
        if (key == Key.RIGHT) {
            playerShip.setDirection(Direction.RIGHT);
        }
        if (key == Key.SPACE) {
            Bullet b = playerShip.fire();
            if (b != null && playerBullets.size() < PLAYER_BULLETS_MAX) {
                playerBullets.add(b);
            }
        }
    }

    @Override
    public void onKeyReleased(Key key) {
        if (key == Key.LEFT & playerShip.getDirection() == Direction.LEFT) {
            playerShip.setDirection(Direction.UP);
        }
        if (key == Key.RIGHT & playerShip.getDirection() == Direction.RIGHT) {
            playerShip.setDirection(Direction.UP);
        }
    }

    @Override
    public void setCellValueEx(int x, int y, Color color, String str) {
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            return;
        }
        super.setCellValueEx(x, y, color, str);
    }
}
