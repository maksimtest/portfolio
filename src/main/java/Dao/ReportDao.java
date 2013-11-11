package Dao;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim_Kuzmenyuk
 * Date: 01.11.13
 * Time: 22:37
 * */
public interface ReportDao {
    public void addReport(String url,String ip,String redirect,String time,long get,long sent,long speed);
    public ArrayList<ArrayList<String>> getReport1();
    public ArrayList<ArrayList<String>> getReport2();
    public ArrayList<ArrayList<String>> getReport3();
    public ArrayList<ArrayList<String>> getReport4();
    public ArrayList<ArrayList<String>> getReport5();
}
