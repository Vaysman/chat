package yuraga.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {
    private final Socket socket;
    private final Thread rxThread; //входящий поток
    private final BufferedReader in;
    private final BufferedWriter out;
    private final TCPConnetionRx eventConnetion; //событие соединения

    public TCPConnection(TCPConnetionRx eventConnetion, String ipAdress, int port) throws Exception { //конструктор , где сокет создается внутри
        this(eventConnetion, new Socket(ipAdress, port)); //использовали конструктор, который  вызывает другой конструктор.

    }

    public TCPConnection(TCPConnetionRx eventConnetion, Socket socket) throws IOException // соединение, когда уже есть сокет.
    {
        this.eventConnetion = eventConnetion;
        this.socket = socket; //принимает один сокет и с этим сокетом создает соединение
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8"))); // определяем поток ввода
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8"))); // определяем поток вывода
        rxThread = new Thread(new Runnable() {  // создаем поток , слушает всё сходящее
            @Override
            public void run() {
                try {
                    eventConnetion.onConnetionReady(TCPConnection.this); // поток стартовал (передали обьект "обрамляющего класса" TCPConnection)
                    while (!rxThread.isInterrupted()) { // пока поток не прерван , мы получаем строчку и передаем ее нашеиу event-у
                        String str = in.readLine();
                        eventConnetion.onReceiveString(TCPConnection.this, str);
                    }

                } catch (IOException e) {
                    eventConnetion.onException(TCPConnection.this, e);

                } finally {
                    eventConnetion.onDisconect(TCPConnection.this);

                }

            }
        });
        rxThread.start();
    }

    public synchronized void sendMassage(String massage) {
        try {
            out.write(massage + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventConnetion.onException(TCPConnection.this, e);
            disconnect();
        }

    }


    public synchronized void disconnect() {
        try {
            rxThread.interrupt();
            socket.close();
        } catch (IOException e) {
            eventConnetion.onException(TCPConnection.this, e);
            disconnect();
        }
    }

    @Override
    public String toString() {
        return "TCPConnection:" + socket.getInetAddress() + " : " + socket.getPort();
    }

    public static void main(String[] args) {


    }
}
