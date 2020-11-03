package rwilk.browseenglish.release.controller;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class MainReleaseController implements Initializable {

  public AnchorPane anchorPaneWordReleaseForm;
  public AnchorPane anchorPaneWordReleaseTable;

  @Override
  public void initialize(URL location, ResourceBundle resources) {

  }

  public void init(ReleaseContextController releaseContextController) {
    try {
      FXMLLoader fxmlLoaderCourseForm = new FXMLLoader();
      fxmlLoaderCourseForm.setLocation(getClass().getResource("/scene/release/release_table.fxml"));
      VBox form = fxmlLoaderCourseForm.load();
      ReleaseTableController releaseTableController = fxmlLoaderCourseForm.getController();

      FXMLLoader fxmlLoaderCourseTable = new FXMLLoader();
      fxmlLoaderCourseTable.setLocation(getClass().getResource("/scene/release/release_form.fxml"));
      VBox table = fxmlLoaderCourseTable.load();
      ReleaseFormController releaseFormController = fxmlLoaderCourseTable.getController();

      releaseTableController.init(releaseContextController);
      releaseFormController.init(releaseContextController);

      anchorPaneWordReleaseTable.getChildren().add(form);
      anchorPaneWordReleaseForm.getChildren().add(table);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
