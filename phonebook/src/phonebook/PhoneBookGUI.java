package phonebook;

import com.formdev.flatlaf.intellijthemes.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

public class PhoneBookGUI {
    static JFrame frame = new JFrame("Książka Telefoniczna");

    private JButton addButton;
    private JButton deleteButton;
    private JTextField searchField;
    private JButton searchButton;
    private JPanel mainPanel;
    private JPanel buttonPanel;
    private JPanel tablePanel;
    private JScrollPane tableScrollPane;
    private JTable entriesTable;

    private final JMenuBar menuBar = new JMenuBar();

    private final String[] columnNames = {
            "ID",
            "Imię",
            "Nazwisko",
            "Nr. Telefonu",
            "Nr. Telefonu",
            "Nr. Telefonu"
    };

    JPopupMenu popupMenu;
    boolean isDevModeActive = false;

    File config = new File("phonebook.config");

    PhoneBook book;


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * @param name if provided it will be used when creating a PhoneBook object
     */

    PhoneBookGUI(String... name) {

        frame.setContentPane(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(800, 600);
        frame.setMinimumSize(new Dimension(500,200));
        frame.setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(new FlatArcIJTheme());
        } catch(UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        if(!config.exists()) {
            try {
                boolean isConfigCreated = config.createNewFile();

                if(isConfigCreated) {
                    System.out.println("Stworzono plik konfiguracyjny!");
                } else {
                    System.out.println("Nie udało się stworzyć pliku konfiguracyjnego!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(name.length != 0) {
            book = new PhoneBook(name);
            setBookNameInConfig(name[0]);
        } else if(getBookNameFromConfig() != null) {
            book = new PhoneBook(getBookNameFromConfig());
        } else {
            System.out.println("Nie można odczytać pliku konfiguracyjnego!");
            book = new PhoneBook();
            setBookNameInConfig(book.rawBookName);
        }

        System.out.println("Otwieranie pliku: "+book.bookName);


        createPopupMenu();

        entriesTable.setDefaultEditor(Object.class, null);
        entriesTable.setFocusable(false);
        entriesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int selectedRow = entriesTable.getSelectedRow();

                if(e.getClickCount() == 2 && selectedRow != -1) {
                    new ModifyEntryDialog() {
                        @Override
                        void onOpen() {
                            nameField.setText((String) entriesTable.getValueAt(selectedRow,1));
                            surnameField.setText((String) entriesTable.getValueAt(selectedRow,2));
                            phoneNumberField.setText((String) entriesTable.getValueAt(selectedRow,3));
                            additionalPhoneNumberField.setText((String) entriesTable.getValueAt(selectedRow,4));
                            additionalPhoneNumberField2.setText((String) entriesTable.getValueAt(selectedRow,5));
                        }

                        @Override
                        void onOK() {
                            String name = nameField.getText();
                            String surname = surnameField.getText();
                            String phoneNumber = phoneNumberField.getText();

                            System.out.println(name);
                            System.out.println(surname);
                            System.out.println(phoneNumber.matches("^[0-9]{9,11}$"));

                            if(!name.isEmpty() && !surname.isEmpty() && phoneNumber.matches("^[0-9]{9,11}$")) {
                                System.out.println("Modifying entry...");

                                book.modifyEntry(
                                        String.valueOf(entriesTable.getValueAt(selectedRow,0)),
                                        name,
                                        surname,
                                        phoneNumber,
                                        additionalPhoneNumberField.getText(),
                                        additionalPhoneNumberField2.getText()
                                );
                            }

                            dispose();
                            refreshTable();
                        }
                    };
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON3) {
                    int rowAtPoint = entriesTable.rowAtPoint(e.getPoint());

                    if (rowAtPoint >= 0 && rowAtPoint < entriesTable.getRowCount() && entriesTable.getSelectedRows().length < 2) {
                        entriesTable.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                    }

                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        refreshTable();

        addButtonsActionListeners();

        createMenuBar();


        frame.setJMenuBar(menuBar);
        frame.setVisible(true);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Creates popup menu used in JTable.
     * The menu allows deleting entries.
     */

    void createPopupMenu() {
        popupMenu = new JPopupMenu();

        JMenuItem deletePopupMenuItem = new JMenuItem("Usuń");
        deletePopupMenuItem.addActionListener(e -> deleteEntries());

        popupMenu.add(deletePopupMenuItem);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Creates the menu bar and sets up menu items' action listeners
     */

    void createMenuBar() {
        JMenu fileMenu = new JMenu("Plik");

        JMenuItem createBookMenuItem = new JMenuItem("Stwórz...");
        createBookMenuItem.addActionListener(e -> new CreateBookDialog() {
            @Override
            void onOK() {
                new PhoneBookGUI(bookNameField.getText());
                dispose();
            }
        });

        JMenuItem openBookMenuItem = new JMenuItem("Otwórz...");
        openBookMenuItem.addActionListener(e -> new OpenBookDialog() {
            @Override
            void onOK() {

                if(!bookFilesList.isSelectionEmpty()) {
                    new PhoneBookGUI((String) bookFilesList.getSelectedValue());
                    dispose();
                }

            }
        });


        JMenu optionsMenu = new JMenu("Opcje");
        JMenuItem devModeMenuItem = new JMenuItem("Tryb developerski");
        devModeMenuItem.addActionListener(e -> {
            if(!isDevModeActive) {
                isDevModeActive = true;

                JMenuItem generateMenuItem = new JMenuItem("Generuj...");
                generateMenuItem.addActionListener(e1 -> new GenerateEntriesDialog() {
                    @Override
                    void onOK() {
                        try {
                            System.out.println("Generowanie "+numberField.getText()+" wpisów...");
                            generateEntries(Integer.parseInt(numberField.getText()));
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            //System.out.println("Podano nieprawidłową liczbę!");
                        }
                        dispose();
                    }
                });

                optionsMenu.add(generateMenuItem);
            }
        });


        JMenu infoMenu = new JMenu("Informacje");

        JMenuItem authorMenuItem = new JMenuItem("Autor");
        authorMenuItem.addActionListener(e -> new AuthorInfoDialog());


        optionsMenu.add(devModeMenuItem);

        infoMenu.add(authorMenuItem);

        fileMenu.add(openBookMenuItem);
        fileMenu.add(createBookMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);
        menuBar.add(infoMenu);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Adds all action listeners to buttons
     */

    private void addButtonsActionListeners() {
        searchButton.addActionListener(e -> findEntries(searchField.getText()));

        deleteButton.addActionListener(e -> deleteEntries());

        addButton.addActionListener(e -> new AddEntryDialog() {
            @Override
            void onOK() {

                String name = nameField.getText();
                String surname = surnameField.getText();
                String phoneNumber = phoneNumberField.getText();

                if(!name.isEmpty() && !surname.isEmpty() && phoneNumber.matches("^[0-9]{9,11}$")) {

                    book.addEntry(
                            name,
                            surname,
                            phoneNumber,
                            additionalPhoneNumberField1.getText(),
                            additionalPhoneNumberField2.getText()
                    );
                }

                dispose();
                refreshTable();
            }
        });

        searchField.addActionListener(e -> findEntries(searchField.getText()));
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Gets last used phonebook file's name
     * @return name of the last used phonebook file
     */

    String getBookNameFromConfig() {
        Properties props = new Properties();

        try(FileInputStream configReader = new FileInputStream(config)) {
            props.load(configReader);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return props.getProperty("bookFileName");
    }

    /**
     * Writes name of the phonebook into the config file
     * @param name the name you want to write
     */

    void setBookNameInConfig(String name) {
        try {
            BufferedWriter configWriter = new BufferedWriter(new FileWriter(config));

            configWriter.write("bookFileName="+name);
            configWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static void main(String[] args) {
        FlatArcIJTheme.setup();

        new PhoneBookGUI();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Fills data in the table with entries
     */

    void refreshTable() {

        String[][] entries = new String[book.entries.size()][];

        for(int x = 0; x < entries.length; x++) {
            entries[x] = new String[] {
                    book.entries.get(x).id,
                    book.entries.get(x).name,
                    book.entries.get(x).surname,
                    book.entries.get(x).phoneNumbers[0],
                    book.entries.get(x).phoneNumbers[1],
                    book.entries.get(x).phoneNumbers[2]
            };
        }

        entriesTable.setModel(new DefaultTableModel(
                entries,
                columnNames
        ));

    }

    /**
     * Finds entries containing a given keywords and shows them in the table
     * @param keyword contains the value you want to search for
     */

    void findEntries(String keyword) {

        Entry[] searchResult = book.search(keyword);

        String[][] entries = new String[searchResult.length][];

        for(int x = 0; x < entries.length; x++) {
            entries[x] = new String[] {
                    searchResult[x].id,
                    searchResult[x].name,
                    searchResult[x].surname,
                    searchResult[x].phoneNumbers[0],
                    searchResult[x].phoneNumbers[1],
                    searchResult[x].phoneNumbers[2]
            };
        }

        entriesTable.setModel(new DefaultTableModel(
                entries,
                columnNames
        ));

    }

    /**
     * Deletes all selected entries
     */

    void deleteEntries() {
        int[] selectedRows = entriesTable.getSelectedRows();

        ArrayList<String> indexes = new ArrayList<>();

        System.out.println("Usuwanie "+selectedRows.length+" wpisów...");

        for(int selectedRow : selectedRows) {
            indexes.add((String) entriesTable.getValueAt(selectedRow,0));
            //book.deleteEntry((String) entriesTable.getValueAt(selectedRow,0));
        }

        book.deleteEntry(indexes.toArray(new String[0]));

        refreshTable();
    }

    /**
     * Generates random entries.
     * Used for testing.
     * @param numberOfEntries the number of entries you want to create
     */

    void generateEntries(int numberOfEntries) {
        String[] names = {
                "Adam",
                "Marian",
                "Jan",
                "Janusz",
                "Szymon",
                "Dawid",
                "Kacper",
                "Mariusz",
                "Jarosław",
                "Łukasz",
                "Kordian",
                "Konrad"
        };
        String[] surnames = {
                "Nowak",
                "Kowalski",
                "Wiśniewski",
                "Wójcik",
                "Kowalczyk",
                "Kamiński",
                "Lewandowski",
                "Zieliński",
                "Szymański",
                "Woźniak",
                "Dąbrowski",
                "Kozłowski",
                "Jankowski",
                "Mazur",
                "Wojciechowski",
                "Kwiatkowski",
                "Krawczyk",
                "Kaczmarek",
                "Piotrowski",
                "Grabowski",
                "Zając",
                "Pawłowski",
                "Michalski",
                "Król",
                "Jabłoński",
                "Wróbel",
                "Wieczorek",
                "Nowakowski",
                "Majewski",
                "Olszewski",
                "Stępień",
                "Jaworski",
                "Malinowski",
                "Dudek",
                "Adamczyk",
                "Pawlak",
                "Górski",
                "Nowicki",
                "Sikora",
                "Walczak",
                "Witkowski",
                "Baran",
                "Rutkowski",
                "Michalak",
                "Szewczyk",
                "Ostrowski",
                "Tomaszewski",
                "Pietrzak",
                "Wróblewski",
                "Borowski",
                "Kaczyński"
        };

        long randomSurname, randomName;

        ArrayList<String[]> entries = new ArrayList<>();

        for(int x = 0; x < numberOfEntries; x++) {
            randomSurname = Math.round(Math.random()*(surnames.length-1));
            randomName = Math.round(Math.random()*(names.length-1));

            String randomPhone = String.valueOf(Math.round((int) (Math.random()*999999999-100000000)+100000000));
            String randomPhone1 = String.valueOf(Math.round((int) (Math.random()*999999999-100000000)+100000000));
            String randomPhone2 = String.valueOf(Math.round((int) (Math.random()*999999999-100000000)+100000000));

            entries.add(new String[]{names[(int) randomName],surnames[(int) randomSurname],randomPhone,randomPhone2,randomPhone1});

        }

        String[][] entriesArray = entries.toArray(new String[entries.size()][]);
        book.addEntry(entriesArray);

        refreshTable();
    }
}
