package Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created with IntelliJ IDEA.
 * User: max
 * Date: 01.11.13
 * Time: 11:43
 * To change this template use File | Settings | File Templates.
 */

/**
 * Created with IntelliJ IDEA.
 * User: Maksim_Kuzmenyuk
 * Date: 01.11.13
 * Time: 18:35
 * To change this template use File | Settings | File Templates.
 */
public class ServerNetty {
    private static int countConnection;

    private final int port;

    public ServerNetty(int port) {
        this.port = port;
    }

    public void run() throws Exception {
            // Configure the server.
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.option(ChannelOption.SO_BACKLOG, 1024);
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ServerNettyInit());

                Channel ch = b.bind(port).sync().channel();
                ch.closeFuture().sync();
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new ServerNetty(port).run();
    }
    public static int getCountConnection(){
        System.out.println("getCountConnection");
        return countConnection;
    }
    public static void addCountConnection(){
        System.out.println("addCountConnection");
        countConnection++;
    }
    public static void delCountConnection(){
        System.out.println("delCountConnection");
        countConnection--;
        if(countConnection<1)countConnection=0;
    }
}
