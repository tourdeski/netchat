package org.netchat;

import javax.imageio.IIOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.*;
import java.net.Socket;
import java.awt.event.WindowEvent;


public class ChatClient extends JFrame implements Runnable {

    protected Socket socket;
    protected DataInputStream inputStream;
    protected DataOutputStream outputStream;
    protected JTextField inTextField;
    protected JTextArea outTextArea;
    protected boolean isOn;
    protected JButton jbSend;

    public ChatClient(String title, DataOutputStream dos, DataInputStream dis, Socket s) {
        super(title);
        inputStream = dis;
        outputStream = dos;
        socket = s;

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(BorderLayout.CENTER, outTextArea = new JTextArea());
        outTextArea.setEditable(false);
        cp.add(BorderLayout.SOUTH, inTextField = new JTextField());

        jbSend = new JButton("Send");

        jbSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Send")) {
                    try {
                        outputStream.writeUTF(inTextField.getText());
                        outputStream.flush();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                        isOn = false;
                    }
                    inTextField.setText("");
                }
            }

        });
        cp.add(BorderLayout.AFTER_LINE_ENDS, jbSend);


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                isOn = false;
                try {
                    outputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setVisible(true);
        inTextField.requestFocus();
        (new Thread(this)).start();
    }

    @Override
    public void run() {
        isOn = true;
        try {
            while (isOn) {
                String line = inputStream.readUTF();
                outTextArea.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inTextField.setVisible(false);
            validate();
        }
    }

    public static void main(String[] args) throws IOException {// запуск клиента и его подклучени€ к серверу


            Socket socket = new Socket("localhost", 8082);
            DataInputStream dis = null;
            DataOutputStream dos = null;
            try {
                dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    if(ChatServer.isAccess()) {
                        new ChatClient("Chat localhost ", dos, dis, socket);//вот здесь не должно создаватьс€ окно(и экземпл€р), клиента,если стоит false
                    }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    if (dos != null) dos.close();
                } catch (IOException ex2) {
                    ex2.printStackTrace();
                }
                try {
                    socket.close();
                } catch (IOException ex3) {
                    ex3.printStackTrace();
                }
            }
        }
    }


