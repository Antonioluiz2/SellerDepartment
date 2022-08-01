package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.JOptionPane;

import db.DbException;
import gui.listeners.DataChangerListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationExceptions;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable{
	private Department entity;

	private DepartmentService service;

	//atualizar a lista
	private List<DataChangerListener> dataChangerListeners =new ArrayList<>();

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;

	@FXML
	private Label labelErrorName;

	@FXML
	private Button btSave;

	@FXML
	private Button btCancel;
	

	public void setDepartment(Department entity) {
		this.entity=entity;
	}
	public void setDepartmentService(DepartmentService service) {
		this.service=service;
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
			JOptionPane.showMessageDialog(null, "Derpartamento Salvo com Sucesso!");

		}catch (ValidationExceptions e) {
			setErrorMenssages(e.getErros());
		}
		catch (DbException e) {
			Alerts.showAlert("Error saving obj", null, e.getMessage(), AlertType.ERROR);
		}

	}
	//o evento será emitido para cada solicitação -DataChangerListener
	private void notifyDataChangeListeners() {
		for (DataChangerListener listener : dataChangerListeners) {
			listener.onDataListener();
		}

	}
	private Department getFormData() {
		Department obj=new Department();
		ValidationExceptions excep= new ValidationExceptions("Validaçãod e erro");

		obj.setId(Utils.tryParseToInt(txtId.getText()));

		//verificação de erros na inserção do valor, e lançamento da exceção
		if(txtName.getText()==null || txtName.getText().trim().equals("")) {
			excep.addErros("name", "O campo não pode ser Vazio");
		}
		obj.setName(txtName.getText());
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
		Constraints.setTextFieldMaxLength(txtName, 10);
		JOptionPane.showMessageDialog(null, "Bem Vindo!");
	}

	public void updateFormData() {
		if(entity==null) {
			throw new IllegalStateException("o entity esta Nullo");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());

	}
	//mostar mensagem de erro caso não seja inserido um valor no nome
	private void setErrorMenssages(Map<String,String>erros) {
		Set<String> fields = erros.keySet();

		if(fields.contains("name")) {
			labelErrorName.setText(erros.get("name"));
		}
	}
}
