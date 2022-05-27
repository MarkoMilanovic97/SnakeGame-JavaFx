package rs.ac.bg.fon.game.snake.studentsnakegame;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.awt.Point;

import static javafx.application.Platform.exit;

public class GameApplication extends Application {

    //background settings
    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;
    private static final int ROWS = 20;
    private static final int COLS = 20;
    private static final int SQUARE_SIZE = WIDTH / ROWS;



    //control settings
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;


    //food settings
    private static final String[] FOOD = new String[]{"file:src/main/resources/img/blackboard.png", "file:src/main/resources/img/books.png", "file:src/main/resources/img/certificate.png", "file:src/main/resources/img/homework.png", "file:src/main/resources/img/test.png"};
    private Image foodImage;
    private int foodX;
    private int foodY;


    //snake settings
    private List<Point> snakeBody = new ArrayList<>();
    private Point snakeHead;

    //game settings
    private boolean gameOver;
    private boolean inMenu = true;
    private int currentDirection;
    private int score = 0;

    //drawing object
    private GraphicsContext graphicsContext;


    @Override
    public void start(Stage stage) {
        Group root = new Group();
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        stage.setTitle("Snake Game");
        stage.setScene(scene);
        stage.show();
        graphicsContext = canvas.getGraphicsContext2D();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200), e -> runMainMenu(graphicsContext)));
        timeline.play();

        scene.setOnKeyPressed(keyEvent -> {
            KeyCode keyCode = keyEvent.getCode();
            if(keyCode == KeyCode.RIGHT || keyCode == KeyCode.D){
                if(currentDirection != LEFT){
                    currentDirection = RIGHT;
                }
            }

            if(keyCode == KeyCode.LEFT || keyCode == KeyCode.A){
                if(currentDirection != RIGHT){
                    currentDirection = LEFT;
                }
            }

            if(keyCode == KeyCode.UP || keyCode == KeyCode.W){
                if(currentDirection != DOWN){
                    currentDirection = UP;
                }
            }

            if(keyCode == KeyCode.DOWN || keyCode == KeyCode.S){
                if(currentDirection != UP){
                    currentDirection = DOWN;
                }
            }
            if(keyCode == KeyCode.ESCAPE){
                exit();
            }
            if(keyCode == KeyCode.ENTER){
                if(inMenu){
                    for(int i = 0; i < 3; i++){
                        snakeBody.add(new Point(5, ROWS / 2));
                    }
                    snakeHead = snakeBody.get(0);
                    generateFood();

                    Timeline timeline1 = new Timeline(new KeyFrame(Duration.millis(200), e -> run(graphicsContext)));
                    timeline1.setCycleCount(Animation.INDEFINITE);
                    timeline1.play();
                    inMenu = false;
                }
            }
        });
    }

    private void runMainMenu(GraphicsContext graphicsContext){
        drawBackground(graphicsContext);
        drawMenu(graphicsContext);
    }

    private void run(GraphicsContext graphicsContext){
        if(gameOver){
            graphicsContext.setFill(Color.RED);
            graphicsContext.setFont(new Font("Digital-7", 70));
            graphicsContext.fillText("Game Over", WIDTH / 3.5, HEIGHT / 2.0);
            return;
        }
        drawBackground(graphicsContext);
        drawFood(graphicsContext);
        drawSnake(graphicsContext);
        drawScore();

        for(int i = snakeBody.size() - 1; i >= 1; i--){
            snakeBody.get(i).x = snakeBody.get(i - 1).x;
            snakeBody.get(i).y = snakeBody.get(i - 1).y;
        }

        switch (currentDirection) {
            case RIGHT -> moveRight();
            case LEFT -> moveLeft();
            case UP -> moveUp();
            case DOWN -> moveDown();
        }
        gameOver();
        eatFood();
    }

    private void generateFood() {
        start:
        while (true) {
            foodX = (int) (Math.random() * ROWS);
            foodY = (int) (Math.random() * COLS);

            for (Point snake : snakeBody) {
                if (snake.getX() == foodX && snake.getY() == foodY) {
                    continue start;
                }
            }
            foodImage = new Image(FOOD[(int) (Math.random() * FOOD.length)]);
            break;
        }
    }

    private void drawBackground(GraphicsContext graphicsContext){
        for(int i = 0; i < ROWS; i++){
            for(int j = 0; j < COLS; j++){
                if((i + j) % 2 == 0) graphicsContext.setFill(Color.web("a4764a"));
                else graphicsContext.setFill(Color.web("9b7653"));

                graphicsContext.fillRect(i * SQUARE_SIZE, j * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
        }
    }

    private void drawMenu(GraphicsContext graphicsContext){
        graphicsContext.setFill(Color.YELLOW);
        graphicsContext.setFont(new Font("Digital-7", 50));
        graphicsContext.fillText("Press Enter to start the game", WIDTH / 7.5, HEIGHT / 3.0);
        graphicsContext.fillText("Press Esc to quit", WIDTH / 3.5, HEIGHT / 2.0);
    }

    private void drawFood(GraphicsContext graphicsContext) {
        graphicsContext.drawImage(foodImage, foodX * SQUARE_SIZE, foodY * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
    }

    private void drawSnake(GraphicsContext graphicsContext) {
        graphicsContext.setFill(Color.web("6CBB3C"));
        graphicsContext.drawImage(new Image("file:src/main/resources/img/cap.png"),  snakeHead.getX() * SQUARE_SIZE, snakeHead.getY() * SQUARE_SIZE, SQUARE_SIZE -1 , SQUARE_SIZE - 1);
        for(int i = 1; i < snakeBody.size(); i++){
            graphicsContext.fillRoundRect(snakeBody.get(i).getX() * SQUARE_SIZE, snakeBody.get(i).getY() * SQUARE_SIZE, SQUARE_SIZE -1 , SQUARE_SIZE - 1, 20, 20);
        }
    }

    private void drawScore(){
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(new Font("Digital-7", 35));
        graphicsContext.fillText("Score: " + score, 10, 35);
    }

    private void moveRight(){
        snakeHead.x++;
    }

    private void moveLeft(){
        snakeHead.x--;
    }

    private void moveUp(){
        snakeHead.y--;
    }

    private void moveDown(){
        snakeHead.y++;
    }

    public void gameOver(){
        if(snakeHead.x < 0 || snakeHead.y < 0 || snakeHead.x * SQUARE_SIZE >= WIDTH || snakeHead.y * SQUARE_SIZE >= HEIGHT){
            gameOver = true;
        }


        for(int i = 1; i < snakeBody.size(); i++){
            if(snakeHead.x == snakeBody.get(i).getX() && snakeHead.getY() == snakeBody.get(i).getY()){
                gameOver = true;
                break;
            }
        }
    }

    private void eatFood(){
        if(snakeHead.getX() == foodX && snakeHead.getY() == foodY){
            snakeBody.add(new Point(-1, -1));
            generateFood();
            score += 5;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}