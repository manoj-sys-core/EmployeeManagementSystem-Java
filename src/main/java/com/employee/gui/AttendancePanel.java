package com.employee.gui;

import com.employee.dao.AttendanceDAO;
import com.employee.dao.EmployeeDAO;
import com.employee.model.Attendance;
import com.employee.model.Employee;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Vector;

public class AttendancePanel extends JPanel {
    private final AttendanceDAO attendanceDAO;
    private final EmployeeDAO employeeDAO;
    private JTable attendanceTable;
    private DefaultTableModel tableModel;
    private JComboBox<Employee> employeeCombo;
    private JTextField dateField, checkInField, checkOutField, notesField, searchField;
    private JComboBox<String> statusCombo, filterStatusCombo;
    private JButton markButton, checkInButton, checkOutButton, updateButton, deleteButton, refreshButton, exportButton;
    private JLabel statusLabel;

    public AttendancePanel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ignored) {}

        attendanceDAO = new AttendanceDAO();
        employeeDAO = new EmployeeDAO();

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(247, 249, 252));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        initializeComponents();
        layoutComponents();
        setupListeners();
        loadAttendance();
    }

    private void initializeComponents() {
        Font font = new Font("Poppins", Font.PLAIN, 14);

        String[] columns = {"ID", "Employee", "Date", "Check In", "Check Out", "Status", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        attendanceTable = new JTable(tableModel);
        attendanceTable.setFont(font);
        attendanceTable.setRowHeight(30);
        attendanceTable.setGridColor(new Color(230, 230, 230));
        attendanceTable.setShowVerticalLines(false);
        attendanceTable.setSelectionBackground(new Color(33, 150, 243, 40));
        attendanceTable.setSelectionForeground(Color.BLACK);
        attendanceTable.getTableHeader().setFont(new Font("Poppins SemiBold", Font.BOLD, 14));

        attendanceTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 247, 250));
                return c;
            }
        });

        employeeCombo = new JComboBox<>();
        loadEmployees();
        dateField = new JTextField(LocalDate.now().toString(), 12);
        checkInField = new JTextField(8);
        checkOutField = new JTextField(8);
        notesField = new JTextField(20);
        searchField = new JTextField(15);
        statusCombo = new JComboBox<>(new String[]{"PRESENT", "ABSENT", "LATE", "HALF_DAY"});
        filterStatusCombo = new JComboBox<>(new String[]{"All", "PRESENT", "ABSENT", "LATE", "HALF_DAY"});

        // === Rounded gradient buttons ===
        markButton = createGradientButton("📝 Mark Attendance", new Color(33, 150, 243), new Color(30, 136, 229));
        checkInButton = createGradientButton("⏱ Check In", new Color(76, 175, 80), new Color(56, 142, 60));
        checkOutButton = createGradientButton("🚪 Check Out", new Color(255, 152, 0), new Color(251, 140, 0));
        updateButton = createGradientButton("✏️ Update", new Color(63, 81, 181), new Color(48, 63, 159));
        deleteButton = createGradientButton("🗑 Delete", new Color(244, 67, 54), new Color(229, 57, 53));
        refreshButton = createGradientButton("⟳ Refresh", new Color(100, 181, 246), new Color(66, 165, 245));
        exportButton = createGradientButton("📤 Export CSV", new Color(0, 150, 136), new Color(0, 121, 107));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(font);
        statusLabel.setForeground(new Color(33, 150, 243));
    }

    private JButton createGradientButton(String text, Color start, Color end) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, start, getWidth(), getHeight(), end);
                g2.setPaint(gradient);
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
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setFont(new Font("Poppins", Font.PLAIN, 13));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(new Color(255, 255, 255, 230));
            }
            public void mouseExited(MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
        });
        return btn;
    }

    private void layoutComponents() {
        JPanel topPanel = new JPanel(new BorderLayout(15, 15));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JPanel quickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        quickPanel.setOpaque(false);
        quickPanel.add(new JLabel("Employee:"));
        quickPanel.add(employeeCombo);
        quickPanel.add(checkInButton);
        quickPanel.add(checkOutButton);
        quickPanel.add(statusLabel);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(filterStatusCombo);
        searchPanel.add(refreshButton);

        topPanel.add(quickPanel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(15, 25, 15, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Employee:", "Date:", "Check In:", "Check Out:", "Status:", "Notes:"};
        JComponent[] fields = {employeeCombo, dateField, checkInField, checkOutField, statusCombo, notesField};
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            formPanel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            formPanel.add(fields[i], gbc);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(markButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(exportButton);

        JScrollPane tableScroll = new JScrollPane(attendanceTable);
        tableScroll.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        tableScroll.getViewport().setBackground(Color.WHITE);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(Color.WHITE);
        tableCard.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));
        tableCard.add(tableScroll, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(new Color(247, 249, 252));
        centerPanel.add(formPanel, BorderLayout.NORTH);
        centerPanel.add(buttonPanel, BorderLayout.CENTER);
        centerPanel.add(tableCard, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(centerPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
    }

    private void setupListeners() {
        markButton.addActionListener(e -> markAttendance());
        checkInButton.addActionListener(e -> quickCheckIn());
        checkOutButton.addActionListener(e -> quickCheckOut());
        updateButton.addActionListener(e -> updateAttendance());
        deleteButton.addActionListener(e -> deleteAttendance());
        refreshButton.addActionListener(e -> loadAttendance());
        exportButton.addActionListener(e -> exportToCSV());
        searchField.addActionListener(e -> filterAttendance());
        filterStatusCombo.addActionListener(e -> filterAttendance());
        attendanceTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedAttendance();
        });
    }

    // === DAO logic ===
    private void loadEmployees() {
        employeeCombo.removeAllItems();
        for (Employee emp : employeeDAO.getAllEmployees()) employeeCombo.addItem(emp);
    }

    private void loadAttendance() {
        tableModel.setRowCount(0);
        for (Attendance att : attendanceDAO.getAllAttendance()) addAttendanceToTable(att);
    }

    private void addAttendanceToTable(Attendance att) {
        Vector<Object> row = new Vector<>();
        row.add(att.getAttendanceId());
        Employee emp = employeeDAO.getEmployeeById(att.getEmployeeId());
        row.add(emp != null ? emp.getFullName() : "Unknown");
        row.add(att.getDate());
        row.add(att.getCheckIn());
        row.add(att.getCheckOut());
        row.add(att.getStatus());
        row.add(att.getNotes());
        tableModel.addRow(row);
    }

    private void loadSelectedAttendance() {
        int row = attendanceTable.getSelectedRow();
        if (row == -1) return;
        dateField.setText(tableModel.getValueAt(row, 2).toString());
        checkInField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        checkOutField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        statusCombo.setSelectedItem(tableModel.getValueAt(row, 5));
        notesField.setText(String.valueOf(tableModel.getValueAt(row, 6)));
    }

    private void markAttendance() {
        Employee emp = (Employee) employeeCombo.getSelectedItem();
        if (emp == null) return;

        try {
            Attendance a = new Attendance();
            a.setEmployeeId(emp.getEmployeeId());
            a.setDate(LocalDate.parse(dateField.getText()));
            a.setCheckIn(checkInField.getText().isEmpty() ? null : LocalTime.parse(checkInField.getText()));
            a.setCheckOut(checkOutField.getText().isEmpty() ? null : LocalTime.parse(checkOutField.getText()));
            a.setStatus((String) statusCombo.getSelectedItem());
            a.setNotes(notesField.getText().trim());

            if (attendanceDAO.markAttendance(a)) {
                JOptionPane.showMessageDialog(this, "Attendance marked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAttendance();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quickCheckIn() {
        Employee emp = (Employee) employeeCombo.getSelectedItem();
        if (emp == null) return;
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        Attendance a = attendanceDAO.getAttendanceByEmployeeAndDate(emp.getEmployeeId(), today);
        if (a == null) {
            a = new Attendance();
            a.setEmployeeId(emp.getEmployeeId());
            a.setDate(today);
            a.setCheckIn(now);
            a.setStatus("PRESENT");
            a.setNotes("");
        } else {
            a.setCheckIn(now);
        }

        if (attendanceDAO.markAttendance(a)) {
            statusLabel.setText("Checked in at " + now);
            statusLabel.setForeground(new Color(76, 175, 80));
            loadAttendance();
        }
    }

    private void quickCheckOut() {
        Employee emp = (Employee) employeeCombo.getSelectedItem();
        if (emp == null) return;
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        Attendance a = attendanceDAO.getAttendanceByEmployeeAndDate(emp.getEmployeeId(), today);
        if (a == null) {
            JOptionPane.showMessageDialog(this, "Please check in first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        a.setCheckOut(now);
        if (attendanceDAO.updateAttendance(a)) {
            statusLabel.setText("Checked out at " + now);
            statusLabel.setForeground(new Color(255, 152, 0));
            loadAttendance();
        }
    }

    private void updateAttendance() {
        int row = attendanceTable.getSelectedRow();
        if (row == -1) return;
        try {
            int id = (int) tableModel.getValueAt(row, 0);
            Attendance a = new Attendance();
            a.setAttendanceId(id);
            a.setDate(LocalDate.parse(dateField.getText()));
            a.setCheckIn(checkInField.getText().isEmpty() ? null : LocalTime.parse(checkInField.getText()));
            a.setCheckOut(checkOutField.getText().isEmpty() ? null : LocalTime.parse(checkOutField.getText()));
            a.setStatus((String) statusCombo.getSelectedItem());
            a.setNotes(notesField.getText());

            if (attendanceDAO.updateAttendance(a)) {
                JOptionPane.showMessageDialog(this, "Attendance updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAttendance();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAttendance() {
        int row = attendanceTable.getSelectedRow();
        if (row == -1) return;
        int id = (int) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected record?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && attendanceDAO.deleteAttendance(id)) {
            JOptionPane.showMessageDialog(this, "Deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAttendance();
        }
    }

    private void filterAttendance() {
        String search = searchField.getText().trim().toLowerCase();
        String status = (String) filterStatusCombo.getSelectedItem();
        tableModel.setRowCount(0);

        for (Attendance a : attendanceDAO.getAllAttendance()) {
            Employee emp = employeeDAO.getEmployeeById(a.getEmployeeId());
            String name = emp != null ? emp.getFullName().toLowerCase() : "";
            boolean matchesSearch = search.isEmpty() || name.contains(search);
            boolean matchesStatus = status.equals("All") || a.getStatus().equals(status);
            if (matchesSearch && matchesStatus) addAttendanceToTable(a);
        }
    }

    private void exportToCSV() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".csv")) path += ".csv";

            try (java.io.PrintWriter w = new java.io.PrintWriter(path)) {
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    w.print(tableModel.getColumnName(i));
                    if (i < tableModel.getColumnCount() - 1) w.print(",");
                }
                w.println();
                for (int r = 0; r < tableModel.getRowCount(); r++) {
                    for (int c = 0; c < tableModel.getColumnCount(); c++) {
                        w.print(tableModel.getValueAt(r, c));
                        if (c < tableModel.getColumnCount() - 1) w.print(",");
                    }
                    w.println();
                }
                JOptionPane.showMessageDialog(this, "Exported successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
