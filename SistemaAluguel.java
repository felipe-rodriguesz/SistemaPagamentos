import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SistemaAluguel extends JFrame {

    public SistemaAluguel() {
        setTitle("Sistema de Aluguel");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Painel de Abas
        JTabbedPane tabbedPane = new JTabbedPane();

        // Painel do Cliente
        JPanel clientePanel = criarPainelCliente();
        tabbedPane.addTab("Cliente", clientePanel);

        // Painel do Administrador
        JPanel adminPanel = criarPainelAdministrador();
        tabbedPane.addTab("Administrador", adminPanel);

        add(tabbedPane);
    }

    // Criando painel de Cliente
    private JPanel criarPainelCliente() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));

        adicionarBotao(panel, "Fazer Reserva");
        adicionarBotao(panel, "Alugar Item");
        adicionarBotao(panel, "Renovar Aluguel");
        adicionarBotao(panel, "Cancelar Aluguel");
        adicionarBotao(panel, "Consultar Histórico");

        return panel;
    }

    // Criando painel do Administrador
    private JPanel criarPainelAdministrador() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));

        adicionarBotao(panel, "Gerenciar Aluguéis");
        adicionarBotao(panel, "Gerenciar Pagamentos");
        adicionarBotao(panel, "Gerar Relatórios");

        return panel;
    }

    // Método para adicionar botões com ação
    private void adicionarBotao(JPanel panel, String titulo) {
        JButton button = new JButton(titulo);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Ação: " + titulo, "Informação", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        panel.add(button);
    }

    // Método principal para executar o programa
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SistemaAluguel().setVisible(true);
        });
    }
}