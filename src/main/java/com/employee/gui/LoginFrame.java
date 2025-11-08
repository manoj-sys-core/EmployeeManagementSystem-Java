package com.employee.gui;

import com.employee.dao.UserDAO;
import com.employee.model.User;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;


public class LoginFrame extends JFrame {
    private final UserDAO userDAO;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private GradientButton loginButton, exitButton;
    private JCheckBox showPassword;

    public LoginFrame() {
        try { UIManager.setLookAndFeel(new FlatLightLaf()); } catch (Exception ignored) {}

        userDAO = new UserDAO();
        initComponents();
        layoutComponents();
        setupListeners();
        configureFrame();
    }

    private void initComponents() {
        usernameField = new JTextField(1); // width controlled by layout
        passwordField = new JPasswordField(1);
        showPassword = new JCheckBox("Show Password");

        usernameField.putClientProperty("JTextField.placeholderText", "name@company.com");
        passwordField.putClientProperty("JTextField.placeholderText", "Enter your password");

        Font f = new Font("Poppins", Font.PLAIN, 14);
        usernameField.setFont(f);
        passwordField.setFont(f);
        showPassword.setOpaque(false);

        loginButton = new GradientButton("Sign in", new Color(0x7C3AED), new Color(0x06B6D4));
        exitButton  = new GradientButton("Exit", new Color(0x64748B), new Color(0x94A3B8));

        loginButton.setPreferredSize(new Dimension(120, 40));
        exitButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setHorizontalAlignment(SwingConstants.CENTER);
        exitButton.setHorizontalAlignment(SwingConstants.CENTER);

        // accessible tooltips
        loginButton.setToolTipText("Sign in to Manthan HR Suite");
        exitButton.setToolTipText("Exit application");
    }

