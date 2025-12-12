package Modelo;

public class Consulta { 
    private Medico medicoAtual;
    private Paciente pacienteConsultado;
    private int dataHora;
    private String Status; //(Agendada, Finalizada, Cancelada)
    //private Relatorio (sintomas/tratamento), 
    //private Avaliacao (nota/texto).


    //Getters e Setters
    public Medico getMedicoAtual() {
        return medicoAtual;
    }
    public void setMedicoAtual(Medico medicoAtual) {
        this.medicoAtual = medicoAtual;
    }
    public Paciente getPacienteConsultado() {
        return pacienteConsultado;
    }
    public void setPacienteConsultado(Paciente pacienteConsultado) {
        this.pacienteConsultado = pacienteConsultado;
    }
    public int getDataHora() {
        return dataHora;
    }
    public void setDataHora(int dataHora) {
        this.dataHora = dataHora;
    }
    public String getStatus() {
        return Status;
    }
    public void setStatus(String status) {
        Status = status;
    }
}
