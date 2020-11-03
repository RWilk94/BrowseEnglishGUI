package rwilk.browseenglish;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

@SpringBootApplication
public class BrowseEnglishGUI extends Application {

  private ConfigurableApplicationContext context;
  private Parent rootNode;

  @Override
  public void init() throws Exception {
    SpringApplicationBuilder builder = new SpringApplicationBuilder(BrowseEnglishGUI.class);
    context = builder.run(getParameters().getRaw().toArray(new String[0]));

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/scene/main.fxml"));
    loader.setControllerFactory(context::getBean);
    rootNode = loader.load();
    super.init();
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("LearnEnglishGUI");
    Scene scene = new Scene(rootNode, 1366, 766);
    scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

    scene.setOnKeyPressed(e -> {
      if (e.getCode() == KeyCode.ENTER) {
        Button btn = (Button)scene.lookup("#buttonAdd");
        btn.fire();
      }
    });

    primaryStage.setScene(scene);
    // scene.getStylesheets().add(getClass().getResource("/stylesheet.css").toExternalForm())
    primaryStage.centerOnScreen();
    primaryStage.show();
  }

  @Override
  public void stop() throws Exception {
    context.close();
  }
}
