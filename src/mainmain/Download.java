package mainmain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import static mainmain.Backup.imageRealname;

/** 블로그 또는 인터넷의 데이터를 로컬로 다운로드하는 기능들이 모여 있습니다.*/
public class Download {

    String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public static String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %ciB", value / 1024.0, ci.current());
    }


	/**
	 * 다운로드 버퍼 사이즈
	 */
	final static int buffersize = 16384;
    /**
	 * fileAddress에서 파일을 읽어, 다운로드 디렉토리에 다운로드
	 *
	 * @param URL
	 * @param 파일명_색인
	 * @param 다운로드할_경로
	 * @param imgNum
	 */
	public void fileUrlReadAndDownload(String fileAddress, String localFileName, String downloadDir,int imgNum) {
		Log log = new Log();
		OutputStream outStream = null;
		URLConnection uCon = null;

		InputStream is = null;
		try {
			URL Url;
			byte[] buf;
			int byteRead;
			int byteWritten = 0;
			Url = new URL(fileAddress);

			uCon = Url.openConnection();
			//파일이름찾는 부분 시작 @@@@신버전 티스토리에선 헤더에 파일 이름을 넣지 않는다. 즉 실패. 하지만 첨부파일 다운로드에선 빛을 보일 수 있을지도? 아직 테스트해보지 않았다.@@@@
			uCon.connect();
			String extension_raw = uCon.getHeaderField("Content-Disposition");
			
			//헤더예시 --> Content-Disposition: inline; filename="008.png"; filename*=UTF-8''008.png
			// raw = "attachment; filename=abc.jpg"
			if(extension_raw != null && extension_raw.indexOf("filename=\"") != -1) {
			    String netFileName = extension_raw.split("filename=\"")[1]; //getting value after '='
			    netFileName = netFileName.split("\"")[0];
			    log.println("[파일] 원본 파일 이름 : " + netFileName);
//			    netFileName = netFileName.split("\\.")[1]; //getting value after '.'
//			    log.println("[파일] 원본 파일 확장자 : " + netFileName);
			    localFileName=localFileName+"_"+netFileName;
			} else {
			    // fall back to random generated file name?
				localFileName=localFileName+"_"+fileAddress.substring(fileAddress.lastIndexOf("/")+1);
				if(localFileName != null && localFileName.indexOf("?") != -1) //물음표(GET쿼리 등)가 있으면 Windows에서 파일 저장 불가능(파일 이름, 디렉터리 이름 또는 볼륨 레이블 구문이 잘못되었습니다) 오류 발생.
					localFileName = localFileName.split("\\?")[0]; //img9_zip.gif?_version_=tistory-aa685cac6411243b8334d0c6f53f8d458177bada
				
				//TODO: 저 URL 끝이 이미지 확장자가 맞는지(jpg png gif heic 등등) 확인해야 한다.
				//localFileName=localFileName+".jpg";
			}
			
			//파일이름찾는 부분 끝
			outStream = new BufferedOutputStream(new FileOutputStream(downloadDir + "\\" + localFileName));
			imageRealname[imgNum] = localFileName; //전역으로 이미지 이름을 저장. 추후 html에서 링크를 치환할 때 사용된다.
			
			


			is = uCon.getInputStream();
			buf = new byte[buffersize];
			while ((byteRead = is.read(buf)) != -1) {
				outStream.write(buf, 0, byteRead);
				byteWritten += byteRead;
			}
			log.println("[사진] 주소 :" + fileAddress);
			log.println("[사진] 이름 : " + localFileName +" ("+humanReadableByteCountBin(byteWritten)+")"); //humanReadableByteCountSI

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}










    public String attachmentDownload()
    {
        return "";
        
    }
}
