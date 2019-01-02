import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class MyChannelServer implements Runnable{

    private  ServerSocketChannel serverSocketChannel;

    private String host;
    private int port;

    public MyChannelServer(String host, int port){
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(host, port));

            serverSocketChannel.configureBlocking(false);

            // robimy selector i rejestrujemy go w kanale, zeby sobie rejestrowal polaczenie
            Selector selector = Selector.open();
            SelectionKey sscKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                selector.select();

                Set keys = selector.selectedKeys();

                Iterator iterator = keys.iterator();

                while (iterator.hasNext()) {
                    //pobieram klucz
                    SelectionKey key = (SelectionKey) iterator.next();
                    iterator.remove();

                    if(key.isAcceptable()){
                        //klient prosi o polaczenie
                        SocketChannel clientChannel = serverSocketChannel.accept();
                        clientChannel.configureBlocking(false);

                        clientChannel.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
                        continue;
                    }else if(key.isReadable()){
                        //kanal gotowy do czytania
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        MyServerProtocol myServerProtocol = new MyServerProtocol();




                        continue;
                    }else if(key.isWritable()){
                        //mozemy pisac
                        SocketChannel clientChannel = (SocketChannel) key.channel();




                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
