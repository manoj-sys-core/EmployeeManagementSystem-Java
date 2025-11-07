package com.employee.gui;

import com.employee.dao.AttendanceDAO;
import com.employee.dao.DepartmentDAO;
import com.employee.dao.EmployeeDAO;
import com.employee.model.Department;
import com.employee.model.Employee;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

public class ReportsPanel extends JPanel {
    private final EmployeeDAO employeeDAO;
    private final DepartmentDAO departmentDAO;
    private final AttendanceDAO attendanceDAO;
    private JTabbedPane reportTabs;
    private JTextField startDateField, endDateField;
    private JButton generateButton, exportButton;

    public ReportsPanel() {
        employeeDAO = new EmployeeDAO();
        departmentDAO = new DepartmentDAO();
        attendanceDAO = new AttendanceDAO();

        initializeComponents();
        layoutComponents();
        setupListeners();
        generateAllReports();
    }

    private void initializeComponents() {
        reportTabs = new JTabbedPane();
        reportTabs.setFont(new Font("Poppins", Font.PLAIN, 14));
        reportTabs.setBackground(Color.WHITE);

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        startDateField = new JTextField(startDate.toString(), 10);
        endDateField = new JTextField(endDate.toString(), 10);

        generateButton = createGradientButton("Generate Reports", new Color(56, 102, 255), new Color(46, 90, 240));
        exportButton = createGradientButton("📤 Export CSV", new Color(0, 184, 148), new Color(0, 151, 136));
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
        btn.setFont(new Font("Poppins SemiBold", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(180, 40));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setForeground(new Color(255, 255, 255, 230));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
        });
        return btn;
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(247, 249, 252));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("📊 Reports & Analytics");
        title.setFont(new Font("Poppins SemiBold", Font.BOLD, 24));
        title.setForeground(new Color(33, 37, 41));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));
        filterPanel.add(new JLabel("From:"));
        filterPanel.add(startDateField);
        filterPanel.add(new JLabel("To:"));
        filterPanel.add(endDateField);
        filterPanel.add(generateButton);
        filterPanel.add(exportButton);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(15, 25, 15, 25)
        ));
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(reportTabs, BorderLayout.CENTER);
    }

    private void setupListeners() {
        generateButton.addActionListener(this::generateReports);
        exportButton.addActionListener(this::exportCSV);
    }

    private void generateReports(ActionEvent e) {
        generateAllReports();
        JOptionPane.showMessageDialog(this, "Reports updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateAllReports() {
        reportTabs.removeAll();

        reportTabs.addTab("Employee Overview", createCardPanel(createEmployeeStatsChart()));
        reportTabs.addTab("Department Analysis", createCardPanel(createDepartmentChart()));
        reportTabs.addTab("Attendance Trends", createCardPanel(createAttendanceChart()));
        reportTabs.addTab("Salary Insights", createCardPanel(createSalaryChart()));
        reportTabs.addTab("Performance Metrics", createCardPanel(createPerformanceTable()));

        reportTabs.revalidate();
        reportTabs.repaint();
    }

    private JPanel createCardPanel(JComponent chartPanel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.add(chartPanel, BorderLayout.CENTER);
        return card;
    }

    private ChartPanel createEmployeeStatsChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Department dept : departmentDAO.getAllDepartments()) {
            int count = departmentDAO.getEmployeeCountByDepartment(dept.getDepartmentId());
            if (count > 0) dataset.setValue(dept.getName(), count);
        }

        JFreeChart chart = ChartFactory.createPieChart("Employee Distribution by Department", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setSectionOutlinesVisible(false);
        plot.setSimpleLabels(true);
        chart.setBackgroundPaint(Color.WHITE);
        return new ChartPanel(chart);
    }

    private ChartPanel createDepartmentChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Department dept : departmentDAO.getAllDepartments()) {
            int count = departmentDAO.getEmployeeCountByDepartment(dept.getDepartmentId());
            dataset.addValue(count, "Employees", dept.getName());
        }

        JFreeChart chart = ChartFactory.createBarChart("Employees per Department", "Department", "Employees", dataset);
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(99, 102, 241));
        renderer.setBarPainter(new StandardBarPainter());
        chart.setBackgroundPaint(Color.WHITE);
        return new ChartPanel(chart);
    }

    private ChartPanel createAttendanceChart() {
        TimeSeries present = new TimeSeries("Present");
        TimeSeries absent = new TimeSeries("Absent");
        TimeSeries late = new TimeSeries("Late");

        try {
            LocalDate start = LocalDate.parse(startDateField.getText());
            LocalDate end = LocalDate.parse(endDateField.getText());
            LocalDate current = start;

            while (!current.isAfter(end)) {
                present.add(new Day(java.sql.Date.valueOf(current)), attendanceDAO.getAttendanceCountByStatus("PRESENT", current, current));
                absent.add(new Day(java.sql.Date.valueOf(current)), attendanceDAO.getAttendanceCountByStatus("ABSENT", current, current));
                late.add(new Day(java.sql.Date.valueOf(current)), attendanceDAO.getAttendanceCountByStatus("LATE", current, current));
                current = current.plusDays(1);
            }
        } catch (Exception ignored) {}

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(present);
        dataset.addSeries(absent);
        dataset.addSeries(late);

        JFreeChart chart = ChartFactory.createTimeSeriesChart("Attendance Trends", "Date", "Count", dataset, true, true, false);
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(new Color(230, 230, 230));
        plot.setRangeGridlinePaint(new Color(230, 230, 230));

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(56, 102, 255));
        renderer.setSeriesPaint(1, new Color(255, 99, 132));
        renderer.setSeriesPaint(2, new Color(255, 193, 7));
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShapesVisible(1, true);
        renderer.setSeriesShapesVisible(2, true);
        plot.setRenderer(renderer);

        chart.setBackgroundPaint(Color.WHITE);
        return new ChartPanel(chart);
    }

    private ChartPanel createSalaryChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Employee> employees = employeeDAO.getAllEmployees();

        for (Department dept : departmentDAO.getAllDepartments()) {
            double avg = employees.stream()
                    .filter(e -> e.getDepartmentId() == dept.getDepartmentId())
                    .mapToDouble(Employee::getSalary)
                    .average().orElse(0);
            dataset.addValue(avg, "Average Salary", dept.getName());
        }

        JFreeChart chart = ChartFactory.createBarChart("Average Salary by Department", "Department", "Salary ($)", dataset);
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(102, 187, 255));
        renderer.setBarPainter(new StandardBarPainter());
        chart.setBackgroundPaint(Color.WHITE);
        return new ChartPanel(chart);
    }

    private JScrollPane createPerformanceTable() {
        String[] cols = {"Metric", "Value", "Details"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        table.setFont(new Font("Poppins", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Poppins SemiBold", Font.BOLD, 14));
        table.setRowHeight(30);
        table.setBackground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 8));

        List<Employee> employees = employeeDAO.getAllEmployees();
        List<Department> departments = departmentDAO.getAllDepartments();

        model.addRow(new Object[]{"👥 Total Employees", employees.size(), "All Departments"});
        long active = employees.stream().filter(e -> "ACTIVE".equalsIgnoreCase(e.getStatus())).count();
        model.addRow(new Object[]{"✅ Active Employees", active, "Currently Working"});
        double avgSalary = employees.stream().mapToDouble(Employee::getSalary).average().orElse(0);
        model.addRow(new Object[]{"💰 Average Salary", "$" + String.format("%.2f", avgSalary), "All Employees"});

        model.addRow(new Object[]{"🏢 Departments", departments.size(), "Registered in System"});
        model.addRow(new Object[]{"📈 Best Performing Employees", "Top Attendance ≥90%", ""});

        for (Employee emp : employees) {
            double percent = attendanceDAO.getAttendancePercentage(emp.getEmployeeId(), LocalDate.now().minusDays(30), LocalDate.now());
            if (percent >= 90) {
                model.addRow(new Object[]{"⭐ " + emp.getFullName(), String.format("%.1f%%", percent), emp.getPosition()});
            }
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(new LineBorder(new Color(230, 230, 230), 1, true));
        return scroll;
    }

    private void exportCSV(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export Report as CSV");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));

        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".csv")) path += ".csv";

            try (PrintWriter writer = new PrintWriter(path)) {
                writer.println("Employee Management System - Reports Export");
                writer.println("Generated: " + LocalDate.now());
                writer.println();

                List<Employee> employees = employeeDAO.getAllEmployees();
                List<Department> departments = departmentDAO.getAllDepartments();

                writer.println("EMPLOYEE SUMMARY");
                writer.println("Total Employees," + employees.size());
                writer.println("Active Employees," + employees.stream().filter(e1 -> "ACTIVE".equalsIgnoreCase(e1.getStatus())).count());
                writer.println();

                writer.println("DEPARTMENT SUMMARY");
                for (Department d : departments) {
                    int count = departmentDAO.getEmployeeCountByDepartment(d.getDepartmentId());
                    writer.println(d.getName() + "," + count);
                }
                writer.println();

                writer.println("AVERAGE SALARY BY DEPARTMENT");
                for (Department d : departments) {
                    double avg = employees.stream()
                            .filter(emp -> emp.getDepartmentId() == d.getDepartmentId())
                            .mapToDouble(Employee::getSalary)
                            .average()
                            .orElse(0);
                    writer.println(d.getName() + "," + String.format("%.2f", avg));
                }

                writer.flush();
                JOptionPane.showMessageDialog(this, "✅ Exported successfully to:\n" + path,
                        "Export Complete", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "❌ Export failed: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
