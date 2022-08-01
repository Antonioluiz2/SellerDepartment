package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JOptionPane;

import db.DbException;
import gui.listeners.DataChangerListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationExceptions;
import model.services.DepartmentService;
import model.services.SellerService;

public class SellerFormController implements Initializable{
	private Seller entity;

	private SellerService service;
	private DepartmentService departmentservice;

	//atualizar a lista
	private List<DataChangerListener> dataChangerListeners =new ArrayList<>();

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtEmail;
	@FXML
	private DatePicker dpBirthDate;
	@FXML
	private TextField txtBaseSalary;
	@FXML
	private ComboBox<Department> comboBoxDepartment;

	@FXML
	private Label labelErrorName;
	@FXML
	private Label labelErrorEmail;
	@FXML
	private Label labelErrorBirthDate;
	@FXML
	private Label labelErrorBaseSalary;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;

	@FXML
	private ObservableList<Department> obsList;

	public void setSeller(Seller entity) {
		this.entity=entity;
	}
	public void setServices(SellerService service , DepartmentService departmentservice) {
		this.service=service;
		this.departmentservice=departmentservice;
	}

	//atualizar a lista
	public void subscribeDataChageListener(DataChangerListener listener) {
		dataChangerListeners.add(listener);
	}

	@FXML
	public void onBtSaveAction(ActionEvent event) {
		if(entity==null) {
			throw new IllegalStateException("Entity esta nullo");
		}
		if(service==null) {
			throw new IllegalStateException("service esta nullo");
		}
		try {
			entity=getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
			JOptionPane.showMessageDialog(null, "Seller Salvo com Sucesso!");

		}catch (ValidationExceptions e) {
			setErrorMenssages(e.getErros());
		}
		catch (DbException e) {
			e.printStackTrace();
			Alerts.showAlert("Error saving obj", null, e.getMessage(), AlertType.ERROR);
		}

	}
	//o evento será emitido para cada solicitação -DataChangerListener
	private void notifyDataChangeListeners() {
		for (DataChangerListener listener : dataChangerListeners) {
			listener.onDataListener();
		}

	}
	private Seller getFormData() {
		Seller obj=new Seller();
		ValidationExceptions excep= new ValidationExceptions("Validação de erro");

		obj.setId(Utils.tryParseToInt(txtId.getText()));

		//verificação de erros na inserção do valor, e lançamento da exceção
		if(txtName.getText()==null || txtName.getText().trim().equals("")) {
			excep.addErros("name", "O campo não pode ser Vazio");
		}
		obj.setName(txtName.getText());
		
		if(txtEmail.getText()==null || txtEmail.getText().trim().equals("")) {
			excep.addErros("email", "O campo não pode ser Vazio");
		}
		obj.setEmail(txtEmail.getText());
		
		//peagndo um valor do Date Picker
		if(dpBirthDate.getValue()==null) {
			excep.addErros("birthDate", "O campo não pode ser Vazio");
		}else {
			Instant instant= Instant.from(dpBirthDate.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setBirthDate(Date.from(instant));
		}
		
		
		if(txtBaseSalary.getText()==null || txtBaseSalary.getText().trim().equals("")) {
			excep.addErros("baseSalary", "O campo não pode ser Vazio");
		}
		obj.setBaseSalary(Utils.tryParseDouble(txtBaseSalary.getText()));
		obj.setDepartment(comboBoxDepartment.getValue());
		
		if(excep.getErros().size()>0) {
			throw excep;
		}
		return obj;
	}
	@FXML
	public void onBtCancelAction(ActionEvent event) {
		//fechar a janela
		Utils.currentStage(event).close();
		JOptionPane.showMessageDialog(null, "A ação foi cancelada");
	}


	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}

	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 70);
		Constraints.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(dpBirthDate, "dd/MM/yyyy");
		Constraints.setTextFieldDouble(txtBaseSalary);
		initializeComboBoxDepartment();
		JOptionPane.showMessageDialog(null, "Bem Vindo!");
	}

	//preencher os dados no form, pegando os dados do Obj seller
	public void updateFormData() {
		if(entity==null) {
			throw new IllegalStateException("o entity esta Nullo");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		if(entity.getBirthDate()!=null) {
			dpBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
		}
		Locale.setDefault(Locale.US);
		txtBaseSalary.setText(String.format("%2f", entity.getBaseSalary()));
		 
		//Para validar cadastro de novo vendedor
		if(entity.getDepartment()==null) {
			comboBoxDepartment.getSelectionModel().selectFirst();
		}else {
			//selecionando o combobox
			comboBoxDepartment.setValue(entity.getDepartment());
		}
		
	}

	public void loadAssociatedObjects() {
		List<Department> list= departmentservice.findAll();
		obsList= FXCollections.observableArrayList(list);
		comboBoxDepartment.setItems(obsList);
	}
	//mostar mensagem de erro caso não seja inserido um valor no nome
	private void setErrorMenssages(Map<String,String>erros) {
		Set<String> fields = erros.keySet();

//		if(fields.contains("name")) {
//			labelErrorName.setText(erros.get("name"));
//		}else {
//			labelErrorName.setText(erros.get(""));
//		}
			labelErrorName.setText(fields.contains("name")? erros.get("name"):"");
			labelErrorEmail.setText(fields.contains("email")?erros.get("email"):"");
			labelErrorBaseSalary.setText(fields.contains("baseSalary")?erros.get("baseSalary"):"");
			labelErrorBirthDate.setText(fields.contains("birthDate")?erros.get("birthDate"):"");
	
	}
	
		private void initializeComboBoxDepartment() {
			Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
				@Override
				protected void updateItem(Department item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty ? "" : item.getName());
				}
			};
			comboBoxDepartment.setCellFactory(factory);
			comboBoxDepartment.setButtonCell(factory.call(null));
			
		}

	}
