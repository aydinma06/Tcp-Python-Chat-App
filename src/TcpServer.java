import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TcpServer {
    protected ArrayList myListofClients;
    protected ServerSocket welcomeSocket;
    protected int myPortNumber = 16330;

    public TcpServer(){
        myListofClients = new ArrayList( );
        try {
            welcomeSocket = new ServerSocket(myPortNumber);
            System.out.println(myPortNumber + " portu dinlenmeye başladı.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(myPortNumber + " portu dinlenmesi başarısız oldu.");
        }
    }

    public static void main(String[] argv) throws IOException {
        TcpServer myServer = new TcpServer(); //To use Constructor
        myServer.serverThings();
    }

    public void serverThings() throws IOException {
        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            String hostName = connectionSocket.getInetAddress( ).getHostName();
            System.out.println(hostName + " hostu bağlandı.");
            ThreadForEachClient newClientThread = new ThreadForEachClient(connectionSocket, hostName);
            synchronized (myListofClients){
                myListofClients.add(newClientThread);
                newClientThread.start();
            }
        }
    }

    public ArrayList getMyListofClients(){return myListofClients;}

    protected class ThreadForEachClient extends Thread {
        protected BufferedReader inFromClient;
        protected PrintWriter myPrintWriter;
        protected Socket clientSocket;
        protected String clientIp;
        protected String clientName;

        public ThreadForEachClient(Socket connectionSocket, String hostName) {
            clientSocket = connectionSocket;
            clientIp = hostName;
            try {
                myPrintWriter = new PrintWriter(clientSocket.getOutputStream( ), true);
                inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            String messageComesFromClient = null;
            try {
                messageComesFromClient = inFromClient.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!welcomeSocket.isClosed()) {
                if(messageComesFromClient.contains("=>") && clientName == null) {
                    int clientNameSeperatorPosition = messageComesFromClient.indexOf(">");
                    clientName = messageComesFromClient.substring(clientNameSeperatorPosition+1);
                    System.out.println(clientName + " bağlandı.");
                    try {
                        messageComesFromClient = inFromClient.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if (messageComesFromClient.equals("***sendMeUserList***")){
                    String userList = getUserList();
                    myPrintWriter.println(userList);
                    try {
                        messageComesFromClient = inFromClient.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    int seperatorPosition = messageComesFromClient.indexOf(":");
                    String receiver = messageComesFromClient.substring(0, seperatorPosition);
                    String actualMessage = messageComesFromClient.substring(seperatorPosition + 1);
                    ThreadForEachClient threadtobeSendMessage = isAvailable(receiver);
                    if (threadtobeSendMessage != null) {
                        threadtobeSendMessage.myPrintWriter.println(clientName + " : " + actualMessage);
                        myPrintWriter.println(clientName + " : " + actualMessage);

                        try {
                            saveToFile(clientName + " : " + actualMessage + "\n",clientName,threadtobeSendMessage.clientName);
                            saveToFile(clientName + " : " + actualMessage+ "\n",threadtobeSendMessage.clientName,clientName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    else
                        System.out.println("Alıcıya ulaşılamadı.");
                    try {
                        messageComesFromClient = inFromClient.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        protected ThreadForEachClient isAvailable(String receiver) {
            synchronized(myListofClients) {
                for (int i=0; i<myListofClients.size( ); i++) {
                    ThreadForEachClient searchingForClient = (ThreadForEachClient)myListofClients.get(i);
                    if (searchingForClient.clientName.equals(receiver))
                        return searchingForClient;
                }
            }
            return null;
        }

        protected String getUserList() {
            String userList="";
            synchronized(myListofClients) {
                for (int i=0; i<myListofClients.size( ); i++) {
                    ThreadForEachClient connectedClient = (ThreadForEachClient)myListofClients.get(i);
                    userList = connectedClient.clientName + ";" +userList  ;
                }
            }
            return userList;
        }

        protected void saveToFile(String message, String clientName, String reciever) throws IOException {
            String fileName1 = clientName + "_" +reciever+".txt";
            //String fileName2 = reciever + "_" +clientName+".txt";//threadtobeSendMessage.clientName
            String pathOfFile = System.getProperty("user.dir") + "\\messages\\" + fileName1;
            //String pathOfFile2 = System.getProperty("user.dir") + "/messages/" + fileName2+".txt";
            File file1 = new File(pathOfFile); file1.createNewFile();
            //File file2 = new File(pathOfFile2); file2.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file1,true);
            //FileOutputStream fileOutputStream2 = new FileOutputStream(file2,true);
            byte[] strToBytes = message.getBytes();
            fileOutputStream.write(strToBytes);
            //fileOutputStream2.write(strToBytes);
            fileOutputStream.close();
        }

    }
}


