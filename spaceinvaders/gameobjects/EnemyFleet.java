package com.javarush.games.spaceinvaders.gameobjects;

import com.javarush.engine.cell.Game;
import com.javarush.games.spaceinvaders.Direction;
import com.javarush.games.spaceinvaders.ShapeMatrix;
import com.javarush.games.spaceinvaders.SpaceInvadersGame;

import java.util.*;

public class EnemyFleet {

    private static final int ROWS_COUNT = 3;
    private static final int COLUMNS_COUNT = 10;
    private static final int STEP = ShapeMatrix.ENEMY.length + 1;
    private List<EnemyShip> ships;
    private Direction direction = Direction.RIGHT;

    public EnemyFleet() {
        createShips();
    }

    private void createShips() {
        ships = new ArrayList<EnemyShip>();
        for (int y = 0; y < ROWS_COUNT; y++) {
            for (int x = 0; x < COLUMNS_COUNT; x++) {
                ships.add(new EnemyShip(x * STEP, y * STEP + 12));
            }
        }
        ships.add(new Boss(STEP * COLUMNS_COUNT / 2 - ShapeMatrix.BOSS_ANIMATION_FIRST.length / 2 - 1, 5));
    }

    public void draw(Game game) {
        for (int i = 0; i < ships.size(); i++) {
            ships.get(i).draw(game);
        }
    }

    private double getLeftBorder() {
        double min = ships.get(0).x;

        for (EnemyShip ships : ships) {
            if (ships.x < min) min = ships.x;
        }

        return min;
    }

    private double getRightBorder() {
        double max = ships.get(0).x + ships.get(0).width;

        for (EnemyShip ships : ships) {
            if ((ships.x + ships.width) > max) max = ships.x + ships.width;
        }
        return max;
    }

    private double getSpeed() {
        return Math.min(2.0, 3.0 / ships.size());
    }

    public void move() {

        boolean flag = false;
        if (ships.size() != 0) {
            if (direction == Direction.LEFT && getLeftBorder() < 0) {
                direction = Direction.RIGHT;
                flag = true;
            }
            if (direction == Direction.RIGHT && getRightBorder() > SpaceInvadersGame.WIDTH) {
                direction = Direction.LEFT;
                flag = true;
            }
            if (flag) {
                for (int i = 0; i < ships.size(); i++) {
                    ships.get(i).move(Direction.DOWN, getSpeed());
                }
            } else {
                for (int i = 0; i < ships.size(); i++) {
                    ships.get(i).move(direction, getSpeed());
                }
            }
        }
    }

    public Bullet fire(Game game) {
        if (ships.size() < 1) {
            return null;
        } else if (game.getRandomNumber(100 / SpaceInvadersGame.COMPLEXITY) > 0) {
            return null;
        } else {
            return ships.get(game.getRandomNumber(ships.size())).fire();
        }
    }

    public void deleteHiddenShips() {
        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i).isVisible() == false) {
                ships.remove(i);
            }
        }
    }

    public double getBottomBorder() {
        try {
            if (ships != null && !ships.isEmpty()) {
                double max = ships.get(0).y + ships.get(0).height;
                for (EnemyShip enemyShip : ships) {
                    if (enemyShip.y + enemyShip.height > max)
                        max = enemyShip.y + enemyShip.height;
                }
                return max;
            } else {
                throw new NoSuchElementException();
            }
        } catch (NoSuchElementException e) {
            return 0;
        }
    }

    public int getShipsCount() {
        return ships.size();
    }

    public int verifyHit(List<Bullet> bullets) {
        int result = 0;
        if (bullets.isEmpty()) return result;
        for (EnemyShip ship : ships) {
            for (Bullet bullet : bullets) {
                if (ship.isCollision(bullet) && ship.isAlive && bullet.isAlive) {
                    ship.kill();
                    bullet.kill();
                    result += ship.score;
                }
            }
        }
        return result;
    }
}

