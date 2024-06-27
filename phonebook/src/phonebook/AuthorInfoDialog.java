package phonebook;

import javax.swing.*;
import java.awt.event.*;

public class AuthorInfoDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel authorNameLabel;

    public AuthorInfoDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setLocationRelativeTo(null);
        setResizable(false);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        pack();
        setVisible(true);
    }

    private void onOK() {
        // add your code here
        dispose();
    }


    public static void main(String[] args) {
        AuthorInfoDialog dialog = new AuthorInfoDialog();

    }
}
