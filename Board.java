
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Board {
    private int width = 920, height = 1000;    
    private int status;
    private int player;
    private int total;

    private JLabel statusLabel;
    private JFrame game;
    private Blocks button[][];

    Socket client;
    PrintWriter out;

    Board(String name, int player, String Address, int PORT) throws UnknownHostException, IOException {
        this.player = player;
        this.status = 1;
        this.total = 0;

        client = new Socket(Address, PORT);
        Thread listener = new Listener(client);
        listener.start();
        out = new PrintWriter(client.getOutputStream(), true);

        game = new JFrame(name);
        game.setSize(width, height);
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = game.getContentPane();

        statusLabel = new JLabel(status == player?"Your Turn" : "Opponent's Turn", SwingConstants.CENTER);
        statusLabel.setHorizontalTextPosition(AbstractButton.CENTER);
        statusLabel.setVerticalTextPosition(AbstractButton.BOTTOM);
        statusLabel.setOpaque(true);
        statusLabel.setFont(statusLabel.getFont().deriveFont(50.0f));
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setBackground(Color.decode("#E9C77F"));
        contentPane.add(BorderLayout.NORTH, statusLabel);

        JPanel Button = new JPanel(new GridLayout(15, 15));
        Button.setBackground(Color.decode("#E9C77F"));
        contentPane.add(Button, BorderLayout.CENTER);

        button = new Blocks[15][15];
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                Icon img = Icons(i, j, "no_chess");
                Blocks block = new Blocks(i, j, img);
                Border emptyBorder = BorderFactory.createEmptyBorder();
                block.setBorder(emptyBorder);
                block.addActionListener(new Rule());
                button[i][j] = block;
                Button.add(button[i][j]);
            }
        }

        JButton start = new JButton();
        start.setText("Restart");
        start.setFont(start.getFont().deriveFont(30.0f));
        contentPane.add(BorderLayout.SOUTH, start);

        start.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
                out.println("0");
			}
		});
        start.setHorizontalTextPosition(AbstractButton.CENTER);
        start.setVerticalTextPosition(AbstractButton.CENTER);

        this.game.setVisible(true);
    }

    public Icon Icons(int x, int y, String type) {
        Icon img;

        if (x == 14 && y == 14)
            img = new ImageIcon("./image/" + type + "/right_bottom.png");
        else if (x == 14 && y == 0)
            img = new ImageIcon("./image/" + type + "/left_bottom.png");
        else if (x == 0 && y == 0)
            img = new ImageIcon("./image/" + type + "/left_top.png");
        else if (x == 0 && y == 14)
            img = new ImageIcon("./image/" + type + "/right_top.png");
        else if (y == 14)
            img = new ImageIcon("./image/" + type + "/right_side.png");
        else if (x == 14)
            img = new ImageIcon("./image/" + type + "/bottom_side.png");
        else if (y == 0)
            img = new ImageIcon("./image/" + type + "/left_side.png");
        else if (x == 0)
            img = new ImageIcon("./image/" + type + "/top_side.png");
        else
            img = new ImageIcon("./image/" + type + "/block.png");

        return img;
    }

    class Rule implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            // TODO Auto-generated method stub
            Blocks b = (Blocks)e.getSource();
            int x = b.get_place()[0], y = b.get_place()[1];
            if(b.status == 0 && status == player){
                out.println(player);
                out.println(x);
                out.println(y);
                total++;
            }
        }

    }

    class Listener extends Thread{    
        final Socket s;

        public  Listener(Socket s) {
            this.s = s;
        }

        public void GoBangRule(int x, int y, int user){
            for(int i = 0, count = 0; i<15 ; i++){
                if(button[i][y].status == user) count++;
                else count = 0;
                if(count>=5){
                    statusLabel.setText(user==player?"You Win!!":"You Lose!!");
                    status = 0;
                    return;
                }
            }
    
            for(int j = 0, count = 0 ; j<15 ; j++){
                if(button[x][j].status == user) count++;
                else count = 0;
                if(count>=5){
                    statusLabel.setText(user==player?"You Win!!":"You Lose!!");
                    status = 0;
                    return;
                }
            }
    
            for(int i = x>y?(x-y):0 , j = x<y?(y-x):0, count = 0 ; i<15 && j<15 ; i++, j++){
                if(button[i][j].status == user) count++;
                else count = 0;
                if(count>=5){
                    statusLabel.setText(user==player?"You Win!!":"You Lose!!");
                    status = 0;
                    return;
                }
            }
    
            int count = 0;
            for(int direct = 0 ; x+direct<15 && y-direct>=0 ; direct++){
                if(button[x+direct][y-direct].status == user) count ++;
                else break;
                if(count>=5){
                    statusLabel.setText(user==player?"You Win!!":"You Lose!!");
                    status = 0;
                    return;
                }
            }
            
            for(int direct = 1 ; x-direct>=0 && y+direct<15 ; direct++){
                if(button[x-direct][y+direct].status == user) count ++;
                else break;
                if(count>=5){
                    statusLabel.setText(user==player?"You Win!!":"You Lose!!");
                    status = 0;
                    return;
                }
            }
    
            if(total == 225){
                statusLabel.setText(" DUEL !!");
                return;
            }
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(
                                            new InputStreamReader(
                                                this.s.getInputStream())
                                        );
                while(true){
                    int user, x_axis, y_axis;
                    user = Integer.valueOf(in.readLine());
                    System.out.printf("%d\n", user);
                    if(user>0){
                        x_axis = Integer.valueOf(in.readLine());
                        y_axis = Integer.valueOf(in.readLine());               
                        
                        button[x_axis][y_axis].status = user;
                        Icon img;
                        if(status == 1){
                            img = Icons(x_axis, y_axis, "white_chess");
                            status = 2;
                            statusLabel.setText(status == player?"Your Turn" : "Opponent's Turn");
                            GoBangRule(x_axis, y_axis, 1);
                        }
                        else{
                            img = Icons(x_axis, y_axis, "black_chess");
                            status = 1;
                            statusLabel.setText(status == player?"Your Turn" : "Opponent's Turn");
                            GoBangRule(x_axis, y_axis, 2);
                        }
                        button[x_axis][y_axis].setIcon(img);

                        System.out.printf("%d: %d %d\n", user, x_axis, y_axis);
                    }
                    else{    
                        System.out.printf("%d\n", user);
                        status = 1;
                        statusLabel.setText(status == player?"Your Turn" : "Opponent's Turn");

                        for(int i = 0 ; i<15 ; i++) {
                            for(int j = 0 ; j<15 ; j++) {
                                Icon img = Icons(i, j, "no_chess");
                                button[i][j].setIcon(img);
                                button[i][j].status = 0;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    


}
