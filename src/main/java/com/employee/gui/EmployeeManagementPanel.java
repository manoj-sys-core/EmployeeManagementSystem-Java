package com.employee.gui;

import com.employee.dao.DepartmentDAO;
import com.employee.dao.EmployeeDAO;
import com.employee.model.Department;
import com.employee.model.Employee;
import com.employee.utils.ImageUtils;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class EmployeeManagementPanel extends JPanel {
    private final EmployeeDAO employeeDAO;
    private final DepartmentDAO departmentDAO;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> departmentFilter, positionFilter;
    private JButton addButton, editButton, deleteButton, refreshButton;
    private JLabel profilePictureLabel;
    private JPanel profileCard, tableCard;

    public EmployeeManagementPanel() {
        try { FlatLightLaf.setup(); } catch (Exception ignored) {}
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(247, 249, 252));

        employeeDAO = new EmployeeDAO();
        departmentDAO = new DepartmentDAO();

        initializeComponents();
        layoutComponents();
        setupListeners();
        loadEmployees();
    }

    /* ================= ICONS ================= */
    private Icon createAddIcon(Color color) {
        return new Icon() {
            public int getIconWidth() { return 22; }
            public int getIconHeight() { return 22; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x + 11, y + 4, x + 11, y + 18);
                g2.drawLine(x + 4, y + 11, x + 18, y + 11);
            }
        };
    }

    private Icon createEditIcon(Color color) {
        return new Icon() {
            public int getIconWidth() { return 22; }
            public int getIconHeight() { return 22; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawLine(x + 5, y + 15, x + 17, y + 5);
                g2.drawRect(x + 4, y + 14, 14, 4);
            }
        };
    }

    private Icon createDeleteIcon(Color color) {
        return new Icon() {
            public int getIconWidth() { return 22; }
            public int getIconHeight() { return 22; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRect(x + 6, y + 8, 10, 10);
                g2.drawLine(x + 4, y + 8, x + 18, y + 8);
                g2.drawLine(x + 9, y + 11, x + 9, y + 15);
                g2.drawLine(x + 13, y + 11, x + 13, y + 15);
                g2.drawLine(x + 7, y + 4, x + 15, y + 4);
            }
        };
    }

    private Icon createRefreshIcon(Color color) {
        return new Icon() {
            public int getIconWidth() { return 22; }
            public int getIconHeight() { return 22; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(x + 4, y + 4, 14, 14, 45, 270);
                g2.drawLine(x + 15, y + 5, x + 18, y + 2);
                g2.drawLine(x + 15, y + 5, x + 12, y + 2);
            }
        };
    }

    /* ================= INITIALIZATION ================= */
    private void initializeComponents() {
        Font font = new Font("Segoe UI", Font.PLAIN, 15);

        String[] columns = {"ID", "First Name", "Last Name", "Email", "Phone", "Department", "Position", "Salary", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        employeeTable = new JTable(tableModel);
        employeeTable.setFont(font);
        employeeTable.setRowHeight(42);
        employeeTable.setShowGrid(false);
        employeeTable.setIntercellSpacing(new Dimension(0, 0));
        employeeTable.setSelectionBackground(new Color(219, 234, 254));
        employeeTable.setSelectionForeground(Color.BLACK);
        employeeTable.setFillsViewportHeight(true);
        customizeTable();

        searchField = new JTextField(20);
        searchField.setFont(font);
        searchField.setBorder(new RoundedBorder(12));
        searchField.setToolTipText("Search employees...");

        departmentFilter = new JComboBox<>();
        departmentFilter.addItem("All Departments");
        departmentFilter.setFont(font);
        departmentFilter.setBorder(new RoundedBorder(12));

        positionFilter = new JComboBox<>();
        positionFilter.addItem("All Positions");
        positionFilter.setFont(font);
        positionFilter.setBorder(new RoundedBorder(12));

        addButton = createGradientButton(" Add Employee", new Color(56,189,248), new Color(37,99,235), createAddIcon(Color.WHITE));
        editButton = createGradientButton(" Edit", new Color(16,185,129), new Color(5,150,105), createEditIcon(Color.WHITE));
        deleteButton = createGradientButton(" Delete", new Color(239,68,68), new Color(185,28,28), createDeleteIcon(Color.WHITE));
        refreshButton = createGradientButton(" Refresh", new Color(251,191,36), new Color(245,158,11), createRefreshIcon(Color.WHITE));

        profilePictureLabel = new JLabel("No Image", SwingConstants.CENTER);
        profilePictureLabel.setPreferredSize(new Dimension(200, 200));
        profilePictureLabel.setOpaque(true);
        profilePictureLabel.setBackground(Color.WHITE);
        profilePictureLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        profilePictureLabel.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));

        profileCard = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        profileCard.setOpaque(false);
        profileCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        profileCard.setPreferredSize(new Dimension(260, 300));

        JLabel profileTitle = new JLabel(" Profile Preview", SwingConstants.CENTER);
        profileTitle.setFont(new Font("Segoe UI Semibold", Font.BOLD, 17));
        profileTitle.setForeground(new Color(37, 99, 235));

        profileCard.add(profileTitle, BorderLayout.NORTH);
        profileCard.add(profilePictureLabel, BorderLayout.CENTER);

        loadDepartments();
        loadPositions();
    }

    /* ================= LAYOUT ================= */
    private void layoutComponents() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        JLabel title = new JLabel(" Employee Management");
        title.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        title.setForeground(new Color(37, 99, 235));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.add(searchField);
        filterPanel.add(departmentFilter);
        filterPanel.add(positionFilter);
        filterPanel.add(refreshButton);

        header.add(title, BorderLayout.WEST);
        header.add(filterPanel, BorderLayout.EAST);

        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        buttonBar.setBackground(Color.WHITE);
        buttonBar.add(addButton);
        buttonBar.add(editButton);
        buttonBar.add(deleteButton);

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        tableCard = new JPanel(new BorderLayout(10, 10));
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));
        tableCard.add(buttonBar, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        JPanel mainBody = new JPanel(new GridBagLayout());
        mainBody.setBackground(new Color(247, 249, 252));
        mainBody.setBorder(new EmptyBorder(25, 30, 25, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 20);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        mainBody.add(tableCard, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.3;
        mainBody.add(profileCard, gbc);

        add(header, BorderLayout.NORTH);
        add(mainBody, BorderLayout.CENTER);
    }

    /* ================= TABLE DESIGN ================= */
    private void customizeTable() {
        JTableHeader header = employeeTable.getTableHeader();
        header.setBackground(new Color(230, 240, 255));
        header.setForeground(new Color(37, 99, 235));
        header.setFont(new Font("Segoe UI Semibold", Font.BOLD, 15));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        employeeTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(247, 250, 255));
                } else {
                    c.setBackground(new Color(219, 234, 254));
                }
                setBorder(noFocusBorder);
                setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
                return c;
            }
        });
    }

    /* ================= LISTENERS ================= */
    private void setupListeners() {
        addButton.addActionListener(e -> addEmployee());
        editButton.addActionListener(e -> editEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        refreshButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                loadEmployees();
                profilePictureLabel.setIcon(null);
                profilePictureLabel.setText("No Image");
                profilePictureLabel.repaint();
            });
        });

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { searchEmployees(); }
        });

        employeeTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) displayEmployeeProfile();
            }
        });

        profilePictureLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) changeProfilePicture();
            }
        });
    }

    /* ================= EMPLOYEE DISPLAY ================= */
    private void displayEmployeeProfile() {
        int row = employeeTable.getSelectedRow();
        if (row == -1) return;
        int id = (int) tableModel.getValueAt(row, 0);
        Employee emp = employeeDAO.getEmployeeById(id);

        profilePictureLabel.setText("Loading...");
        profilePictureLabel.setIcon(null);

        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() {
                if (emp != null && emp.getProfilePicture() != null && !emp.getProfilePicture().isEmpty()) {
                    return ImageUtils.resizeImage(emp.getProfilePicture(), 200, 200);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        profilePictureLabel.setIcon(icon);
                        profilePictureLabel.setText("");
                    } else {
                        profilePictureLabel.setText("No Image");
                    }
                } catch (Exception e) {
                    profilePictureLabel.setText("Error loading image");
                }
            }
        }.execute();
    }

    /* ================= CRUD OPS ================= */
    private void addEmployee() {
        EmployeeDialog dialog = new EmployeeDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Add Employee", null);
        dialog.setVisible(true);
        if (dialog.isSaved()) loadEmployees();
    }

    private void editEmployee() {
        int row = employeeTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an employee to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        Employee emp = employeeDAO.getEmployeeById(id);
        EmployeeDialog dialog = new EmployeeDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Edit Employee", emp);
        dialog.setVisible(true);
        if (dialog.isSaved()) loadEmployees();
    }

    private void deleteEmployee() {
        int row = employeeTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an employee to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        String name = tableModel.getValueAt(row, 1) + " " + tableModel.getValueAt(row, 2);
        if (JOptionPane.showConfirmDialog(this, "Delete " + name + "?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (employeeDAO.deleteEmployee(id)) loadEmployees();
        }
    }

    private void searchEmployees() {
        String s = searchField.getText().trim();
        String dept = departmentFilter.getSelectedItem().toString();
        String pos = positionFilter.getSelectedItem().toString();
        if ("All Departments".equals(dept)) dept = "";
        if ("All Positions".equals(pos)) pos = "";

        tableModel.setRowCount(0);
        for (Employee e : employeeDAO.searchEmployees(s, dept, pos)) {
            Department d = departmentDAO.getDepartmentById(e.getDepartmentId());
            tableModel.addRow(new Object[]{
                    e.getEmployeeId(), e.getFirstName(), e.getLastName(),
                    e.getEmail(), e.getPhone(), d != null ? d.getName() : "N/A",
                    e.getPosition(), e.getSalary(), e.getStatus()
            });
        }
    }

    private void changeProfilePicture() {
        int row = employeeTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an employee first.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            String path = file.getAbsolutePath();
            int id = (int) tableModel.getValueAt(row, 0);
            Employee e = employeeDAO.getEmployeeById(id);
            e.setProfilePicture(path);
            if (employeeDAO.updateEmployee(e)) {
                profilePictureLabel.setIcon(ImageUtils.resizeImage(path, 200, 200));
                profilePictureLabel.setText("");
            }
        }
    }

    /* ================= HELPERS ================= */
    private JButton createGradientButton(String text, Color c1, Color c2, Icon icon) {
        JButton button = new JButton(text, icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(190, 50));
        button.setBorder(new EmptyBorder(8, 18, 8, 18));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(10);
        return button;
    }

    private void loadEmployees() {
        tableModel.setRowCount(0);
        for (Employee e : employeeDAO.getAllEmployees()) {
            Department d = departmentDAO.getDepartmentById(e.getDepartmentId());
            tableModel.addRow(new Object[]{
                    e.getEmployeeId(), e.getFirstName(), e.getLastName(),
                    e.getEmail(), e.getPhone(), d != null ? d.getName() : "N/A",
                    e.getPosition(), e.getSalary(), e.getStatus()
            });
        }
    }

    private void loadDepartments() {
        for (Department d : departmentDAO.getAllDepartments()) departmentFilter.addItem(d.getName());
    }

    private void loadPositions() {
        employeeDAO.getAllEmployees().stream().map(Employee::getPosition).distinct().forEach(positionFilter::addItem);
    }

    static class RoundedBorder extends LineBorder {
        public RoundedBorder(int radius) { super(new Color(220, 220, 220), 1, true); }
    }
}
