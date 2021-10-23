import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatClientSocketListener implements Runnable {
    private ObjectInputStream socketIn;
    private ArrayList<String> blocked;
    private ArrayList<Boolean> jaredMode;
    private ArrayList<Boolean> isBold;
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RESET = "\u001B[0m";



    public ChatClientSocketListener(ObjectInputStream socketIn, ArrayList<String> blocked, ArrayList<Boolean> jaredMode, ArrayList<Boolean> isBold) {
        this.socketIn = socketIn;
        this.blocked = blocked;
        this.jaredMode = jaredMode;
        this.isBold = isBold;
    }

    private void processChatMessage(MessageStoC_Chat m) {
        if(isBold.get(0)){
            System.out.println(ANSI_YELLOW + "This text should be yellow" + ANSI_RESET);
        }
        else if (!jaredMode.get(0)) {
            System.out.println(m.userName + ": " + m.msg);
        }
        else{
            System.out.println("Jared: " + m.msg);
        }
    }

    private void processWelcomeMessage(MessageStoC_Welcome m) {
        if(!jaredMode.get(0)) {
            System.out.println(m.userName + " joined the server!");
        }
        else{
            System.out.println("Bossman joined the server!");
        }
    }


    private void processExitMessage(MessageStoC_Exit m) {
        if(!jaredMode.get(0)) {
            System.out.println(m.userName + " left the server!");
        }
        else{
            System.out.println("Bossman left the server!");
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message msg = (Message) socketIn.readObject();
                if (msg instanceof MessageStoC_Welcome) {
                    processWelcomeMessage((MessageStoC_Welcome) msg);
                }
                else if (msg instanceof MessageStoC_Chat) {
                    if (!blocked.contains(((MessageStoC_Chat) msg).userName))
                        processChatMessage((MessageStoC_Chat) msg);
                }
                else if (msg instanceof MessageStoC_Exit) {
                    processExitMessage((MessageStoC_Exit) msg);
                }
                else {
                    System.out.println("Unhandled message type: " + msg.getClass());
                }
            }
        } catch (Exception ex) {
            System.out.println("Exception caught in listener - " + ex);
        } finally{
            System.out.println("Client Listener exiting");
        }
    }
}
