package kr.co.qtii.oci;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * @date : Feb 23, 2018
 * @version : 
 * @author : 강석진
 * @description :
 * @history : 
 */
public class FileDeleteTask extends TimerTask {
	private String dir; 
	private long modifiedTimeFilter;
	
	public FileDeleteTask(String dir, long modifiedTimeFilter) {
		this.dir = dir;
		this.modifiedTimeFilter = modifiedTimeFilter;
	}
	
	@Override
	public void run() {
		long current = System.currentTimeMillis();
		List<File> files = new ArrayList<File>();
		listf(dir, files);
		int delCnt = 0;
		for (File file : files) {
			if (modifiedTimeFilter < current - file.lastModified()) {
				if (file.delete())
					delCnt++;
			}
		}
//		deleteTime = current;
		System.out.println(String.format("현재파일 갯수:%s, 삭제된 파일 갯수:%s", files.size(), delCnt));
		
		List<File> dirs = new ArrayList<File>();
		listd(dir, dirs);
		for (File d : dirs) {
			if (d.listFiles().length == 0) {
				d.delete();
			}
		}
	}
	
	private void listf(String directoryName, List<File> files) {
		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				files.add(file);
			} else if (file.isDirectory()) {
				listf(file.getAbsolutePath(), files);
			}
		}
	}
	
	private void listd(String directoryName, List<File> dirs) {
		File directory = new File(directoryName);

		// get all the directory from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isDirectory()) {
				dirs.add(file);
				listd(file.getAbsolutePath(), dirs);
			}
		}
	}
}
