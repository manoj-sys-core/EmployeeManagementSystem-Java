package com.employee.gui;

import com.employee.dao.DepartmentDAO;
import com.employee.dao.EmployeeDAO;
import com.employee.model.Department;
import com.employee.model.Employee;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel {
    private EmployeeDAO employeeDAO;
    private DepartmentDAO departmentDAO;

    public DashboardPanel() {
        employeeDAO = new EmployeeDAO();
        departmentDAO = new DepartmentDAO();
        initializeComponents();
        layoutComponents();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void layoutComponents() {
        // Title
        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Stats panel
        JPanel statsPanel = createStatsPanel();

        // Charts panel
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        chartsPanel.add(createDepartmentChart());
        chartsPanel.add(createRoleDistributionChart());

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(chartsPanel, BorderLayout.CENTER);
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Get data
        List<Employee> employees = employeeDAO.getAllEmployees();
        List<Department> departments = departmentDAO.getAllDepartments();

        int totalEmployees = employees.size();
        int activeEmployees = (int) employees.stream().filter(e -> "ACTIVE".equals(e.getStatus())).count();
        int totalDepartments = departments.size();
        double avgSalary = employees.stream().mapToDouble(Employee::getSalary).average().orElse(0);

        // Create stat cards
        panel.add(createStatCard("Total Employees", String.valueOf(totalEmployees), new Color(52, 152, 219)));
        panel.add(createStatCard("Active Employees", String.valueOf(activeEmployees), new Color(46, 204, 113)));
        panel.add(createStatCard("Departments", String.valueOf(totalDepartments), new Color(155, 89, 182)));
        panel.add(createStatCard("Avg. Salary", String.format("$%.2f", avgSalary), new Color(241, 196, 15)));

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.DARK_GRAY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createDepartmentChart() {
        // Get data
        List<Department> departments = departmentDAO.getAllDepartments();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (Department dept : departments) {
            int count = departmentDAO.getEmployeeCountByDepartment(dept.getDepartmentId());
            dataset.addValue(count, "Employees", dept.getName());
        }

        // Create chart
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

        // Customize chart
        chart.setBackgroundPaint(Color.WHITE);

        return new ChartPanel(chart);
    }

    private JPanel createRoleDistributionChart() {
        // Get data
        List<Employee> employees = employeeDAO.getAllEmployees();
        Map<String, Long> roleCount = employees.stream()
                .collect(Collectors.groupingBy(Employee::getPosition, Collectors.counting()));

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Long> entry : roleCount.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        // Create chart
        JFreeChart chart = ChartFactory.createPieChart(
                "Role Distribution",
                dataset,
                true,
                true,
                false
        );

        // Customize chart
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Other", new Color(200, 200, 200));
        plot.setExplodePercent("Other", 0.10);
        plot.setBackgroundPaint(Color.WHITE);

        return new ChartPanel(chart);
    }
}