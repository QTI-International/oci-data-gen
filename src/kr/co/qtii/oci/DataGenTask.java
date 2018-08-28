package kr.co.qtii.oci;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @date : Feb 23, 2018
 * @version :
 * @author : 강석진
 * @description :
 * @history :
 */
public class DataGenTask extends TimerTask {
	private int repeat;
//	private int intvl;
	private int tags;
	private String dir;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHH");
	private int repeatCnt = 0;
	private BufferedWriter writer;
	private String namespace = "5";

	public DataGenTask(int repeat, 
//		int intvl, 
		int tags, String dir) {
		System.out.println(String.format("r:%d, t:%d, d:%s", repeat, 
//			intvl, 
			tags, dir));
		this.repeat = repeat;
//		this.intvl = intvl;
		this.tags = tags;
		this.dir = dir;
		
		this.writer = createNewWriter();
	}

	@Override
	public void run() {
		System.out.println("Data gen start...");
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
//		for (int j = 0; j < intvl; j++) {
		for (int j = 0; j < tags; j++) {
//			System.out.println("j:" + j);
			String time = formatter.format(new Date());
//			String time = System.currentTimeMillis() + "";

//			int seq = ThreadLocalRandom.current().nextInt(0, tags);
			int seq = j;
			String tag_id = "T" + seq;
			String tag_data;
			
			if (seq >= 0 && seq <= 9) {	// 0 ~ 200
				tag_data = String.valueOf(ThreadLocalRandom.current().nextInt(0, 201));
			} else if (seq >= 10 && seq < 30) {	// 0.000 ~ 1.000
				int num = ThreadLocalRandom.current().nextInt(0, 1001);
				tag_data = String.valueOf(num / 1000) + "." + String.valueOf(num % 1000);
			} else if (seq >= 30 && seq < 50) {	// 0.000 ~ 2.000
				int num = ThreadLocalRandom.current().nextInt(0, 2001);
				tag_data = String.valueOf(num / 1000) + "." + String.valueOf(num % 1000);
			} else if (seq >= 50 && seq < 70) {	// 200 ~ 500
				int num = ThreadLocalRandom.current().nextInt(200, 501);
				tag_data = String.valueOf(num);
			} else if (seq >= 70 && seq < 80) {	// 40 ~ 70
				int num = ThreadLocalRandom.current().nextInt(40, 71);
				tag_data = String.valueOf(num);
			} else if (seq >= 80 && seq < 90) {	// 0 ~ 1000
				int num = ThreadLocalRandom.current().nextInt(0, 1001);
				tag_data = String.valueOf(num);
			} else if (seq >= 90 && seq < 100) {	// 10.0 ~ 0.0
				int num = ThreadLocalRandom.current().nextInt(-100, 1);
				if (num == 0) {
					tag_data = "0.0";
				} else if (num > -10) {
					tag_data =
						"-" + String.valueOf(num / 10) + "." + String.valueOf(Math.abs(num % 10));
				} else {
					tag_data = String.valueOf(num / 10) + "." + String.valueOf(Math.abs(num % 10));
				}
			} else if (seq >= 100 && seq < 200) {	// 100 ~ 10000
				tag_data = String.valueOf(ThreadLocalRandom.current().nextInt(100, 10001));
			} else if (seq >= 200 && seq < 300) {	// 100 ~ 300
				tag_data = String.valueOf(ThreadLocalRandom.current().nextInt(100, 301));
			} else if (seq >= 300 && seq < 400) {	// 50 ~ 150
				tag_data = String.valueOf(ThreadLocalRandom.current().nextInt(50, 151));
			} else if (seq >= 400 && seq < 500) {	// 10 ~ 1500
				tag_data = String.valueOf(ThreadLocalRandom.current().nextInt(10, 1501));
			} else {	// 0 ~ 10
				tag_data = String.valueOf(ThreadLocalRandom.current().nextInt(0, 11));
			}

			try {
				String data = String.format("%s,%s,%s,%s", namespace, tag_id, tag_data, time);
				writer.append(data + "\n");
				System.out.println(data);
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		repeatCnt++;
		
		if (repeat < repeatCnt) {
			writer = createNewWriter();
			repeatCnt = 0;
		}

//		System.out.println("Data gen end.");
	}
	
	private BufferedWriter createNewWriter() {
//		System.out.println("Create new writer start.");
		if (writer != null) {
			try {
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			finally {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		Date now = new Date();
		String subDirectoryName = formatter.format(now);
		File targetDirectory = new File(dir + File.separator + subDirectoryName);
		if (!targetDirectory.exists()) targetDirectory.mkdirs();

		String fpath = targetDirectory.getAbsolutePath() + File.separator + "data_"
			+ System.currentTimeMillis() + ".txt";
		BufferedWriter newWriter = null;
		try {
			newWriter = new BufferedWriter(new FileWriter(new File(fpath), true));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("Created data file: " + fpath);
		
		return newWriter;
	}
}
