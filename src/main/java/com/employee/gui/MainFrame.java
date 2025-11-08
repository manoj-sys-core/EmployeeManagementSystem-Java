package com.employee.gui;
import com.employee.model.User;
import com.employee.utils.DatabaseUtil;
import com.employee.dao.UserDAO;
import com.employee.model.User;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;


public class MainFrame extends JFrame {
    private final User currentUser;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private TransitionPanel mainPanel;
    private JLabel userLabel;
    private JButton logoutButton;
    private Map<String, JPanel> pages;

    private final java.util.List<NavItem> navItems = new java.util.ArrayList<>();
    private boolean sidebarCollapsed = false;

    private JPanel navButtonsContainer;
    private JButton collapseButton;

    // palette
    private final Color bg = new Color(246, 248, 250);
    private final Color surface = new Color(255, 255, 255);
    private final Color accent = new Color(37, 99, 235); // blue
    private final Color muted = new Color(100, 116, 139);

    // widths
    private final int expandedWidth = 220;
    private final int collapsedWidth = 68;

    // brand/logo
    private JLabel logoLabel;
    private JLabel nameLabel;

    public MainFrame(User user) {
        this.currentUser = user;
        initializeComponents();
        layoutComponents();
        setupListeners();
        configureFrame();
    }

    private void initializeComponents() {
        contentPanel = new JPanel(new BorderLayout());
        sidebarPanel = new JPanel();
        mainPanel = new TransitionPanel(new CardLayout());

        userLabel = new JLabel("Welcome, " + currentUser.getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        userLabel.setForeground(muted);

        logoutButton = createPillButton("Logout", new Color(239, 68, 68));
        logoutButton.setPreferredSize(new Dimension(96, 34));

        pages = new LinkedHashMap<>();
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            pages.put("Dashboard", new DashboardPanel());
            pages.put("Employees", new EmployeeManagementPanel());
            pages.put("Departments", new DepartmentManagementPanel());
            pages.put("Attendance", new AttendancePanel());
            pages.put("Reports", new ReportsPanel());
            pages.put("Settings", new SettingsPanel());
        } else if ("MANAGER".equalsIgnoreCase(currentUser.getRole())) {
            pages.put("Dashboard", new DashboardPanel());
            pages.put("Employees", new EmployeeManagementPanel());
            pages.put("Attendance", new AttendancePanel());
            pages.put("Reports", new ReportsPanel());
        } else {
            pages.put("My Profile", new EmployeeProfilePanel(currentUser));
            pages.put("My Attendance", new EmployeeAttendancePanel(currentUser));
            pages.put("Leave Requests", new LeaveRequestPanel(currentUser));
        }
    }

