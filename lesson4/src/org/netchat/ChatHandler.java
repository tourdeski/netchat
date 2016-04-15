package org.netchat;

import java.io.*;
import java.net.Socket;
import java.util.*;


public class ChatHandler extends Thread {

    protected Socket socket;
    protected DataInputStream inputStream;
    protected DataOutputStream outputStream;
    protected boolean isOn;
    protected static int amountOfVisitors = 0;

    protected static List<ChatHandler> handlers = Collections.synchronizedList(new ArrayList<ChatHandler>());

    public ChatHandler(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        outputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    public void run() {
        isOn = true;
        handlers.add(this);
        ChatServer.setAmountOfVisitors(++amountOfVisitors);
        try {
            while (isOn) {
                String msg = inputStream.readUTF();
                broadcast(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            handlers.remove(this);
            ChatServer.setAmountOfVisitors(--amountOfVisitors);
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected static void broadcast(String message) {
        synchronized (handlers){
            Iterator<ChatHandler> it = handlers.iterator();
            while (it.hasNext()) {
                ChatHandler chatHandler = it.next();
                try {
                    synchronized (chatHandler.outputStream) {
                        chatHandler.outputStream.writeUTF(message);
                    }
                    chatHandler.outputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    chatHandler.isOn = false;
                }
            }
        }
    }
}