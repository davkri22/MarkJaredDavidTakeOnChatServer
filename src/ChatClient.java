import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private ObjectOutputStream socketOut;
    private ObjectInputStream socketIn;
    private ArrayList<String> blocked = new ArrayList<>();
    private boolean jared;
    public ChatClientSocketListener chat = null;

    public ChatClient(String ip, int port) throws Exception {
        socket = new Socket(ip, port);
        socketOut = new ObjectOutputStream(socket.getOutputStream());
        socketIn = new ObjectInputStream(socket.getInputStream());
    }
 
    // start a thread to listen for messages from the server
    private void startListener() {
        chat = new ChatClientSocketListener(socketIn, blocked);
        new Thread(chat).start();
    }

    private void sendMessage(Message m) throws Exception {
        socketOut.writeObject(m);
//        socketOut.flush();
    }

    private void mainLoop(Scanner in) throws Exception {
        System.out.print("Chat sessions has started - enter a user name: ");
        String name = in.nextLine().trim();

        sendMessage(new MessageCtoS_Join(name));

        String line = in.nextLine().trim();
        while (!line.toLowerCase().startsWith("/quit")) {
            if (line.toLowerCase().startsWith("/jared "))
            {
                chat.setJaredMode();
                line = in.next().trim();
                continue;
            }
            if (line.toLowerCase().startsWith("/block ")){
                blocked.add(line.substring(7));
                System.out.println("User "+ line.substring(7) + " blocked");
                line = in.nextLine().trim();
                continue;
            }
            sendMessage(new MessageCtoS_Chat(line));
            line = in.nextLine().trim();
        }
        sendMessage(new MessageCtoS_Quit());

    }

    private void closeSockets() throws Exception {
        socketIn.close();
        socketOut.close();
        socket.close();
    }

    public static void main(String[] args) throws Exception {
        Scanner userInput = new Scanner(System.in);
        System.out.println("What's the server IP? ");
        String serverip = userInput.nextLine();

        System.out.println("What's the server port? ");
        int port = userInput.nextInt();
        userInput.nextLine();

        ChatClient cc = new ChatClient(serverip, port);

        cc.startListener();
        cc.mainLoop(userInput);

        userInput.close();
        cc.closeSockets();
    }

}
