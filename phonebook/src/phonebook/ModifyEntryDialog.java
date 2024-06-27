package phonebook;

import javax.swing.*;
import java.awt.event.*;

public class ModifyEntryDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel nameFieldLabel;
    private JLabel surnameFieldLabel;
    private JLabel phoneNumberFieldLabel;
    private JLabel additionalPhoneNumberFieldLabel;
    private JLabel additionalPhoneNumberFieldLabel2;
    JTextField nameField;
    JTextField surnameField;
    JTextField phoneNumberField;
    JTextField additionalPhoneNumberField;
    JTextField additionalPhoneNumberField2;

    public ModifyEntryDialog() {
        onOpen();

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

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

    void onOpen() {

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
        ModifyEntryDialog dialog = new ModifyEntryDialog();
    }
}
