package yuraga.chat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientWindow extends JFrame implements ActionListener, TCPConnetionRx {


    private static final String ipAdress = "192.168.0.101";
    private static final int port = 8189;
    private static final int width = 600;
    private static final int height = 600;
    private JTextArea log = new JTextArea();
    private JTextField name = new JTextField("Введите свое имя");
    private JTextField text = new JTextField();
    //private JTextField ipServer = new JTextField("Введите IP сервера");
    private TCPConnection connection;

    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // закрытие окна кретиком
        setSize(width, height);
        setLocationRelativeTo(null); // окно по середине
        setAlwaysOnTop(true); // окно всегда серху

        log.setEditable(false); //запрет редактировать логи
        log.setLineWrap(true); // перенос слов
        text.addActionListener(this);// чтобы строка ввода ловила Enter
        //ipServer.addActionListener(this);

        add(log, BorderLayout.CENTER); // добавили наше в наше окно по центру
        add(text, BorderLayout.SOUTH);
        add(name, BorderLayout.NORTH);
        //add(ipServer,BorderLayout.LINE_START );
        Color nameColor = new Color(149, 187, 220);
        Color logColor = new Color(203, 231, 250);
        log.setBackground(logColor);
        name.setBackground(nameColor);
        JScrollPane jScrollPane = new JScrollPane(log);
        add(jScrollPane);

        setVisible(true);

        try {
            connection = new TCPConnection(this, ipAdress, port);
        } catch (Exception e) {
            printMassage("Connection exception" + e);
        }
    }

    private synchronized void printMassage(String mas) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                log.append(mas + "\n");
                log.setCaretPosition(log.getDocument().getLength()); // перенос каретки в конец строки
            }
        });


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = text.getText();
        if (msg.equals("")) return;
        text.setText(null); // стираем после нажатия кнопки напечатанный текст
        connection.sendMassage(name.getText() + " : " + msg);


    }

    @Override
    public void onConnetionReady(TCPConnection tcpConnection) {
        printMassage("Connection ready...");

    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMassage(value);

    }

    @Override
    public void onDisconect(TCPConnection tcpConnection) {
        printMassage("Connection close");

    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMassage("Connection exception" + e);

    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() { // так как много ограничений по многопоточности, со свингом можно работать только в потоке ЕДТ, поэтому используем анонимный класс.
            @Override
            public void run() {
                new ClientWindow();
            }
        });

    }
}
