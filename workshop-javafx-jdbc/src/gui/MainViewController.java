package gui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;


import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.DepartmentService;
import model.services.SellerService;

public class MainViewController implements Initializable{

	@FXML
	private MenuItem menuItemSeller;
	@FXML
	private MenuItem menuItemDepartment;
	@FXML
	private MenuItem menuItemAbout;

	@FXML
	public void onMenuItemSellerAction() {
		System.out.println("onMenuItemSellerAction");
		loadView("/gui/SellerList.fxml", (SellerListController controller)-> {
			controller.setSellereService(new SellerService());
			controller.updateTableView();
		});
	}
	@FXML
	public void onMenuItemDepartmentAction() {
		System.out.println("onMenuItemDepartmentAction");
		//Passando o argumento como expressão lambda
		loadView("/gui/DepartmentList.fxml", (DepartmentListController controller)-> {
			controller.setDepartmenteService(new DepartmentService());
			controller.updateTableView();
		});
	}
	@FXML
	public void onMenuItemAboutAction() {
		System.out.println("onMenuItemAboutAction");
		loadView("/gui/About.fxml", x->{});
	}

	@Override
	public void initialize(URL uri, ResourceBundle rb) {

	}
	/*Esse metodo é generico, com a finalidade de não repetir ou criar loadView com os mesmos
	 * comandos para os demais metodos Action acima.....o Concumer facilitar a injeção de
	 * um 2º argumento no loadView*/
	private synchronized <T>void loadView(String absoluteName, Consumer<T> iniciar) {

		try {
			FXMLLoader loader =new  FXMLLoader(getClass().getResource(absoluteName));
			VBox newVBox=loader.load();

			Scene mainScene=Main.getMainScene();
			VBox mainVBox=(VBox) ((ScrollPane)mainScene.getRoot()).getContent();
			//VBox mainVBox=(VBox)(mainScene.getRoot()).getParent();
			//JOptionPane.showConfirmDialog(null, mainScene);
			Node mainMenu= mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			
			//Comando para chamar as funcções e executar o segundo paramento
			T controller=loader.getController();
			iniciar.accept(controller);

		} catch (Exception e) {
			Alerts.showAlert("IOException", "OK", e.getMessage(), AlertType.ERROR);
			System.out.println(e);
		}

	}
	
}
