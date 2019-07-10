package main.utils;

import com.jcraft.jsch.SftpProgressMonitor;

public class ProgressMonitor implements SftpProgressMonitor {
	
	private long transfered;
	
	private long filesize;
	
	public ProgressMonitor(long filesize) {
		this.filesize = filesize;
	}
	
	@Override
    public boolean count(long count) {
        transfered = transfered + count;
        if(filesize<1024){
        	System.out.println("已经传输：" + transfered +"byte 总大小："+filesize+"byte");
        }
        if((filesize> 1024) && (filesize<1048576)){
            System.out.println("已经传输：" + transfered/1024 +"K 总大小："+(filesize/1024)+"K");
        }else{
            System.out.println("已经传输：" + transfered/1024/1024 +"M 总大小："+(filesize/1024/1024)+"M");
        }
        return true;
    }
 
    @Override
    public void end() {
        System.out.println("传输完成");
    }
 
    @Override
    public void init(int op, String src, String dest, long max) {
        System.out.println("开始传输");
    }
}
