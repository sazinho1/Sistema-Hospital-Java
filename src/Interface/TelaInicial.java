package Interface;

import java.awt.*;

import javax.print.attribute.standard.MediaSize.NA;
import javax.swing.*;

import Controlador.GerenciadorClinica;
import Modelo.ClinicaException;
import Modelo.Paciente;

public class TelaInicial extends JFrame {

    // O CardLayout é pra gerenciar a troca entre cadastro e login
    private CardLayout cardLayout;
    private JPanel painelPrincipal;

    private GerenciadorClinica gerenciador;
    private java.util.ArrayList<Paciente> listaDePacientes; //Tem que tar escrito o java util pros botoes funcionarem
    private java.util.ArrayList<Modelo.Medico> listaDeMedicos;

    public TelaInicial() {
        // config basica da janela
        setTitle("Clínica Face - Bem-vindo");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centraliza

        // inicializa o cardlayout e o painel principal
        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);
        gerenciador = new GerenciadorClinica();
        
        // Carrega os dados dos arquivos assim que a janela abre
        listaDePacientes = gerenciador.carregarArquivoPacientes();
        listaDeMedicos = gerenciador.carregarArquivoMedicos();

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
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS)); //pra empilhar verticalmente
        painel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 40)); //margem

        // Título
        JLabel titulo = new JLabel("Clínica Face");
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Radio Button é pra escolher entre medico ou paciente
        JRadioButton radioMedico = new JRadioButton("Médico");
        JRadioButton radioPaciente = new JRadioButton("Paciente");
        // Pra garantir que eles não serao apertados juntos, cria um grupo (tipo alternativas)
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(radioMedico);
        grupo.add(radioPaciente);
        // Alinha
        radioMedico.setAlignmentX(LEFT_ALIGNMENT);
        radioPaciente.setAlignmentX(LEFT_ALIGNMENT);
        
        // Campos
        JTextField txtUser = new JTextField();
        JPasswordField txtSenha = new JPasswordField();
        
        // Botões basicos
        JButton btnLogin = new JButton("Login");
        JButton btnIrParaCadastro = new JButton("Não possuo Cadastro");
        JButton btnSair = new JButton("Sair");
        btnSair.setAlignmentX(LEFT_ALIGNMENT);

        AJEITAR ALINHAMENTO INTERFACE PQ AINDA NAO TA Q NEM NA FT

        // Pra trocar pro "Não tenho cadastro"
        btnIrParaCadastro.addActionListener(e -> cardLayout.show(painelPrincipal, "TELA_CADASTRO"));
        
        // Pra sair
        btnSair.addActionListener(e -> System.exit(0));

        // Adicionando os componentes no painel principal
        painel.add(titulo);
        painel.add(Box.createVerticalStrut(20)); // Espaço
        painel.add(radioMedico);
        painel.add(radioPaciente);
        painel.add(new JLabel("User:"));
        painel.add(txtUser);
        painel.add(new JLabel("Senha:"));
        painel.add(txtSenha);
        painel.add(Box.createVerticalStrut(20));
        painel.add(btnLogin);
        painel.add(btnIrParaCadastro); // Botão de troca
        painel.add(btnSair);

        return painel;
    }

    private JPanel criarPainelCadastro() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel titulo = new JLabel("Cadastro - Clínica Face");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Pega os textos pra salvar no arquivo
        JTextField txtNome = new JTextField();
        JTextField txtLogin = new JTextField();
        JPasswordField txtSenha = new JPasswordField();
        JTextField txtIdade = new JTextField();

        // Adiciona eles visualmente no painel
        painel.add(new JLabel("Nome Completo:"));
        painel.add(txtNome);

        painel.add(new JLabel("Idade:"));
        painel.add(txtIdade);

        painel.add(new JLabel("Login de Acesso:"));
        painel.add(txtLogin);

        painel.add(new JLabel("Senha:"));
        painel.add(txtSenha);

        painel.add(Box.createVerticalStrut(20));

        JButton btnCadastrar = new JButton("Cadastrar");
        //Logica do botão de cadastrar 
        btnCadastrar.addActionListener(e -> {
    
            try {
                String nome = txtNome.getText();
                String login = txtLogin.getText();
                String senha = new String(txtSenha.getPassword());
                int idade = Integer.parseInt(txtIdade.getText()); // Cuidado: isso pode dar erro se digitar letras!
                String plano = null; FAlTANDO IMPLEMENTAR A LOGICA COMPLETA PARA ESSE PLANO
                

                Paciente novoPaciente = new Paciente(nome, login, senha, plano, idade);
                listaDePacientes.add(novoPaciente);
                gerenciador.salvarArquivoPacientes(listaDePacientes);

                // Se chegou aqui, é pq deu certo, ai ele puxa um painel com o retorno positivo
                JOptionPane.showMessageDialog(null,"Paciente cadastrado com sucesso!","Sucesso",JOptionPane.INFORMATION_MESSAGE);

                // Opcional: Limpar os campos ou voltar para o login
                txtNome.setText("");
                cardLayout.show(painelPrincipal,"TELA_CADASTRO");

            } catch (ClinicaException erro) {
                JOptionPane.showMessageDialog(null,erro.getMessage(),"Erro no Sistema",JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException erroNumero) {
                //Se o usuário digitar letras no campo idade
                JOptionPane.showMessageDialog(null,"Por favor, digite uma idade válida (apenas números).","Erro de Digitação",JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton btnVoltarLogin = new JButton("Já tenho Login");
        JButton btnSair = new JButton("Sair");

        // Pra voltar pro login
        btnVoltarLogin.addActionListener(e -> cardLayout.show(painelPrincipal, "TELA_LOGIN"));
        
        btnSair.addActionListener(e -> System.exit(0));

        painel.add(titulo);
        painel.add(Box.createVerticalStrut(20));
        painel.add(btnCadastrar);
        painel.add(btnVoltarLogin); // Botão de troca
        painel.add(btnSair);

        return painel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TelaInicial().setVisible(true);
        });
    }
}