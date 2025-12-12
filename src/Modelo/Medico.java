package Modelo;

import java.util.ArrayList;

public class Medico extends Usuario{
    private String especialidade;
    private ArrayList<Consulta> agendaConsultas;

    //Getter, Setter e Construtor
    public Medico (String nome,String login, String senha, String especialidade, String planoSaude){
        super(nome, login, senha, planoSaude);
        this.especialidade = especialidade;
    }
    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
}
