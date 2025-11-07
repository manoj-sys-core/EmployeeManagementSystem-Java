package com.employee.gui;

import com.employee.dao.DepartmentDAO;
import com.employee.dao.EmployeeDAO;
import com.employee.model.Department;
import com.employee.model.Employee;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Vector;

public class DepartmentManagementPanel extends JPanel {

    private final DepartmentDAO departmentDAO;
    private final EmployeeDAO employeeDAO;
    private JTable departmentTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, searchField;
    private JTextArea descriptionArea;
    private JComboBox<Employee> managerCombo;
    private JButton addButton, updateButton, deleteButton, clearButton, refreshButton, viewEmployeesButton;
    private JLabel statsLabel;

    public DepartmentManagementPanel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {}

        departmentDAO = new DepartmentDAO();
        employeeDAO = new EmployeeDAO();

        initializeComponents();
        layoutComponents();
        setupListeners();
        loadDepartments();
    }

    private void initializeComponents() {
        Font font = new Font("Poppins", Font.PLAIN, 14);

        // === Table ===
        String[] columns = {"ID", "Name", "Description", "Manager", "Employees", "Created"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        departmentTable = new JTable(tableModel);
        departmentTable.setFont(font);
        departmentTable.getTableHeader().setFont(new Font("Poppins SemiBold", Font.BOLD, 14));
        departmentTable.setRowHeight(30);
        departmentTable.setShowHorizontalLines(true);
        departmentTable.setGridColor(new Color(230, 230, 230));
        departmentTable.setSelectionBackground(new Color(33, 150, 243, 40));
        departmentTable.setSelectionForeground(Color.BLACK);

        // === Fields ===
        nameField = new JTextField(25);
        nameField.setFont(font);

        descriptionArea = new JTextArea(3, 25);
        descriptionArea.setFont(font);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 210, 210), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        managerCombo = new JComboBox<>();
        loadManagers();

        // === Search ===
        searchField = new JTextField(18);
        searchField.setFont(font);
        searchField.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        searchField.putClientProperty("JTextField.placeholderText", "Search department...");

        // === Rounded Gradient Buttons ===
        addButton = createGradientButton("➕ Add", new Color(56, 142, 60), new Color(76, 175, 80));
        updateButton = createGradientButton("✏️ Update", new Color(33, 150, 243), new Color(30, 136, 229));
        deleteButton = createGradientButton("🗑 Delete", new Color(211, 47, 47), new Color(244, 67, 54));
        clearButton = createGradientButton("Clear", new Color(117, 117, 117), new Color(97, 97, 97));
        refreshButton = createGradientButton("⟳ Refresh", new Color(100, 181, 246), new Color(66, 165, 245));
        viewEmployeesButton = createGradientButton("👥 View Employees", new Color(0, 150, 136), new Color(0, 121, 107));

        statsLabel = new JLabel("Loading...");
        statsLabel.setFont(new Font("Poppins SemiBold", Font.BOLD, 13));
        statsLabel.setForeground(new Color(0x2196F3));
    }

    private JButton createGradientButton(String text, Color start, Color end) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(0, 0, start, getWidth(), getHeight(), end);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2.setColor(Color.WHITE);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 3);
                g2.dispose();
            }
        };
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setFont(new Font("Poppins", Font.BOLD, 13));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setForeground(new Color(255, 255, 255, 230)); }
            public void mouseExited(MouseEvent e) { btn.setForeground(Color.WHITE); }
        });
        return btn;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(247, 249, 252));
        setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("🏢 Department Management");
        titleLabel.setFont(new Font("Poppins SemiBold", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 5));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("🔍"));
        searchPanel.add(searchField);
        searchPanel.add(refreshButton);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(15, 25, 15, 25)
        ));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(20, 25, 20, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Department Name:"), gbc);
        gbc.gridx = 1; formPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        formPanel.add(descScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Manager:"), gbc);
        gbc.gridx = 1; formPanel.add(managerCombo, gbc);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        buttonRow.setBackground(Color.WHITE);
        buttonRow.add(addButton);
        buttonRow.add(updateButton);
        buttonRow.add(deleteButton);
        buttonRow.add(clearButton);
        buttonRow.add(viewEmployeesButton);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.add(statsLabel);

        JScrollPane tableScroll = new JScrollPane(departmentTable);
        tableScroll.getViewport().setBackground(Color.WHITE);
        tableScroll.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));
        tableCard.add(tableScroll, BorderLayout.CENTER);
        tableCard.add(statsPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(new Color(247, 249, 252));
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(buttonRow, BorderLayout.CENTER);
        centerPanel.add(tableCard, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(centerPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
    }

    private void setupListeners() {
        addButton.addActionListener(e -> addDepartment());
        updateButton.addActionListener(e -> updateDepartment());
        deleteButton.addActionListener(e -> deleteDepartment());
        clearButton.addActionListener(e -> clearForm());
        refreshButton.addActionListener(e -> loadDepartments());
        viewEmployeesButton.addActionListener(e -> viewDepartmentEmployees());
        searchField.addActionListener(e -> searchDepartments());

        departmentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) loadSelectedDepartment();
            }
        });
    }

    private void loadManagers() {
        managerCombo.removeAllItems();
        managerCombo.addItem(null);
        List<Employee> employees = employeeDAO.getAllEmployees();
        for (Employee e : employees) managerCombo.addItem(e);
    }

    private void loadDepartments() {
        tableModel.setRowCount(0);
        List<Department> departments = departmentDAO.getAllDepartments();
        int totalEmployees = 0;

        for (Department d : departments) {
            addDepartmentToTable(d);
            totalEmployees += departmentDAO.getEmployeeCountByDepartment(d.getDepartmentId());
        }

        statsLabel.setText("Departments: " + departments.size() + " | Total Employees: " + totalEmployees);
    }

    private void addDepartmentToTable(Department d) {
        Vector<Object> row = new Vector<>();
        row.add(d.getDepartmentId());
        row.add(d.getName());
        row.add(d.getDescription());

        String managerName = "Not Assigned";
        if (d.getManagerId() > 0) {
            Employee m = employeeDAO.getEmployeeById(d.getManagerId());
            if (m != null) managerName = m.getFullName();
        }
        row.add(managerName);
        row.add(departmentDAO.getEmployeeCountByDepartment(d.getDepartmentId()));
        row.add(d.getCreatedAt());
        tableModel.addRow(row);
    }

    private void loadSelectedDepartment() {
        int row = departmentTable.getSelectedRow();
        if (row == -1) return;

        int id = (int) tableModel.getValueAt(row, 0);
        Department d = departmentDAO.getDepartmentById(id);
        if (d == null) return;

        nameField.setText(d.getName());
        descriptionArea.setText(d.getDescription());

        if (d.getManagerId() > 0) {
            for (int i = 0; i < managerCombo.getItemCount(); i++) {
                Employee e = managerCombo.getItemAt(i);
                if (e != null && e.getEmployeeId() == d.getManagerId()) {
                    managerCombo.setSelectedIndex(i);
                    break;
                }
            }
        } else managerCombo.setSelectedIndex(0);
    }

    private void addDepartment() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter department name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Department d = new Department();
        d.setName(nameField.getText().trim());
        d.setDescription(descriptionArea.getText().trim());
        Employee m = (Employee) managerCombo.getSelectedItem();
        d.setManagerId(m != null ? m.getEmployeeId() : 0);

        if (departmentDAO.addDepartment(d)) {
            JOptionPane.showMessageDialog(this, "Department added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadDepartments();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add department.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateDepartment() {
        int row = departmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a department to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        Department d = departmentDAO.getDepartmentById(id);
        if (d == null) return;

        d.setName(nameField.getText().trim());
        d.setDescription(descriptionArea.getText().trim());
        Employee m = (Employee) managerCombo.getSelectedItem();
        d.setManagerId(m != null ? m.getEmployeeId() : 0);

        if (departmentDAO.updateDepartment(d)) {
            JOptionPane.showMessageDialog(this, "Updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadDepartments();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteDepartment() {
        int row = departmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a department to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        int count = (int) tableModel.getValueAt(row, 4);

        if (count > 0) {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete '" + name + "' as it has " + count + " employees.\nReassign or remove them first.",
                    "Delete Blocked", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete department '" + name + "'?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION && departmentDAO.deleteDepartment(id)) {
            JOptionPane.showMessageDialog(this, "Deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadDepartments();
            clearForm();
        }
    }

    private void clearForm() {
        nameField.setText("");
        descriptionArea.setText("");
        managerCombo.setSelectedIndex(0);
        departmentTable.clearSelection();
    }

    private void searchDepartments() {
        String search = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);

        List<Department> departments = departmentDAO.getAllDepartments();
        for (Department d : departments) {
            if (search.isEmpty() || d.getName().toLowerCase().contains(search) ||
                    d.getDescription().toLowerCase().contains(search)) {
                addDepartmentToTable(d);
            }
        }
    }

    private void viewDepartmentEmployees() {
        int row = departmentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a department first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this),
                "Employees in " + name, true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);

        String[] cols = {"ID", "Name", "Email", "Phone", "Position", "Salary", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Poppins", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Poppins SemiBold", Font.BOLD, 14));

        for (Employee e : employeeDAO.getAllEmployees()) {
            if (e.getDepartmentId() == id) {
                model.addRow(new Object[]{
                        e.getEmployeeId(), e.getFullName(), e.getEmail(), e.getPhone(),
                        e.getPosition(), e.getSalary(), e.getStatus()
                });
            }
        }

        dialog.add(new JScrollPane(table));
        dialog.setVisible(true);
    }
}