    private void layoutComponents() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(surface);
        topBar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(8, 14, 8, 14)
        ));

        JLabel appTitle = new JLabel("Employee Management");
        appTitle.setFont(new Font("Segoe UI Semibold", Font.BOLD, 16));
        appTitle.setForeground(accent);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        right.setOpaque(false);

        JTextField search = new JTextField(16);
        search.setPreferredSize(new Dimension(200, 28));
        search.setBorder(new CompoundBorder(
                new LineBorder(new Color(226, 232, 240), 1, true),
                new EmptyBorder(4, 8, 4, 8)
        ));
        search.setBackground(new Color(250, 252, 255));
        search.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        right.add(search);
        right.add(userLabel);
        right.add(logoutButton);

        topBar.add(appTitle, BorderLayout.WEST);
        topBar.add(right, BorderLayout.EAST);

        // SIDEBAR
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBackground(bg);
        sidebarPanel.setBorder(new MatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));
        sidebarPanel.setPreferredSize(new Dimension(expandedWidth, getHeight()));

        // BRAND
        JPanel brand = new JPanel(new BorderLayout());
        brand.setOpaque(false);
        brand.setBorder(new EmptyBorder(10, 12, 10, 12));

        logoLabel = createLogoLabel();

        nameLabel = new JLabel(currentUser.getUsername());
        nameLabel.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        nameLabel.setForeground(new Color(33, 37, 41));
        nameLabel.setBorder(new EmptyBorder(0, 8, 0, 0));
        nameLabel.setOpaque(false);

        JPanel brandLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        brandLeft.setOpaque(false);
        brandLeft.add(logoLabel);
        brandLeft.add(nameLabel);

        brand.add(brandLeft, BorderLayout.WEST);

        // collapse placed centered vertically in its area (avoid overlap)
        JPanel collapsePanel = new JPanel(new GridBagLayout());
        collapsePanel.setOpaque(false);
        collapseButton = createIconButton("◀");
        collapseButton.setToolTipText("Collapse sidebar");
        collapseButton.setPreferredSize(new Dimension(34, 34));
        collapseButton.setBorder(new EmptyBorder(6, 6, 6, 6));
        collapsePanel.add(collapseButton);
        brand.add(collapsePanel, BorderLayout.EAST);

        // NAV container
        navButtonsContainer = new JPanel();
        navButtonsContainer.setLayout(new BoxLayout(navButtonsContainer, BoxLayout.Y_AXIS));
        navButtonsContainer.setOpaque(false);
        navButtonsContainer.setBorder(new EmptyBorder(8, 6, 8, 6));

        for (String title : pages.keySet()) {
            String iconName = iconNameForTitle(title);
            NavItem ni = new NavItem(iconName, title);
            navItems.add(ni);
            navButtonsContainer.add(ni.container);
            navButtonsContainer.add(Box.createRigidArea(new Dimension(0, 6)));
        }

        JScrollPane scroll = new JScrollPane(navButtonsContainer,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBackground(bg);

        // footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 12, 10, 12));
        JLabel ver = new JLabel("v1.0.0");
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ver.setForeground(muted);
        footer.add(ver, BorderLayout.WEST);

        sidebarPanel.add(brand, BorderLayout.NORTH);
        sidebarPanel.add(scroll, BorderLayout.CENTER);
        sidebarPanel.add(footer, BorderLayout.SOUTH);

        // add pages with names
        for (Map.Entry<String, JPanel> entry : pages.entrySet()) {
            JPanel p = entry.getValue();
            p.setName(entry.getKey());
            p.putClientProperty("cardName", entry.getKey());
            mainPanel.add(p, entry.getKey());
        }

        contentPanel.add(topBar, BorderLayout.NORTH);
        contentPanel.add(sidebarPanel, BorderLayout.WEST);
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        add(contentPanel);

        // show first page and set active
        if (!pages.isEmpty() && !navItems.isEmpty()) {
            String first = pages.keySet().iterator().next();
            mainPanel.showCard(first);
            // ensure first nav item is active and shows correctly
            navItems.get(0).setActive(true);
        }

        // collapse action uses field collapseButton
        collapseButton.addActionListener(e -> toggleSidebar());

        // improve window close with modal confirmation (acts like a modal)
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int res = JOptionPane.showConfirmDialog(MainFrame.this,
                        "Do you really want to exit the application?",
                        "Exit",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (res == JOptionPane.YES_OPTION) {
                    DatabaseUtil.closeConnection();
                    dispose();
                    System.exit(0);
                }
            }
        });
    }

    private JLabel createLogoLabel() {
        JLabel lbl = new JLabel();
        lbl.setOpaque(false);
        lbl.setText("");
        lbl.setPreferredSize(new Dimension(44, 44)); // slightly bigger brand logo

        // Try to load profile.png in resources/icons first (so your profile image shows).
        ImageIcon profileIcon = loadIconResource("/icons/profile.png", 44, 44);
        if (profileIcon != null) {
            lbl.setIcon(profileIcon);
        } else {
            // fallback brand icon if profile not present
            ImageIcon brandIcon = loadIconResource("/icons/brand.png", 44, 44);
            if (brandIcon != null) lbl.setIcon(brandIcon);
        }
        return lbl;
    }

    /**
     * build icon base name from title if you don't provide explicit icons
     */
    private String iconNameForTitle(String title) {
        String t = title.toLowerCase();
        if (t.contains("dash")) return "home";
        if (t.contains("employee")) return "employees";
        if (t.contains("department")) return "departments";
        if (t.contains("attend")) return "attendance";
        if (t.contains("report")) return "reports";
        if (t.contains("setting")) return "settings";
        if (t.contains("profile")) return "profile";
        if (t.contains("leave")) return "leave";
        return "dot";
    }

    private class NavItem {
        JPanel container;
        JLabel iconLabel;
        JLabel textLabel;
        String pageName;
        String iconBaseName;

        // requested size: 36px icon. container slightly larger for padding
        final int ICON_SIZE = 36;
        final Dimension ICON_PREF = new Dimension(48, 48);

        ImageIcon normalIcon;
        ImageIcon activeIcon; // kept but not auto-applied

        NavItem(String iconBaseName, String title) {
            this.iconBaseName = iconBaseName;
            this.pageName = title;
            container = new JPanel(new BorderLayout());
            container.setOpaque(false);
            container.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            container.setBorder(new EmptyBorder(6, 6, 6, 6));
            container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

            // load icons from resources: /icons/<iconBaseName>.png and <iconBaseName>_active.png
            normalIcon = loadIconResource("/icons/" + iconBaseName + ".png", ICON_SIZE, ICON_SIZE);
            activeIcon = loadIconResource("/icons/" + iconBaseName + "_active.png", ICON_SIZE, ICON_SIZE);
            // Do not swap icon on activation to avoid white icons — we will keep original visible.

            iconLabel = new JLabel();
            iconLabel.setIcon(normalIcon);
            iconLabel.setBorder(new EmptyBorder(0, 6, 0, 6));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setPreferredSize(ICON_PREF);
            iconLabel.setOpaque(false);

            textLabel = new JLabel(title);
            textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            textLabel.setForeground(muted);
            textLabel.setBorder(new EmptyBorder(4, 6, 4, 6));
            textLabel.setOpaque(false);

            // left aligned layout
            JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
            left.setOpaque(false);
            left.add(iconLabel);
            left.add(textLabel);

            container.add(left, BorderLayout.WEST);

            // hover and click
            MouseAdapter ma = new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    // hover highlight (not active)
                    if (!Boolean.TRUE.equals(container.getClientProperty("active"))) {
                        container.setBackground(new Color(245, 249, 255));
                        container.setOpaque(true);
                        textLabel.setForeground(accent);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!Boolean.TRUE.equals(container.getClientProperty("active"))) {
                        container.setOpaque(false);
                        textLabel.setForeground(muted);
                    } else {
                        textLabel.setForeground(accent);
                    }
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    // show page
                    showPage(pageName);
                    setActive(true);
                }
            };
            // attach listeners
            container.addMouseListener(ma);
            iconLabel.addMouseListener(ma);
            textLabel.addMouseListener(ma);

            container.setToolTipText(title);
        }

        void setActive(boolean active) {
            if (active) {
                for (NavItem ni : navItems) {
                    if (ni != this) ni.deactivateVisual();
                }
                container.putClientProperty("active", true);
                container.setOpaque(false);
                container.setBorder(new CompoundBorder(
                        new MatteBorder(0, 6, 0, 0, accent),
                        new EmptyBorder(6, 6, 6, 6)
                ));
                textLabel.setForeground(accent);
                textLabel.setFont(textLabel.getFont().deriveFont(Font.BOLD));
                // icon not changed
            } else {
                deactivateVisual();
            }
        }

        private void deactivateVisual() {
            container.putClientProperty("active", false);
            container.setOpaque(false);
            container.setBorder(new EmptyBorder(6, 6, 6, 6));
            textLabel.setForeground(muted);
            textLabel.setFont(textLabel.getFont().deriveFont(Font.PLAIN));
            if (normalIcon != null) iconLabel.setIcon(normalIcon);
            else iconLabel.setIcon(null);
        }

        void collapseForSidebar(boolean collapsed) {
            container.removeAll();
            if (collapsed) {
                textLabel.setVisible(false);
                JPanel center = new JPanel(new GridBagLayout());
                center.setOpaque(false);
                ImageIcon icon = (ImageIcon) (iconLabel.getIcon());
                JLabel centeredIcon;
                if (icon != null) {
                    centeredIcon = new JLabel(icon);
                    centeredIcon.setPreferredSize(ICON_PREF);
                } else {
                    centeredIcon = new JLabel();
                    centeredIcon.setPreferredSize(ICON_PREF);
                }
                centeredIcon.setOpaque(false);
                center.add(centeredIcon);
                container.add(center, BorderLayout.CENTER);
                container.setToolTipText(pageName);
            } else {
                textLabel.setVisible(true);
                JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
                left.setOpaque(false);
                left.add(iconLabel);
                left.add(textLabel);
                container.add(left, BorderLayout.WEST);
            }
            container.revalidate();
            container.repaint();
        }
    }

    private JButton createPillButton(String text, Color baseColor) {
        final Color color = baseColor;
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, color.brighter(), 0, h, color.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, h, h);
                setForeground(Color.WHITE);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setForeground(Color.WHITE);
        button.setBorder(new EmptyBorder(6, 10, 6, 10));
        button.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 13));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        return button;
    }

    private JButton createIconButton(String symbol) {
        JButton b = new JButton(symbol);
        b.setBorder(null);
        b.setContentAreaFilled(false);
        b.setFocusPainted(false);
        b.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 14));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void setupListeners() {
        logoutButton.addActionListener(e -> logout());
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            DatabaseUtil.closeConnection();
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    private void toggleSidebar() {
        sidebarCollapsed = !sidebarCollapsed;
        collapseButton.setText(sidebarCollapsed ? "▶" : "◀");

        sidebarPanel.setPreferredSize(new Dimension(sidebarCollapsed ? collapsedWidth : expandedWidth, getHeight()));
        for (NavItem ni : navItems) ni.collapseForSidebar(sidebarCollapsed);

        // Brand adjustments
        if (sidebarCollapsed) {
            nameLabel.setVisible(false);
            logoLabel.setPreferredSize(new Dimension(40, 40));
        } else {
            nameLabel.setVisible(true);
            logoLabel.setPreferredSize(new Dimension(44, 44));
        }

        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }

    private void showPage(String pageName) {
        mainPanel.showCard(pageName);
    }

    private void configureFrame() {
        setTitle("Employee Management System");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.NORMAL);
    }

    private static class TransitionPanel extends JPanel {
        private final CardLayout cardLayout;

        public TransitionPanel(CardLayout cl) {
            super(cl);
            this.cardLayout = cl;
        }

        public void showCard(String name) {
            if (name == null) return;
            cardLayout.show(this, name);
        }
    }

    // Helper: load icon resource and scale using progressive multi-step scaling
    private ImageIcon loadIconResource(String resourcePath, int w, int h) {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) return null;
            BufferedImage img = ImageIO.read(is);
            BufferedImage scaled = progressiveDownscale(img, w, h);
            return new ImageIcon(scaled);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Progressive multi-step downscale:
     * repeatedly scale down by half (or to target) using good rendering hints.
     * This typically yields sharper results than single-step massive downscales.
     */
    private BufferedImage progressiveDownscale(BufferedImage src, int targetW, int targetH) {
        if (src == null) return null;
        int currentW = src.getWidth();
        int currentH = src.getHeight();

        // If already small or equal, just return a high-quality scaled copy
        if (currentW == targetW && currentH == targetH) {
            return copyBufferedImage(src);
        }

        BufferedImage img = src;
        // If source is smaller than target (unlikely), upscale with high-quality
        if (currentW < targetW || currentH < targetH) {
            return scaleOnce(img, targetW, targetH, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        }

        // progressive downscale by half each step until near target
        while ((currentW / 2) >= targetW && (currentH / 2) >= targetH) {
            int nextW = Math.max(targetW, currentW / 2);
            int nextH = Math.max(targetH, currentH / 2);
            img = scaleOnce(img, nextW, nextH, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            currentW = img.getWidth();
            currentH = img.getHeight();
        }

        // final precise scale with bicubic
        if (currentW != targetW || currentH != targetH) {
            img = scaleOnce(img, targetW, targetH, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        }
        return img;
    }

    private BufferedImage scaleOnce(BufferedImage src, int w, int h, Object interpolationHint) {
        BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dest.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolationHint);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(src, 0, 0, w, h, null);
        g2.dispose();
        return dest;
    }

    private BufferedImage copyBufferedImage(BufferedImage src) {
        if (src == null) return null;
        BufferedImage copy = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = copy.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.drawImage(src, 0, 0, null);
        g2.dispose();
        return copy;
    }

    // Helper: convert Image to BufferedImage (kept for compatibility)
    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) return (BufferedImage) img;
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        if (w <= 0 || h <= 0) { w = 32; h = 32; }
        BufferedImage bimage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bimage.createGraphics();
        bGr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        bGr.drawImage(img, 0, 0, w, h, null);
        bGr.dispose();
        return bimage;
    }

    // white silhouette generator left available (not applied automatically)
    private BufferedImage makeWhiteSilhouette(BufferedImage src, int w, int h) {
        if (src == null) return null;
        BufferedImage scaled = progressiveDownscale(src, w, h);
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(scaled, 0, 0, null);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int px = out.getRGB(x, y);
                int alpha = (px >> 24) & 0xff;
                if (alpha != 0) {
                    int white = (alpha << 24) | (255 << 16) | (255 << 8) | 255;
                    out.setRGB(x, y, white);
                } else {
                    out.setRGB(x, y, px);
                }
            }
        }
        g.dispose();
        return out;
    }
}
