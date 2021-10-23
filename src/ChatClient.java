import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private ObjectOutputStream socketOut;
    private ObjectInputStream socketIn;
    private ArrayList<String> blocked = new ArrayList<>();
    private ArrayList<Boolean> jaredMode = new ArrayList<>(List.of(false));
    private ArrayList<Boolean> isBold = new ArrayList<>(List.of(false));




    public ChatClient(String ip, int port) throws Exception {
        socket = new Socket(ip, port);
        socketOut = new ObjectOutputStream(socket.getOutputStream());
        socketIn = new ObjectInputStream(socket.getInputStream());
    }

    // start a thread to listen for messages from the server
    private void startListener() {
        new Thread(new ChatClientSocketListener(socketIn, blocked, jaredMode, isBold)).start();
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
            if(line.equalsIgnoreCase("/jared")){
                setJaredMode();
                line = in.nextLine().trim();
                continue;
            }

            if (line.equalsIgnoreCase("/bold")){
                isBold.set(0, !isBold.get(0));
                line = in.nextLine().trim();
                continue;
            }

            if (line.toLowerCase().startsWith("/block ")){
                blocked.add(line.substring(7));
                System.out.println("User "+ line.substring(7) + " blocked");
                line = in.nextLine().trim();
                continue;
            }
            if (line.toLowerCase().startsWith("/unblock ")){
                if (blocked.remove(line.substring(9)))
                    System.out.println("User "+ line.substring(9) + " unblocked");
                else
                    System.out.println("User " + line.substring(9) + " not blocked");
                line = in.nextLine().trim();
                continue;
            }
            if (line.equalsIgnoreCase("/blocked")){
                System.out.println("Blocked Users: " + blocked);
                line = in.nextLine().trim();
                continue;
            }

            sendMessage(new MessageCtoS_Chat(line));
            line = in.nextLine().trim();
        }
        sendMessage(new MessageCtoS_Quit());

    }

    public void setJaredMode(){
        jaredMode.set(0, !jaredMode.get(0));
        if (jaredMode.get(0)) {
            for (int i = 0; i < 10; i++) {
                System.out.println("!!!JARED MODE ENABLED!!!");
            }
        }
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
