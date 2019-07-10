package main.utils;

import com.jcraft.jsch.*;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import org.apache.commons.lang.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

/**
 * 文件工具类.<br>
 * 1.所有的文件路径必须以'/'开头和结尾，否则路径最后一部分会被当做是文件名<br>
 * 2. @since version-0.3 方法出现异常的时候，<del>会关闭sftp连接(但是不会关闭session和channel)</del>(del @ version 0.31)，异常会抛出<br>
 * @author Leon Lee
 */
public class SftpUtil {

    /**
     * 文件路径前缀. /ddit-remote
     */
    private static final String PRE_FIX = "";

    /**
     * sftp连接池.
     */
    private static final Map<String, Channel> SFTP_CHANNEL_POOL = new HashMap<String, Channel>();

    /**
     * 获取sftp协议连接.
     * @param host 主机名
     * @param port 端口
     * @param username 用户名
     * @param password 密码
     * @return 连接对象
     * @throws JSchException 异常
     */
    public static ChannelSftp getSftpConnect(final String host, final int port, final String username,final String password) throws JSchException {
        Session sshSession = null;
        Channel channel = null;
        ChannelSftp sftp = null;
        String key = host + "," + port + "," + username + "," + password;
        if (null == SFTP_CHANNEL_POOL.get(key)) {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            channel = sshSession.openChannel("sftp");
            channel.connect();
            SFTP_CHANNEL_POOL.put(key, channel);
        } else {
            channel = SFTP_CHANNEL_POOL.get(key);
            sshSession = channel.getSession();
            if (!sshSession.isConnected())
                sshSession.connect();
            if (!channel.isConnected())
                channel.connect();
        }
        sftp = (ChannelSftp) channel;
        return sftp;
    }

