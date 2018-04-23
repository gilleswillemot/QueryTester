/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import domain.DomainController;
import gui.MainGuiController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class startUp extends Application {

	public static void main(String[] args) {
		Application.launch(startUp.class, args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		MainGuiController frame = new MainGuiController(new DomainController());

		Scene scene = new Scene(frame);
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.show();
	}



}
