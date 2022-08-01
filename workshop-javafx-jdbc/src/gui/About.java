package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class About implements Initializable{
	@FXML
	private Button btSair;
	
	@FXML
	public void onBtSairAction(ActionEvent event) {
		//fechar a janela
		Utils.currentStage(event).close();
		JOptionPane.showMessageDialog(null, "O Programa será Fechado!!");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO Auto-generated method stub
		
	}
}
