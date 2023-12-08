package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        private JComboBox ExtraTitleComboBox;

        public static void callEmployeeTable() {
            JFrame frame = new JFrame("Employee");
            frame.setContentPane(new Employee().EmployeeMainTable);
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        }


        private void table_load() {
            try (Connection connection = Connect.getConnetion();
                 PreparedStatement pst = connection.prepareStatement("SELECT employee_id, fio, t2.salary, " +
                         "department_name, t2.job_title, t3.job_title AS extra_job " +
                         "FROM employee " +
                         "INNER JOIN departments on departments.department_id = employee.department_id " +
                         "INNER JOIN title t2 ON employee.title_id=t2.title_id " +
                         "INNER JOIN title t3 ON employee.extratitle_id=t3.title_id " +
                         "ORDER BY employee.employee_id")) {
                employee_table.setModel(new DefaultTableModel(
                        null,
                        new String[]{"id", "Ф.И.О.", "Зарплата", "Отдел", "Должность", "Доп. обязанности"}
                ));

                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    String id = rs.getString(1);
                    String fio = rs.getString(2);
                    String salary = rs.getString(3);
                    String department = rs.getString(4);
                    String title = rs.getString(5);
                    String extraTitle = rs.getString(6);

                    String[] tbData = {id, fio, salary, department, title, extraTitle};
                    DefaultTableModel tblModel = (DefaultTableModel) employee_table.getModel();
                    tblModel.addRow(tbData);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        private void loadDepartment() {
            try (Connection connection = Connect.getConnetion();
                 PreparedStatement pst = connection.prepareStatement("SELECT " +
                         "department_name, department_id " +
                         "FROM departments")) {
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    String name = rs.getString(1);

                    DepartmentComboBox.addItem(name);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }

        private void loadTitle() {
            try (Connection connection = Connect.getConnetion();
                 PreparedStatement pst = connection.prepareStatement("SELECT job_title, title_id " +
                         "FROM title")) {
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    String name = rs.getString(1);

                    TitleComboBox.addItem(name);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        private void loadExtraTitle() {
            try (Connection connection = Connect.getConnetion();
                 PreparedStatement pst = connection.prepareStatement("SELECT job_title, title_id " +
                         "FROM title")) {
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    String name = rs.getString(1);

                    ExtraTitleComboBox.addItem(name);
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }


        private Employee() {
            table_load();
            loadDepartment();
            loadTitle();
            loadExtraTitle();
            saveButton.addActionListener(e -> {
                String fio = txtFIO.getText();
                int department = DepartmentComboBox.getSelectedIndex() + 1;
                int title = TitleComboBox.getSelectedIndex() + 1;
                int extraTitle = ExtraTitleComboBox.getSelectedIndex() + 1;

                try (Connection connection = Connect.getConnetion();
                     PreparedStatement pst = connection.prepareStatement("INSERT INTO employee(fio, department_id, title_id, extratitle_id) " +
                             "VALUES (?, ?, ?, ?)")) {

                    pst.setString(1, fio);
                    pst.setInt(2, department);
                    pst.setInt(3, title);
                    pst.setInt(4, extraTitle);
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
                             "title_id, extratitle_id FROM employee " +
                             "WHERE employee_id = ?")) {

                    pst.setInt(1, id);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        String fio = rs.getString(1);
                        String department = rs.getString(2);
                        String title = rs.getString(3);
                        String extraTitle = rs.getString(4);

                        txtFIO.setText(fio);
                        DepartmentComboBox.setSelectedIndex(Integer.parseInt(department) - 1);
                        TitleComboBox.setSelectedIndex(Integer.parseInt(title) - 1);
                        ExtraTitleComboBox.setSelectedIndex(Integer.parseInt(extraTitle) - 1);

                    } else {
                        txtFIO.setText("");
                        DepartmentComboBox.setSelectedIndex(0);
                        TitleComboBox.setSelectedIndex(0);
                        ExtraTitleComboBox.setSelectedIndex(0);
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
                int department = DepartmentComboBox.getSelectedIndex()+1;
                int title = TitleComboBox.getSelectedIndex()+1;
                int extraTitle = ExtraTitleComboBox.getSelectedIndex()+1;


                try (Connection connection = Connect.getConnetion();
                     PreparedStatement pst = connection.prepareStatement("UPDATE employee " +
                             "SET fio = ?, department_id = ?, title_id = ?, extratitle_id = ? " +
                             "WHERE employee_id = ?")) {

                    pst.setString(1, fio);
                    pst.setInt(2, department);
                    pst.setInt(3, title);
                    pst.setInt(4, extraTitle);
                    pst.setInt(5, id);
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