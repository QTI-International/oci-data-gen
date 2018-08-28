package kr.co.qtii.oci;

import java.util.Timer;

public class DataGen {
	private String dir = null;
	private int period = 0;
	private int repeat = 0;
//	private int intvl = 0;
	private int tags = 0;
	private boolean delete = false;
	private long deletePeriod;
//	private long deleteTime;
	private long modifiedTimeFilter;

	private String usage() {
		StringBuilder sb = new StringBuilder();
		sb.append("java kr.co.qtii.oci.DataGen <options>\n");
		sb.append("options: \n");
		sb.append("    -d: Directory path\n");
		sb.append("    -s: 데이터 생성 주기(초)\n");
		sb.append("    -r: 생성 데이터의 한 파일내에서의 반복횟수 \n");
		sb.append("    -t: Number of tag\n");
//		sb.append("    -i: 초당 생성 데이터\n");
		sb.append("    -del: 파일 삭제\n\n");
		sb.append("Example: java kr.co.qtii.oci.DataGen -d /home/kangzerg/temp/ -s 1 -r 3600 -t 500 -del ");
		return sb.toString();
	}

	public void setOptions(String[] args) throws Exception {
		for (int i = 0; i < args.length; i++) {
			String ops = args[i];

			if ("-d".equals(ops)) {
				// Directory path
				dir = args[i + 1];
			} else if ("-s".equals(ops)) {
				// 데이터 생성 주기(초)
				period = Integer.parseInt(args[i + 1]);
			} else if ("-r".equals(ops)) {
				// Number of data rows
				repeat = Integer.parseInt(args[i + 1]);
			} else if ("-t".equals(ops)) {
				// Number of data tags
				tags = Integer.parseInt(args[i + 1]);
//			} else if ("-i".equals(ops)) {
//				// Interval in milliseconds
//				intvl = Integer.parseInt(args[i + 1]);
			} else if ("-del".equals(ops)) {
				delete = true;
			}
		}

		if (dir == null || "".equals(dir)) {
			System.out.print("Directory: ");
			dir = "/home/kangzerg/temp";// new BufferedReader(new
										// InputStreamReader(System.in)).readLine().trim();
			if ("".equals(dir)) {
				throw new Exception("Directory: " + dir);
			}
		}
		
		if (period == 0) {
			System.out.print("Period: ");
			period = 1; // Integer.parseInt(new BufferedReader(new
						// InputStreamReader(System.in)).readLine().trim());
			if (period == 0) {
				throw new Exception("Period: " + period);
			}
		}

		if (repeat == 0) {
			System.out.print("Rows: ");
			repeat = 1; // Integer.parseInt(new BufferedReader(new
						// InputStreamReader(System.in)).readLine().trim());
			if (repeat == 0) {
				throw new Exception("Rows: " + repeat);
			}
		}

		if (tags == 0) {
			System.out.print("Tags: ");
			tags = 500; // Integer.parseInt(new BufferedReader(new
						// InputStreamReader(System.in)).readLine().trim());
			if (tags == 0) {
				throw new Exception("Tags: " + tags);
			}
		}

//		if (intvl == 0) {
//			System.out.print("Rows per sec: ");
//			intvl = 500; // Long.parseLong(new BufferedReader(new
//							// InputStreamReader(System.in)).readLine().trim());
//			if (intvl == 0) {
//				throw new Exception("Interval: " + intvl);
//			}
//		}
		
		// repeat가 1이라면...1000초(약16분)에 한번 삭제 프로세스가 작동하고 수정된지 100초 지난 파일을 삭제한다.
		// repeat가 10이라면...10000초(약2시간45분)에 한번 삭제 프로세스가 작동하고 수정된지 1000초 지난 파일을 삭제한다.
		
		// repeat가 600이라면...600000초(약7일)에 한번 삭제 프로세스가 작동하고 수정된지 60000초(약16시간40분) 지난 파일을 삭제한다.
		
		// 주기적으로 파일이 1000개 생성되면 삭제 프로세스 작동.
		deletePeriod = repeat * 1000 * 1000L;
		modifiedTimeFilter = repeat * 100 * 1000L;
//		deleteTime = System.currentTimeMillis();

		System.out.println("");
		System.out.println("-d(Directory): " + dir);
		System.out.println("-s(Period): " + period);
		System.out.println("-r(Rows): " + repeat);
		System.out.println("-t(tags): " + tags);
//		System.out.println("-i(Interval): " + intvl);
		System.out.println("-del(Delete file): " + delete);
		System.out.println("");
	}

	public void go() throws Exception {
		
		Timer dataGenTimer = new Timer("DataGen-Timer-thread", false);
		dataGenTimer.scheduleAtFixedRate(new DataGenTask(repeat, 
//			intvl, 
			tags, dir), 1000L, period * 1000L);
		
		if (delete) {
			Timer fileDeleteTimer = new Timer("FileDelete-Timer-thread", false);
			fileDeleteTimer.scheduleAtFixedRate(new FileDeleteTask(dir, modifiedTimeFilter), 1000L, deletePeriod);
		}
	}

	public static void main(String[] args) throws Exception {
		DataGen dataGen = new DataGen();
		System.out.println(dataGen.usage());
		System.out.println("");

		dataGen.setOptions(args);
		dataGen.go();
	}
}
