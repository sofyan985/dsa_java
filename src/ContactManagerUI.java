import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class ContactManagerUI extends JFrame {
    private final ContactManager manager = new ContactManager();
    private final JTable contactTable;
    private final DefaultTableModel tableModel;
    private final JTextField nameField = new JTextField(15);
    private final JTextField phoneField = new JTextField(15);
    private final JTextField searchField = new JTextField(15);
    private final JComboBox<String> categoryCombo = new JComboBox<>(new String[]{"All", "Family", "Friends", "Work", "Other"});
    private final Stack<String> searchHistory = new Stack<>();
    
    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);    // Blue
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241); // Light Gray
    private static final Color TEXT_COLOR = new Color(44, 62, 80);         // Dark Blue
    private static final Color PANEL_COLOR = new Color(255, 255, 255);     // White
    private static final Color NAVBAR_COLOR = new Color(52, 73, 94);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);

    public ContactManagerUI() {
        // Initialize table model
        String[] columnNames = {"Name", "Phone", "Category"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        contactTable = new JTable(tableModel);
        
        // Customize table appearance
        contactTable.setFont(new Font("Arial", Font.PLAIN, 14));
        contactTable.setRowHeight(25);
        contactTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        contactTable.getTableHeader().setBackground(PRIMARY_COLOR);
        contactTable.getTableHeader().setForeground(Color.WHITE);
        contactTable.setGridColor(new Color(200, 200, 200));
        contactTable.setShowGrid(true);
        contactTable.setSelectionBackground(PRIMARY_COLOR.brighter());
        contactTable.setSelectionForeground(Color.WHITE);
        
        // Set column widths
        contactTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        contactTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        contactTable.getColumnModel().getColumn(2).setPreferredWidth(100);

        setTitle("Contact Manager System");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);
        setLocationRelativeTo(null);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Navigation Bar
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(NAVBAR_COLOR);
        navBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Title in navbar
        JLabel titleLabel = new JLabel("Contact Manager System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        navBar.add(titleLabel, BorderLayout.WEST);

        // Navigation Buttons
        JPanel navButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navButtons.setBackground(NAVBAR_COLOR);

        String[] navItems = {
            "Show A-Z", "Show FIFO", "Show LIFO",
            "Quick Sort", "Merge Sort", "Search History"
        };

        for (String label : navItems) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 14));
            button.setForeground(Color.WHITE);
            button.setBackground(NAVBAR_COLOR);
            button.setBorderPainted(true);
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(NAVBAR_COLOR.brighter());
                }
                public void mouseExited(MouseEvent e) {
                    button.setBackground(NAVBAR_COLOR);
                }
            });

            switch (label) {
                case "Show A-Z":
                    button.addActionListener(e -> updateTable(manager.getContactsInOrder()));
                    break;
                case "Show FIFO":
                    button.addActionListener(e -> updateTable(manager.getContactsFIFO()));
                    break;
                case "Show LIFO":
                    button.addActionListener(e -> updateTable(manager.getContactsLIFO()));
                    break;
                case "Quick Sort":
                    button.addActionListener(e -> updateTable(manager.getContactsQuickSort()));
                    break;
                case "Merge Sort":
                    button.addActionListener(e -> updateTable(manager.getContactsMergeSort()));
                    break;
                case "Search History":
                    button.addActionListener(e -> showSearchHistory());
                    break;
            }
            navButtons.add(button);
        }

        navBar.add(navButtons, BorderLayout.EAST);
        mainPanel.add(navBar, BorderLayout.NORTH);

        // Left Panel (Input and Search)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(BACKGROUND_COLOR);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        // Add Contact Panel
        JPanel addContactPanel = new JPanel();
        addContactPanel.setLayout(new BoxLayout(addContactPanel, BoxLayout.Y_AXIS));
        addContactPanel.setBackground(PANEL_COLOR);
        addContactPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Add New Contact"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Name field
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        namePanel.setBackground(PANEL_COLOR);
        namePanel.add(new JLabel("Name:"));
        namePanel.add(nameField);
        addContactPanel.add(namePanel);
        addContactPanel.add(Box.createVerticalStrut(10));

        // Phone field
        JPanel phonePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        phonePanel.setBackground(PANEL_COLOR);
        phonePanel.add(new JLabel("Phone:"));
        phonePanel.add(phoneField);
        addContactPanel.add(phonePanel);
        addContactPanel.add(Box.createVerticalStrut(10));

        // Category field
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoryPanel.setBackground(PANEL_COLOR);
        categoryPanel.add(new JLabel("Category:"));
        categoryPanel.add(categoryCombo);
        addContactPanel.add(categoryPanel);
        addContactPanel.add(Box.createVerticalStrut(15));

        // Add button with border
        JButton addButton = new JButton("Add Contact");
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(true);
        addButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR.darker(), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> addContact());
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        addButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                addButton.setBackground(PRIMARY_COLOR.darker());
            }
            public void mouseExited(MouseEvent e) {
                addButton.setBackground(PRIMARY_COLOR);
            }
        });
        
        addContactPanel.add(addButton);

        leftPanel.add(addContactPanel);
        leftPanel.add(Box.createVerticalStrut(20));

        // Search Panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setBackground(PANEL_COLOR);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Search Contacts"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchInputPanel.setBackground(PANEL_COLOR);
        searchInputPanel.add(new JLabel("Search:"));
        searchInputPanel.add(searchField);
        searchPanel.add(searchInputPanel);
        searchPanel.add(Box.createVerticalStrut(10));

        // Search button with border
        JButton searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 14));
        searchButton.setBackground(PRIMARY_COLOR);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.setBorderPainted(true);
        searchButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR.darker(), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(e -> searchContact());
        searchButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        searchButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                searchButton.setBackground(PRIMARY_COLOR.darker());
            }
            public void mouseExited(MouseEvent e) {
                searchButton.setBackground(PRIMARY_COLOR);
            }
        });
        
        searchPanel.add(searchButton);

        leftPanel.add(searchPanel);
        leftPanel.add(Box.createVerticalGlue());

        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Center Panel (Table)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(BACKGROUND_COLOR);

        JScrollPane scrollPane = new JScrollPane(contactTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Contact List"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        centerPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private void updateTable(String contacts) {
        tableModel.setRowCount(0); // Clear the table
        if (contacts.equals("No contacts found.")) {
            return;
        }
        
        String[] lines = contacts.split("\n");
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(" - ");
            if (parts.length >= 2) {
                String name = parts[0];
                String[] remaining = parts[1].split(" \\(");
                String phone = remaining[0];
                String category = remaining[1].replace(")", "");
                tableModel.addRow(new Object[]{name, phone, category});
            }
        }
    }

    private void addContact() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String category = (String) categoryCombo.getSelectedItem();

        if (name.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both name and phone.", 
                "Input Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        manager.addContact(name, phone, category);
        nameField.setText("");
        phoneField.setText("");
        updateTable(manager.getContactsInOrder());
        
        JOptionPane.showMessageDialog(this, 
            "Contact added successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void searchContact() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term");
            return;
        }

        String result = manager.searchContact(searchTerm);
        if (result != null) {
            searchHistory.push(searchTerm);
            updateTable(result);
        } else {
            tableModel.setRowCount(0);
            JOptionPane.showMessageDialog(this, "No contacts found matching: " + searchTerm);
        }
    }

    private void showSearchHistory() {
        if (searchHistory.isEmpty()) {
            tableModel.setRowCount(0);
            JOptionPane.showMessageDialog(this, "No search history available");
            return;
        }

        StringBuilder history = new StringBuilder("Search History (Most Recent First):\n\n");
        Stack<String> tempStack = new Stack<>();
        while (!searchHistory.isEmpty()) {
            String term = searchHistory.pop();
            tempStack.push(term);
            history.append("• ").append(term).append("\n");
        }
        
        while (!tempStack.isEmpty()) {
            searchHistory.push(tempStack.pop());
        }
        
        tableModel.setRowCount(0);
        for (String term : history.toString().split("\n")) {
            if (term.trim().startsWith("•")) {
                tableModel.addRow(new Object[]{term.substring(2), "", "Search Term"});
            }
        }
    }
}