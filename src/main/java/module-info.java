module rs.ac.bg.fon.game.snake.studentsnakegame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.graphics;


    opens rs.ac.bg.fon.game.snake.studentsnakegame to javafx.fxml;
    exports rs.ac.bg.fon.game.snake.studentsnakegame;
}