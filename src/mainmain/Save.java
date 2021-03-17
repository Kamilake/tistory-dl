package mainmain;

import java.io.File;
import static mainmain.Backup.myDir;
import static mainmain.Backup.blogName;

/** 버퍼 또는 캐시에 저장된 데이터를 디스크에 영구적으로 저장하는 것과 관련된 정보를 저장하고 있습니다. */
public class Save {

	/** 페이지 번호를 주면 전체 절대경로를 반환합니다. @param 페이지번호 */
	public String saveDir(int pageNum) { // 페이지 번호로 저장 경로지정
		//Log log = new Log();
		// String blogName = "testblog2";
		// String myDir = "";

		String path = myDir + "Backup/" + blogName + "/" + pageNum;

		File folder = new File(path);
		if (!folder.exists()) {
			File blogroot = new File(myDir + "Backup/" + blogName);
			if (!blogroot.exists()) {
				File root = new File(myDir + "Backup");
				if (!root.exists())
					root.mkdir();
				blogroot.mkdir();
			}
			folder.mkdir();
		}
		return path;
	}
	
//deprecated 더 이상 사용되지 않음.
	// private static void copyFileUsingStream(File source, File dest) throws IOException {
	// 	InputStream is = null;
	// 	OutputStream os = null;
	// 	try {
	// 		is = new FileInputStream(source);
	// 		os = new FileOutputStream(dest);
	// 		byte[] buffer = new byte[1024];
	// 		int length;
	// 		while ((length = is.read(buffer)) > 0) {
	// 			os.write(buffer, 0, length);
	// 		}
	// 	} finally {
	// 		is.close();
	// 		os.close();
	// 	}
	// }





	/**
	 * 파일 이동. 주로 임시 저장공간의 파일을 영구적인 위치로 옮길 때 사용합니다. @
	 * 
	 * @param 원본
	 * @param 대상
	 * @return
	 */
	public String moveFile(File original_dir, File move_dir) {
		Log log = new Log();

		if (original_dir.exists()) {
			// 폴더의 내용물 확인 -> 폴더 & 파일..
			File[] fileNames = original_dir.listFiles(); // 내용 목록 반환
			// log.println("--------------폴더 읽기-----------------");
			// for(int i=0; i< fileNames.length; i++) {
			// if(fileNames[i].isDirectory()) {
			// log.println(fileNames[i].getName()); //폴더 존재 유무
			// }
			// }

			for (int ii = 0; ii < fileNames.length; ii++) {
				if (fileNames[ii].isFile()) {
					if (fileNames[ii].exists()) {
						if (original_dir.exists()) {
						}
						File MoveFile = new File(move_dir, fileNames[ii].getName()); // 이동될 파일 경로 및 파일 이름
						fileNames[ii].renameTo(MoveFile); // 변경(이동)
						log.println("[첨부파일]" + fileNames[ii].getName()); // 폴더내에 있는 파일 리스트
						fileNames[ii].delete();
					}
				}
			}
		}
		return "";

	}

}
