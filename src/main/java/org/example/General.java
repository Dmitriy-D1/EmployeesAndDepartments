package org.example;

import javax.swing.*;

public class General {
    private JButton departments_table;
    private JButton employees_table;
    private JButton title_table;
    private JPanel Main;


    protected static void callGeneralTable() {
        JFrame frame = new JFrame("General");
        frame.setContentPane(new General().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private General() {
        departments_table.addActionListener(e -> Department.callDepartmentTable());
        employees_table.addActionListener(e -> Employee.callEmployeeTable());
        title_table.addActionListener(e -> Title.callTitleTable());
    }
}
