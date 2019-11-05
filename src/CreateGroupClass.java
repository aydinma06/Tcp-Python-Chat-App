
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.*;

public class CreateGroupClass extends JPanel implements ListSelectionListener,ActionListener
{
    protected DefaultListModel<String> userModel;
    private JList<String> userGroupList;
    private JButton createGroupButton;
    private JTextField groupName;
    private JLabel jcomp4;
    protected String selectedUsers;
    protected String hostName;
    protected String[] userList;

    public CreateGroupClass(String hostName,String[] userList) {
        this.hostName = hostName;
        this.userList = userList;
        //construct preComponents
        String[] userGroupListItems = {"Item 1", "Item 2", "Item 3"};

        //construct components
        userModel = new DefaultListModel<>();
        userGroupList = new JList<String>(userModel);
        userGroupList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        userGroupList.addListSelectionListener(this);
        createGroupButton = new JButton ("Oluştur");
        createGroupButton.addActionListener(this);
        groupName = new JTextField (5);
        jcomp4 = new JLabel ("Grup Adı :");

        //adjust size and set layout
        setPreferredSize (new Dimension (162, 198));
        setLayout (null);

        //add components
        add (userGroupList);
        add (createGroupButton);
        add (groupName);
        add (jcomp4);

        //set component bounds (only needed by Absolute Positioning)
        userGroupList.setBounds (0, 0, 165, 150);
        createGroupButton.setBounds (0, 175, 165, 25);
        groupName.setBounds (65, 150, 100, 25);
        jcomp4.setBounds (0, 150, 100, 25);
    }

    public void create(){
        JFrame frame = new JFrame ("MyPanel");
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add (new CreateGroupClass(hostName,userList));
        frame.pack();
        frame.setVisible (true);
        for(int i=0; i<userList.length;i++)
        {
            userModel.addElement( userList[i] );
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        String destinations = "";
        Object obj[ ] = userGroupList.getSelectedValues();
        for(int i = 0; i < obj.length; i++)
        {
            destinations += (String) obj[i] + "/";
        }
        selectedUsers = destinations;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String fileName1 = hostName + "_" +"groups"+".txt";
        String pathOfFile = System.getProperty("user.dir") + "\\groups\\" + fileName1;
        File file1 = new File(pathOfFile);
        try {
            file1.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file1,true);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        String myText = groupName.getText() + "*" +selectedUsers;
        byte[] strToBytes = myText.getBytes();
        try {
            fileOutputStream.write(strToBytes);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            fileOutputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
