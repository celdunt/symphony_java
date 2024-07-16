package loc.ex.symphony.ui;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import loc.ex.symphony.listview.Article;

public class ConfirmNamingController {


    public TextField nameField;
    public Button confirmButton;

    public Article article;

    public void initialize() {

        initConfirmButtonBehavior();

    }

    private void initConfirmButtonBehavior() {

        nameField.setOnKeyPressed(action -> {
            if (action.getCode() == KeyCode.ENTER) {
                confirmAction(action.getSource());
            }
        });
        confirmButton.onActionProperty().set(actionEvent -> {
            confirmAction(actionEvent.getSource());
        });

    }

    public void confirmAction(Object source) {

        if (article != null && !nameField.getText().isEmpty()) {
            article.setName(nameField.getText()); //define name article
            ArticlesController.additionArticle.set(article); //define and init addition article
            ((Stage) ((Node) source).getScene().getWindow()).close();//close window
        }

    }

}
