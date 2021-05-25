package chat.server;

import auth.Storage;
import io.netty.channel.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class MainHandler extends SimpleChannelInboundHandler<Object> {//ChannelInboundHandlerAdapter { ////Inbound - мы работаем на вход, обрабатываем входящие
    private static final List<Channel> channels = new ArrayList<>();
    private String clientName;
    private static int newClientIndex = 1;
    private File file;
    Logger log = LogManager.getLogger(ServerApp.class);
    private Storage storage;
    private ServerApp serverApp;
    private String name;

    public void setFile(File file) {
        this.file = file;
    }

    private final ExecutorService threadPool;
    private final static int BASIC_BUFFER_SIZE = 1024 * 512;

    public MainHandler(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {//вызывается при открытии соединения и готовности передавать данные
        log.info("Клиент подключился "+ctx);
        channels.add(ctx.channel());
        clientName = "Client #"+ newClientIndex;
        newClientIndex++;

        //broadcastMessage("SERVER",);



         /*threadPool.submit(() -> {
//            Controller controller = new Controller();
//            File chooseFile = controller.getFile();


            //final String fileName = "morrowind-fullrest-repack-4.0.0";
            //final String fileName = chooseFile.getName();
            //log.info("получен файл "+chooseFile.getName());
            //final File file = new File(fileName);
            try (RandomAccessFile accessFile = new RandomAccessFile(file, "r")) { //позволяет перемещаться по файлу,
            //читать из него или писать в него, как вам будет угодно. Вы также сможете заменить существующие части файла,
            //речь идет о обновлении содержимого файла, а точней о обновлении фрагмента файла.
                long length = file.length(); // размер файла?
                long pointer = accessFile.getFilePointer(); // получаем текущее состояние курсора в файле
                long available = length - pointer;
                while (available > 0) {
                    byte[] buffer;
                    if (available > BASIC_BUFFER_SIZE) {
                        buffer = new byte[BASIC_BUFFER_SIZE];
                    } else {
                        buffer = new byte[(int) available];
                    }
                    accessFile.read(buffer);
                    FileMessage fileMessage = new FileMessage();

                    //fileMessage.setFileName(fileName);
                    fileMessage.setStartPosition(pointer);
                    fileMessage.setData(buffer);
                    final Channel channel = ctx.channel();
                    //Использование ограничения размера буфера перед отправкой
                    while (true) {
                        if (channel.isActive()) {
                            if (channel.isWritable()) {
                                ctx.writeAndFlush(fileMessage);
                                break;
                            } else {
                                Thread.sleep(10);
                            }
                        } else {
                            return;
                        }
                        broadcastMessage("server",fileMessage);
                    }


//                    Отправка клиенту по мере исполнения поставленных задач
//                    Необходимо выполнять только из треда не относящегося к NioEventLoopGroup
//                    ctx.writeAndFlush(fileMessage).sync();

                        pointer = accessFile.getFilePointer();
                        available = length - pointer;
                    }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        }
                        });*/
    }

//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {//чтение входящих сообщений
//        System.out.println("Получен файл: "+msg.toString());
//        broadcastMessage(clientName, msg);
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object message) throws Exception {
        log.info(ctx);
        if (message instanceof String){
            log.info("Получено сообщение: "+message);
            broadcastMessage(clientName, message);
        }else {
            File file;
            log.info("Получен файл: "+message);
            file = (File)message;
            uploadFile(file);
        }
    }

    public void uploadFile(File file) {
        try {
            System.out.println("Свойста: " + Files.getAttribute(file.toPath(), "creationTime"));
            File fileCopy = new File("server/src/main/FileSystem/"+ file.getName());
            if (!fileCopy.exists()){
                fileCopy.createNewFile();
                Files.copy(file.toPath(), fileCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);
                Files.setLastModifiedTime(fileCopy.toPath(), FileTime.fromMillis(System.currentTimeMillis()));// присваиваем скопированному файлу системную дату
                System.out.println("Файл скопирован в директорию сервера: " + fileCopy.toString());
            }else {//если файл уже существуует
                long lastModifiedTimeFirstFile = Files.getLastModifiedTime(file.toPath()).toMillis();//получаем  время последнего изменения файла в long
                long lastModifiedTimeCopyFile = Files.getLastModifiedTime(fileCopy.toPath()).toMillis();
                System.out.println(lastModifiedTimeCopyFile+"\n"+lastModifiedTimeFirstFile);//для протокола
                if (lastModifiedTimeCopyFile < lastModifiedTimeFirstFile){
                    Files.copy(file.toPath(), fileCopy.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    Files.setLastModifiedTime(fileCopy.toPath(),FileTime.fromMillis(System.currentTimeMillis()));// присваиваем скопированному файлу системную дату
                    System.out.println("Файл скопирован в директорию сервера: " + fileCopy.toString());
                    //mainArea.appendText("Файл скопирован в директорию сервера: "+fileCopy.toString()+"\n");
                }else{
                    System.out.println("На сервере более новая версия файла или такая же");
                    //mainArea.appendText("На сервере более новая версия файла или такая же\n");
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doAuth(Object message) {

//        try {
//                String mayBeCredentials = (String) message;
//                if (mayBeCredentials.startsWith("-auth")){
//                    String[] credentials = mayBeCredentials.split("\\s");
//                    String mayBeNickname = storage.getAuthenticationService().
//                            findNicknameLoginAndPassword(credentials[1], credentials[2]);
//                    if (mayBeNickname != null){
//                        if (!storage.isNickNameOccupied(mayBeNickname)){
//                            serverApp.sendMessage("[INFO] Auth Ok");
//                            name = mayBeNickname;
//                            //storage.messageToAll(String.format("[%s] logged in", name));
//                            storage.addingUser(this);
//                            return;
//                        } else {
//                            serverApp.sendMessage("[INFO] Current user is logged in");
//                        }
//                    } else {
//                        serverApp.sendMessage("[INFO] Wrong login or password");
//                    }
//                }
//
//        }catch (Exception e){
//            throw new RuntimeException("SWW", e);
//        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Клиент "+clientName+" отвалился");
        channels.remove(ctx.channel());
        broadcastMessage("SERVER", "Клиент "+clientName+" вышел из сети");
        ctx.close();
    }

    public void broadcastMessage(String clientName, Object message){
        String out = String.format("[%s]: %s\n", clientName, message.toString());
        for (Channel c: channels){
            c.writeAndFlush(out);
        }
    }

}
//
