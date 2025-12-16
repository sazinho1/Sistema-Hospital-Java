package Interface;

import Controlador.GerenciadorClinica;
import Modelo.ClinicaException;
import Modelo.Medico;
import Modelo.Paciente;
import java.awt.*;
import javax.swing.*;

public class TelaInicial extends JFrame {

    // O CardLayout é pra gerenciar a troca entre cadastro e login
    private final CardLayout cardLayout;
    private final JPanel painelPrincipal;

    private final GerenciadorClinica gerenciador;
    private final java.util.ArrayList<Paciente> listaDePacientes; //Tem que tar escrito o java util pros botoes funcionarem
    private final java.util.ArrayList<Medico> listaDeMedicos;

    public TelaInicial() {
        // config basica da janela
        setTitle("Clínica Face - Bem-vindo");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centraliza

        // inicializa o cardlayout e o painel principal
        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);
        
        // Carrega os dados dos arquivos assim que a janela abre
        gerenciador = new GerenciadorClinica();
        listaDePacientes = gerenciador.getPacientes(); 
        listaDeMedicos = gerenciador.getMedicos();

        // cria os paineis (cartas)
        JPanel cartaLogin = criarPainelLogin();
        JPanel cartaCadastro = criarPainelCadastro();

        // adiciona as cartas e o apelido delas
        painelPrincipal.add(cartaLogin, "TELA_LOGIN");
        painelPrincipal.add(cartaCadastro, "TELA_CADASTRO");
        add(painelPrincipal);

