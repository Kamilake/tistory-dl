package mainmain;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.file.WatchService;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;

import static mainmain.Backup.imageRealname;

/** 블로그 또는 인터넷의 데이터를 로컬로 다운로드하는 기능들이 모여 있습니다. */
public class Download {

	/**
	 * 다운로드 완료까지 대기. crdownload는 무시
	 * 
	 * @param targetDirectory (크롬이 파일 저장하는 곳)
	 * @param polltimeout     10 (초)
	 * @param polltimeunit    TimeUnit.SECONDS
	 * @throws InterruptedException
	 */
	public static void observeCompleteDL(String targetDirectory, long polltimeout, TimeUnit polltimeunit)
			throws InterruptedException {
		Log log = new Log();
		// targetDirectory =
		// "D:/Users/exjang/Documents/GitHub/Hyper_Tistory_Backupper/DownloadTemp/";
		try {

			WatchService fileWatchService = FileSystems.getDefault().newWatchService();
			WatchKey watchKey_path = Paths.get(targetDirectory).register(fileWatchService, StandardWatchEventKinds.ENTRY_CREATE);
			boolean valid = true;
			int ttl = 5;

			while (valid && ttl != 0) {
				ttl = ttl - 1;
				WatchKey watchKey = fileWatchService.poll(polltimeout, polltimeunit);
				if (watchKey == null) {

					System.out.println("[첨부파일] 실패 - 시간 초과 (남은 재시도 횟수 : " + ttl + ")");
					watchKey_path.cancel();
					continue;
				}

				// System.out.println("sdfsfasafd");
				// watchKey_take = fileWatchService.take();
				int ttl2 = 100;
				for (WatchEvent<?> event : watchKey.pollEvents()) {
					// System.out.println("aa :" + event.context().toString() + " , time : " +
					// LocalDateTime.now());
					try {
						if (StandardWatchEventKinds.ENTRY_CREATE.equals(event.kind())) {
							String fileName = event.context().toString();
							if (FilenameUtils.getExtension(fileName).equals("crdownload")
									|| FilenameUtils.getExtension(fileName).equals("tmp")) {// 아직 다운중
								log.println("[첨부파일] 다운로드 시작 :" + fileName);
							} else {
								log.println("[첨부파일] 다운로드 완료 :" + fileName);
								watchKey.reset();
								watchKey.cancel();
								watchKey.reset();
								watchKey.cancel();
								return;
							}

							// if(FilenameUtils.getExtension(fileName));

						} else {
							log.println("UNKNOWN EVENT ......");
						}
					} catch (Exception e3) {
						System.out.println("[첨부파일] 실패 - 알 수 없음 (남은 재시도 횟수 : " + ttl2-- + ")");
						if (ttl2 == 0)
							break;
						continue;
					}
				}
				valid = watchKey.reset();

			}
			// watchKey_path.reset();
			// watchKey_path.cancel();

			log.println("[첨부파일] 다운로드 실패 - TTL 초과");
		} catch (IOException e) {
			log.println("[첨부파일] 다운로드 실패 ->");
			e.printStackTrace();
		}
	}

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
	public void fileUrlReadAndDownload(String fileAddress, String localFileName, String downloadDir, int imgNum) {
		Log log = new Log();
		Optimize opt = new Optimize();
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
			// 파일이름찾는 부분 시작 @@@@신버전 티스토리에선 헤더에 파일 이름을 넣지 않는다. 즉 실패. 하지만 첨부파일 다운로드에선 빛을 보일 수
			// 있을지도? 아직 테스트해보지 않았다.@@@@
			uCon.connect();
			String extension_raw = uCon.getHeaderField("Content-Disposition");


			// 헤더예시 --> Content-Disposition: inline; filename="008.png";
			// filename*=UTF-8''008.png
			// raw = "attachment; filename=abc.jpg"
			if (extension_raw != null && extension_raw.indexOf("filename=\"") != -1) {
				extension_raw = URLDecoder.decode(new String(extension_raw.getBytes("ISO-8859-1"), "UTF-8"), "UTF-8");
				String netFileName = extension_raw.split("filename=\"")[1]; // getting value after '='
				netFileName = netFileName.split("\"")[0];
				netFileName = opt.escapeWindowsFilename(netFileName);
				log.println("[파일] 원본 파일 이름 : " + netFileName);
				// netFileName = netFileName.split("\\.")[1]; //getting value after '.'
				// log.println("[파일] 원본 파일 확장자 : " + netFileName);
				localFileName = localFileName + "_" + netFileName;
			} else {
				// fall back to random generated file name?
				localFileName = localFileName + "_" + fileAddress.substring(fileAddress.lastIndexOf("/") + 1);
				if (localFileName != null && localFileName.indexOf("?") != -1) // 물음표(GET쿼리 등)가 있으면 Windows에서 파일 저장 불가능(파일 이름, 디렉터리
																																																																			// 이름 또는 볼륨 레이블 구문이 잘못되었습니다) 오류 발생.
					localFileName = localFileName.split("\\?")[0]; // img9_zip.gif?_version_=tistory-aa685cac6411243b8334d0c6f53f8d458177bada

				// TODO: 저 URL 끝이 이미지 확장자가 맞는지(jpg png gif heic 등등) 확인해야 한다.
				// localFileName=localFileName+".jpg";
			}

			// 파일이름찾는 부분 끝
			outStream = new BufferedOutputStream(new FileOutputStream(downloadDir + "\\" + localFileName));
			imageRealname[imgNum] = localFileName; // 전역으로 이미지 이름을 저장. 추후 html에서 링크를 치환할 때 사용된다.

			is = uCon.getInputStream();
			buf = new byte[buffersize];
			while ((byteRead = is.read(buf)) != -1) {
				outStream.write(buf, 0, byteRead);
				byteWritten += byteRead;
			}
			log.println("[사진] 주소 :" + fileAddress);
			log.println("[사진] 이름 : " + localFileName + " (" + humanReadableByteCountBin(byteWritten) + ")"); // humanReadableByteCountSI

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

	public String attachmentDownload() {
		return "";

	}
}
