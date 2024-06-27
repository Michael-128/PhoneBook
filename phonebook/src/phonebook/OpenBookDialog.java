package phonebook;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class OpenBookDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    JList bookFilesList;
    private JScrollPane bookFilesListScrollPane;

    public OpenBookDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setLocationRelativeTo(null);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);



        File currFolder = new File("./");
        File[] listAllOfFiles = currFolder.listFiles();

        DefaultListModel<String> listOfBooks = new DefaultListModel<>();

        for(File file : listAllOfFiles) {
            if(file.getName().substring(file.getName().lastIndexOf(".") + 1).equals("pb")) {
                listOfBooks.addElement(file.getName().substring(0, file.getName().lastIndexOf(".pb")));
            }
        }

        bookFilesList.setModel(listOfBooks);

        pack();
        setMinimumSize(new Dimension(200,300));
        setVisible(true);
    }

    void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        OpenBookDialog dialog = new OpenBookDialog();
    }
}