        // mostra a primeira tela
        cardLayout.show(painelPrincipal, "TELA_LOGIN");
    }

    // ------------------- metodos pra desenhar as telas -------------------
   private JPanel criarPainelLogin() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Título
        JLabel titulo = new JLabel("Clínica Face");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(titulo);
        painel.add(Box.createVerticalStrut(20));

        // Seleção do tipo
        JPanel linhaTipo = new JPanel(new FlowLayout(FlowLayout.CENTER)); // FOI USADO FLOW LAYOUT PQ FICARIA MAIS ADEQUADO PARA WINDOWS, APESAR DO LABCOMP SER LINUX
        JRadioButton radioMedico = new JRadioButton("Médico");
        JRadioButton radioPaciente = new JRadioButton("Paciente");
        
        // Grupo para garantir que só marca um
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(radioMedico);
        grupo.add(radioPaciente);
        radioPaciente.setSelected(true); // Começa com paciente marcado

        linhaTipo.add(radioMedico);
        linhaTipo.add(radioPaciente);
        painel.add(linhaTipo);

        // Campo de user/login
        JPanel linhaUser = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtUser = new JTextField();
        txtUser.setPreferredSize(new Dimension(200, 25));
        linhaUser.add(new JLabel("Login: "));
        linhaUser.add(txtUser);
        painel.add(linhaUser);

        // Campo Senha
        JPanel linhaSenha = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPasswordField txtSenha = new JPasswordField();
        txtSenha.setPreferredSize(new Dimension(200, 25));
        linhaSenha.add(new JLabel("Senha: "));
        linhaSenha.add(txtSenha);
        painel.add(linhaSenha);
        painel.add(Box.createVerticalStrut(20));

        // Botões
        JPanel linhaBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnLogin = new JButton("Entrar");
        JButton btnIrParaCadastro = new JButton("Cadastrar Paciente");
        JButton btnSair = new JButton("Sair");

        linhaBotoes.add(btnLogin);
        linhaBotoes.add(btnIrParaCadastro);
        linhaBotoes.add(btnSair);
        painel.add(linhaBotoes);

        // Ação do botão de login
        btnLogin.addActionListener(e -> {
            String loginDigitado = txtUser.getText().toUpperCase();
            String senhaDigitada = new String(txtSenha.getPassword()).toUpperCase(); 

            if (radioPaciente.isSelected()) { // Procura o login na lista de pacientes
                boolean achou = false;
                for (Paciente p : listaDePacientes) {
                    if (p.getLogin().equals(loginDigitado) && p.getSenha().equals(senhaDigitada)) {
                        achou = true;
                        JOptionPane.showMessageDialog(null, "Bem-vindo(a), " + p.getNome() + "!");
                        
                        new TelaPrincipal(p, gerenciador).setVisible(true); // Abre a tela principal passando o paciente
                        dispose(); // Fecha a tela de login inicial

                        break;
                    }
                }
                if (!achou) {
                    JOptionPane.showMessageDialog(null, "Login ou Senha incorretos", "Erro", JOptionPane.ERROR_MESSAGE);
                }

            } else {
                 boolean achou = false;
                 for (Medico m : listaDeMedicos) {
                    if (m.getLogin().equals(loginDigitado) && m.getSenha().equals(senhaDigitada)) {
                        achou = true;
                        JOptionPane.showMessageDialog(null, "Olá, Dr(a). " + m.getNome());
                        
                        new TelaPrincipal(m, gerenciador).setVisible(true); // Abre a principal passando o medico
                        dispose(); // Fecha a tela de login
                        
                        break;
                    }
                }
                if (!achou) JOptionPane.showMessageDialog(null, "Médico não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnIrParaCadastro.addActionListener(e -> cardLayout.show(painelPrincipal, "TELA_CADASTRO"));
        btnSair.addActionListener(e -> System.exit(0));

        return painel;
    }

    private JPanel criarPainelCadastro() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Cadastro - Clínica Face");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(titulo);
        painel.add(Box.createVerticalStrut(20));

        // Linha Nome
        JPanel linhaNome = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtNome = new JTextField();
        txtNome.setPreferredSize(new Dimension(250, 25));
        linhaNome.add(new JLabel("Nome Completo:"));
        linhaNome.add(txtNome);
        painel.add(linhaNome);

        // Linha Idade
        JPanel linhaIdade = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtIdade = new JTextField();
        txtIdade.setPreferredSize(new Dimension(50, 25)); // Campo menor pra idade
        linhaIdade.add(new JLabel("Idade:"));
        linhaIdade.add(txtIdade);
        painel.add(linhaIdade);

        // Linha Login
        JPanel linhaLogin = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtLogin = new JTextField();
        txtLogin.setPreferredSize(new Dimension(150, 25));
        linhaLogin.add(new JLabel("Login de Acesso:"));
        linhaLogin.add(txtLogin);
        painel.add(linhaLogin);

        // Linha Senha
        JPanel linhaSenha = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtSenha = new JTextField(); // Não é password field pq se não o user não veria a senha que ele digita
        txtSenha.setPreferredSize(new Dimension(150, 25));
        linhaSenha.add(new JLabel("Senha:"));
        linhaSenha.add(txtSenha);
        painel.add(linhaSenha);

        painel.add(Box.createVerticalStrut(5));

        // Linha Plano
        JPanel linhaPlano = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Logica pra pegar o plano ou dar null:
        JCheckBox chkTemPlano = new JCheckBox("Possuo Plano de Saúde");
        JTextField txtNomePlano = new JTextField();
        txtNomePlano.setPreferredSize(new Dimension(150, 25));
        txtNomePlano.setEnabled(false); // Começa desligado

        // Checando a existencia do plano
        chkTemPlano.addActionListener(n -> {
            txtNomePlano.setEnabled(chkTemPlano.isSelected());
            if (!chkTemPlano.isSelected()) {
                txtNomePlano.setText(""); // Limpa se desmarcar
            }
        });

        linhaPlano.add(chkTemPlano);
        linhaPlano.add(new JLabel("Nome do Plano:"));
        linhaPlano.add(txtNomePlano);
        painel.add(linhaPlano);

        painel.add(Box.createVerticalStrut(20));

        JPanel linhaBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton btnCadastrar = new JButton("Cadastrar");
        //Logica do botão de cadastrar 
        btnCadastrar.addActionListener(e -> {

            try {
                String nome = txtNome.getText();
                String login = txtLogin.getText();
                String senha = txtSenha.getText().toUpperCase();
                int idade = Integer.parseInt(txtIdade.getText());
                String plano = null;
                if (chkTemPlano.isSelected()) {
                    plano = txtNomePlano.getText();
                    if (plano.isEmpty()) { // Pra não deixar salvar plano com nome vazio
                        JOptionPane.showMessageDialog(null, "Digite o nome do plano!");
                        return;
                    }
                }

                Paciente novoPaciente = new Paciente(nome, login, senha, plano, idade);
                listaDePacientes.add(novoPaciente);
                gerenciador.salvarArquivoPacientes(listaDePacientes);

                // Se chegou aqui, é pq deu certo, ai ele puxa um painel com o retorno positivo
                JOptionPane.showMessageDialog(null, "Paciente cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                // Pra limpar todos as caixas do cadastro apos cadastrar
                txtNome.setText("");
                txtLogin.setText("");
                txtSenha.setText("");
                txtIdade.setText("");
                txtNomePlano.setText("");
                
                cardLayout.show(painelPrincipal, "TELA_CADASTRO");

            } catch (ClinicaException erro) {
                JOptionPane.showMessageDialog(null, erro.getMessage(), "Erro no Sistema", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException erroNumero) {
                //Se o usuário digitar letras no campo idade
                JOptionPane.showMessageDialog(null, "Por favor, digite uma idade válida (apenas números).", "Erro de Digitação", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton btnVoltarLogin = new JButton("Já tenho Login");
        JButton btnSair = new JButton("Sair");

        // Pra voltar pro login
        btnVoltarLogin.addActionListener(e -> cardLayout.show(painelPrincipal, "TELA_LOGIN"));

        btnSair.addActionListener(e -> System.exit(0));

        // Adiciona botões na linha auxiliar
        linhaBotoes.add(btnCadastrar);
        linhaBotoes.add(btnVoltarLogin);
        linhaBotoes.add(btnSair);

        // Adiciona a linha de botões no painel principal
        painel.add(linhaBotoes);

        return painel;
    }
}
