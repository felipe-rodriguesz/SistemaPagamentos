package main.java.com.SistemadePagamentoeAluguel.views;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import main.java.com.SistemadePagamentoeAluguel.controllers.RelatorioController;
import main.java.com.SistemadePagamentoeAluguel.models.DateRange;
import main.java.com.SistemadePagamentoeAluguel.models.Relatorio;

public class AdministradorView extends JFrame {
    private final RelatorioController relatorioController = new RelatorioController();
    private JComboBox<String> tipoRelatorioComboBox;
    private JButton btnGerarRelatorio;
    private JTextArea areaRelatorio;

    public AdministradorView() {
        configurarJanela();
        inicializarComponentes();
    }

    private void configurarJanela() {
        setTitle("Painel Administrativo");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel de Controle
        JPanel painelControle = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tipoRelatorioComboBox = new JComboBox<>(new String[]{"Aluguéis", "Pagamentos"});
        btnGerarRelatorio = new JButton("Gerar Relatório");
        
        btnGerarRelatorio.addActionListener(this::gerarRelatorio);
        painelControle.add(new JLabel("Tipo de Relatório:"));
        painelControle.add(tipoRelatorioComboBox);
        painelControle.add(btnGerarRelatorio);

        // Área de Texto
        areaRelatorio = new JTextArea();
        areaRelatorio.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaRelatorio);

        painelPrincipal.add(painelControle, BorderLayout.NORTH);
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);

        add(painelPrincipal);
    }

    public boolean solicitarLogin() {
        JPanel painelLogin = new JPanel(new GridLayout(3, 2));
        JTextField campoUsuario = new JTextField();
        JPasswordField campoSenha = new JPasswordField();

        painelLogin.add(new JLabel("Usuário:"));
        painelLogin.add(campoUsuario);
        painelLogin.add(new JLabel("Senha:"));
        painelLogin.add(campoSenha);

        int resultado = JOptionPane.showConfirmDialog(
            this,
            painelLogin,
            "Login Administrativo",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (resultado == JOptionPane.OK_OPTION) {
            String usuario = campoUsuario.getText();
            char[] senha = campoSenha.getPassword();
            return validarCredenciais(usuario, senha);
        }
        return false;
    }

    private boolean validarCredenciais(String usuario, char[] senha) {
        // Mock - Substituir por validação real
        boolean valido = "admin".equals(usuario) && new String(senha).equals("admin123");
        java.util.Arrays.fill(senha, '0'); // Limpar senha da memória
        return valido;
    }

    private void gerarRelatorio(ActionEvent e) {
        try {
            DateRange periodo = obterPeriodo();
            String tipo = ((String) tipoRelatorioComboBox.getSelectedItem()).toLowerCase();
            
            Relatorio relatorio = relatorioController.gerarRelatorio(tipo, periodo);
            exibirRelatorio(relatorio);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private DateRange obterPeriodo() {
        // Implementar seletor de datas (ex: JDatePicker)
        return new DateRange(new Date(), new Date()); // Mock
    }

    private void exibirRelatorio(Relatorio relatorio) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RELATÓRIO ===\n");
        sb.append("ID: ").append(relatorio.getId()).append("\n");
        sb.append("Tipo: ").append(relatorio.getTipo()).append("\n");
        sb.append("Data: ").append(relatorio.getDataGeracao()).append("\n\n");
        relatorio.getDados().forEach(dado -> sb.append(dado).append("\n"));
        
        areaRelatorio.setText(sb.toString());
    }

    public void exibirPainelControle() {
        setVisible(true);
    }

    protected Relatorio gerarRelatorioPersonalizado(String tipo, DateRange periodo) {
        List<String> dadosRelatorio = new ArrayList<>();
        dadosRelatorio.add("=== Relatório " + tipo + " ===");
        dadosRelatorio.add("Período: " + periodo.getInicio() + " até " + periodo.getFim());
        dadosRelatorio.add("Data de geração: " + new Date());
        dadosRelatorio.add("==================");
        int id = relatorioController.gerarIdRelatorio();

        return new Relatorio(id, tipo, dadosRelatorio, new Date());
    }
}