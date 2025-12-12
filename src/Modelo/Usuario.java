package Modelo;

public class Usuario {
    private String nome;
    private String planoSaude;
    private String login;
    private String senha;

    //Getters, Setters e Construtor
    public Usuario(String nome,String login, String senha, String planoSaude) {
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.planoSaude = planoSaude;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getPlanoSaude() {
        return planoSaude;
    }
    public void setPlanoSaude(String planoSaude) {
        this.planoSaude = planoSaude;
    }
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }

    
}
