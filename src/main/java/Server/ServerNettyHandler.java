package Server;

import Dao.ReportJDBCDao;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.traffic.TrafficCounter;

import java.text.SimpleDateFormat;
import java.util.*;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim_Kuzmenyuk
 * Date: 01.11.13
 */
public class ServerNettyHandler  extends ChannelInboundHandlerAdapter {

    private TrafficCounter trafficCounter;
    //
    ServerNettyHandler(TrafficCounter traffic){
        trafficCounter = traffic;
    }
    ///
    private boolean isCorrectQuery=false;
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        //Если соединение корректно, то уменьшить количество соединений
        if(isCorrectQuery){
            ServerNetty.delCountConnection();
            isCorrectQuery=false;
        }
        ctx.flush();
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            String uri = req.getUri();
            String redirectUrl="";
            System.out.println("uri="+uri);
            byte[] responseText="Не корректный запрос. 404".getBytes();
            String ip = ctx.pipeline().channel().remoteAddress().toString().substring(1);
            System.out.println("Uri="+uri+" ip="+ip);

            if(uri.indexOf("/hello")>=0){
                addStat(uri, ip, "");
                Thread.sleep(10000L);
                responseText = "Hello World!".getBytes();
            }
            else if(uri.indexOf("/redirect?url=")>=0){
                redirectUrl = uri.substring(14);
                addStat(uri, ip, redirectUrl);
            }
            else if(uri.indexOf("/status")>=0){
                addStat(uri, ip, "");
                responseText = getStatus();
            }
            // http://localhost:8080/hello
            // http://localhost:8080/redirect?url=http://bored.ua/result&post=17
            // http://localhost:8080/status
            if (is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
            }
            boolean keepAlive = isKeepAlive(req);
            FullHttpResponse response;
            if(!redirectUrl.equals("")){
                System.out.println("redirect:"+redirectUrl);
                response = new DefaultFullHttpResponse(HTTP_1_1,MOVED_PERMANENTLY);
                response.headers().set(LOCATION, redirectUrl);
            } else{
                response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(responseText));
                response.headers().set(CONTENT_TYPE, "text/html;  charset=UTF-8");
                response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
            }
            System.out.println("!!! keepAlive="+keepAlive);
            if (!keepAlive) {
                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, Values.KEEP_ALIVE);
                ctx.write(response);
            }

        }
    }

    private void addStat(String query,String ip,String redirect){
        System.out.println("!!! "+query);
        //Увеличение счетчика соединений
        if(!isCorrectQuery){
            ServerNetty.addCountConnection();
            //Установка признака корректности соединения
            isCorrectQuery=true;
        }

        //Определение текущего времени в формате для записи в БД
        Date today = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
        String time= f.format(today);

        //Определение трафика
        trafficCounter.stop();
        long getBytes = trafficCounter.cumulativeReadBytes();
        long sentBytes = trafficCounter.cumulativeWrittenBytes();
        long speed = trafficCounter.lastWriteThroughput();
        trafficCounter.resetCumulativeTime();

        //Запись в БД
        ReportJDBCDao bd = new ReportJDBCDao();
        bd.addReport(query, ip, redirect, time,getBytes,sentBytes,speed);

    }

    public byte[] getStatus(){
        ReportJDBCDao bd = new ReportJDBCDao();

        String s1="<html><title>Статус-отчет</title><body><h1>Статистический отчет</h1>";

        byte[] a = s1.getBytes();
        byte[] b = transformToTable(bd.getReport1(),"");
        byte[] c = transformToTable(bd.getReport2(),"");
        byte[] d = transformToTable(bd.getReport3(),"Счетчик запросов на каждый IP");
        byte[] e = transformToTable(bd.getReport4(),"Количество переадрессаций по URL");
        byte[] f = ("<h3>Количество открытых соединений в данный момент:"+ServerNetty.getCountConnection()+"</h3>").getBytes();
        byte[] g = transformToTable(bd.getReport5(),"Статистика последних запросов(16)");
        byte[] h = ("</body></html>").getBytes();

        return  joinArrays(joinArrays(a,b,c),joinArrays(d,e,f),joinArrays(g,h));
    }
    public byte[] transformToTable(ArrayList<ArrayList<String>> mass,String name){
        String s="<br>";
        //Если есть заголовок текущей части отчета, то выводить шапку
        if(!name.equals(""))s= "<h3>"+name+"</h3>";

        byte[] res = (s+"<table>").getBytes();
        String stroka;
        for(int i = 0;i<mass.size();i++){
            stroka = "<tr>";
            for(int j = 0;j<mass.get(i).size();j++){
                stroka += "<td style='border:2px solid maroon'>"+mass.get(i).get(j)+"</td>";
            }
            stroka += "</tr>";
            res = joinArrays(res,stroka.getBytes());
        }
        res = joinArrays(res,"</table>".getBytes());
        return res;
    }

    public byte[] joinArrays(byte[] a,byte[] b){
        byte[] c= new  byte[a.length+b.length];
        for(int i = 0;i<a.length;i++){
            c[i]=a[i];
        }
        for(int j = 0;j<b.length;j++){
            c[j+a.length]=b[j];
        }
        return c;

    }
    public byte[] joinArrays(byte[] a,byte[] b,byte[] c){
        byte[] r= new  byte[a.length+b.length+c.length];
        int k=0;
        for(int i = 0;i<a.length;i++){
            r[k++]=a[i];
        }
        for(int j = 0;j<b.length;j++){
            r[k++]=b[j];
        }
        for(int i = 0;i<c.length;i++){
            r[k++]=c[i];
        }
        return r;

    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}



