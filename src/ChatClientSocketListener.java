import java.io.ObjectInputStream;
import java.util.ArrayList;

public class ChatClientSocketListener implements Runnable {
    private ObjectInputStream socketIn;
    private ArrayList<String> blocked;
    public boolean jaredMode = false;


    public ChatClientSocketListener(ObjectInputStream socketIn, ArrayList<String> blocked) {
        this.socketIn = socketIn;
        this.blocked = blocked;
    }

    public void setJaredMode(){
        jaredMode = !jaredMode;
        System.out.println("This is working");
    }

    private void processChatMessage(MessageStoC_Chat m) {
        if (!jaredMode) {
            System.out.println(m.userName + ": " + m.msg);
        }

    }

    private void processWelcomeMessage(MessageStoC_Welcome m) {
        if(!jaredMode) {
            System.out.println(m.userName + " joined the server!");
        }
    }


    private void processExitMessage(MessageStoC_Exit m) {
        if(!jaredMode) {
            System.out.println(m.userName + " left the server!");
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message msg = (Message) socketIn.readObject();
                if(msg.toString().startsWith("/jared")){
                    setJaredMode();
                }

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
