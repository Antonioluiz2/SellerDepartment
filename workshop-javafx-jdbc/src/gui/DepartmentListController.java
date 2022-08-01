package gui;

import java.io.IOException;
import java.net.URL;
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
import model.entities.Department;
import model.services.DepartmentService;

public class DepartmentListController implements Initializable, DataChangerListener{
	private DepartmentService service;

	//Essas sãoa s marcações para o FXML-comunicação
	@FXML
	private TableView<Department> tableViewDepartment;
	@FXML
	private TableColumn<Department, Integer> tableColumnId;
	@FXML
	private TableColumn<Department, String> tableColumnName;

	@FXML
	TableColumn<Department, Department> tableColumnEDIT;

	@FXML
	TableColumn<Department, Department> tableColumnREMOVE;

	@FXML
	private Button btNew;

	private ObservableList<Department> obsList;
	@FXML
	private void onBtNewAction(javafx.event.ActionEvent event) {
		//JOptionPane.showMessageDialog(null, "Hello World!");
		Stage parentStage=Utils.currentStage(event);
		Department obj=new Department();
		createDialogForm(obj,"/gui/DepartmentForm.fxml", parentStage);
	}
	//Injetçãoi de dependecia do DepartmentService
	public void setDepartmenteService(DepartmentService service) {
		this.service= service;
	};

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

		//			//Ajuste da tableview a tela, acompanhando largura e altura de dimensionamento
		Stage stage=(Stage) Main.getMainScene().getWindow();
		tableViewDepartment.prefHeightProperty().bind(stage.heightProperty());
	}
	public void updateTableView() {
		if(service==null) {
			throw new IllegalStateException("Serviço esta Nulo");
		}
		List<Department> list=service.findAll();
		//obslist recebe a lista department
		obsList=FXCollections.observableArrayList(list);
		//a tableView recebe a obslist
		tableViewDepartment.setItems(obsList);
		initEditButtons();
		initRemoveButtons();
	}
	private void createDialogForm(Department obj,String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader =new  FXMLLoader(getClass().getResource(absoluteName));
			Pane pane=loader.load();

			//Referencia para o controlador para a tela de formulario.
			DepartmentFormController controller=loader.getController();
			//carregar os metodos do objeto no formulario
			controller.setDepartment(obj);
			controller.setDepartmentService(new DepartmentService());
			controller.subscribeDataChageListener(this);
			controller.updateFormData();

			Stage dialogStage= new Stage();
			dialogStage.setTitle("Entre com um Departamento");
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
		tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("Editar");
			@Override
			protected void updateItem(Department obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(
						event -> createDialogForm(
								obj, "/gui/DepartmentForm.fxml",Utils.currentStage(event)));
			}
		});
	}
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>() {
			private final Button button = new Button("remove");
			@Override
			protected void updateItem(Department obj, boolean empty) {
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
	private void removeEntity(Department obj) {
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
