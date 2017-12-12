package com.sorrentocorp.mview;

import com.google.common.eventbus.EventBus;

import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxMain extends Application {
	EventBus eventBus = new EventBus("mview");

	MainController controller;

	@Override
	public void start(Stage stage) {
		List<String> params = getParameters().getUnnamed();
		controller = new MainController(params);
		eventBus.register(controller);

		stage.setScene(new Scene(controller.createRootPane()));
		stage.show();
	}

	public static void main(String[] args) { Application.launch(args); }
}
