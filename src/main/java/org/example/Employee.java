package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeMap;

public class Employee {
    private JTable employee_table;
    private JButton searchButton;
    private JButton deleteButton;
    private JTextField textEmployee_Id;
    private JButton updateButton;
    private JButton saveButton;
    private JTextField txtFIO;
    private JPanel EmployeeMainTable;
    private JComboBox<String> DepartmentComboBox;
    private JComboBox<String> TitleComboBox;

    public static void callEmployeeTable() {
        JFrame frame = new JFrame("Employee");
        frame.setContentPane(new Employee().EmployeeMainTable);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


    private void table_load() {
        try (Connection connection = Connect.getConnetion();
             PreparedStatement pst = connection.prepareStatement("SELECT " +
                     "employee_id, fio, salary, " +
                     "department_name, job_title " +
                     "FROM employee, departments, title " +
                     "WHERE departments.department_id = employee.department_id " +
                     "AND title.title_id = employee.title_id " +
                     "ORDER BY employee.employee_id")) {
            employee_table.setModel(new DefaultTableModel(
                    null,
                    new String[]{"id", "Ф.И.О.", "Зарплата", "Отдел", "Должность"}
            ));

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String id = rs.getString(1);
                String fio = rs.getString(2);
                String salary = rs.getString(3);
                String department = rs.getString(4);
                String title = rs.getString(5);

                String[] tbData = {id, fio, salary, department, title};
                DefaultTableModel tblModel = (DefaultTableModel) employee_table.getModel();
                tblModel.addRow(tbData);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int changeDepartment() {
        TreeMap<String, Integer> treeMap = new TreeMap<>();
        try (Connection connection = Connect.getConnetion();
             PreparedStatement pst = connection.prepareStatement("SELECT " +
                     "department_name, department_id " +
                     "FROM departments")) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String name = rs.getString(1);
                Integer id = rs.getInt(2);

                treeMap.put(name, id);
                DepartmentComboBox.addItem(name);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return treeMap.get(DepartmentComboBox.
                getItemAt(DepartmentComboBox.getSelectedIndex()));
    }

    private int changeTitle() {
        TreeMap<String, Integer> treeMap = new TreeMap<>();
        try (Connection connection = Connect.getConnetion();
             PreparedStatement pst = connection.prepareStatement("SELECT job_title, title_id " +
                     "FROM title")) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String name = rs.getString(1);
                Integer id = rs.getInt(2);

                treeMap.put(name, id);
                TitleComboBox.addItem(name);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return treeMap.get(TitleComboBox.getItemAt(TitleComboBox.getSelectedIndex()));
    }


    private Employee() {
        table_load();
        changeDepartment();
        changeTitle();
        saveButton.addActionListener(e -> {
            String fio = txtFIO.getText();
            int department = DepartmentComboBox.getSelectedIndex() + 1;
            int title = TitleComboBox.getSelectedIndex() + 1;

            try (Connection connection = Connect.getConnetion();
                 PreparedStatement pst = connection.prepareStatement("INSERT INTO employee(fio, department_id, title_id) " +
                         "VALUES (?, ?, ?)")) {

                pst.setString(1, fio);
                pst.setInt(2, department);
                pst.setInt(3, title);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Данные добавлены");
                table_load();
                txtFIO.setText("");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        searchButton.addActionListener(e -> {
            int id = Integer.parseInt(textEmployee_Id.getText());

            try (Connection connection = Connect.getConnetion();
                 PreparedStatement pst = connection.prepareStatement("SELECT " +
                         "fio, department_id, " +
                         "title_id FROM employee " +
                         "WHERE employee_id = ?")) {

                pst.setInt(1, id);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    String fio = rs.getString(1);
                    String department = rs.getString(2);
                    String title = rs.getString(3);

                    txtFIO.setText(fio);
                    DepartmentComboBox.setSelectedIndex(Integer.parseInt(department) - 1);
                    TitleComboBox.setSelectedIndex(Integer.parseInt(title) - 1);

                } else {
                    txtFIO.setText("");
                    DepartmentComboBox.setSelectedIndex(0);
                    TitleComboBox.setSelectedIndex(0);
                    JOptionPane.showMessageDialog(null,
                            "Сотрудник не найден");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        updateButton.addActionListener(e -> {
            int id = Integer.parseInt(textEmployee_Id.getText());
            String fio = txtFIO.getText();
            int department = changeDepartment();
            int title = changeTitle();

            try (Connection connection = Connect.getConnetion();
                 PreparedStatement pst = connection.prepareStatement("UPDATE employee " +
                         "SET fio = ?, department_id = ?, title_id = ? " +
                         "WHERE employee_id = ?")) {

                pst.setString(1, fio);
                pst.setInt(2, department);
                pst.setInt(3, title);
                pst.setInt(4, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Данные изменены");
                table_load();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        deleteButton.addActionListener(e -> {
            int id = Integer.parseInt(textEmployee_Id.getText());

            try (Connection connection = Connect.getConnetion();
                 PreparedStatement pst = connection.prepareStatement("DELETE FROM employee " +
                         "WHERE employee_id = ?")) {
                pst.setInt(1, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Данные Удалены");
                table_load();
                txtFIO.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        "Вы не можете уволить сотрудника,\n" +
                                "который является начальником отдела.\n" +
                                "В первую очередь требуется назначить\n" +
                                "нового начальника.");
            }
        });
    }
}