package com.employee.gui;

import com.employee.dao.AttendanceDAO;
import com.employee.dao.DepartmentDAO;
import com.employee.dao.EmployeeDAO;
import com.employee.model.Attendance;
import com.employee.model.Department;
import com.employee.model.Employee;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportsPanel extends JPanel {
    private EmployeeDAO employeeDAO;
    private DepartmentDAO departmentDAO;
    private AttendanceDAO attendanceDAO;
    private JTabbedPane reportTabs;
    private JTextField startDateField;
    private JTextField endDateField;
    private JButton generateButton;
    private JButton exportButton;
    private JButton pdfButton;

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

        // Date fields
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        startDateField = new JTextField(startDate.toString(), 10);
        endDateField = new JTextField(endDate.toString(), 10);

        generateButton = new JButton("Generate Reports");
        exportButton = new JButton("Export to CSV");
        pdfButton = new JButton("Export to PDF");
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Reports & Analytics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("From:"));
        controlPanel.add(startDateField);
        controlPanel.add(new JLabel("To:"));
        controlPanel.add(endDateField);
        controlPanel.add(generateButton);
        controlPanel.add(exportButton);
        controlPanel.add(pdfButton);

        // Create report tabs
        reportTabs.addTab("Employee Statistics", createEmployeeStatsPanel());
        reportTabs.addTab("Department Analysis", createDepartmentAnalysisPanel());
        reportTabs.addTab("Attendance Trends", createAttendanceTrendsPanel());
        reportTabs.addTab("Salary Reports", createSalaryReportsPanel());
        reportTabs.addTab("Performance Metrics", createPerformanceMetricsPanel());

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(reportTabs, BorderLayout.CENTER);
    }

    private void setupListeners() {
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateAllReports();
            }
        });

        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToCSV();
            }
        });

        pdfButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToPDF();
            }
        });
    }

    private JPanel createEmployeeStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create pie chart for employee distribution by department
        DefaultPieDataset dataset = new DefaultPieDataset();
        List<Department> departments = departmentDAO.getAllDepartments();

        for (Department dept : departments) {
            int count = departmentDAO.getEmployeeCountByDepartment(dept.getDepartmentId());
            if (count > 0) {
                dataset.setValue(dept.getName(), count);
            }
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Employee Distribution by Department",
                dataset,
                true,
                true,
                false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Other", new Color(200, 200, 200));
        plot.setBackgroundPaint(Color.WHITE);
        plot.setLegendLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator(
                "{0} ({1} employees - {2})"));

        panel.add(new ChartPanel(chart), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDepartmentAnalysisPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create bar chart for department-wise employee count
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Department> departments = departmentDAO.getAllDepartments();

        for (Department dept : departments) {
            int count = departmentDAO.getEmployeeCountByDepartment(dept.getDepartmentId());
            dataset.addValue(count, "Employees", dept.getName());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Department-wise Employee Count",
                "Department",
                "Number of Employees",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);

        panel.add(new ChartPanel(chart), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAttendanceTrendsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        try {
            LocalDate startDate = LocalDate.parse(startDateField.getText());
            LocalDate endDate = LocalDate.parse(endDateField.getText());

            // Create time series chart for attendance trends
            TimeSeries presentSeries = new TimeSeries("Present");
            TimeSeries absentSeries = new TimeSeries("Absent");
            TimeSeries lateSeries = new TimeSeries("Late");

            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                int presentCount = attendanceDAO.getAttendanceCountByStatus("PRESENT", currentDate, currentDate);
                int absentCount = attendanceDAO.getAttendanceCountByStatus("ABSENT", currentDate, currentDate);
                int lateCount = attendanceDAO.getAttendanceCountByStatus("LATE", currentDate, currentDate);

                presentSeries.add(new Day(java.sql.Date.valueOf(currentDate)), presentCount);
                absentSeries.add(new Day(java.sql.Date.valueOf(currentDate)), absentCount);
                lateSeries.add(new Day(java.sql.Date.valueOf(currentDate)), lateCount);

                currentDate = currentDate.plusDays(1);
            }

            TimeSeriesCollection dataset = new TimeSeriesCollection();
            dataset.addSeries(presentSeries);
            dataset.addSeries(absentSeries);
            dataset.addSeries(lateSeries);

            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                    "Attendance Trends",
                    "Date",
                    "Count",
                    dataset,
                    true,
                    true,
                    false
            );

            XYPlot plot = chart.getXYPlot();
            plot.setBackgroundPaint(Color.WHITE);
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setSeriesShapesVisible(0, true);
            renderer.setSeriesShapesVisible(1, true);
            renderer.setSeriesShapesVisible(2, true);
            plot.setRenderer(renderer);

            panel.add(new ChartPanel(chart), BorderLayout.CENTER);
        } catch (Exception e) {
            panel.add(new JLabel("Invalid date format. Please use YYYY-MM-DD format.", JLabel.CENTER), BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createSalaryReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create bar chart for average salary by department
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<Department> departments = departmentDAO.getAllDepartments();

        for (Department dept : departments) {
            List<Employee> employees = employeeDAO.getAllEmployees();
            double avgSalary = employees.stream()
                    .filter(e -> e.getDepartmentId() == dept.getDepartmentId())
                    .mapToDouble(Employee::getSalary)
                    .average()
                    .orElse(0);

            if (avgSalary > 0) {
                dataset.addValue(avgSalary, "Average Salary", dept.getName());
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Average Salary by Department",
                "Department",
                "Average Salary ($)",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        chart.setBackgroundPaint(Color.WHITE);

        panel.add(new ChartPanel(chart), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPerformanceMetricsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create table with detailed statistics
        String[] columns = {"Metric", "Value", "Details"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        try {
            LocalDate startDate = LocalDate.parse(startDateField.getText());
            LocalDate endDate = LocalDate.parse(endDateField.getText());

            // Add statistics
            List<Employee> employees = employeeDAO.getAllEmployees();
            List<Department> departments = departmentDAO.getAllDepartments();

            tableModel.addRow(new Object[]{"Total Employees", employees.size(), "All departments"});

            long activeEmployees = employees.stream().filter(e -> "ACTIVE".equals(e.getStatus())).count();
            tableModel.addRow(new Object[]{"Active Employees", activeEmployees, "Currently employed"});

            double avgSalary = employees.stream().mapToDouble(Employee::getSalary).average().orElse(0);
            tableModel.addRow(new Object[]{"Average Salary", String.format("$%.2f", avgSalary), "Across all employees"});

            // Attendance statistics
            int totalPresent = attendanceDAO.getAttendanceCountByStatus("PRESENT", startDate, endDate);
            int totalAbsent = attendanceDAO.getAttendanceCountByStatus("ABSENT", startDate, endDate);
            int totalLate = attendanceDAO.getAttendanceCountByStatus("LATE", startDate, endDate);

            tableModel.addRow(new Object[]{"Total Present (Period)", totalPresent, startDate + " to " + endDate});
            tableModel.addRow(new Object[]{"Total Absent (Period)", totalAbsent, startDate + " to " + endDate});
            tableModel.addRow(new Object[]{"Total Late (Period)", totalLate, startDate + " to " + endDate});

            // Department-wise details
            for (Department dept : departments) {
                int count = departmentDAO.getEmployeeCountByDepartment(dept.getDepartmentId());
                tableModel.addRow(new Object[]{dept.getName() + " Employees", count, dept.getDescription()});
            }

            // Top performers (employees with best attendance)
            tableModel.addRow(new Object[]{"", "", ""});
            tableModel.addRow(new Object[]{"Top Performers", "", "Best Attendance (%)"});

            for (Employee emp : employees) {
                double attendancePercent = attendanceDAO.getAttendancePercentage(emp.getEmployeeId(), startDate, endDate);
                if (attendancePercent >= 90) {
                    tableModel.addRow(new Object[]{emp.getFullName(),
                            String.format("%.1f%%", attendancePercent),
                            emp.getPosition()});
                }
            }

        } catch (Exception e) {
            tableModel.addRow(new Object[]{"Error", "Invalid date format", "Please use YYYY-MM-DD format"});
        }

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void generateAllReports() {
        // Refresh all report panels
        reportTabs.removeAll();

        reportTabs.addTab("Employee Statistics", createEmployeeStatsPanel());
        reportTabs.addTab("Department Analysis", createDepartmentAnalysisPanel());
        reportTabs.addTab("Attendance Trends", createAttendanceTrendsPanel());
        reportTabs.addTab("Salary Reports", createSalaryReportsPanel());
        reportTabs.addTab("Performance Metrics", createPerformanceMetricsPanel());

        reportTabs.revalidate();
        reportTabs.repaint();

        JOptionPane.showMessageDialog(this, "Reports generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.endsWith(".csv")) {
                filePath += ".csv";
            }

            try (java.io.PrintWriter writer = new java.io.PrintWriter(filePath)) {
                writer.println("Employee Management System Report");
                writer.println("Generated on: " + new java.util.Date());
                writer.println("Period: " + startDateField.getText() + " to " + endDateField.getText());
                writer.println();

                // Export employee statistics
                writer.println("EMPLOYEE STATISTICS");
                writer.println("==================");
                List<Employee> employees = employeeDAO.getAllEmployees();
                writer.println("Total Employees: " + employees.size());
                writer.println("Active Employees: " + employees.stream().filter(e -> "ACTIVE".equals(e.getStatus())).count());
                writer.println();

                // Export department statistics
                writer.println("DEPARTMENT STATISTICS");
                writer.println("=====================");
                List<Department> departments = departmentDAO.getAllDepartments();
                for (Department dept : departments) {
                    int count = departmentDAO.getEmployeeCountByDepartment(dept.getDepartmentId());
                    writer.println(dept.getName() + ": " + count + " employees");
                }
                writer.println();

                // Export attendance statistics
                try {
                    LocalDate startDate = LocalDate.parse(startDateField.getText());
                    LocalDate endDate = LocalDate.parse(endDateField.getText());

                    writer.println("ATTENDANCE STATISTICS");
                    writer.println("======================");
                    writer.println("Present: " + attendanceDAO.getAttendanceCountByStatus("PRESENT", startDate, endDate));
                    writer.println("Absent: " + attendanceDAO.getAttendanceCountByStatus("ABSENT", startDate, endDate));
                    writer.println("Late: " + attendanceDAO.getAttendanceCountByStatus("LATE", startDate, endDate));
                } catch (Exception e) {
                    writer.println("Invalid date range for attendance statistics");
                }

                JOptionPane.showMessageDialog(this, "Report exported successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to export report", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToPDF() {
        // This is a placeholder for PDF export
        // You would need to add a PDF library like iText or Apache PDFBox
        JOptionPane.showMessageDialog(this,
                "PDF export functionality requires additional libraries.\n" +
                        "Please add iText or Apache PDFBox to your project dependencies.",
                "PDF Export",
                JOptionPane.INFORMATION_MESSAGE);
    }
}