package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class SceneManager {
    private static Stage mainStage;

    public static void setStage(Stage stage) {
        mainStage = stage;
    }

    public static void loadScene(String fxml) {
        try {
            mainStage.setScene(new Scene(FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/" + fxml)))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadScene(String fxml, Object data) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/" + fxml));
            mainStage.setScene(new Scene(loader.load()));
            Object controller = loader.getController();

            if (controller instanceof DataReceiver) {
                ((DataReceiver) controller).initData(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
