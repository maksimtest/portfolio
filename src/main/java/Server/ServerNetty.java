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
 */

/**
 * Created with IntelliJ IDEA.
 * User: Maksim_Kuzmenyuk
 * Date: 01.11.13
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
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ServerNettyInit())
                        .option(ChannelOption.SO_BACKLOG,128);
                        //.childOption(ChannelOption.SO_KEEPALIVE,true);

                Channel ch = bootstrap.bind(port).sync().channel();
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
        return countConnection;
    }
    public static void addCountConnection(){
        countConnection++;
    }
    public static void delCountConnection(){
        countConnection--;
        if(countConnection<1)countConnection=0;
    }
}

