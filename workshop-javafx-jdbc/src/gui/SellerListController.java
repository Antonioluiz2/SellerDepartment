package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangerListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangerListener{
	private SellerService service;

	//Essas sãoa s marcações para o FXML-comunicação
	@FXML
	private TableView<Seller> tableViewSeller;
	@FXML
	private TableColumn<Seller, Integer> tableColumnId;
	@FXML
	private TableColumn<Seller, String> tableColumnName;
	@FXML
	private TableColumn<Seller, String> tableColumnEmail;
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;

	@FXML
	TableColumn<Seller, Seller> tableColumnEDIT;

	@FXML
	TableColumn<Seller, Seller> tableColumnREMOVE;
	
	

	@FXML
	private Button btNew;

	private ObservableList<Seller> obsList;
	@FXML
	private void onBtNewAction(javafx.event.ActionEvent event) {
		//JOptionPane.showMessageDialog(null, "Hello World!");
		Stage parentStage=Utils.currentStage(event);
		Seller obj=new Seller();
		createDialogForm(obj,"/gui/SellerForm.fxml", parentStage);
	}
	//Injetçãoi de dependecia do SellerService
	public void setSellereService(SellerService service) {
		this.service= service;
	};

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);

		//			//Ajuste da tableview a tela, acompanhando largura e altura de dimensionamento
		Stage stage=(Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
		
		
	
	}
	public void updateTableView() {
		if(service==null) {
			throw new IllegalStateException("Serviço esta Nulo");
		}
		List<Seller> list=service.findAll();
		//obslist recebe a lista department
		obsList=FXCollections.observableArrayList(list);
		//a tableView recebe a obslist
		tableViewSeller.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}
	private void createDialogForm(Seller obj,String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader =new  FXMLLoader(getClass().getResource(absoluteName));
			Pane pane=loader.load();

			//Referencia para o controlador para a tela de formulario.
			SellerFormController controller=loader.getController();
			//carregar os metodos do objeto no formulario
			controller.setSeller(obj);				
			controller.setServices(new SellerService(), new DepartmentService());
			controller.loadAssociatedObjects();
			controller.subscribeDataChageListener(this);
			controller.updateFormData();

			Stage dialogStage= new Stage();
			dialogStage.setTitle("Entre com um Seller");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();

		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IO Exception", "Erro ao ler a View", e.getMessage(), AlertType.ERROR);
		}
	}
	//recebendo o evento DataChangerListener
	@Override
	public void onDataListener() {
		updateTableView();

	}
	//Um botão que fara a edição do departamento, recebendo um obj e o editando.
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("Editar");
			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(
								obj, "/gui/SellerForm.fxml",Utils.currentStage(event)));
			}
		});
	}
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("remove");
			@Override
			protected void updateItem(Seller obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
				
			}
		});
	}
	private void removeEntity(Seller obj) {
		Optional<ButtonType> result=Alerts.showConfirmation("Confirmation", "Tem certeza que deseja Excluir");
		if(result.get()==ButtonType.OK) {
			if(service==null) {
				throw new IllegalStateException("O serviço esta Nullo");
			}
			try {
				service.remove(obj);
				updateTableView();
				JOptionPane.showMessageDialog(null, "Departamento Excluido!");
			}catch (DbIntegrityException e) {
				Alerts.showAlert("Erro ao Remover Obj", null, e.getMessage(), AlertType.ERROR);
				
			}	
		}
	}
}
