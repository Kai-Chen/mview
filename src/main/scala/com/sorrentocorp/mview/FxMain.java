package com.sorrentocorp.mview;

import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
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

	TextArea getLogArea(int id) {
		TextArea retval = new TextArea(String.valueOf(id));
    retval.setStyle("-fx-highlight-fill: lightgray; -fx-highlight-text-fill: firebrick; -fx-font-size: 20px;");
    retval.setEditable(false);
    retval.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent t) { t.consume(); }
    });
		return retval;
	}

	@Override
	public void start(Stage stage) {
		List<String> params = getParameters().getUnnamed();
		numOfPanes = params.size();

		root = new VBox();
		Node[] logs = new Node[numOfPanes];
		for (int i = 0; i < numOfPanes; i++)
			logs[i] = getLogArea(i+1);
		root.getChildren().addAll(logs);

		stage.setScene(new Scene(root));
		stage.show();
	}

	public static void main(String[] args) { Application.launch(args); }
}
