
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.*;
import javax.swing.event.*;

public class LoginClass extends JPanel implements ActionListener {
    protected JTextField userNameTextField;
    protected JButton loginButton;
    protected JLabel userNameLabel;
    protected static JFrame frame;

    public LoginClass()  {
        //construct components
        userNameTextField = new JTextField (5);
        loginButton = new JButton ("Giriş Yap");
        loginButton.addActionListener(this::actionPerformed);
        userNameLabel = new JLabel ("Kullanıcı Adı    :");

        //adjust size and set layout
        setPreferredSize (new Dimension (352, 59));
        setLayout (null);

        //add components
        add (userNameTextField);
        add (loginButton);
        add (userNameLabel);

        //set component bounds (only needed by Absolute Positioning)
        userNameTextField.setBounds (100, 0, 250, 25);
        loginButton.setBounds (115, 30, 100, 25);
        userNameLabel.setBounds (0, 0, 100, 25);
    }


    public static void main (String[] args) {

        String pathOfFile = System.getProperty("user.dir") + "\\messages\\" + "asd"+".txt";

        System.out.println(pathOfFile);

        frame = new JFrame ("MyPanel");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add (new LoginClass());
        frame.pack();
        frame.setVisible (true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String clientName = userNameTextField.getText();
        frame.dispose();
        JFrame newframe = new JFrame ("Mammy's Chat Application");
        newframe.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        try {
            newframe.getContentPane().add (new TcpClient(clientName,frame));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        newframe.pack();
        newframe.setVisible (true);

    }
}