package mainmain;
import java.io.File;
import static mainmain.Backup.myDir;
import static mainmain.Backup.blogName;

public class Save {
	/** 페이지 번호를 주면 전체 절대경로를 반환합니다. @param 페이지번호 */
    public String saveDir(int pageNum) { // 페이지 번호로 저장 경로지정
		// String blogName = "testblog2";
		// String myDir = "";

		String path = myDir + "Backup/" + blogName + "/" + pageNum;
		File blogroot = new File(myDir + "Backup/" + blogName);
		if (!blogroot.exists())
			blogroot.mkdir();

		File folder = new File(path);
		if (!folder.exists())
			folder.mkdir();
		return path;
	}
    
}
