package org.netchat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ChatServer extends JFrame {
    private JTextArea outTextArea;
    private static JLabel amountOfVisitors;
    protected JButton jbAccess;
    private static boolean access ;// вот статична€ переменна€, котора€ регулирует подключение новых клиентов к  серверу
    public static ChatServer instance;// и сейчас тк она не проинициализированна, почему то класс чат лиента вообще никогда не
                                        // создает экземпл€р, даже после нажати€ кнопки, если ставим true - он всегда создает экземпл€ры
    ChatServer(String title) throws IOException {
        super(title);
        access = true;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setVisible(true);
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(BorderLayout.CENTER, outTextArea = new JTextArea());
        cp.add(BorderLayout.SOUTH, amountOfVisitors = new JLabel());
        cp.add(BorderLayout.NORTH, jbAccess = new JButton("Access OFF"));
        amountOfVisitors.setText("amount visitors: 0");
        ServerSocket service = new ServerSocket(8082);

        jbAccess.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Access OFF")) {
                    access = false;// здесь мен€етс€ значение переменной по нажатию кнопки
                    jbAccess.setText("Access ON");
                }
                if (e.getActionCommand().equals("Access ON")) {
                    access = true;
                    jbAccess.setText("Access OFF");
                }
            }
        });
        try {
            while (true) {
                Socket s = service.accept();// вечный цикл который ждет подключени€ новых клиентов
                if (access) {
                    outTextArea.append("Accepted from " + s.getInetAddress() + "\n");
                    ChatHandler handler = new ChatHandler(s);
                    handler.start();
                } else {
                    outTextArea.append("Access is close\n");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            service.close();
        }
    }

    public static ChatServer singlton() {
        if (instance == null) {
            try {
                instance = new ChatServer("Chat Server");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }return instance;
    }

    public static void setAmountOfVisitors(int amountOfVisitors) {
        ChatServer.amountOfVisitors.setText("amount visitors: " + amountOfVisitors);
    }

    public static boolean isAccess() {
        return access;
    }

    public static void main(String[] args) {//точка входа в программу дл€ сервера

       ChatServer.singlton();
    }
}