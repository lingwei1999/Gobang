import java.net.*;
import java.io.*;

public class Game {
    
    private static final String ADDRESS = "127.0.0.1";// 連線的ip
    private static final int PORT = 8888;// 連線的port
    public static void main(String argv[]) throws UnknownHostException, IOException {
        Board a = new Board("Player 1", 1, ADDRESS, PORT);
        Board b = new Board("Player 2", 2, ADDRESS, PORT);
    }
}
