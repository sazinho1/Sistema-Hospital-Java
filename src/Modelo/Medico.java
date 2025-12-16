package Modelo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Medico extends Usuario{
    private String especialidade;
    private ArrayList<Consulta> agendaConsultas;

    // mapa que guarda a fila de espera por data
    // um mapa associa uma key(string/nome) com um valor (ex. idade/descrição)
    private Map<String, Queue<Paciente>> listaDeEspera;

    //Getter, Setter e Construtor
    public Medico (String nome,String login, String senha, String especialidade, String planoSaude){
        super(nome, login, senha, planoSaude);
        this.especialidade = especialidade;
        this.agendaConsultas = new ArrayList<>();
    }

    public String getEspecialidade() {
        return especialidade;
    }
    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }
    public ArrayList<Consulta> getAgendaConsultas() {
        return agendaConsultas;
    }
    public void setAgendaConsultas(ArrayList<Consulta> agendaConsultas) {
        this.agendaConsultas = agendaConsultas;
    }
    public void adicionarConsulta(Consulta c){
        agendaConsultas.add(c);
    }
    public Map<String, Queue<Paciente>> getListaDeEspera() {
        return listaDeEspera;
    }

    public void adicionarNaEspera(String data, Paciente p) {
        // Se não tiver fila pra esse dia, cria uma
        this.listaDeEspera.putIfAbsent(data, new LinkedList<>());
        // Adiciona o paciente na fila desse dia
        this.listaDeEspera.get(data).add(p);
    }
    public Paciente retirarDaEspera(String data) {
        if (this.listaDeEspera.containsKey(data) && !this.listaDeEspera.get(data).isEmpty()) {
            return this.listaDeEspera.get(data).poll(); // Remove e retorna o primeiro da fila
        }
        return null;
    }
}
