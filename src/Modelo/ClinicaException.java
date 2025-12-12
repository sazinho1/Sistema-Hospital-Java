package Modelo;

public class ClinicaException extends Exception {

    // Construtor simples que recebe a mensagem de erro
    public ClinicaException(String mensagem) {
        super(mensagem);
    }

    // Construtor que recebe a mensagem e o erro
    public ClinicaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