    /**
     * 下载文件-sftp协议.
     * @param downloadFile 下载的文件
     * @param saveFile 存在本地的路径
     * @param sftp sftp连接
     * @return 文件
     * @throws Exception 异常
     */
    public static File download(final String downloadFile, final String saveFile, final ChannelSftp sftp)
            throws Exception {
        FileOutputStream os = null;
        File file = new File(saveFile);
        try {
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                file.createNewFile();
            }
            os = new FileOutputStream(file);
            List<String> list = formatPath(downloadFile);
            sftp.get(list.get(0) + list.get(1), os);
        } catch (Exception e) {
            throw e;
        } finally {
            os.close();
        }
        return file;
    }

    /**
     * 下载文件-sftp协议.
     * @param downloadFile 下载的文件
     * @param sftp sftp连接
     * @return 文件 byte[]
     * @throws Exception 异常
     */
    public static byte[] downloadAsByte(final String downloadFile, final ChannelSftp sftp) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            List<String> list = formatPath(downloadFile);
            sftp.get(list.get(0) + list.get(1), os);
        } catch (Exception e) {
            throw e;
        } finally {
            os.close();
        }
        return os.toByteArray();
    }

    /**
     * 删除文件-sftp协议.
     * @param pathString 要删除的文件
     * @param sftp sftp连接
     * @throws SftpException 异常
     */
    public static void rmFile(final String pathString, final ChannelSftp sftp) throws SftpException {
        List<String> list = formatPath(pathString);
        String dir = list.get(0);
        String file = list.get(1);
        if (dirExist(dir + file, sftp)) {
            sftp.rm(list.get(0) + list.get(1));
        }
    }

    /**
     * 删除文件夹-sftp协议.如果文件夹有内容，则会抛出异常.
     * @param pathString 文件夹路径
     * @param sftp sftp连接
     * @throws SftpException 异常
     */
    public static void rmDir(final String pathString, final ChannelSftp sftp, final boolean recursion)
            throws SftpException {
        String fp = formatPath(pathString).get(0);
        if (dirExist(fp, sftp)) {
            if (recursion)
                exeRmRec(fp, sftp);
            else
                sftp.rmdir(fp);
        }
    }

    /**
     * 递归删除执行.
     * @param pathString 文件路径
     * @param sftp sftp连接
     * @throws SftpException
     */
    private static void exeRmRec(final String pathString, final ChannelSftp sftp) throws SftpException {
        @SuppressWarnings("unchecked")
        Vector<LsEntry> vector = sftp.ls(pathString);
        if (vector.size() == 1) { // 文件，直接删除
            sftp.rm(pathString);
        } else if (vector.size() == 2) { // 空文件夹，直接删除
            sftp.rmdir(pathString);
        } else {
            String fileName = "";
            // 删除文件夹下所有文件
            for (LsEntry en : vector) {
                fileName = en.getFilename();
                if (".".equals(fileName) || "..".equals(fileName)) {
                    continue;
                } else {
                    exeRmRec(pathString + "/" + fileName, sftp);
                }
            }
            // 删除文件夹
            sftp.rmdir(pathString);
        }
    }

    /**
     * 上传文件-sftp协议.
     * @param srcFile 源文件
     * @param dir 保存路径
     * @param fileName 保存文件名
     * @param sftp sftp连接
     * @throws Exception 异常
     */
    public static int uploadFile(final String srcFile, final String dir, final String fileName, final ChannelSftp sftp)throws SftpException {
        File file = new File(srcFile);
        if(!file.exists()) {
            return 500;
        }else {
            //mkdir(dir, sftp);
        	ProgressMonitor monitor = new ProgressMonitor(file.length());
            sftp.cd(dir);
            sftp.put(srcFile,fileName,monitor);
            return 0;
        }

    }

    /**
     * 上传文件-sftp协议.
     * @param srcFile 源文件路径，/xxx/xx.yy 或 x:/xxx/xxx.yy
     * @param sftp sftp连接
     * @return 上传成功与否
     * @throws SftpException 异常
     */
    public static boolean uploadFile(final String srcFile, final ChannelSftp sftp) throws SftpException {
        File file = new File(srcFile);
        if (file.exists()) {
            List<String> list = formatPath(srcFile);
            uploadFile(srcFile, list.get(0), list.get(1), sftp);
            return true;
        }
        return false;
    }

    /**
     * 根据路径创建文件夹.
     * @param dir 路径 必须是 /xxx/xxx/ 不能就单独一个/
     * @param sftp sftp连接
     * @throws SftpException 异常
     */
    public static boolean mkdir(final String dir, final ChannelSftp sftp) throws SftpException {
        if (StringUtils.isBlank(dir))
            return false;
        String md = dir.replaceAll("\\\\", "/");
        if (md.indexOf("/") != 0 || md.length() == 1)
            return false;
        return mkdirs(md, sftp);
    }

    /**
     * 递归创建文件夹.
     * @param dir 路径
     * @param sftp sftp连接
     * @return 是否创建成功
     * @throws SftpException 异常
     */
    private static boolean mkdirs(final String dir, final ChannelSftp sftp) throws SftpException {
        String dirs = dir.substring(1, dir.length() - 1);
        String[] dirArr = dirs.split("/");
        String base = "";
        for (String d : dirArr) {
            base += "/" + d;
            if (dirExist(base + "/", sftp)) {
                continue;
            } else {
                sftp.mkdir(base + "/");
            }
        }
        return true;
    }

    /**
     * 判断文件夹是否存在.
     * @param dir 文件夹路径， /xxx/xxx/
     * @param sftp sftp协议
     * @return 是否存在
     */
    private static boolean dirExist(final String dir, final ChannelSftp sftp) {
        try {
            Vector<?> vector = sftp.ls(dir);
            if (null == vector)
                return false;
            else
                return true;
        } catch (SftpException e) {
            return false;
        }
    }

    /**
     * 格式化路径.
     * @param srcPath 原路径. /xxx/xxx/xxx.yyy 或 X:/xxx/xxx/xxx.yy
     * @return list, 第一个是路径（/xxx/xxx/）,第二个是文件名（xxx.yy）
     */
    public static List<String> formatPath(final String srcPath) {
        List<String> list = new ArrayList<String>(2);
        String repSrc = srcPath.replaceAll("\\\\", "/");
        int firstP = repSrc.indexOf("/");
        int lastP = repSrc.lastIndexOf("/");
        String fileName = lastP + 1 == repSrc.length() ? "" : repSrc.substring(lastP + 1);
        String dir = firstP == -1 ? "" : repSrc.substring(firstP, lastP);
        dir = PRE_FIX + (dir.length() == 1 ? dir : (dir + "/"));
        list.add(dir);
        list.add(fileName);
        return list;
    }

    /**
     * 关闭协议-sftp协议.(关闭会导致连接池异常，因此不建议用户自定义关闭)
     * @param sftp sftp连接
     */
    public static void exit(final ChannelSftp sftp) {
        sftp.exit();
    }

}
