package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Department {
    private JTable department_table;
    private JButton searchButton;
    private JButton deleteButton;
    private JTextField textDepartment_Id;
    private JButton updateButton;
    private JButton saveButton;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JTextField txtManager;
    private JTextField txtDepartment;
    private JPanel DepartmentMainTable;
    private JTable employee_table;

    protected static void callDepartmentTable() {
        JFrame frame = new JFrame("Department");
        frame.setContentPane(new Department().DepartmentMainTable);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


    private void table_load() {
        try (Connection connection = Connect.getConnetion();
             PreparedStatement pst = connection.prepareStatement("SELECT " +
                     "departments.department_id, " +
                     "department_name, fio, phone, email " +
                     "FROM departments, employee " +
                     "WHERE employee.employee_id = departments.manager_id " +
                     "ORDER BY departments.department_id")) {

            department_table.setModel(new DefaultTableModel(
                    null,
                    new String[]{"id отдела", "Отдел", "Начальник отдела", "Телефон", "Email"}
            ));

            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String id = rs.getString(1);
                String department = rs.getString(2);
                String manager = rs.getString(3);
                String phone = rs.getString(4);
                String email = rs.getString(5);

                String[] tbData = {id, department, manager, phone, email};
                DefaultTableModel tblModel = (DefaultTableModel) department_table.getModel();
                tblModel.addRow(tbData);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void employee_load(int department_id) {
        try (Connection connection = Connect.getConnetion();
             PreparedStatement pst = connection.prepareStatement("SELECT employee_id, " +
                     "fio, job_title " +
                     "FROM employee, title " +
                     "WHERE title.title_id = employee.title_id " +
                     "AND department_id = ?")) {
            employee_table.setModel(new DefaultTableModel(
                    null,
                    new String[]{"id сотрудника", "Сотрудник", "Должность"}
            ));

            pst.setInt(1, department_id);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String id = rs.getString(1);
                String fio = rs.getString(2);
                String title = rs.getString(3);

                String[] tbData = {id, fio, title};
                DefaultTableModel tblModel = (DefaultTableModel) employee_table.getModel();
                tblModel.addRow(tbData);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Department() {
        table_load();
        saveButton.addActionListener(e -> {
            int manager;
            if (txtManager.getText().isEmpty()){
                manager = 1;
            } else manager = Integer.parseInt(txtManager.getText());

            String department = txtDepartment.getText();
            String phone = txtPhone.getText();
            String email = txtEmail.getText();

            try (Connection connection = Connect.getConnetion();
                 PreparedStatement pst = connection.prepareStatement("INSERT INTO " +
                         "departments(manager_id, department_name, phone, email) VALUES (?, ?, ?, ?)")) {

                pst.setInt(1, manager);
                pst.setString(2, department);
                pst.setString(3, phone);
                pst.setString(4, email);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Данные добавлены");
                table_load();
                txtDepartment.setText("");
                txtManager.setText("");
                txtPhone.setText("");
                txtEmail.setText("");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        searchButton.addActionListener(e -> {
            int id = Integer.parseInt(textDepartment_Id.getText());
            employee_load(id);

            try (Connection connection = Connect.getConnetion();
                 PreparedStatement pst = connection.prepareStatement("SELECT " +
                         "department_name, manager_id, phone, email FROM departments " +
                         "WHERE department_id = ?")) {

                pst.setInt(1, id);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    String department = rs.getString(1);
                    String manager = rs.getString(2);
                    String phone = rs.getString(3);
                    String email = rs.getString(4);

                    txtDepartment.setText(department);
                    txtManager.setText(manager);
                    txtPhone.setText(phone);
                    txtEmail.setText(email);
                } else {
                    txtDepartment.setText("");
                    txtManager.setText("");
                    txtPhone.setText("");
                    txtEmail.setText("");
                    JOptionPane.showMessageDialog(null,
                            "Отдел не найден");
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        updateButton.addActionListener(e -> {
            int id = Integer.parseInt(textDepartment_Id.getText());
            int manager = Integer.parseInt(txtManager.getText());
            String department = txtDepartment.getText();
            String phone = txtPhone.getText();
            String email = txtEmail.getText();

            try (Connection connection = Connect.getConnetion();
                 PreparedStatement pst = connection.prepareStatement("UPDATE  departments " +
                         "SET department_name = ?, manager_id = ?, phone = ?," +
                         " email = ? WHERE department_id = ?")) {
                pst.setString(1, department);
                pst.setInt(2, manager);
                pst.setString(3, phone);
                pst.setString(4, email);
                pst.setInt(5, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Данные изменены");
                table_load();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        deleteButton.addActionListener(e -> {
            int id = Integer.parseInt(textDepartment_Id.getText());

            try (Connection connection = Connect.getConnetion();
                 PreparedStatement pst = connection.prepareStatement("DELETE FROM " +
                         "departments WHERE department_id = ?")) {

                pst.setInt(1, id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Данные Удалены");
                table_load();
                txtDepartment.setText("");
                txtManager.setText("");
                txtPhone.setText("");
                txtEmail.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null,
                        "Вы не можете расформировать отдел,\n" +
                                "в котором числятся сотрудники.\n" +
                                "Переведите их в другие отделы\n" +
                                "или увольте.");
            }
        });
    }
}