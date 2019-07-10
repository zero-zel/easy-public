package main.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class LinuxUtil {

    public static void runIt(Map<String,String> config){
        String host = config.get("host");
        String hostName = config.get("hostName");
        String password = config.get("password");
        try{
            Connection conn = new Connection(host);
            conn.connect();
            boolean isAuthenticated = conn.authenticateWithPassword(hostName, password);
            if (isAuthenticated == false) {
                throw new IOException("Authentication failed.");
            }
            Session sess = conn.openSession();
            String jiaoben = null;
            if("jar".equals(config.get("type"))){
                jiaoben = scriptJar(config);
            }else if("war".equals(config.get("type"))){
                jiaoben = scriptWar(config);
            }
            sess.execCommand(jiaoben);
            InputStream stdout = new StreamGobbler(sess.getStdout());
            BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
            while(true){
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(line);
            }
            System.out.println("部署完成,谢谢您的使用");
            sess.close();
            conn.close();
        }catch (IOException e){
            e.printStackTrace(System.err);
            System.exit(2);
        }
    }

	public static String scriptJar(Map<String,String> config){
        StringBuilder sb = new StringBuilder();
        System.out.println("2.1 正在关闭进程:" + config.get("nameCh"));
        sb.append("A=`ps -ef|grep 'java -jar " + config.get("proName") + "'|grep -v grep|awk '{print $2}'`;");
        sb.append("kill -9 $A;");
        sb.append("sleep 3;");
        sb.append("cd " + config.get("path") + ";");
        System.out.println("2.2 进入服务器目录：" + config.get("path") + "，删除日志文件:nohup.out");
        sb.append("rm -rf nohup.out;");
        sb.append("sleep 3;");
        System.out.println("2.3 启动项目：" + config.get("nameCh"));
        sb.append("nohup java -jar " + config.get("proName") + " >nohup.out &");
        sb.append("sleep 3;");
        System.out.println("脚本整理完毕,项目启动中...");
        return sb.toString();
    }

    public static String scriptWar(Map<String, String> config) {
        StringBuilder sb = new StringBuilder();
        System.out.println("2.1 正在关闭tomcat:" + config.get("tomcatName"));
        sb.append("A=`ps -ef|grep '" + config.get("tomcatName") + "'|grep -v grep|awk '{print $2}'`;");
        sb.append("kill -9 $A;");
        sb.append("sleep 3;");
        sb.append("cd " + config.get("tomcatPath") + "logs;");
        System.out.println("2.2 删除日志文件:catalina.out");
        sb.append("rm -rf catalina.out;");
        sb.append("sleep 3;");
        System.out.println("2.3 启动项目：" + config.get("nameCh"));
        sb.append("JAVA_HOME="+config.get("javaPath")+";export JAVA_HOME;");
        sb.append("sh " + config.get("tomcatPath") + "bin/startup.sh;");
        sb.append("sleep 5;");
        System.out.println("脚本整理完毕,项目启动中...");
        return sb.toString();
    }

}
