package yuraga.chat;

import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnetionRx {

    private final ArrayList<TCPConnection> connections = new ArrayList<>(); //соединенией будет несколько, нужно их хранилище

    public static void main(String[] args) {
        new ChatServer();

    }

    @Override
    public synchronized void onConnetionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection);
        sendToAll("Client connected " + tcpConnection);

    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAll(value);


    }

    @Override
    public synchronized void onDisconect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection);
        sendToAll("Client disconnected " + tcpConnection);


    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection : " + e);

    }

    private void sendToAll(String value) {
        System.out.println(value);
        for (TCPConnection con : connections) {
            con.sendMassage(value); // пробежались по всемм соединеним и передали нашу строку каждом из них
        }
    }


    private ChatServer() {
        System.out.println("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(8189)) { //создали серсерсокет который слушает порт
            while (true) {
                //суть серверного цикла
                //далее в бесконечном цикле мы устанавливаем соединениеб , как только устанавливается соединение
                // мы создаем TCPСonnection, передаем в его конструктор аобьект сокета, который вызывает это соединение(с помощтю метода accept)
                // в качестве события соединения мы передаем наш класс, с помощью this, так как наш класс унаследовал этот интерфейс.
                try {
                    new TCPConnection(this, serverSocket.accept());// на каждое соединение нуно создать новый экземпляр TCPСonnection

                } catch (Exception e) {
                    System.out.println("TCPConnection exception - " + e); // если тчо то случися при подключении с клиентом
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e); // поднимаем исклюение доверху и роняем приложение
        }
    }
}
