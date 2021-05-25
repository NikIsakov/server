package chat.client;


import auth.Storage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import tcp.FileMessage;
import tcp.Message;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler extends SimpleChannelInboundHandler<Object> {
    private Callback onMessageReceivedCallback;

    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private final static int BASIC_BUFFER_SIZE = 1024 * 512;


    public ClientHandler(Callback onMessageReceivedCallback) {
        this.onMessageReceivedCallback = onMessageReceivedCallback;
    }

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("get file "+msg);
//
//        if (onMessageReceivedCallback != null) {
//            onMessageReceivedCallback.callback(msg);
//        }
//    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        onMessageReceivedCallback.callback("Please enter credentials. Sample [-auth login password]\n");

    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object message) throws Exception {

        if (onMessageReceivedCallback != null) {
            System.out.println("get message "+message);
            onMessageReceivedCallback.callback(message);
        }

//        threadPool.submit(() -> {
//            Controller controller = new Controller();
//            File chooseFile = (File) message;
//            while (controller.getFile() != null) {
//
//                System.out.println("Получили файл из контроллера в клиентхандлер" + chooseFile);
//
//                final String fileName = chooseFile.getName();
//                //log.info("получен файл "+chooseFile.getName());
//                final File file = new File(fileName);
//                try (RandomAccessFile accessFile = new RandomAccessFile(file, "r")) { //позволяет перемещаться по файлу,
//                    //читать из него или писать в него, как вам будет угодно. Вы также сможете заменить существующие части файла,
//                    //речь идет о обновлении содержимого файла, а точней о обновлении фрагмента файла.
//                    long length = file.length(); // размер файла?
//                    long pointer = accessFile.getFilePointer(); // получаем текущее состояние курсора в файле
//                    long available = length - pointer;
//                    while (available > 0) {
//                        byte[] buffer;
//                        if (available > BASIC_BUFFER_SIZE) {
//                            buffer = new byte[BASIC_BUFFER_SIZE];
//                        } else {
//                            buffer = new byte[(int) available];
//                        }
//                        accessFile.read(buffer);
//                        FileMessage fileMessage = new FileMessage();
//
//                        fileMessage.setFileName(fileName);
//                        fileMessage.setStartPosition(pointer);
//                        fileMessage.setData(buffer);
//                        final Channel channel = ctx.channel();
//                        //Использование ограничения размера буфера перед отправкой
//                        while (true) {
//                            if (channel.isActive()) {
//                                if (channel.isWritable()) {
//                                    ctx.writeAndFlush(fileMessage);
//                                    break;
//                                } else {
//                                    Thread.sleep(10);
//                                }
//                            } else {
//                                return;
//                            }
//                        }
//
////                    Отправка клиенту по мере исполнения поставленных задач
////                    Необходимо выполнять только из треда не относящегося к NioEventLoopGroup
////                    ctx.writeAndFlush(fileMessage).sync();
//
//                        pointer = accessFile.getFilePointer();
//                        available = length - pointer;
//                        System.out.println("передали файл серверу");
//                    }
//                } catch (IOException | InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }

    /*private void doAuth(Object message) {
        onMessageReceivedCallback.callback("Please enter credentials. Sample [-auth login password]");
        try {
            while (true){
                String mayBeCredentials = (String) message;
                if (mayBeCredentials.startsWith("-auth")){
                    String[] credentials = mayBeCredentials.split("\\s");
                    String mayBeNickname = storage.getAuthenticationService().
                            findNicknameLoginAndPassword(credentials[1], credentials[2]);
                    if (mayBeNickname != null){
                        if (!storage.isNickNameOccupied(mayBeNickname)){
                            onMessageReceivedCallback.callback("[INFO] Auth Ok");
                            name = mayBeNickname;
                            //storage.messageToAll(String.format("[%s] logged in", name));
                            storage.addingUser(this);
                            return;
                        } else {
                            onMessageReceivedCallback.callback("[INFO] Current user is logged in");
                        }
                    } else {
                        onMessageReceivedCallback.callback("[INFO] Wrong login or password");
                    }
                }
            }
        }catch (Exception e){
            throw new RuntimeException("SWW", e);
        }
    }*/
}
//
