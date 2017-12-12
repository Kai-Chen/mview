package com.sorrentocorp.mview;

import com.google.common.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class MainController {
	private final List<String> lognames = new ArrayList<>();
  private final Map<String, TextArea> logs = new HashMap<>();

	public MainController(List<String> lognames) {
		this.lognames.addAll(lognames);
	}

	Pane createRootPane() {
		Pane retval = new VBox(10);
		for (String n: lognames)
			retval.getChildren().add(createLogArea(n));
		return retval;
	}

	Pane createLogArea(String id) {
		Pane retval = new VBox(5);
		retval.getChildren().add(new Label(id));

		TextArea logarea = new TextArea("blank");
    logarea.setStyle("-fx-highlight-fill: lightgray; -fx-highlight-text-fill: firebrick; -fx-font-size: 12px;");
    logarea.setEditable(false);
		retval.getChildren().add(logarea);

		logs.put(id, logarea);

		return retval;
	}

	TextArea getLogArea(String id) {
		return logs.get(id);
	}

	@Subscribe
	public void fileTruncated(FileTruncated event) {
		System.out.println("received file truncated event " + event);
	}

	@Subscribe
	public void fileModified(FileModified event) {
		System.out.println("received file modified event " + event);
	}
}
