package br.com.alfatecmarine.sisalmox.Exception;

public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String msgErro) {
        super(msgErro);
    }
}
