import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;

public class PixelCoordGetter extends Application {

    static Robot robot;

    public static void main(String[] args) {
        try{robot = new Robot();}catch(AWTException e){}
        launch(args);
    }

    @Override
    public void start(Stage ps) throws Exception {
        VBox vb = new VBox();
        vb.setBackground(Background.EMPTY);
        vb.setStyle("-fx-border-color:blue;");
        vb.setOnMouseClicked(e -> {
            System.out.print(e.getScreenX() + " | " + e.getScreenY() + "   --   ");
            getPixelColor(e);
        });
        Scene scn = new Scene(vb, 400, 400);
        scn.setFill(Color.TRANSPARENT);
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scn);
        stage.show();
    }

    private void getPixelColor(MouseEvent e) {
        Thread t = new Thread(() -> {
            java.awt.Color color = robot.getPixelColor((int)e.getScreenX(), (int)e.getScreenY());
            System.out.println(color.getRed() + " | " + color.getGreen() + " | " + color.getBlue());
        });
        t.start();
    }
}
