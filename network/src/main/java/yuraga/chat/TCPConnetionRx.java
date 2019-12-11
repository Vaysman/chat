package yuraga.chat;

public interface TCPConnetionRx { //описываем ситуациии которые могут возникнуть в TCP соеднении

    void onConnetionReady(TCPConnection tcpConnection); //готовое соединение

    void onReceiveString(TCPConnection tcpConnection, String value); //принимаем входящую строку

    void onDisconect(TCPConnection tcpConnection); //дисконект

    void onException(TCPConnection tcpConnection, Exception e); //исключение и передаем обьект искл
}
