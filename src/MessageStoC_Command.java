public class MessageStoC_Command extends Message {
    public String[] params;
    public String command;

    public MessageStoC_Command(String text) {
        this.command = text.split(" ")[0];
        this.params = text.split(" ");
    }
}
