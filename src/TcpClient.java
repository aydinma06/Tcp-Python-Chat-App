import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import static java.lang.System.out;


public class TcpClient extends JPanel implements ActionListener{
    protected JTextArea messageArea;
    protected JTextField messageField;
    protected JButton sendButton;
    protected JList<String> userList;
    protected JList<String> groupList;
    protected JButton selectUserButton;
    protected JButton selectGroupButton;
    protected JButton createGroup;
    protected JTextField searchField;
    protected JButton searchButton;
    protected JButton refreshButton;
    protected JFrame frame=null;
    protected static String myResponse=null;
    protected static int myPortNumber = 16330;
    protected static BufferedReader myBufferReader;
    protected static BufferedReader myInputStream;
    protected static PrintWriter myOutputStream;
    protected Socket mySocket;
    protected static String responseFromBufferReader;
    protected String hostName;
    protected String recieverName;
    protected DefaultListModel<String> userModel;
    protected DefaultListModel<String> groupModel;
    protected ThreadForChecking checkThread;
    protected String[] userListItems = {};
    protected String[] groupListItems = {};
    protected String myUserList;
    protected String[] myUserArray;

    public TcpClient() throws IOException{

    }

    public TcpClient(String hostName, JFrame frame) throws IOException {
        this.hostName=hostName;
        createGUI();
        InetAddress address= null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        mySocket = new Socket(address,myPortNumber);
        myBufferReader = new BufferedReader(new InputStreamReader(System.in));
        myInputStream = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
        myOutputStream = new PrintWriter(mySocket.getOutputStream());
        myOutputStream.println("=>"+hostName);
        myOutputStream.flush();
        checkThread = new ThreadForChecking();
        checkThread.start();

        System.out.println("Client Address : "+ address);

    }

