package Interface;

import Controlador.GerenciadorClinica;
import Modelo.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TelaPrincipal extends JFrame {

    // Abas para navegação
    private JTabbedPane abas;

    // Instancia do gerenciador para pegar os medicos e pacientes atualizados
    private GerenciadorClinica gerenciador;

    
    // Construtor pro paciente
    public TelaPrincipal(Paciente p, GerenciadorClinica ger) {
        this.gerenciador = ger;
        configurarJanela();
        
        JLabel lblBoasVindas = new JLabel("Olá, " + p.getNome() + ". Seu plano: " + (p.getPlanoSaude() == null ? "Particular" : p.getPlanoSaude()));
        lblBoasVindas.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lblBoasVindas.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblBoasVindas, BorderLayout.NORTH);

        abas = new JTabbedPane();
        
        // Pra agendar consulta
        JPanel painelAgendar = new JPanel(new BorderLayout());
        
        // Painel de bvsca
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtBusca = new JTextField(20);
        JButton btnBuscar = new JButton("Filtrar por Nome/Especialidade");
        painelBusca.add(new JLabel("Buscar:"));
        painelBusca.add(txtBusca);
        painelBusca.add(btnBuscar);
        painelAgendar.add(painelBusca, BorderLayout.NORTH);

        // Tabela de medicos em colunas
        String[] colunas = {"Nome do Médico", "Especialidade", "Plano Atendido"};
        // O DefaultTableModel é pra guardar os dados da tabela
        DefaultTableModel modelodaTabela = new DefaultTableModel(colunas, 0) {
            @Override // Pra bloquear a edição das células
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        JTable tabelaMedicos = new JTable(modelodaTabela);
        JScrollPane scrollTabela = new JScrollPane(tabelaMedicos);
        painelAgendar.add(scrollTabela, BorderLayout.CENTER);

        // Botão pra agendar
        JButton btnRealizarAgendamento = new JButton("Agendar Consulta com Selecionado");
        JPanel painelBotao = new JPanel();
        painelBotao.add(btnRealizarAgendamento);
        painelAgendar.add(painelBotao, BorderLayout.SOUTH);

        // Logica pra preencher a tabela
        // Runnable é uma função executada num thread durante a execução (gemini falou pra usar -> tentei entender mas nn consegui e funcionou assim)
        Runnable preencherTabela = () -> {
            modelodaTabela.setRowCount(0); // Limpa a tabela
            String palavraDigitada = txtBusca.getText().toUpperCase();

            for (Medico m : gerenciador.getMedicos()) {
                // Logica pra pular o medico se ele tem o plano diferente em relação ao do paciente
                if (p.getPlanoSaude() != null && !p.getPlanoSaude().equalsIgnoreCase(m.getPlanoSaude())) {
                    continue; 
                }

                // Filtro da busca
                if (m.getNome().toUpperCase().contains(palavraDigitada) || m.getEspecialidade().toUpperCase().contains(palavraDigitada)) {
                    Object[] linha = { m.getNome(), m.getEspecialidade(), m.getPlanoSaude() }; // Constroi a linha
                    modelodaTabela.addRow(linha); // Add ela na tabela
                }
            }
        };

        // Chama a função pela primeira vez pra ela carregar os dados
        preencherTabela.run();
        
        // Faz o botão buscar funcionar
        btnBuscar.addActionListener(e -> preencherTabela.run());

        // Botão de agendamento
        btnRealizarAgendamento.addActionListener(e -> {
            // Verifica se tem linha selecionada
            int linhaSelecionada = tabelaMedicos.getSelectedRow();
            if (linhaSelecionada == -1) {
                JOptionPane.showMessageDialog(null, "Selecione um médico na tabela primeiro!");
                return;
            }

            // Pega o nome do medico na tabela para buscar o objeto real
            String nomeMedico = (String) modelodaTabela.getValueAt(linhaSelecionada, 0);
            String especialidade = (String) modelodaTabela.getValueAt(linhaSelecionada, 1);

            // Buscando o objeto do medico na lista do gerenciador
            Medico medicoAlvo = null;
            for (Medico m : gerenciador.getMedicos()) {
                // Comparando ignorando maiúsculas e minúsculas pra garantir (nome + especialidade)
                if (m.getNome().equalsIgnoreCase(nomeMedico) && m.getEspecialidade().equalsIgnoreCase(especialidade)) {
                    medicoAlvo = m;
                    break;
                }
            }

            // Se por algum milagre não achar o médico
            if (medicoAlvo == null) {
                JOptionPane.showMessageDialog(null, "Erro: Médico não encontrado no sistema!");
                return;
            }

            // Pedindo a data
            String dataDigitada = JOptionPane.showInputDialog("Digite a data da consulta (DD/MM/AAAA):");

             // Se o usuário cancelar ou deixar vazio, não faz nada
            if (dataDigitada == null || dataDigitada.isEmpty()) {
                return;
            }

            try {
                boolean agendou = gerenciador.agendarConsulta(medicoAlvo, p, dataDigitada);

                if (agendou) {
                    JOptionPane.showMessageDialog(null, "Consulta Confirmada para " + dataDigitada + "!");
                } else {
                     // Se retornou false, é pq tem 3 ou mais
                    int resp = JOptionPane.showConfirmDialog(null,
                            "Agenda cheia para esta data! Deseja entrar na Lista de Espera?",
                            "Lotado", JOptionPane.YES_NO_OPTION);

                    if (resp == JOptionPane.YES_OPTION) {
                        // Adiciona na estrutura de espera do médico
                        gerenciador.colocarNaListaDeEspera(medicoAlvo, p, dataDigitada);
                        JOptionPane.showMessageDialog(null, "Você entrou na lista de espera para o dia " + dataDigitada + ".\nSe alguém cancelar, você será encaixado automaticamente.");
                    }
                }
            } catch (ClinicaException ex) {
                JOptionPane.showMessageDialog(null, "Erro ao salvar: " + ex.getMessage());
            }
        });

        abas.addTab("Agendar Consulta", painelAgendar);

        // Aba de consultas
        JPanel painelConsultas = new JPanel(new BorderLayout());

        // Colunas: Médico, Especialidade, Data, Status
        String[] colunasMinhas = {"Médico", "Especialidade", "Data", "Status"};
        DefaultTableModel modeloMinhas = new DefaultTableModel(colunasMinhas, 0) {
            // Pra não poder mecher de lugar na interface
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        JTable tabelaMinhas = new JTable(modeloMinhas);
        painelConsultas.add(new JScrollPane(tabelaMinhas), BorderLayout.CENTER);

        // Painel de Botões (Cancelar e Avaliar)
        JPanel painelBotoesSul = new JPanel();
        JButton btnAtualizarMinhas = new JButton("Atualizar");
        JButton btnCancelar = new JButton("Cancelar Selecionada");
        JButton btnAvaliar = new JButton("Avaliar Médico"); // Implementar depois se der tempo!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        painelBotoesSul.add(btnAtualizarMinhas);
        painelBotoesSul.add(btnCancelar);
        painelBotoesSul.add(btnAvaliar);
        painelConsultas.add(painelBotoesSul, BorderLayout.SOUTH);

       // Função para carregar as consultas DESTE paciente
        Runnable carregarMinhasConsultas = () -> {
            modeloMinhas.setRowCount(0);
            // Varre todos os médicos para achar consultas deste paciente (p)
            for (Medico m : gerenciador.getMedicos()) {
                if (m.getAgendaConsultas() != null) {
                    for (Consulta c : m.getAgendaConsultas()) {
                       // Se a consulta é deste paciente logado
                        if (c.getPacienteConsultado().getNome().equals(p.getNome())
                                && c.getPacienteConsultado().getLogin().equals(p.getLogin())) {

                            modeloMinhas.addRow(new Object[]{
                                m.getNome(),
                                m.getEspecialidade(),
                                c.getDataConsulta(),
                                c.getStatus()
                            });
                        }
                    }
                }
            }
        };

        // Ação do Botão Atualizar
        btnAtualizarMinhas.addActionListener(e -> carregarMinhasConsultas.run());

        // Carrega logo ao abrir
        carregarMinhasConsultas.run();

        // Ação do botão de cancelar
        btnCancelar.addActionListener(e -> {
            int linha = tabelaMinhas.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(null, "Selecione uma consulta para cancelar.");
                return;
            }

            // Recuperando o objeto Consulta pelo conteudo da linha
            String nomeMedico = (String) modeloMinhas.getValueAt(linha, 0);
            String data = (String) modeloMinhas.getValueAt(linha, 2);

            // Busca a consulta no sistema
            Consulta consultaAlvo = null;
            for (Medico m : gerenciador.getMedicos()) {
                if (m.getNome().equals(nomeMedico)) {
                    for (Consulta c : m.getAgendaConsultas()) {
                        if (c.getDataConsulta().equals(data) && c.getPacienteConsultado().getLogin().equals(p.getLogin())) {
                            consultaAlvo = c;
                            break;
                        }
                    }
                }
            }

            if (consultaAlvo != null) {
                if (consultaAlvo.getStatus().equals("Finalizada")) {
                    JOptionPane.showMessageDialog(null, "Não é possível cancelar consultas já finalizadas!");
                    return;
                }

                try {
                    // CHAMA O GERENCIADOR PARA CANCELAR E RODAR A FILA DE ESPERA
                    boolean cancelou = gerenciador.cancelarConsulta(consultaAlvo);
                    if (cancelou) {
                        JOptionPane.showMessageDialog(null, "Consulta cancelada com sucesso!");
                        carregarMinhasConsultas.run(); // Atualiza a tabela visual
                    }
                } catch (ClinicaException ex) {
                    JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
                }
            }
        });

        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        btnAvaliar.addActionListener(e -> {
            int linha = tabelaMinhas.getSelectedRow();
            if (linha == -1) {
                JOptionPane.showMessageDialog(null, "Selecione uma consulta FINALIZADA para avaliar.");
                return;
            }
            
            String status = (String) modeloMinhas.getValueAt(linha, 3);
            if (!status.equalsIgnoreCase("Finalizada")) {
                JOptionPane.showMessageDialog(null, "Você só pode avaliar consultas que já aconteceram (Finalizadas)!");
                return;
            }

            String notaStr = JOptionPane.showInputDialog("Dê uma nota de 1 a 5 para o atendimento:");
            if(notaStr != null && !notaStr.isEmpty()){
                JOptionPane.showMessageDialog(null, "Avaliação registrada! Obrigado.");
                // Aqui você poderia salvar a nota no objeto Medico se quisesse ir além
            }
        });

        abas.addTab("Minhas Consultas", painelConsultas);

        add(abas, BorderLayout.CENTER);
    }


    // Construtor pra medico
    public TelaPrincipal(Medico m, GerenciadorClinica gerenciador) {
        configurarJanela();

        JLabel lblBoasVindas = new JLabel("Painel Médico - Dr(a). " + m.getNome());
        lblBoasVindas.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lblBoasVindas.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblBoasVindas, BorderLayout.NORTH);

        // Pra criar as abas do medico
        abas = new JTabbedPane();

        // Aba da agenda do medico
        JPanel painelAgenda = new JPanel(new BorderLayout());
        
        // Tabela com a agenda do medico
        String[] colunas = {"Data", "Paciente", "Status"};
        javax.swing.table.DefaultTableModel modeloAgenda = new javax.swing.table.DefaultTableModel(colunas, 0);
        JTable tabelaAgenda = new JTable(modeloAgenda);
        painelAgenda.add(new JScrollPane(tabelaAgenda), BorderLayout.CENTER);

        // Botao pra atualizar a lista
        JButton btnAtualizar = new JButton("Atualizar Lista");
        painelAgenda.add(btnAtualizar, BorderLayout.NORTH);

        // Lógica pra preencher a tabela
        Runnable atualizarAgenda = () -> {
            modeloAgenda.setRowCount(0); // Limpa
            if (m.getAgendaConsultas() != null) {
                for (Modelo.Consulta c : m.getAgendaConsultas()) { // Esse "Modelo" é pra garantir o package da classe Consulta
                    // Adiciona na tabela
                    Object[] linha = { c.getDataConsulta(), c.getPacienteConsultado().getNome(), c.getStatus() };
                    modeloAgenda.addRow(linha);
                }
            }
        };
        // Carrega ao abrir
        atualizarAgenda.run();

        // Botão de realizar a consulta
        JButton btnRealizar = new JButton("Realizar Consulta (Finalizar)");
        painelAgenda.add(btnRealizar, BorderLayout.SOUTH);

        btnRealizar.addActionListener(e -> {
            int linha = tabelaAgenda.getSelectedRow();
            
            // Verifica se tem linha selecionada (-1 significa "nada selecionado")
            if (linha == -1) {
                JOptionPane.showMessageDialog(null, "Por favor, clique em uma linha da tabela para selecionar a consulta!");
                return;
            }

           // Verifica se a lista de consultas não está vazia (Segurança extra)
            if (m.getAgendaConsultas() == null || m.getAgendaConsultas().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Não há consultas na agenda.");
                return;
            }

            // Verifica se o indice é valido para evitar erro de sincronia
            if (linha >= m.getAgendaConsultas().size()) {
                JOptionPane.showMessageDialog(null, "Erro de sincronia: Atualize a lista!");
                return;
            }

            Modelo.Consulta consultaSelecionada = m.getAgendaConsultas().get(linha);

            if (consultaSelecionada.getStatus().equals("Finalizada")) {
                JOptionPane.showMessageDialog(null, "Essa consulta já foi finalizada!");
                return;
            }

            realizarConsulta(m, linha);
        });


        abas.addTab("Minha Agenda", painelAgenda);
        
        // Painel de admin para cadastro de novos médicos (SÓ É ABERTO COM A SENHA 012345)
        JPanel painelAdmin = new JPanel(new FlowLayout());
        painelAdmin.add(new JLabel("Cadastro de novo medico"));
        JButton btnCadastrarMedico = new JButton("Cadastrar Novo Médico");
        
        
        btnCadastrarMedico.addActionListener(e -> {
            // Cria um campo de senha para o pop-up
            JPasswordField pwdAdmin = new JPasswordField();
            Object[] message = {
                "Para cadastrar um novo médico, insira a senha de Administrador:", 
                pwdAdmin
            };

            // Mostra o pop-up
            int opcao = JOptionPane.showConfirmDialog(null, message, "Acesso Restrito", JOptionPane.OK_CANCEL_OPTION);

            if (opcao == JOptionPane.OK_OPTION) {
                String senhaDigitada = new String(pwdAdmin.getPassword());
                
                // Verifica a senha
                if (senhaDigitada.equals("012345")) {
                    mostrarCadastroMedico(gerenciador); // Libera o acesso
                } else {
                    JOptionPane.showMessageDialog(null, "Senha incorreta! Acesso negado.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        painelAdmin.add(btnCadastrarMedico);
        abas.addTab("Administração", painelAdmin);

        add(abas, BorderLayout.CENTER);
    }

    private void mostrarCadastroMedico(GerenciadorClinica gerenciador) {
        JTextField txtNome = new JTextField();
        JTextField txtLogin = new JTextField();
        JTextField txtSenha = new JTextField(); // Mesma coisa da tela inicial: txt ao inves de password pro cara poder ler oq ele ta escrevendo
        JTextField txtPlano = new JTextField();

        // Lista de especialidades da clínica
        String[] especialidades = {
            "DERMATOLOGISTA", 
            "ENDOCRINOLOGISTA", 
            "NUTRICIONISTA", 
            "INFECTOLOGISTA", 
            "CIRURGIA(O) PLASTICA(O)"
        };
       // Pra criar a caixa de seleção com essas opções
        JComboBox<String> comboEspecialidade = new JComboBox<>(especialidades);

        // Montando o pop-up
        Object[] message = {
            "Nome Completo:", txtNome,
            "Especialidade:", comboEspecialidade, // Pra adicionar o combo em vez de texto
            "Plano Atendido:", txtPlano,
            "Login:", txtLogin,
            "Senha:", txtSenha
        };

        int option = JOptionPane.showConfirmDialog(null, message, "Cadastrar Novo Médico", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                if (txtNome.getText().isEmpty() || txtLogin.getText().isEmpty() || txtSenha.getText().isEmpty()) {
                     JOptionPane.showMessageDialog(null, "Preencha todos os dados obrigatórios!");
                     return;
                }

               // Cria o medico propriamente dito
                Medico novoMedico = new Medico(
                    txtNome.getText().toUpperCase(), 
                    txtLogin.getText().toUpperCase(), 
                    txtSenha.getText().toUpperCase(), 
                    (String) comboEspecialidade.getSelectedItem(), // Pega o item selecionado da lista
                    txtPlano.getText().toUpperCase()
                );

                gerenciador.getMedicos().add(novoMedico); 
                gerenciador.salvarArquivoMedicos(gerenciador.getMedicos()); 

                JOptionPane.showMessageDialog(null, "Médico(a) " + novoMedico.getNome() + " cadastrado(a)!");

            } catch (ClinicaException ex) {
                JOptionPane.showMessageDialog(null, "Erro ao salvar: " + ex.getMessage());
            } catch (NullPointerException e){
                JOptionPane.showMessageDialog(null, "Erro ao salvar: " + e.getMessage());
            }
        }
    }

    public void realizarConsulta(Medico medicoSelecionado, int linha) {
        // Esse "Modelo" é pra garantir o package da classe Consulta
        Modelo.Consulta consultaSelecionada = medicoSelecionado.getAgendaConsultas().get(linha); 

        if (consultaSelecionada.getStatus().equals("Finalizada")) {
            JOptionPane.showMessageDialog(null, "Essa consulta já foi finalizada!");
            return;
        }

        // Abre janela pra escrever o relatório
        String relatorio = JOptionPane.showInputDialog("Descreva sintomas e tratamento:");
        if (relatorio != null && !relatorio.isEmpty()) {
            consultaSelecionada.setRelatorio(relatorio);
            consultaSelecionada.setStatus("Finalizada");
            
            // Logica do pagamento
            String mensagemPagamento = "";
            if (consultaSelecionada.getPacienteConsultado().getPlanoSaude() == null) {
                double valor = 0.0;
                // Define o valor baseada na especialidade do Médico atual
                switch (medicoSelecionado.getEspecialidade().toUpperCase()) {
                    case "DERMATOLOGISTA": valor = 300.00; break;
                    case "ENDOCRINOLOGISTA": valor = 280.00; break;
                    case "NUTRICIONISTA": valor = 200.00; break;
                    case "INFECTOLOGISTA": valor = 350.00; break;
                    case "CIRURGIA(O) PLASTICA(O)": valor = 500.00; break; 
                    default: valor = 250.00; 
                }
                mensagemPagamento = String.format("\n\nPACIENTE PARTICULAR!\nValor: R$ %.2f", valor);
            } else {
                mensagemPagamento = "\n\nPaciente com plano: " + consultaSelecionada.getPacienteConsultado().getPlanoSaude() + "\nCobrança enviada ao convênio.";
            }

            JOptionPane.showMessageDialog(null, "Consulta Finalizada com Sucesso!" + mensagemPagamento);
            
            try {
                // Se não salvar, quando fechar o programa a consulta volta a ser "Agendada"
                gerenciador.salvarArquivoMedicos(gerenciador.getMedicos());
            } catch (ClinicaException e) {
                JOptionPane.showMessageDialog(null, "Erro ao salvar alteração: " + e.getMessage());
            }
        }
    }

    // Metodo pra configurar o basico da janela
    private void configurarJanela() {
        setTitle("Sistema Clínica Face");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Botao de sair
        JButton btnSair = new JButton("Logout");
        btnSair.addActionListener(e -> {
            dispose(); 
            new TelaInicial().setVisible(true); 
            // Fecha a janela atual e abre a do login
        });
        
        // Painel pro botao ficar no canto
        JPanel painelSul = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelSul.add(btnSair);
        add(painelSul, BorderLayout.SOUTH);
    }
}