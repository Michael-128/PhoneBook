package phonebook;

import javax.swing.*;
import java.awt.event.*;

public class AddEntryDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;

    JLabel nameFieldLabel;
    JLabel surnameFieldLabel;
    JLabel phoneNumberFieldLabel;
    JLabel additionalPhoneNumberFieldLabel1;
    JLabel additionalPhoneNumberFieldLabel2;

    JTextField nameField;
    JTextField surnameField;
    JTextField phoneNumberField;
    JTextField additionalPhoneNumberField1;
    JTextField additionalPhoneNumberField2;

    public AddEntryDialog() {
        setTitle("Dodaj wpis");
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

        pack();
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
        AddEntryDialog dialog = new AddEntryDialog();

    }
}
