import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    static ServerSocket serverSocket;
    static ArrayList<Socket> s = new ArrayList<Socket>(2);

    static class ClientHandler extends Thread {
        final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                while(true){
                    int player, x_axis, y_axis;
                    player = Integer.valueOf(in.readLine());
                    if(player>0){
                        x_axis = Integer.valueOf(in.readLine());
                        y_axis = Integer.valueOf(in.readLine());
                        System.out.println(player + " " + x_axis + " " + y_axis);
                        for(Socket i: s){
                            PrintWriter out = new PrintWriter(i.getOutputStream(), true);
                            out.println(player);
                            out.println(x_axis);
                            out.println(y_axis);
                        }
                    }
                    else{
                        System.out.println(player);
                        for(Socket i: s){
                            PrintWriter out = new PrintWriter(i.getOutputStream(), true);
                            out.println(player);
                        }
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(8888);

        while (true) {
            Socket socket = null;
            System.out.println("等待客戶端連線..");
            try {
                socket = serverSocket.accept();// 偵聽並接受到此套接字的連線,返回一個Socket物件

                s.add(socket);
                Thread t = new ClientHandler(socket);
                System.out.println("A new client is connected : " + socket); 
                
                System.out.println("Assigning new thread for this client"); 
                t.start();
                

            } catch (IOException e) {
                socket.close(); 
                // e.printStackTrace();
            }
        }
    }
}

