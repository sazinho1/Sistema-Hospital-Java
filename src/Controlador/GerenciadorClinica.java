package Controlador;

import Modelo.ClinicaException;
import Modelo.Consulta;
import Modelo.Medico;
import Modelo.Paciente;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GerenciadorClinica {

    private ArrayList<Medico> medicos = new ArrayList<>();
    private ArrayList<Paciente> pacientes = new ArrayList<>();

    public GerenciadorClinica() {
        this.medicos = carregarArquivoMedicos();
        this.pacientes = carregarArquivoPacientes();
    }

    //Getters e Setters
    public ArrayList<Medico> getMedicos() {
        return medicos;
    }

    public void setMedicos(ArrayList<Medico> medicos) {
        this.medicos = medicos;
    }

    public ArrayList<Paciente> getPacientes() {
        return pacientes;
    }

    public void setPacientes(ArrayList<Paciente> pacientes) {
        this.pacientes = pacientes;
    }

//  -------------------------- MÉDICOS --------------------------
    public void salvarArquivoMedicos(ArrayList<Medico> medicos) throws ClinicaException {
        //Estrutura do Medico CSV: NOME;LOGIN;SENHA;ESPECIALIDADE;PLANO_ATENDIDO

        // O false é pra recriar o arquivo em vez de salvar la no final
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("medicos.csv", false))) {

            for (Medico m : medicos) {
                String linha = m.getNome().toUpperCase() + ";" + m.getLogin().toUpperCase() + ";" + m.getSenha().toUpperCase() + ";" + m.getEspecialidade().toUpperCase() + ";" + m.getPlanoSaude().toUpperCase();

                writer.write(linha);
                writer.newLine(); // pula pra linha de baixo
            }

        } catch (IOException e) {
            throw new ClinicaException("Falha ao salvar os dados dos medicos.", e);
        } 
     

    }

    public ArrayList<Medico> carregarArquivoMedicos() {
        ArrayList<Medico> listaCarregada = new ArrayList<>();
        File arquivo = new File("medicos.csv");

        if (!arquivo.exists()) {
            return listaCarregada; //retorna vazia se for a 1ª vez
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;

            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(";", -1);

                // Se a linha estiver quebrada ou incompleta, pula ela para não travar o programa
                if (dados.length < 5) {
                    continue;
                } 

                // A ordem aqui tem que ser igual a do salvar
                String nome = dados[0];
                String login = dados[1];
                String senha = dados[2];
                String Especialidade = dados[3];
                String PlanoSaude = dados[4];

                // Recria o objeto
                Medico m = new Medico(nome, login, senha, Especialidade, PlanoSaude);
                listaCarregada.add(m);
            }

        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + e.getMessage());
        }

        this.medicos = listaCarregada; 
        return listaCarregada;
    }

//  -------------------------- PACIENTES --------------------------
    public void salvarArquivoPacientes(ArrayList<Paciente> pacientes) throws ClinicaException {
        //Estrutura do Paciente CSV: NOME;LOGIN;SENHA;PLANO_SAUDE;IDADE

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("pacientes.csv", false))) {

            for (Paciente p : pacientes) {

                //pra, se o plano for nulo, ele alterar para 'SEM_PLANO'
                String plano = (p.getPlanoSaude() == null) ? "SEM_PLANO" : p.getPlanoSaude();

                String linha = p.getNome().toUpperCase() + ";" + p.getLogin().toUpperCase() + ";" + p.getSenha().toUpperCase() + ";" + plano.toUpperCase() + ";" + p.getIdade();

                writer.write(linha);
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ClinicaException("Falha ao salvar os dados dos pacientes.", e);
        }

    }

    public ArrayList<Paciente> carregarArquivoPacientes() {
        ArrayList<Paciente> listaCarregada = new ArrayList<>();
        File arquivo = new File("pacientes.csv");

        if (!arquivo.exists()) {
            return listaCarregada; //retorna vazia se for a 1ª vez
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha;

            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(";");

                // Pra não quebrar se tiver alguma linha em branco no arquivo
                if (dados.length < 5) {
                    continue;
                }

                String nome = dados[0];
                String login = dados[1];
                String senha = dados[2];
                String planoLido = dados[3];
                String planoFinal; // variavel usada pro construtor

                // Se estiver escrito "SEM_PLANO" ou "null", vira o null real do Java
                if (planoLido.equalsIgnoreCase("SEM_PLANO") || planoLido.equalsIgnoreCase("null")) {
                    planoFinal = null;
                } else {
                    planoFinal = planoLido;
                }

                int idadeFinal;
                try {
                    idadeFinal = Integer.parseInt(dados[4].trim());
                } catch (NumberFormatException e) {
                    System.err.println("Erro ao ler idade do paciente " + nome + ": " + dados[4]); // Caso alguem tenha escrito "vinte" ao inves de "20" 
                    idadeFinal = 0; // Define 0 como padrão em caso de erro
                }

                Paciente p = new Paciente(nome, login, senha, planoFinal, idadeFinal);
                listaCarregada.add(p);

                }
            }
            catch (IOException e) {
                System.err.println("Erro ao ler arquivo: " + e.getMessage());
                
            }
            
            this.pacientes = listaCarregada;
            return listaCarregada;
        }

    // --------------------------CONSULTAS---------------------------

    // A LOGICA DE CONSULTAS ESTÁ TODA AQUI E NÃO NUMA CLASSE "GERENCIADOR CONSULTAS" POIS GERARIA UMA DEPENDENCIA MUITO GRANDE
    // DO GERENCIADOR CLINICA PARA CONSEGUIR LER E ESCREVER NOS ARQUIVOS, O QUE RESULTARIA NUM FEATURE ENVY 

        public boolean agendarConsulta(Medico medico, Paciente paciente, String data) throws ClinicaException {
        // Inicializa a consulta se estiver vazia
        if (medico.getAgendaConsultas() == null) {
            Consulta c = new Consulta(medico, paciente, data);
            medico.adicionarConsulta(c);
        }

        // Conta quantas consultas já existem nesse dia
        int consultasNoDia = 0;
        for (Modelo.Consulta c : medico.getAgendaConsultas()) { // Esse "Modelo" é pra garantir o package da classe Consulta
            // Adiciona 1 ao contador de consultas se a consulta for no dia
            if (c.getDataConsulta().equals(data)) { 
                consultasNoDia++;
            }
        }

        if (consultasNoDia >= 3) {
            return false; // Lotado
        }

        // Se tiver vaga, cria e salva
        Modelo.Consulta novaConsulta = new Modelo.Consulta(medico, paciente, data); // Esse "Modelo" é pra garantir o package da classe Consulta
        medico.getAgendaConsultas().add(novaConsulta);

        // Salva a alteração no arquivo, já que o estado do médico foi mudado
        salvarArquivoMedicos(this.medicos);
        
        return true;
    }

    }
