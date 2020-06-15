package mainmain;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

//https://gdtbgl93.tistory.com/154
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


public class Backup {
	//String blogTitle = new String();
	static String blogName = new String();
	//String pageTitle = new String();

	//String[] imgTitle = new String[1000];
	String[] imgURL = new String[1000];

	static int lyricLength;
	String[] result1 = new String[lyricLength];

	public static String saveDir(int pageNum) { // 페이지 번호로 저장 경로지정
		// 추후에 blogurl에서 아이디 뽑아서 폴더명으로 지정
		//String blogName = "testblog2";
		//String myDir = "P:/Tistory/"; // 추후 자신의 exe파일이 있는 곳으로 교체
		String myDir = "";

		String path = myDir + "./../Backup/" + blogName + "/" + pageNum;
		File blogroot = new File(myDir + "Backup/" + blogName);
		if (!blogroot.exists())
			blogroot.mkdir();
		
		File folder = new File(path);
		if (!folder.exists())
			folder.mkdir();
		return path;
	}

	private static void copyFileUsingStream(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}

//////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 버퍼 사이즈
	 */
	final static int size = 1024;

	/**
	 * fileAddress에서 파일을 읽어, 다운로드 디렉토리에 다운로드
	 *
	 * @param fileAddress
	 * @param localFileName
	 * @param downloadDir
	 */
	public static void fileUrlReadAndDownload(String fileAddress, String localFileName, String downloadDir) {
		OutputStream outStream = null;
		URLConnection uCon = null;

		InputStream is = null;
		try {
			URL Url;
			byte[] buf;
			int byteRead;
			int byteWritten = 0;
			Url = new URL(fileAddress);
			outStream = new BufferedOutputStream(new FileOutputStream(downloadDir + "\\" + localFileName));

			uCon = Url.openConnection();
			is = uCon.getInputStream();
			buf = new byte[size];
			while ((byteRead = is.read(buf)) != -1) {
				outStream.write(buf, 0, byteRead);
				byteWritten += byteRead;
			}

			System.out.println("이름 : " + localFileName);
			System.out.println("크기 : " + byteWritten + "바이트");
			System.out.println("다운로드 완료");

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

	/**
	 *
	 * @param fileAddress
	 * @param downloadDir
	 */

	public static void main(String[] args) {////////// MAIN
		System.out.println("참고: Chrome에서 사진이 전부 로딩되지 않거나 X박스 등으로 보여도 사진은 정상적으로 저장됩니다.");
		System.out.println("참고: 실행 파일과 같은 디렉터리에 chromedriver.exe 파일이 있어야 합니다.");
		System.out.println("참고: 실행 파일 경로 속 Backup 폴더에 데이터가 저장됩니다.");
		System.out.println("참고: 지금은 블로그 본문 HTML 텍스트와 사진만 백업이 가능합니다.");
		System.out.println("참고: 티스토리 기본 블로그 주소 중 앞 부분(○○○.tistory.com)만 입력해주세요. ex) bxmpe.tistory.com이라면 bxmpe");
		
		System.out.println("\nHyper Tistory Backup v1.0.0-alpha  -  Kamilake.com\n");
		
		blogName = "bxmpe";
		
		System.out.print("블로그 주소 앞 부분을 입력해주세요 : ");
		Scanner scan = new Scanner(System.in);
		blogName = scan.nextLine();
		Backup backup = new Backup();
		backup.crawl();
		scan.close();
	}

	// WebDriver
	private WebDriver driver;

	private WebElement imageClass;
	private WebElement blogView;

	// Properties
	public static final String WEB_DRIVER_ID = "webdriver.chrome.driver";
	public static final String WEB_DRIVER_PATH = "chromedriver.exe";
	
	
	// private String base_url = "https://papago.naver.com/?sk=ja";

	public Backup() {
		super();

		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--window-size=1000,4000");
		options.setCapability("ignoreProtectedModeSettings", true);
		driver = new ChromeDriver(options);

	}

	public void crawl() {
		
		
		int pageNum = 303; //시작페이지 startPage
		try {
			driver.get("https://" + blogName + ".tistory.com/m/");
			int emptyPageCount = 0; //빈 페이지 계산 후 일정량이 넘어가면 크롤링 종료
			// Thread.sleep(5000);
			for (/* int pageNum = 1 */;/* pageNum <= 블로그끝 */; pageNum++) { // 블로그 게시글 하나를 색인하는 for문
				System.out.println("검색중인 페이지 : " + pageNum);
				driver.navigate().to("https://" + blogName + ".tistory.com/m/" + pageNum);
				// liveBtn = driver.findElement(By.id("txtSource"));
				// liveBtn.click();
				// driver.navigate().to("https://naver.com/");

				//
				//
				//
				//
				//
				//
				System.out.println("페이지가 존재하는지 확인");
				JavascriptExecutor js_dellike = (JavascriptExecutor) driver;
				try {
					js_dellike.executeScript("var element = arguments[0]; element.parentNode.removeChild(element);",
						driver.findElement(By.className("container_postbtn")));
				}catch (Exception e) {
					// 좋아요 공감 삭제가 실패한다는 뜻은 해당 페이지가 없다는 뜻.
					System.out.println("빈 페이지 건너뛰기 (연속 "+emptyPageCount+++"번째)");
					if(emptyPageCount == 25) {
						System.out.println("백업이 모두 완료되었습니다.");
						return; //종료.
					}
					continue;
				}emptyPageCount=0;
					
				//
				//
				//
				//
				//
				//
				System.out.println(saveDir(pageNum));
				System.out.print("HTML 다운로드...");
				File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				File dest_scrFile = new File(saveDir(pageNum) + "/preview.jpg");
				copyFileUsingStream(scrFile, dest_scrFile);
				// container_postbtn #post_button_group (좋아요 공감 버튼)삭제
				System.out.println("완료\n미리보기 이미지 생성...");
				// blogview_content (본문 블록)찾아서 복제
				blogView = driver.findElement(By.className("blogview_content"));

				String innerHTML = blogView.getAttribute("innerHTML");
				innerHTML = innerHTML.replaceAll("(img src=\")(.*?)(\" )",
						"img src=\"이_문자열은_이미지_주소가_치환되기_전_임시_저장되는_문자열입니다\" ");
				innerHTML = innerHTML.replaceAll("srcset=", "alt=");
				for (int i = 0; i < 1000; i++)
					innerHTML = innerHTML.replaceFirst("이_문자열은_이미지_주소가_치환되기_전_임시_저장되는_문자열입니다", "img" + i + ".jpg");

				BufferedWriter writer = new BufferedWriter(new FileWriter(saveDir(pageNum) + "/index.html"));
				writer.write(innerHTML);
				writer.close();
				//
				//
				//
				//
				//
				//
				System.out.println("새 편집기 사진 검색 시작");
				for (int i = 0; i < 1000; i++) {
					try {
						imageClass = driver.findElement(By.className("imageblock"));
					} catch (Exception e) {
						System.out.println("새 편집기 사진 검색 완료 : " + i-- + "개");
						break;
					}
					imageClass = imageClass.findElement(By.tagName("img"));
					imgURL[i] = imageClass.getAttribute("src");// 사진 주소들 저장해두기
					// System.out.println("사진 " + i + " 주소: " + imgURL[i]);
					System.out.println("사진 " + i);
					fileUrlReadAndDownload(imgURL[i], "img" + i + ".jpg", saveDir(pageNum));
					JavascriptExecutor js_delimg = (JavascriptExecutor) driver;
					js_delimg.executeScript("var element = arguments[0]; element.parentNode.removeChild(element);",
							driver.findElement(By.className("imageblock")));
					try {
						Thread.sleep(10);
					} catch (Exception e) {
					} // sleepcatch
				} // for (int i = 0; i < 1000; i++) }
					//
					//
					//
					//
					//
					//
				for (; true;); // 한개만 색인시 true
			} // 블로그 게시글 하나를 색인하는 for문 닫기

		} catch (Exception e) {

			e.printStackTrace();
			System.out.println(e);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException ee) {
			}
		}

		driver.close();

		return;
	}

}