package model.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ValidationExceptions extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//Essa ação carregará todos os erros possiveis
	private Map<String, String> erros= new HashMap<>();
			
	public ValidationExceptions(String msg) {
		super(msg);
	}
	public Map<String, String> getErros(){
		return erros;
		
	}
	public void addErros(String fieldName, String errorMensage) {
		erros.put(fieldName, errorMensage);
	}
	
}
