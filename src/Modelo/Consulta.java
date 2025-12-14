package Modelo;

public class Consulta { 
    private Medico medicoAtual;
    private Paciente pacienteConsultado;
    private String dataConsulta; // Ex. 19/12/2025
    
    private String status; //(Agendada, Finalizada, Cancelada)
    private String Relatorio; //(sintomas/tratamento), 
    //private Avaliacao (nota/texto).
    
    // Construtor pra facilitar criar consultas
    public Consulta(Medico medico, Paciente paciente, String data) {
        this.medicoAtual = medico;
        this.pacienteConsultado = paciente;
        this.dataConsulta = data;
        this.status = "Agendada";
    }
    
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
    public String getDataConsulta() {
        return dataConsulta;
    }
    public void setDataConsulta(String dataConsulta) {
        this.dataConsulta = dataConsulta;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getRelatorio() {
        return Relatorio;
    }
    public void setRelatorio(String Relatorio) {
        this.Relatorio = Relatorio;
    }
}
