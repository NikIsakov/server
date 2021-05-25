package chat.client;


import handler.JsonDecoder;
import handler.JsonEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tcp.Message;

import java.util.concurrent.ExecutorService;

public class Network {

    private SocketChannel channel;
    private Callback onMessageReceivedCallback;
    private ExecutorService threadPool;

    private static final String HOST = "localhost";
    private static final int PORT = 8180;

    public Network(Callback onMessageReceivedCallback) {
        this.onMessageReceivedCallback = onMessageReceivedCallback;
        Thread t = new Thread(() -> {
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();
            try{
                Bootstrap b = new Bootstrap();//позволяет настроить клиента перед запуском
                b.group(workerGroup)//в параметрах workerGroup отвечает и за соединение и за обмен данными
                        .channel(NioSocketChannel.class)//Указываем использование класса NioSocketChannel для создания канала после того,
                        //как установлено входящее соединение
                        .handler(new ChannelInitializer<SocketChannel>() {//Указываем обработчики, которые будем использовать для открытого канала (Channel или SocketChannel?) .
                            //ChannelInitializer помогает пользователю сконфигурировать новый канал

                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                channel = socketChannel;
                                //наполняем трубу обработчиками сообщений(потоков данных)
                                // для входящих - слева направо, для исходящих справа налево
                                socketChannel.pipeline().addLast(
                                        new LengthFieldBasedFrameDecoder(1024*1024, 0,3,0,3),
                                        new LengthFieldPrepender(3),
                                        new ObjectDecoder(1024*1024, ClassResolvers.cacheDisabled(null)), //десериализатор netty входящего потока байтов в объект сообщения
                                        new ObjectEncoder(),//сериализатор netty объекта сообщения в исходящих поток байтов
                                        new ByteArrayDecoder(),
                                        new ByteArrayEncoder(),
                                        new JsonDecoder(),
                                        new JsonEncoder(),
                                        new ClientHandler(onMessageReceivedCallback));
                            }
                        });
                ChannelFuture future = b.connect(HOST, PORT).sync(); //устанавливаем подключение к серверу и начинаем принимать входящие сообщения
                future.channel().closeFuture().sync();
                //onMessageReceivedCallback.callback("Please enter credentials. Sample [-auth login password]");

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                workerGroup.shutdownGracefully();
            }
        });
        t.start();
    }

    public void sendMessage (Object msg){
        channel.writeAndFlush(msg);
    }

    public void close() {
        channel.close();
    }
}