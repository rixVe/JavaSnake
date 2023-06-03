package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class GamePanel extends JPanel implements KeyListener, Runnable {

    private final int SCREEN_DIMENSIONS = 600;
    private final int TILE_SIZE = 25;
    private final int NUMBER_OF_TILES = SCREEN_DIMENSIONS^2/TILE_SIZE;

    private int snakeLength = 6;
    private int score = 0;

    private int[] snakeX = new int[NUMBER_OF_TILES];
    private int[] snakeY = new int[NUMBER_OF_TILES];
    private int appleX;
    private int appleY;

    //There can only be one input per frame
    private boolean canInput = false;

    private enum Direction {
        UP,
        DOWN,
        RIGHT,
        LEFT
    }

    Direction direction = Direction.RIGHT;

    Random random;

    Thread thread;

    public GamePanel() {
        this.addKeyListener(this);
        this.setFocusable(true);
        int TIMER_SPACE = 32;
        this.setPreferredSize(new Dimension(SCREEN_DIMENSIONS, SCREEN_DIMENSIONS + TIMER_SPACE));
        this.setBackground(Color.DARK_GRAY);


        random = new Random();

        thread = new Thread(this);
        thread.start();
    }

    //Method run in thread.start()
    @Override
    public void run() {
        newGame();

        while(thread != null) {
            logic();

            repaint();


            canInput = true;
            try {
                int DELAY = 75;
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // No input when logic() is running
            canInput = false;
        }
    }

    private void newGame() {
        newApple();

        snakeLength = 6;
        score = 0;

        snakeX = new int[NUMBER_OF_TILES];
        snakeY = new int[NUMBER_OF_TILES];

        direction = Direction.RIGHT;
    }

    public void logic() {

        checkApple();

        //Moving the snake
        for(int i = snakeLength - 1; i >= 0; i--) {
            if(i != 0) { //Not head, move tile to where the next one is
                snakeX[i] = snakeX[i-1];
                snakeY[i] = snakeY[i-1];
            }
            else { //Head, move it in the direction chosen
                switch(direction) {
                    case UP -> snakeY[i] -= TILE_SIZE;
                    case DOWN -> snakeY[i] += TILE_SIZE;
                    case RIGHT -> snakeX[i]+= TILE_SIZE;
                    case LEFT -> snakeX[i]-= TILE_SIZE;
                }
            }
        }
        checkCollision();
    }

    private void newApple() {
        //Make sure it is centered in the grid
        appleX = random.nextInt(SCREEN_DIMENSIONS/ TILE_SIZE) * TILE_SIZE;
        appleY = random.nextInt(SCREEN_DIMENSIONS / TILE_SIZE) * TILE_SIZE;
        for(int i = 0; i < snakeLength; i++) {  //Don't create an apple that is inside the snake
            if(appleX == snakeX[i] && appleY == snakeY[i]) {
                newApple();
                break;
            }
        }
    }

    private void checkApple() {
        //We have to look in the future to check if the snake will collide with an apple
        int tempSnakeX = snakeX[0];
        int tempSnakeY = snakeY[0];
        switch(direction) {
            case UP -> tempSnakeY = snakeY[0] - TILE_SIZE;
            case DOWN -> tempSnakeY = snakeY[0] + TILE_SIZE;
            case RIGHT -> tempSnakeX = snakeX[0]+ TILE_SIZE;
            case LEFT -> tempSnakeX = snakeX[0]- TILE_SIZE;
        }
        if(tempSnakeX == appleX && tempSnakeY == appleY) {
            snakeLength++;
            score++;
            newApple();
        }
    }

    private void checkCollision() {
        for(int i = 1; i < snakeLength; i++) {
            //If head touches a body part or if head sticks out of the frame
            if((snakeX[0] == snakeX[i] && snakeY[0] == snakeY[i]) ||
                    snakeX[0] >= SCREEN_DIMENSIONS ||
                    snakeX[0] < 0 ||
                    snakeY[0] >= SCREEN_DIMENSIONS ||
                    snakeY[0] < 0) {
                newGame();
                break;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) { //Method inherited from JFrame
        super.paintComponent(g); //I don't know what it does, fortunately it works


        g.drawLine(0, SCREEN_DIMENSIONS, SCREEN_DIMENSIONS, SCREEN_DIMENSIONS);

        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Consolas", Font.PLAIN, 30));
        g.drawString("Score: " + score, 5, SCREEN_DIMENSIONS + 26);

        for(int i = 0; i < snakeLength; i++) {
            if(i == 0) //Head of the snake
                g.setColor(Color.GREEN);
            else //Body of the snake
                g.setColor(new Color(80, 190, 80));
            g.fillRect(snakeX[i], snakeY[i], TILE_SIZE , TILE_SIZE);
        }

        g.setColor(Color.RED);
        g.fillOval(appleX, appleY, TILE_SIZE ,TILE_SIZE);

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(canInput) { //Only one input per frame
            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP -> {
                    if (direction != Direction.DOWN) { //We can't turn 180 degrees
                        direction = Direction.UP;
                        canInput = false; //Only one input per frame
                    }
                }
                case KeyEvent.VK_DOWN -> {
                    if (direction != Direction.UP) {
                        direction = Direction.DOWN;
                        canInput = false;
                    }
                }
                case KeyEvent.VK_RIGHT -> {
                    if (direction != Direction.LEFT) {
                        direction = Direction.RIGHT;
                        canInput = false;
                    }
                }
                case KeyEvent.VK_LEFT -> {
                    if (direction != Direction.RIGHT) {
                        direction = Direction.LEFT;
                        canInput = false;
                    }
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

}
