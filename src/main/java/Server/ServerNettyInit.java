package Server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim_Kuzmenyuk
 * Date: 01.11.13
 */
public class ServerNettyInit extends ChannelInitializer<SocketChannel> {
    private TrafficCounter trafficCounter;
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        //
        GlobalTrafficShapingHandler globalTrafficShapingHandler = new GlobalTrafficShapingHandler(ch.eventLoop());
        trafficCounter = globalTrafficShapingHandler.trafficCounter();
        trafficCounter.start();
        //
        p.addLast(globalTrafficShapingHandler);
        //
        p.addLast("codec", new HttpServerCodec());
        p.addLast("handler", new ServerNettyHandler(trafficCounter));
    }
}



