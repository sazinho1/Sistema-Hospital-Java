package Modelo;

public class Paciente extends Usuario{
    private int idade;

    //Getter, Setter e Construtor
    public Paciente(String nome,String login, String senha, String planoSaude, int idade) {
        super(nome, login, senha, planoSaude);
        this.idade = idade;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    
}
