import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ChatClientSocketListener implements Runnable {
    private final ArrayList<String> COMMANDS = new ArrayList<>(Arrays.asList("block", "unblock", "jared"));
    private ObjectInputStream socketIn;
    private ArrayList<String> blocked = new ArrayList<>();
    private boolean jaredMode = false;


    public ChatClientSocketListener(ObjectInputStream socketIn) {
        this.socketIn = socketIn;

    }

    private  void setJaredMode(){
        jaredMode = !jaredMode;
    }

    private void processCommand(MessageStoC_Command c) {
        if (!COMMANDS.contains(c.command))
            System.out.println("Command not recognized");
        else{
            switch (c.command){
                case "block":
                    blocked.add(c.params[1]);
                    System.out.println("User " + c.params[1] + " blocked");
                    break;
                case "unblock":
                    blocked.remove(c.params[1]);
                    System.out.println("User " + c.params[1] + " unblocked");
                    break;
                case "jared":
                    setJaredMode();
                    break;
            }
        }

    }

    private void processChatMessage(MessageStoC_Chat m) {
        if (!jaredMode) {
            System.out.println(m.userName + ": " + m.msg);
        }
        else{
            System.out.println("Jared: " + m.msg);
        }

    }

    private void processWelcomeMessage(MessageStoC_Welcome m) {
        if(!jaredMode) {
            System.out.println(m.userName + " joined the server!");
        }
        else{
            System.out.println("Bossman joined the server!");
        }
    }


    private void processExitMessage(MessageStoC_Exit m) {
        if(!jaredMode) {
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
                else if (msg instanceof MessageStoC_Command){
                    processCommand((MessageStoC_Command) msg);
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
