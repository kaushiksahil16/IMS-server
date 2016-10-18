package cserver;
import java.net.*;
import java.util.*;
import java.io.*;
import java.sql.*;
public class SMain
{
    public static void main(String a[]){
    HashMap<String,Socket> hm=new HashMap<String,Socket>();
 try{
     Class.forName("oracle.jdbc.OracleDriver");
           }catch(ClassNotFoundException e){System.out.println(e);}
     try{
    Connection con=DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE","system","manager");
     ServerSocket ss=new ServerSocket(1111);
     System.out.println("Server Started...................................");
         while(true)
    {
        Socket s=ss.accept();
BufferedReader br=new BufferedReader(new InputStreamReader(s.getInputStream()));
String nm=br.readLine();
hm.put(nm, s);
        SThread st=new SThread(s,hm,con);
    }
    }catch(Exception e){System.out.println("Error is "+e.toString());}
}
}
