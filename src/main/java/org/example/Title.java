package org.example;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Title {
    private JPanel TitleMainTable;
    private JTextField txtJob_title;
    private JButton saveButton;
    private JTable title_table;
    private JButton searchButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JTextField textTitle_Id;
    private JTextField txtSalary;

    protected static void callTitleTable() {
        JFrame frame = new JFrame("Title");
        frame.setContentPane(new Title().TitleMainTable);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


    void table_load() {
        try (Connection connection = Connect.getConnetion();
             PreparedStatement pst = connection.prepareStatement("SELECT * FROM title")) {
            title_table.setModel(new DefaultTableModel(
                    null,
                    new String[]{"id", "Должность", "Зарплата"}
            ));
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String id = rs.getString(1);
                String title = rs.getString(2);
                String salary = rs.getString(3);

                String[] tbData = {id, title, salary};
                DefaultTableModel tblModel = (DefaultTableModel) title_table.getModel();
                tblModel.addRow(tbData);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public Title() {
        table_load();
        saveButton.addActionListener(e -> {
            String title = txtJob_title.getText();
            int salary = Integer.parseInt(txtSalary.getText());


            try (Connection connection = Connect.getConnetion();
                 PreparedStatement pst = connection.prepareStatement("INSERT INTO " +
                         "title(job_title, salary) VALUES (?, ?)")) {
                pst.setString(1, title);
                pst.setInt(2, salary);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Данные добавлены");
                table_load();
                txtJob_title.setText("");
                txtSalary.setText("");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        });
        searchButton.addActionListener(e -> {
            int id = Integer.parseInt(textTitle_Id.getText());

            try (Connection connection = Connect.getConnetion();
                 PreparedStatement pst = connection.prepareStatement("SELECT job_title, " +
                         "salary FROM title " +
                         "WHERE title_id = ?")) {
                pst.setInt(1, id);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    String title = rs.getString(1);
                    String salary = rs.getString(2);
                    txtJob_title.setText(title);
                    txtSalary.setText(salary);
                } else {
                    txtJob_title.setText("");
                    txtSalary.setText("");
                    JOptionPane.showMessageDialog(null,
                            "Должность не найдена");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        updateButton.addActionListener(e -> {
            int id = Integer.parseInt(textTitle_Id.getText());
            String title = txtJob_title.getText();
            int salary = Integer.parseInt(txtSalary.getText());

            try (Connection connection = Connect.getConnetion();
                 PreparedStatement pst = connection.prepareStatement("UPDATE title " +
                         "SET job_title = ?, salary = ? " +
                         "WHERE title_id = ?")) {
                pst.setString(1, title);
                pst.setInt(2, salary);
                pst.setInt(3, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Данные изменены");
                table_load();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        deleteButton.addActionListener(e -> {
            int id = Integer.parseInt(textTitle_Id.getText());

            try (Connection connection = Connect.getConnetion();
                 PreparedStatement pst = connection.prepareStatement("DELETE FROM " +
                         "title WHERE title_id = ?")) {
                pst.setInt(1, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Данные Удалены");
                table_load();
                txtJob_title.setText("");
                txtSalary.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        "Вы не можете упраздниить должность \n" +
                                "на которую назначены сотрудники.\n" +
                                "В первую очередь сотрудникам требуется \n" +
                                "назначить новую должность или уволить их.");
            }
        });
    }
}
