package com.sorrentocorp.mview;

import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxMain extends Application {
	static final int DEFAULT_NUM_OF_PANES = 3;

	Pane root;
	int numOfPanes;

	Pane getLogArea(String id) {
		Pane retval = new VBox(5);
		retval.getChildren().add(new Label(id));

		TextArea logarea = new TextArea("blank");
    logarea.setStyle("-fx-highlight-fill: lightgray; -fx-highlight-text-fill: firebrick; -fx-font-size: 12px;");
    logarea.setEditable(false);
		retval.getChildren().add(logarea);

		return retval;
	}

	@Override
	public void start(Stage stage) {
		List<String> params = getParameters().getUnnamed();
		numOfPanes = params.size();

		root = new VBox(10);
		for (int i = 0; i < numOfPanes; i++)
			root.getChildren().add(getLogArea(params.get(i)));

		stage.setScene(new Scene(root));
		stage.show();
	}

	public static void main(String[] args) { Application.launch(args); }
}
