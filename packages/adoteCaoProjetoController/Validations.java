package adoteCaoProjetoController;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import adoteCaoProjetoModel.Dao;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Validations {
	
	Dao dao = new Dao();

	private final String ERROR_CITY = "&Cidade Invalida";
	private final String ERROR_NEIGHBORHOOD = "&Bairro invalido";
	private final String ERROR_CEP = "&CEP invalido";
	private final String ERROR_LOGIN = "&Login invalido";
	private final String ERROR_PASSWORD = "&Senha invalida";
	private final String ERROR_NAME = "&Nome invalido";
	private final String ERROR_CPF = "&CPF invalido";
	private final String ERROR_BIRTH = "&Data de nascimento invalida";
	private final String ERROR_ONG_NAME = "&Nome da ONG invalido";
	private final String NO_ERROR = "&Dados validos";
	private final String ERROR_EMPTY = "&Nao pode ser nulo";
	private final String ERROR_EMAIL_ALREADY_EXISTS = "&Email ja cadastrado";
	private final String ERROR_ONG_NAME_ALREADY_EXISTS = "&Ong ja cadastrado";
	private final String ERROR_CPF_ALREADY_EXISTS = "&cpf ja cadastrado";
	
	private enum ErrorCode {
	        CITY, 
	        NEIGHBORHOOD, 
	        CEP, 
	        LOGIN, 
	        PASSWORD, 
	        NAME,
	        CPF,
	        BIRTH,
	        ONG_NAME,
	        NO_ERROR
	    }
	
	
	//VALIDACAO PARA ADOTANTE
	public String validateInputs(String city, String neighborhood, String cep,
			String login, String password, String name, String cpf, String birth, String ongName, boolean isOng) throws ClassNotFoundException, IOException {
		 String[] inputs = {city, neighborhood, cep, login, password, name, cpf, birth};
		String errorMessage = null;
		for(int i = 0; i < inputs.length; i++) {
			if(inputs[i] == null || inputs[i].isEmpty()) {
				errorMessage = ErrorCode.values()[i] + ERROR_EMPTY;
				return errorMessage;
			}
			if(isOng) {
				if(ongName == null || ongName.isEmpty()) {
					errorMessage = ErrorCode.values()[8]+ ERROR_EMPTY;
				}
			
			}
		}
		if(!validateCep(cep)) {
			errorMessage = ErrorCode.values()[2] + ERROR_CEP;
			return errorMessage;
		}
		else if(!validateLogin(login)) {
			errorMessage = ErrorCode.values()[3] + ERROR_LOGIN;
			return errorMessage;
		}else if(dao.checkForDuplicityOngEmail(login)){	
			errorMessage = ErrorCode.values()[3] + ERROR_EMAIL_ALREADY_EXISTS;
			return errorMessage;
		}
		else if(!validatePassword(password)) {
			errorMessage = ErrorCode.values()[4] + ERROR_PASSWORD;
			return errorMessage;
		}
		else if(!validateCPF(cpf)) {
			errorMessage = ErrorCode.values()[6] + ERROR_CPF;
			return errorMessage;
		}
		else if(dao.checkForDuplicityOngCPF(cpf)) {
			errorMessage = ErrorCode.values()[6] + ERROR_CPF_ALREADY_EXISTS;
		}
		else if(!validateBirth(birth)) {
			errorMessage = ErrorCode.values()[7] + ERROR_BIRTH;
			return errorMessage;
		}else if(dao.checkForDuplicityOngName(ongName)){
			errorMessage = ErrorCode.values()[8] + ERROR_ONG_NAME_ALREADY_EXISTS;
			return errorMessage;
		}else {
			errorMessage = ErrorCode.values()[9] + NO_ERROR;
			return errorMessage;
		}
		return errorMessage;
		
	}

	
	private boolean validatePassword(String password) {
		boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+=\\[\\]{};':\"\\\\|,.<>\\/?-].*");
		return hasSpecialChar;
	}
	
	private boolean validateCep(String cep) {
		String cepPattern = "\\d{8}";
	    return cep.matches(cepPattern);
	}
	private boolean validateLogin(String login) {
		 String emailPattern = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
		    Pattern pattern = Pattern.compile(emailPattern);
		    Matcher matcher = pattern.matcher(login);
		    return matcher.matches();
	}
	private boolean validateCPF(String cpf) {
		cpf = cpf.replaceAll("\\D+", "");
	    if (cpf.length() != 11) {
	        return false;
	    }
	    // VERIFICA SE TODOS OS CARACTERES SAO IGUAIS
	    if (cpf.matches("(\\d)\\1{10}")) {
	    	return false;
	    }
	    // PRIMEIRO NUMERO DE VERIFICACAO
	    int sum = 0;
	    for (int i = 0; i < 9; i++) {
	        sum += (cpf.charAt(i) - '0') * (10 - i);
	    }
	    int firstDigit = 11 - (sum % 11);
	    if (firstDigit > 9) {
	        firstDigit = 0;
	    }
	    // SEGUNDO NUMERO DE VERIFICACAO
	    sum = 0;
	    for (int i = 0; i < 10; i++) {
	        sum += (cpf.charAt(i) - '0') * (11 - i);
	    }
	    int secondDigit = 11 - (sum % 11);
	    if (secondDigit > 9) {
	        secondDigit = 0;
	    }
	    // VERIFICA AMBOS OS NUMEROS DE VERIFICACAO
	    if (firstDigit == (cpf.charAt(9) - '0') && secondDigit == (cpf.charAt(10) - '0')) {
	        return true;
	    } else {
	        return false;
	    }
	}
	
	private boolean validateBirth(String birth){
		try {
            LocalDate date = LocalDate.parse(birth);
            LocalDate earliestValidDate = LocalDate.of(1900, 1, 1);
            LocalDate latestValidDate = LocalDate.now();
            return !date.isBefore(earliestValidDate) && !date.isAfter(latestValidDate);
        } catch (DateTimeParseException e) {
            return false;
        }
    }	
}
