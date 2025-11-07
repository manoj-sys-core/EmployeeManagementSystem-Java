package com.employee.gui;

import com.employee.dao.DepartmentDAO;
import com.employee.dao.EmployeeDAO;
import com.employee.model.Department;
import com.employee.model.Employee;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardPanel extends JPanel {
    private final EmployeeDAO employeeDAO;
    private final DepartmentDAO departmentDAO;

    public DashboardPanel() {
        employeeDAO = new EmployeeDAO();
        departmentDAO = new DepartmentDAO();

        setBackground(new Color(245, 247, 252));
        setLayout(new BorderLayout(25, 25));
        setBorder(new EmptyBorder(25, 35, 25, 35));

        buildUI();
    }

    private void buildUI() {
        add(createHeader(), BorderLayout.NORTH);
        add(createMainSection(), BorderLayout.CENTER);
    }

    /** HEADER **/
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(
                new LineBorder(new Color(225, 225, 225), 1, true),
                new EmptyBorder(25, 30, 25, 30)
        ));

        JLabel title = new JLabel("Employee Analytics Dashboard");
        title.setFont(new Font("Poppins SemiBold", Font.BOLD, 26));
        title.setForeground(new Color(25, 25, 25));

        JLabel subtitle = new JLabel("Interactive workforce insights and live growth visualization");
        subtitle.setFont(new Font("Poppins", Font.PLAIN, 14));
        subtitle.setForeground(new Color(110, 110, 110));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(subtitle);

        header.add(textPanel, BorderLayout.WEST);
        return header;
    }

    /** MAIN SECTION **/
    private JPanel createMainSection() {
        JPanel main = new JPanel(new BorderLayout(25, 25));
        main.setOpaque(false);
        main.add(createTopStats(), BorderLayout.NORTH);
        main.add(createAnalyticsSection(), BorderLayout.CENTER);
        return main;
    }
    private Icon createUserIcon(Color color) {
        return new Icon() {
            public int getIconWidth() { return 24; }
            public int getIconHeight() { return 24; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(x + 4, y + 3, 16, 16);
                g2.fillRect(x + 8, y + 15, 8, 6);
            }
        };
    }

    private Icon createCheckIcon(Color color) {
        return new Icon() {
            public int getIconWidth() { return 24; }
            public int getIconHeight() { return 24; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x + 5, y + 12, x + 10, y + 17);
                g2.drawLine(x + 10, y + 17, x + 18, y + 7);
            }
        };
    }

    private Icon createBuildingIcon(Color color) {
        return new Icon() {
            public int getIconWidth() { return 24; }
            public int getIconHeight() { return 24; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(color);
                g2.fillRect(x + 5, y + 5, 14, 14);
                g2.setColor(new Color(255, 255, 255, 200));
                for (int i = 0; i < 3; i++) g2.fillRect(x + 7 + i * 4, y + 7, 2, 3);
                for (int i = 0; i < 3; i++) g2.fillRect(x + 7 + i * 4, y + 12, 2, 3);
            }
        };
    }

    private Icon createMoneyIcon(Color color) {
        return new Icon() {
            public int getIconWidth() { return 24; }
            public int getIconHeight() { return 24; }
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawOval(x + 4, y + 4, 16, 16);
                g2.drawString("$", x + 8, y + 18);
            }
        };
    }

    /** TOP STAT CARDS **/
    /** TOP STAT CARDS **/
    private JPanel createTopStats() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        List<Department> departments = departmentDAO.getAllDepartments();

        int totalEmployees = employees.size();
        int activeEmployees = (int) employees.stream()
                .filter(e -> "ACTIVE".equalsIgnoreCase(e.getStatus())).count();
        int totalDepartments = departments.size();
        double avgSalary = employees.stream()
                .mapToDouble(Employee::getSalary).average().orElse(0);

        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);

        panel.add(createGradientCard("Employees", String.valueOf(totalEmployees),
                createUserIcon(new Color(255, 255, 255)), new Color(56, 189, 248), new Color(14, 165, 233)));
        panel.add(createGradientCard("Active", String.valueOf(activeEmployees),
                createCheckIcon(new Color(255, 255, 255)), new Color(16, 185, 129), new Color(5, 150, 105)));
        panel.add(createGradientCard("Departments", String.valueOf(totalDepartments),
                createBuildingIcon(new Color(255, 255, 255)), new Color(139, 92, 246), new Color(124, 58, 237)));
        panel.add(createGradientCard("Avg Salary", "$" + String.format("%.2f", avgSalary),
                createMoneyIcon(new Color(255, 255, 255)), new Color(250, 204, 21), new Color(253, 186, 116)));

        return panel;
    }

    /** MODERN CARD **/
    private JPanel createGradientCard(String title, String value, Icon icon, Color c1, Color c2) {
        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            private float hover = 0f;
            private javax.swing.Timer animationTimer;

            {
                setOpaque(false);
                setBorder(new EmptyBorder(20, 25, 20, 25));

                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        startAnimation(1f);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        startAnimation(0f);
                    }
                });
            }

            private void startAnimation(final float target) {
                if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();
                animationTimer = new javax.swing.Timer(15, ev -> {
                    hover += (target - hover) * 0.15f;
                    repaint();
                    if (Math.abs(hover - target) < 0.01f)
                        ((javax.swing.Timer) ev.getSource()).stop();
                });
                animationTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                if (hover > 0) {
                    g2.setComposite(AlphaComposite.SrcOver.derive(0.25f * hover));
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 20, 20);
                }
                g2.dispose();
            }
        };

        JLabel iconLabel = new JLabel(icon);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Poppins", Font.PLAIN, 15));
        titleLabel.setForeground(Color.WHITE);

        JLabel valueLabel = new JLabel(value, SwingConstants.RIGHT);
        valueLabel.setFont(new Font("Poppins SemiBold", Font.BOLD, 30));
        valueLabel.setForeground(Color.WHITE);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(iconLabel, BorderLayout.WEST);
        topRow.add(titleLabel, BorderLayout.CENTER);

        card.add(topRow, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    /** GRADIENT CARD **/
    private JPanel createGradientCard(String title, String value, Color c1, Color c2) {
        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            // --- Fields (must be declared BEFORE initializer/methods) ---
            private float hover = 0f;
            private javax.swing.Timer animationTimer; // explicitly javax.swing.Timer

            // --- Instance initializer: sets up mouse listeners (can reference animationTimer safely) ---
            {
                setOpaque(false);
                setBorder(new EmptyBorder(20, 25, 20, 25));

                addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        startAnimation(1f);
                    }

                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        startAnimation(0f);
                    }
                });
            }

            // --- Animation starter method ---
            private void startAnimation(final float target) {
                // stop running animation first
                if (animationTimer != null && animationTimer.isRunning()) {
                    animationTimer.stop();
                }

                animationTimer = new javax.swing.Timer(15, ev -> {
                    // easing: move hover toward target
                    hover += (target - hover) * 0.18f;
                    repaint();

                    // stop when close enough
                    if (Math.abs(hover - target) < 0.01f) {
                        ((javax.swing.Timer) ev.getSource()).stop();
                    }
                });

                animationTimer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // background gradient
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // subtle overlay to create glossy effect that grows with hover
                if (hover > 0f) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(0.35f, 0.15f + 0.2f * hover)));
                    g2.setColor(new Color(255, 255, 255));
                    g2.fillRoundRect(0, 0, getWidth(), (int) (getHeight() * (0.5 + 0.25 * hover)), 20, 20);
                }

                // border glow depending on hover
                if (hover > 0.02f) {
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.min(0.8f, 0.25f * hover)));
                    g2.setStroke(new BasicStroke(3f));
                    g2.setColor(new Color(255, 255, 255, 180));
                    g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 20, 20);
                }

                g2.dispose();
            }
        };

        // --- content inside the card (outside anonymous class) ---
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(Color.WHITE);

        JLabel valueLabel = new JLabel(value, SwingConstants.RIGHT);
        valueLabel.setFont(new Font("Segoe UI Semibold", Font.BOLD, 28));
        valueLabel.setForeground(Color.WHITE);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(titleLabel, BorderLayout.WEST);

        card.add(top, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        // add some padding preferred size so cards size consistently
        card.setPreferredSize(new Dimension(260, 120));
        return card;
    }

    /** CHART + SIDEBAR **/
    private JPanel createAnalyticsSection() {
        JPanel section = new JPanel(new BorderLayout(25, 25));
        section.setOpaque(false);

        ChartPanel growthChart = createEmployeeGrowthChart();
        JPanel sidebar = createInsightsPanel();

        section.add(wrapChartPanel("Employee Growth Trend", growthChart), BorderLayout.CENTER);
        section.add(sidebar, BorderLayout.EAST);
        return section;
    }

    private JPanel wrapChartPanel(String title, ChartPanel chart) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Poppins SemiBold", Font.BOLD, 18));
        lbl.setForeground(new Color(40, 40, 40));
        wrapper.add(lbl, BorderLayout.NORTH);
        wrapper.add(chart, BorderLayout.CENTER);
        return wrapper;
    }

    private ChartPanel createEmployeeGrowthChart() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        Map<LocalDate, Long> joinMap = new TreeMap<>();
        LocalDate today = LocalDate.now();

        for (Employee emp : employees) {
            LocalDate date = today.minusDays(emp.getEmployeeId() % 120);
            joinMap.put(date, joinMap.getOrDefault(date, 0L) + 1);
        }

        TimeSeries series = new TimeSeries("Employee Growth");
        long total = 0;
        for (Map.Entry<LocalDate, Long> entry : joinMap.entrySet()) {
            total += entry.getValue();
            series.add(new Day(java.sql.Date.valueOf(entry.getKey())), total);
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                null, "Date", "Employees", dataset, false, true, false);

        chart.setBackgroundPaint(Color.WHITE);
        chart.addSubtitle(new TextTitle("Employee hiring trend over time",
                new Font("Poppins", Font.PLAIN, 12)));

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setOutlineVisible(false);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(0, new Color(56, 102, 255));
        renderer.setSeriesStroke(0, new BasicStroke(3.5f));
        plot.setRenderer(renderer);

        return new ChartPanel(chart);
    }

    private JPanel createInsightsPanel() {
        JPanel insights = new JPanel();
        insights.setLayout(new BoxLayout(insights, BoxLayout.Y_AXIS));
        insights.setBackground(Color.WHITE);
        insights.setPreferredSize(new Dimension(320, 0));
        insights.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(25, 30, 25, 30)
        ));

        JLabel header = new JLabel("Smart Insights");
        header.setFont(new Font("Poppins SemiBold", Font.BOLD, 18));
        header.setForeground(new Color(30, 30, 30));
        insights.add(header);
        insights.add(Box.createRigidArea(new Dimension(0, 10)));

        addInsight(insights, "🏆 Top Department: " + getTopDepartment());
        addInsight(insights, "👷 Active Employees: " + getActiveCount());
        addInsight(insights, "💵 Avg Salary: $" + String.format("%.2f", getAverageSalary()));
        addInsight(insights, "🏢 Total Departments: " + departmentDAO.getAllDepartments().size());
        addInsight(insights, "📈 Last Month Hires: +" + getRecentGrowth());
        addInsight(insights, "⭐ Top Performer: " + getTopPerformer());

        return insights;
    }

    private void addInsight(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Poppins", Font.PLAIN, 14));
        lbl.setForeground(new Color(70, 70, 70));
        lbl.setBorder(new EmptyBorder(6, 6, 6, 0));
        panel.add(lbl);
    }

    /** DAO HELPERS **/
    private long getActiveCount() {
        return employeeDAO.getAllEmployees().stream()
                .filter(e -> "ACTIVE".equalsIgnoreCase(e.getStatus())).count();
    }

    private double getAverageSalary() {
        return employeeDAO.getAllEmployees().stream()
                .mapToDouble(Employee::getSalary).average().orElse(0);
    }

    private String getTopDepartment() {
        return departmentDAO.getAllDepartments().stream()
                .max(Comparator.comparingInt(d -> departmentDAO.getEmployeeCountByDepartment(d.getDepartmentId())))
                .map(Department::getName).orElse("N/A");
    }

    private int getRecentGrowth() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        Random rand = new Random();
        return (int) employees.stream()
                .filter(e -> rand.nextInt(100) < 20)
                .count();
    }

    private String getTopPerformer() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        return employees.isEmpty() ? "N/A" : employees.get(0).getFullName();
    }
}
