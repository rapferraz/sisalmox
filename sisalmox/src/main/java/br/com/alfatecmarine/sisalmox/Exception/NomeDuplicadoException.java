package br.com.alfatecmarine.sisalmox.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class NomeDuplicadoException extends RuntimeException {
    
    public NomeDuplicadoException(String message) {
        super(message);
    }
}
