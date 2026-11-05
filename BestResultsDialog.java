import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public final class BestResultsDialog {
    private BestResultsDialog() {
    }

    public static void show(Component parent) {
        List<HighScoreStore.Result> results;
        try {
            results = new HighScoreStore().readResults();
        } catch (IOException exception) {
            JOptionPane.showMessageDialog(
                    parent,
                    "Nie udało się odczytać pliku z wynikami:\n" + exception.getMessage(),
                    "Błąd odczytu wyników",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Window owner = parent == null ? null : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Najlepsze wyniki", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        content.setBackground(new Color(238, 238, 238));

        JLabel title = new JLabel("Najlepsze wyniki");
        title.setFont(new Font(Font.DIALOG, Font.BOLD, 22));
        title.setForeground(new Color(55, 55, 55));
        content.add(title, BorderLayout.NORTH);

        if (results.isEmpty()) {
            JLabel emptyLabel = new JLabel("Brak zapisanych wyników.");
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            content.add(emptyLabel, BorderLayout.CENTER);
        } else {
            String[] columns = {"Seed", "Poziomy", "Czas", "Punkty", "Śmierci"};
            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            for (HighScoreStore.Result result : results) {
                model.addRow(new Object[]{
                        result.getSeed(),
                        result.getLevelCount(),
                        String.format(Locale.ROOT, "%.2fs", result.getElapsedMillis() / 1000.0),
                        result.getPoints(),
                        result.getDeaths()
                });
            }

            JTable table = new JTable(model);
            table.setFillsViewportHeight(true);
            table.setRowHeight(26);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(560, 280));
            content.add(scrollPane, BorderLayout.CENTER);
        }

        JButton closeButton = new JButton("Zamknij");
        closeButton.addActionListener(event -> dialog.dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        content.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setContentPane(content);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