    private void layoutComponents() {
        JPanel root = new JPanel(new BorderLayout());

        // LEFT branding
        JPanel left = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color c1 = new Color(0x021B79);
                Color c2 = new Color(0x0575E6);
                GradientPaint gp = new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // soft decorative circles
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.06f));
                g2.setColor(Color.WHITE);
                int r = Math.min(getWidth(), getHeight()) / 3;
                g2.fillOval(getWidth() - r / 2, getHeight() / 4 - r / 6, r, r);
                g2.fillOval(getWidth() / 6, getHeight() - r / 2, r / 2, r / 2);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
        };
        left.setPreferredSize(new Dimension(420, 0));
        left.setLayout(new GridBagLayout());
        left.setBorder(new EmptyBorder(48, 48, 48, 48));

        GridBagConstraints lg = new GridBagConstraints();
        lg.gridx = 0; lg.gridy = 0; lg.anchor = GridBagConstraints.WEST; lg.insets = new Insets(8,0,8,0);

        JLabel logo = new JLabel("💼");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        logo.setForeground(Color.WHITE);
        left.add(logo, lg);

        lg.gridy++;
        JLabel product = new JLabel("WorkForce One");
        product.setFont(new Font("Poppins", Font.BOLD, 28));
        product.setForeground(Color.WHITE);
        left.add(product, lg);

        lg.gridy++;
        JLabel tagline = new JLabel("People-first HR management, simplified.");
        tagline.setFont(new Font("Poppins", Font.PLAIN, 14));
        tagline.setForeground(new Color(220, 235, 255));
        left.add(tagline, lg);

        lg.gridy++; lg.insets = new Insets(18,0,0,0);
        JPanel bullets = new JPanel(new GridLayout(3,1,6,6));
        bullets.setOpaque(false);
        bullets.add(buildBullet("Smart employee records"));
        bullets.add(buildBullet("Role-based access control"));
        bullets.add(buildBullet("Enterprise-grade security"));
        left.add(bullets, lg);

        // RIGHT area - center the card vertically and horizontally
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(new Color(0xF3F6FB));

        GridBagConstraints rg = new GridBagConstraints();
        rg.fill = GridBagConstraints.BOTH;
        rg.insets = new Insets(12, 12, 12, 12);

        JPanel card = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(0,0,0,12));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(28, 36, 28, 36));
        card.setPreferredSize(new Dimension(480, 420));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2; c.anchor = GridBagConstraints.WEST; c.insets = new Insets(10,6,6,6);

        JLabel welcome = new JLabel("Welcome back");
        welcome.setFont(new Font("Poppins", Font.BOLD, 24));
        welcome.setForeground(new Color(0x0F172A));
        card.add(welcome, c);

        c.gridy++;
        JLabel desc = new JLabel("Sign in to manage your team's records and approvals");
        desc.setFont(new Font("Poppins", Font.PLAIN, 13));
        desc.setForeground(new Color(0x6B7280));
        card.add(desc, c);

        // input fields — consistent width and spacing
        c.gridy++; c.gridwidth = 2; c.fill = GridBagConstraints.HORIZONTAL; c.insets = new Insets(18,6,6,6);
        card.add(buildFieldWithIcon("📧", usernameField), c);

        c.gridy++; c.insets = new Insets(12,6,6,6);
        card.add(buildFieldWithIcon("🔒", passwordField), c);

        // show password (right aligned)
        c.gridy++; c.gridwidth = 1; c.anchor = GridBagConstraints.WEST; c.insets = new Insets(8,6,6,6);
        card.add(showPassword, c);

        // buttons row: left-aligned Sign in, right-aligned Exit
        c.gridy++; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER; c.insets = new Insets(18,6,6,6);
        JPanel btnRow = new JPanel(new BorderLayout());
        btnRow.setOpaque(false);

        JPanel leftWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftWrap.setOpaque(false);
        leftWrap.add(loginButton);

        JPanel rightWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightWrap.setOpaque(false);
        rightWrap.add(exitButton);

        btnRow.add(leftWrap, BorderLayout.WEST);
        btnRow.add(rightWrap, BorderLayout.EAST);
        card.add(btnRow, c);

        // footer
        c.gridy++; JLabel footer = new JLabel("© 2025 Manthan Technologies", SwingConstants.CENTER);
        footer.setFont(new Font("Poppins", Font.PLAIN, 12)); footer.setForeground(new Color(0x9CA3AF));
        card.add(footer, c);

        rg.gridx = 0; rg.gridy = 0; right.add(card, rg);

        root.add(left, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel buildBullet(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Poppins", Font.PLAIN, 13));
        l.setForeground(new Color(225, 235, 255));
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);
        p.add(l);
        return p;
    }

    private JPanel buildFieldWithIcon(String icon, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(10,0));
        p.setOpaque(false);
        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        ico.setBorder(new EmptyBorder(6,6,6,6));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(true);
        wrapper.setBackground(new Color(248,249,250));
        wrapper.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0xE6E9EE)), new EmptyBorder(10,10,10,10)));
        wrapper.add(field, BorderLayout.CENTER);

        p.add(ico, BorderLayout.WEST);
        p.add(wrapper, BorderLayout.CENTER);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                wrapper.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0x3B82F6), 2), new EmptyBorder(9,9,9,9)));
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                wrapper.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0xE6E9EE)), new EmptyBorder(10,10,10,10)));
            }
        });

        return p;
    }

    private void setupListeners() {
        showPassword.addActionListener(e -> passwordField.setEchoChar(showPassword.isSelected() ? (char)0 : '•'));
        loginButton.addActionListener(this::handleLogin);
        exitButton.addActionListener(e -> System.exit(0));
        passwordField.addActionListener(this::handleLogin);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Welcome, " + username + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            new MainFrame(user).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    private void configureFrame() {
        setTitle("Manthan — Employee Management");
        setSize(980, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
    }

    // ---- Gradient button implementation: visible, smooth rounded button ----
    private static class GradientButton extends JButton {
        private final Color start, end;

        public GradientButton(String text, Color start, Color end) {
            super(text);
            this.start = start;
            this.end = end;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(); int h = getHeight();
            Paint gp = new GradientPaint(0, 0, start, w, h, end);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w, h, 18, 18);
            g2.setColor(new Color(255,255,255,30));
            g2.fillRoundRect(0, 0, w, h/2, 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public void setBorder(Border border) {
            // keep default empty border to preserve padding
            super.setBorder(border);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
