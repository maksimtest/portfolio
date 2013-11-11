package Dao;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Maksim_Kuzmenyuk
 * Date: 01.11.13
 */
public class ReportJDBCDao implements ReportDao{
    public void addReport(String query,String ip,String redirect,String time,long get,long sent,long speed){
        Connection connection = null;
        Statement statement = null;
        String url = "jdbc:mysql://localhost/nettyserver";
        try {
            connection = DriverManager.getConnection(url, "root", "root");
            statement = connection.createStatement();
            String s1 = "INSERT INTO `stats` (`query`,`ip`, `time`, `getBytes`, `sentBytes`, `speedBytes`";
            String s2 = ") VALUES ('"+query+"','"+ip+"','"+time+"','"+get+"','"+sent+"','"+speed;
            String s3 = "')";
            //Вставка redirect в бд если не пустое, иначе вставится null
            if(!redirect.equals("")){
                s1+=", `redirect`";
                s2+= "','"+redirect;
            }


            statement.executeUpdate(s1 + s2 + s3);

        } catch (SQLException e) {
            System.out.println("Failed connection to bd");
            e.printStackTrace();
            return;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }

    @Override
    public ArrayList<ArrayList<String>> getReport1() {
        //Количество всего запросов
        ArrayList<ArrayList<String>> massive = new ArrayList<ArrayList<String>>();
        Connection connection = null;
        Statement statement = null;
        int ret =  0;
        String url = "jdbc:mysql://localhost/nettyserver";
        try {
            connection = DriverManager.getConnection(url, "root", "root");
            statement = connection.createStatement();
            ResultSet resultSet  = statement.executeQuery("SELECT count(*) FROM stats ");
            ArrayList<String> mass = new ArrayList<String>();
            mass.add(" Количество всего запросов");
            if(resultSet.next()){
                mass.add(resultSet.getString(1));
            } else{
                mass.add("0");
            }
            massive.add(mass);

        } catch (SQLException e) {
            System.out.println("Connected field");
            e.printStackTrace();
            return massive;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return massive;
    }
    @Override
    public ArrayList<ArrayList<String>> getReport2() {
        ArrayList<ArrayList<String>> massive= new ArrayList<ArrayList<String>>();
        Connection connection = null;
        Statement statement = null;
        int ret =  0;
        String url = "jdbc:mysql://localhost/nettyserver";
        try {
            connection = DriverManager.getConnection(url, "root", "root");
            System.out.println("Connection established");
            statement = connection.createStatement();
            ResultSet resultSet  = statement.executeQuery("SELECT COUNT(*) FROM (SELECT DISTINCT query,ip FROM stats) AS temp ");
            ArrayList<String> mass = new ArrayList<String>();
            mass.add(" Количество уникальных запросов(по одному на IP)");
            if(resultSet.next()){
                mass.add(resultSet.getString(1));
            } else{
                mass.add("0");
            }
                massive.add(mass);

        } catch (SQLException e) {
            System.out.println("Connected field");
            e.printStackTrace();
            return massive;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return massive;
    }

    public ArrayList<ArrayList<String>> getReport3() {
        ArrayList<ArrayList<String>> massive= new ArrayList<ArrayList<String>>();
        Connection connection = null;
        Statement statement = null;
        int ret =  0;
        String url = "jdbc:mysql://localhost/nettyserver";
        try {
            connection = DriverManager.getConnection(url, "root", "root");
            System.out.println("Connection established");
            statement = connection.createStatement();
            ResultSet resultSet  = statement.executeQuery("SELECT ip,COUNT(*),MAX(time) FROM stats GROUP BY ip order by ip");
            ArrayList<String> mass0 = new ArrayList<String>();
            mass0.add(" IP ");
            mass0.add(" Количество запросов");
            mass0.add(" Последний запрос");
            massive.add(mass0);
            while(resultSet.next()){
                ArrayList<String> mass = new ArrayList<String>();
                mass.add(resultSet.getString(1));
                mass.add(resultSet.getString(2));
                mass.add(String.valueOf(resultSet.getDate(3)));
                massive.add(mass);
            }

        } catch (SQLException e) {
            System.out.println("Connected field");
            e.printStackTrace();
            return massive;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return massive;
    }

    @Override
    public ArrayList<ArrayList<String>> getReport4() {
        ArrayList<ArrayList<String>> massive= new ArrayList<ArrayList<String>>();
        Connection connection = null;
        Statement statement = null;
        int ret =  0;
        String url = "jdbc:mysql://localhost/nettyserver";
        try {
            connection = DriverManager.getConnection(url, "root", "root");
            System.out.println("Connection established");
            statement = connection.createStatement();
            ResultSet resultSet  = statement.executeQuery("SELECT redirect,COUNT(*) FROM stats WHERE redirect IS NOT NULL GROUP BY redirect");
            ArrayList<String> mass0 = new ArrayList<String>();
            mass0.add(" URL ");
            mass0.add(" Количество переадрессаций");
            massive.add(mass0);

            while(resultSet.next()){
                ArrayList<String> mass = new ArrayList<String>();
                mass.add(resultSet.getString(1));
                mass.add(resultSet.getString(2));
                massive.add(mass);
            }

        } catch (SQLException e) {
            System.out.println("Connected field");
            e.printStackTrace();
            return massive;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return massive;
    }
       //
    @Override
    public ArrayList<ArrayList<String>> getReport5() {
        ArrayList<ArrayList<String>> massive= new ArrayList<ArrayList<String>>();
        Connection connection = null;
        Statement statement = null;
        int ret =  0;
        String url = "jdbc:mysql://localhost/nettyserver";
        try {
            connection = DriverManager.getConnection(url, "root", "root");
            System.out.println("Connection established");
            statement = connection.createStatement();
            ResultSet resultSet  = statement.executeQuery("SELECT * FROM stats order by time  desc limit 16");
            ArrayList<String> mass0 = new ArrayList<String>();
            mass0.add(" src_ip ");
            mass0.add(" URI ");
            mass0.add(" timestamp ");
            mass0.add(" sent_bytes ");
            mass0.add(" received_bytes ");
            mass0.add(" speed ");
            massive.add(mass0);

            while(resultSet.next()){
                ArrayList<String> mass = new ArrayList<String>();
                mass.add(resultSet.getString(3));
                mass.add(resultSet.getString(2));
                mass.add(resultSet.getString(4));
                mass.add(resultSet.getString(7));
                mass.add(resultSet.getString(6));
                mass.add(resultSet.getString(8));
                massive.add(mass);
            }

        } catch (SQLException e) {
            System.out.println("Connected field");
            e.printStackTrace();
            return massive;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return massive;

    }


}
