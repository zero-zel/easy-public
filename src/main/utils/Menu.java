package main.utils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Menu {

    public static void getMenu(){
        LinkedHashMap<String,Object> menu = (LinkedHashMap<String,Object>)YamlUtil.getYaml("projects");
        Map<String,String> choseMenu = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        sb.append("###########(项目一键部署)########### \n");
        for(Map.Entry<String, Object> entry : menu.entrySet()) {
            Map<String,String> project = (HashMap<String,String>)entry.getValue();
            sb.append(project.get("sort"));
            sb.append(". ");
            sb.append(project.get("nameCh"));
            sb.append("(");
            sb.append(project.get("name"));
            sb.append(") \n");
            choseMenu.put(project.get("sort"),project.get("name"));
        }
        sb.append("选择您要运行的项目,请输入数字:");
        System.out.println(sb.toString());
        Scanner scan = new Scanner(System.in);
        String witch = scan.nextLine();
        witch = choseMenu.get(witch);
        Object project = menu.get(witch);
        if(null == project){
            System.err.println("项目不存在，请您重新选择！！！");
            getMenu();
        }else{
            runIt((Map<String,String>)project);
        }
    }

    public static void runIt(Map<String,String> project){
        String host = project.get("host");
        String hostName = project.get("hostName");
        String password = project.get("password");
        System.out.println("正在连接服务："+ host + " ，请您稍等......");
        try {
            /**
            ChannelSftp sftp = SftpUtil.getSftpConnect(host,22,hostName,password);
            System.out.println("第1步：正在上传项目"+project.get("nameCh")+"，请耐心等待.....");
            String localPath = project.get("localPath") + project.get("proName");
            Integer code = SftpUtil.uploadFile(localPath, project.get("path"), project.get("proName"), sftp);
            SftpUtil.exit(sftp);
            */
            Integer code = 0;
            if(code.intValue() == 0){
                System.out.println("第2步：项目上传完毕,正在部署项目，请耐心等待.....");
                LinuxUtil.runIt(project);
            }else{
                clear();
                System.err.println("文件不存在，请检查您的jar包路径，或者您的jar包资源！！！");
            }
            System.err.println("5秒后退出程序！！！");
            Thread.sleep(5000L);
            System.exit(0);
            /**
        } catch (JSchException e) {
            e.printStackTrace();
        } catch (SftpException e) {
            e.printStackTrace();*/
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }

    /**
     * @param code 颜色代号：背景颜色代号(41-46)；前景色代号(31-36)
     * @param n 数字+m：1加粗；3斜体；4下划线
     * @param content 要打印的内容
     */
    public static void printlnColor(int code,int n,String content){
        System.out.format("\33[%d;%dm%s%n",code, n, content);
    }

    private static void clear() {
        try {
            String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (Exception exception) {

        }
    }

}