    public  void createGUI() {
        //construct preComponents

        //construct components

        messageArea = new JTextArea (5, 5);
        messageArea.setBorder(BorderFactory.createLineBorder(Color.black));
        messageField = new JTextField (5);
        messageField.setBorder(BorderFactory.createLineBorder(Color.black));
        sendButton = new JButton ("Gönder");
        sendButton.setBorder(BorderFactory.createLineBorder(Color.black));
        sendButton.addActionListener(this::actionPerformed);
        userModel = new DefaultListModel<>();
        userList = new JList<> (userModel);
        userList.setBorder(BorderFactory.createLineBorder(Color.black));
        groupModel = new DefaultListModel<>();
        groupList = new JList (groupModel);
        groupList.setBorder(BorderFactory.createLineBorder(Color.black));
        selectUserButton = new JButton ("Kullanıcı Seç");
        selectUserButton.setBorder(BorderFactory.createLineBorder(Color.black));
        selectUserButton.addActionListener(this::actionPerformed);
        selectGroupButton = new JButton ("Grup Seç");
        selectGroupButton.addActionListener(this::actionPerformed);
        selectGroupButton.setBorder(BorderFactory.createLineBorder(Color.black));
        createGroup = new JButton ("Grup Oluştur");
        createGroup.addActionListener(this::actionPerformed);
        createGroup.setBorder(BorderFactory.createLineBorder(Color.black));
        searchField = new JTextField (5);
        searchField.setBorder(BorderFactory.createLineBorder(Color.black));
        searchButton = new JButton ("Ara");
        searchButton.setBorder(BorderFactory.createLineBorder(Color.black));
        searchButton.addActionListener(this::actionPerformed);
        refreshButton = new JButton ("Yenile");
        refreshButton.setBorder(BorderFactory.createLineBorder(Color.black));
        refreshButton.addActionListener(this::actionPerformed);

        //set components properties
        messageArea.setEnabled (false);

        //adjust size and set layout
        setPreferredSize (new Dimension (489, 410));
        setLayout (null);

        //add components
        add (messageArea);
        add (messageField);
        add (sendButton);
        add (userList);
        add (groupList);
        add (selectUserButton);
        add (selectGroupButton);
        add (createGroup);
        add (searchField);
        add (searchButton);
        add (refreshButton);

        //set component bounds (only needed by Absolute Positioning)
        messageArea.setBounds (0, 40, 260, 345);
        messageField.setBounds (0, 385, 260, 25);
        sendButton.setBounds (260, 385, 230, 25);
        userList.setBounds (260, 0, 115, 335);
        groupList.setBounds (375, 0, 115, 335);
        selectUserButton.setBounds (260, 360, 115, 25);
        selectGroupButton.setBounds (375, 360, 115, 25);
        createGroup.setBounds (375, 335, 115, 25);
        searchField.setBounds (0, 0, 115, 25);
        searchButton.setBounds (125, 0, 100, 25);
        refreshButton.setBounds (260, 335, 115, 25);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //System.out.println(e.getActionCommand());
        if (e.getActionCommand().equals("Gönder")) {

            responseFromBufferReader = messageField.getText();
            if(recieverName != null){
                myOutputStream.println(recieverName + ":"+responseFromBufferReader);
                myOutputStream.flush();
            }
            else{
                JOptionPane.showMessageDialog(frame, "Lütfen gönderilecek kişiyi seçiniz");
            }

        }

        else if (e.getActionCommand().equals("Kullanıcı Seç")){
            if(userList.getSelectedIndex() != -1)
                recieverName = userModel.get(userList.getSelectedIndex());
            try {
                readFromFile(hostName,recieverName);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("Alıcı => "+ recieverName);

        }
        else if (e.getActionCommand().equals("Ara")){
            String messageToSearch = searchField.getText();
            try {
                searchFromFile(messageToSearch,hostName,recieverName);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        else if (e.getActionCommand().equals("Yenile")){

            userModel.clear();
            myOutputStream.println("***sendMeUserList***");
            myOutputStream.flush();
            //try {
            //    myUserList= myInputStream.readLine();
            //} catch (IOException ex) {
            //    ex.printStackTrace();
            //}

        }
        else if (e.getActionCommand().equals("Grup Oluştur")){

            CreateGroupClass createGroupClass = new CreateGroupClass(hostName,myUserArray);
            createGroupClass.create();

        }
    }

    protected void readFromFile(String client, String reciever) throws IOException {
        messageArea.setText(null);
        String fileName1 = client + "_" +reciever+".txt";
        String fileName = System.getProperty("user.dir") + "\\messages\\" + fileName1;
        File file = new File(fileName);
        if(file.exists()){
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null){
                messageArea.append(line+"\n");
                //System.out.println(line);
            }
        }
    }

    protected void searchFromFile(String message,String client, String reciever) throws IOException {
        messageArea.setText(null);
        String fileName1 = client + "_" +reciever+".txt";
        String fileName = System.getProperty("user.dir") + "\\messages\\" + fileName1;
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while((line = br.readLine()) != null){
            if(line.contains(message))
                messageArea.append(line+"\n");
            //System.out.println(line);
        }
    }

    protected class ThreadForChecking extends Thread {
        public void run(){
            boolean connectionIsAlive=false;
            try {
                connectionIsAlive = mySocket.getInetAddress().isReachable(3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(connectionIsAlive)
            {

                try {
                    myResponse = myInputStream.readLine();
                    messageArea.append(myResponse + "\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if(myResponse.contains(";"))
                {
                    myUserList = myResponse;
                    int i=0;
                    //myUserList = "Mammy;Mammimia;";
                    messageArea.setText(null);
                    while(myUserList.contains(";"))
                    {
                        int seperatorPosition = myUserList.indexOf(";");
                        String user =  myUserList.substring(0, seperatorPosition);
                        if(!user.equals(hostName)){
                            userModel.addElement( user );
                            //myUserArray[i] = user;
                        }
                        myUserList = myUserList.substring(seperatorPosition+1);
                        i++;
                    }
                }
                System.out.println("Server Response : "+myResponse);
                try {
                    connectionIsAlive = mySocket.getInetAddress().isReachable(1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}