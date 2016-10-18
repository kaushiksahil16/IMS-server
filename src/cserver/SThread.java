package cserver;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class SThread implements Runnable{
HashMap <String,Socket> hm;
BufferedReader br;
PrintStream ps;
Socket s1;
Connection con;
Statement st;
public String getList(){
    try{
    Statement st1=con.createStatement();
    ResultSet rs=st.executeQuery("select distinct deptname from employee");
    String s="";
    while(rs.next()){
    s=s+rs.getString("deptname")+"|";
    ResultSet rs1=st1.executeQuery("select ename,job,active from employee where deptname='"+rs.getString("deptname")+"'");
    while(rs1.next())
    {
        s=s+rs1.getString("ename")+"("+rs1.getString("job")+")";,
    }
    s=s.substring(0, s.length()-1);
    s=s+"|";
    rs1.close();
    }
    s=s.substring(0, s.length()-1);
    rs.close();
    return s;
    }
    catch(SQLException e)
    {
        System.out.println(e);
        return "";
    }
}

    public SThread(Socket s1,HashMap hm1,Connection con1) {
   con=con1;
try{
this.s1=s1;
    st=con.createStatement();
    br=new BufferedReader(new InputStreamReader(s1.getInputStream()));
    ps=new PrintStream(s1.getOutputStream());
}
catch(Exception e){System.out.println(e);}
   new Thread(this).start();
    }

public void run(){
    try{
while(true){
String role1="";
String msg=br.readLine();
if(msg.equals("Login")){
String ename=br.readLine();
String password=br.readLine();
ResultSet rrs=st.executeQuery("select * from employee where ename='"+ename+"'");
while(rrs.next())
role1=role1+rrs.getString(4);
ps.println(role1);
ResultSet rs=st.executeQuery("select * from employee where ename='"+ename+"' and password='"+password+"'");
if(rs.next()){
ps.println("Yes");
String dp=getList();
ps.println(dp);
}
else
ps.println("No");
st.executeUpdate("update employee set active='y' where ename='"+ename+"'");
}

if(msg.equals("Unread Messages")){
String nm=br.readLine().trim();
ResultSet rs=st.executeQuery("select count(*)  from message where to_id='"+nm+"' or to_id='...' and msgread='n'");
rs.next();
int x=10;
x=rs.getInt(1);
ps.println(x);
}


if(msg.equals("Show Online"))
{
    String name="";
    ResultSet rs1=st.executeQuery("select * from employee where active='y'");
    while(rs1.next())
    {
    String oname=rs1.getString("ename");
    String job=rs1.getString("job");
    name=name+oname+"("+job+")"+",";
    }
    name=name.substring(0,name.length()-1);
    ps.println(name);
}



if(msg.equals("msg read"))
{
    String val=br.readLine();
    st.executeUpdate("update message set msgread='y' where msgid="+val);
}

if(msg.equals("Sign Up"))
{
    try
    {
    String ename=br.readLine();
    String password=br.readLine();
    String job=br.readLine();
    String doj=br.readLine();
    String role=br.readLine();
    String deptname=br.readLine();
    String telno=br.readLine();
    String email=br.readLine();
    st.executeUpdate("insert into employee values('"+ename+"','"+password+"','"+job+"','"+role+"','"+deptname+"','"+telno+"','"+email+"','"+doj+"','n')");
    ps.println("Valid");
    }

    catch(SQLException e){System.out.println(e);
    ps.println("Invalid");}

}

if(msg.equals("Change Password"))
{
    String password="";
    String usr=br.readLine();
    String oldpswd=br.readLine();
    String newpswd=br.readLine();
    String cnfrmpswd=br.readLine();


    ResultSet rrst=st.executeQuery("select * from employee where ename='"+usr+"'");
    while(rrst.next())
    {
      password=password+rrst.getString(2);
    }
    if(newpswd.equals(cnfrmpswd) && oldpswd.equals(password))
    {
    st.executeUpdate("update employee set password='"+newpswd+"' where ename='"+usr+"'");
    ps.println("True");
    }
    else
    {
        ps.println("False");
    }
}


if(msg.equals("Send Message")){
String msg1=br.readLine();
StringTokenizer sst=new StringTokenizer(msg1,"|",false);
String from=sst.nextToken();
String subject=sst.nextToken();
String message=sst.nextToken();
String to=sst.nextToken();
ResultSet rs=st.executeQuery("select max(msgid) from message");
rs.next();
int num=rs.getInt(1)+1;
rs.close();
st.executeUpdate("insert into message values(sysdate,'"+to+"','"+from+"','"+message+"','"+subject+"','n',"+num+")");
}

if(msg.equals("My Messages"))
{
    String usr=br.readLine().trim();
    ResultSet rst=st.executeQuery("select * from message where to_id='"+usr+"' or to_id='...'");
    String umsg="";
    while(rst.next())
    {   int num=rst.getInt(7);
        String dt=rst.getDate(1).toString();
        String from=rst.getString(3);
        String sub=rst.getString(5);
        String to=rst.getString(2);
        if(to.equals("..."))from=from+"...";
        String cntnt=rst.getString(4);
        umsg+=num+"|"+dt+"|"+from+"|"+sub+"|"+cntnt+"^";
      }
    if(umsg.length()>0)
    umsg=umsg.substring(0,umsg.length()-1);
    ps.println(umsg);
}

if(msg.equals("User Profile"))
{
    String user=br.readLine().substring(1);
    System.out.println(user);
    String udetail="";
    ResultSet rst=st.executeQuery("select * from employee where ename='"+user+"'");
    while(rst.next())
    {
        String ename=rst.getString(1);
        String job=rst.getString(3);
        String doj=rst.getString(8);
        String role=rst.getString(4);
        String deptname=rst.getString(5);
        String telno=rst.getString(6);
        String email=rst.getString(7);
        udetail+=ename+"|"+job+"|"+doj+"|"+role+"|"+deptname+"|"+telno+"|"+email;
    }
    ps.println(udetail);
}
if(msg.equals("conversation"))
{
    String users="";
ResultSet rs=st.executeQuery("select * from employee");
while(rs.next()){
users=users+rs.getString("ename")+"|";
}
ps.println(users);
}

if(msg.equals("SHOW"))
{

    String trackmsg="";
    String user1=br.readLine();
    String user2=br.readLine();
    ResultSet rs=st.executeQuery("select * from message where (from_id='"+user1+"' and to_id='"+user2+"') or (from_id='"+user2+"' and to_id='"+user1+"') order by dt");
    while(rs.next())
    {
       String date=rs.getDate(1).toString();
       String to=rs.getString(2);
       String from=rs.getString(3);
       String cntnt=rs.getString(4);
       String sub=rs.getString(5);
       trackmsg=trackmsg+date+"|"+to+"|"+from+"|"+sub+"|"+cntnt+"^";
    }
    if(trackmsg.length()>0)
    trackmsg=trackmsg.substring(0,trackmsg.length()-1);
    ps.println(trackmsg);
    }

if(msg.equals("Logged Out"))
{
    st.executeQuery("update employee set active='n' where ename='"+br.readLine()+"'");
}

if(msg.equals("Delete"))
{
    int id=Integer.parseInt(br.readLine());
    st.executeUpdate("delete from message where msgid="+id);
    ps.println("Deleted");
}

if(msg.equals("send file"))
{
   String fname=br.readLine();
   String sname=br.readLine();
   String usr=br.readLine();
   System.out.println("one");
   BufferedOutputStream fos=new BufferedOutputStream(new FileOutputStream(fname));
   String s;
   System.out.println("two");
   while(!((s=br.readLine()).equals("bye")))
   {
       fos.write(s.getBytes());
   }
   fos.flush();
   fos.close();
   ResultSet rs=st.executeQuery("select max(msgid) from message");
   rs.next();
   int n=rs.getInt(1)+1;
   st.executeUpdate("insert into message values(sysdate,'"+sname+"','"+usr+"','"+fname+"','File Received','n',"+n+")");
   ps.println("File Delivered");
}

if(msg.equals("Get File"))
{
    String val=br.readLine();

    InputStream is=new FileInputStream(val);
    BufferedReader b =new BufferedReader(new InputStreamReader(is));
    String s;
       while((s=b.readLine())!=null){
           ps.println(s);
       }
    ps.println("bye");
       ps.println("File Sent");
}
}
    }catch(Exception e){System.out.println(e);}

    }
}
