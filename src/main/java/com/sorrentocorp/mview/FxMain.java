package com.sorrentocorp.mview;

import com.google.common.eventbus.EventBus;

import java.util.List;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FxMain extends Application {
	EventBus eventBus = new EventBus("mview");

	MainController controller;
	Tail tailService;
	Thread tailThread;

	@Override
	public void start(Stage stage) {
		List<String> params = getParameters().getUnnamed();

		controller = new MainController(params);
		eventBus.register(controller);

		tailService = new Tail(eventBus, params);
		tailThread = new Thread(tailService);
		tailThread.start();

		stage.setScene(new Scene(controller.createRootPane()));
		stage.show();
	}

	@Override
	public void stop () throws Exception {
		tailService.stop();
		tailThread.join();
	}

	public static void main(String[] args) { Application.launch(args); }
}
