package chat.server;

import handler.JsonDecoder;
import handler.JsonEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {
    private SocketChannel channel;
    private static final int PORT = 8180;
    Logger log = LogManager.getLogger(ServerApp.class);

    public ServerApp() {
        start();
    }

    private void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);//создаем пул потоков
        EventLoopGroup workerGroup = new NioEventLoopGroup();//пул потоков для обработки потоков данных
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        try {
            ServerBootstrap b = new ServerBootstrap();////позволяет настроить сервер перед запуском
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)//используем канал для подключения клиентов к серверу
                    .childHandler(new ChannelInitializer<SocketChannel>() {//настраиваем процесс общения с клиентом, когда кто-то подключится информация о соединении лежит в SocketChannel

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            channel = socketChannel;
                            socketChannel.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024*1024, 0,3,0,3),
                                    new LengthFieldPrepender(3),
                                    new ObjectDecoder(1024*1024, ClassResolvers.cacheDisabled(null)), //десериализатор netty входящего потока байтов в объект сообщения
                                    new ObjectEncoder(), //сериализатор netty объекта сообщения в исходящии поток байтов
                                    new ByteArrayDecoder(),
                                    new ByteArrayEncoder(),
                                    new JsonDecoder(),
                                    new JsonEncoder(),
                                    new MainHandler(threadPool));
                        }
                    });
            ChannelFuture future = b.bind(PORT).sync();//указываем что сервак должен стартануть на порту , sync - запускаем эту задачу
            log.info("Connection...");
            future.channel().closeFuture().sync();//closeFuture() мы ждем когда кто нибудь этот сервак закроет
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();//закрываем пулы потоков
            workerGroup.shutdownGracefully();
        }
    }

    public void sendMessage (Object msg){
        channel.writeAndFlush(msg);
    }
}
//
