package Interface;

import Controlador.GerenciadorClinica;
import Modelo.ClinicaException;
import Modelo.Medico;
import Modelo.Paciente;
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

        // Ação do Botão Agendar (Faremos isso no próximo passo, por enquanto só avisa)!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        btnRealizarAgendamento.addActionListener(e -> {
            int linhaSelecionada = tabelaMedicos.getSelectedRow();
            if (linhaSelecionada == -1) {
                JOptionPane.showMessageDialog(null, "Selecione um médico na tabela primeiro!");
            } else {
                String nomeMedico = (String) modelodaTabela.getValueAt(linhaSelecionada, 0);
                JOptionPane.showMessageDialog(null, "Você escolheu: " + nomeMedico + "\n(Próximo passo: Abrir calendário)");
            }
        });

        abas.addTab("Agendar Consulta", painelAgendar);

        // Aba de consultas
        JPanel painelConsultas = new JPanel();
        painelConsultas.add(new JLabel("Aqui aparecerão seus agendamentos..."));
        abas.addTab("Minhas Consultas", painelConsultas);

        add(abas, BorderLayout.CENTER);

        //
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
        JPanel painelAgenda = new JPanel();
        painelAgenda.add(new JLabel("Aqui você verá sua agenda do dia..."));//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
            "CIRURGIÃ(O) PLASTICA(O)"
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

                gerenciador.carregarArquivoMedicos().add(novoMedico); 
                gerenciador.salvarArquivoMedicos(gerenciador); // TA DANDO ERRO AQUI

                JOptionPane.showMessageDialog(null, "Médico(a) " + novoMedico.getNome() + " cadastrado(a)!");

            } catch (ClinicaException ex) {
                JOptionPane.showMessageDialog(null, "Erro ao salvar: " + ex.getMessage());
            } catch (NullPointerException e){
                JOptionPane.showMessageDialog(null, "Erro ao salvar: " + e.getMessage());
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