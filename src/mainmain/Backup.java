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
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Backup {
	// String blogTitle = new String();
	static String blogName = new String();
	// String pageTitle = new String();

	// String[] imgTitle = new String[1000];
	String[] imgURL = new String[1000];

	public static String saveDir(int pageNum) { // 페이지 번호로 저장 경로지정
		// 추후에 blogurl에서 아이디 뽑아서 폴더명으로 지정
		// String blogName = "testblog2";
		//String myDir = "P:/Tistory/"; // 추후 자신의 exe파일이 있는 곳으로 교체
		 String myDir = "";

		String path = myDir + "Backup/" + blogName + "/" + pageNum;
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
			System.out.println("주소 :" + fileAddress);
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

		System.out.println("\nHyper Tistory Backup v1.1.2-alpha  -  Kamilake.com\n");

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

		int pageNum = 0; // 시작페이지 startPage
		int imgNum = 0; // 다운로드할 이미지 번호를 지정(임시로만 사용) 중복이미지 필터링에 사용된다.
		int delay = 4000; // 색인 딜레이
		
		String HiResURL = ""; // 원본이미지 주소를 저장하게 될 공간
		try {
			driver.get("https://" + blogName + ".tistory.com/m/");
			int emptyPageCount = 0; // 빈 페이지 계산 후 일정량이 넘어가면 크롤링 종료
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

				// 제목 다운로드
				System.out.print("페이지가 존재하는지 확인...");
				JavascriptExecutor js_dellike = (JavascriptExecutor) driver;
				try {
					js_dellike.executeScript("var element = arguments[0]; element.parentNode.removeChild(element);",
							driver.findElement(By.className("container_postbtn")));
				} catch (Exception e) {
					
					
					try {
						blogView = driver.findElement(By.className("tit_error"));
						if(blogView.getAttribute("innerHTML").equals("존재하지 않는 <span class=\"br_line\"><br></span>페이지 입니다.")) {
							// 좋아요 공감 삭제가 실패한다는 뜻은 해당 페이지가 없다는 뜻.
							System.out.println("빈 페이지 건너뛰기 (연속 " + emptyPageCount++ + "번째)");
							
							if (emptyPageCount == 25) {
								System.out.println("백업이 모두 완료되었습니다.");
								return; // 종료.
							}
							} else {

								// if(과도트래픽 조건 확인) 과도트래픽이면 대기
								System.out.println("빈 페이지가 아닌 다른 에러 페이지입니다.\nEnter 키를 눌러서 이어서 진행하거나 Ctrl+C 키로 종료합니다.");
								System.in.read();
							}
							continue;
						
						
						
					} catch (Exception e2) {
						// TODO: handle exception
						//비밀번호 게시글일때
						//자세한확인조건 추가필요
						WebElement passwordInput;
						try {
							passwordInput= driver.findElement(By.id("password"));
							passwordInput.sendKeys("yuri11");
							passwordInput.sendKeys(Keys.ENTER);
							System.out.println("비밀번호 게시글 ");
						} catch (Exception e3) {
							// TODO: handle exception
							System.out.println("빈 페이지도, 비밀번호 게시글도, 에러 페이지도 아닌 다른 페이지입니다.\nEnter 키를 눌러서 이어서 진행하거나 Ctrl+C 키로 종료합니다.");
							System.in.read();
						}

					}
					
					
					
				}
				System.out.println("완료");
				emptyPageCount = 0;
				try {
					OutputStream title = new FileOutputStream(saveDir(pageNum) + "/Title_Info.txt");
					WebElement titleElement;
					titleElement = driver.findElement(By.className("blogview_tit"));
					titleElement.findElement(By.className("tit_blogview")); // 작동하지 않는다. h2 클래스를 찾으면 될 듯.
					System.out.println(titleElement.getText());
					byte[] by = titleElement.getText().getBytes();
					title.write(by);
					title.close();
				} catch (Exception e) {
					System.out.println("제목이 생각했던 위치에 없는 것 같군요.. 제목 저장은 일단 넘어갑니다.");
				}
				System.out.print("4초 대기중...");
				try {
					Thread.sleep(delay);
				} catch (InterruptedException ee) {// 과도트래픽 방지
				}
				System.out.println("완료");
				// System.out.println("번 : "); 303번 함대 유치원

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
				System.out.print("완료\n미리보기 이미지 생성...");
				// blogview_content (본문 블록)찾아서 복제
				blogView = driver.findElement(By.className("blogview_content"));

				String innerHTML = blogView.getAttribute("innerHTML"); // 사진 모두 찾고 치환시작
//				innerHTML = innerHTML.replaceAll("(img src=\")(.*?)(\" )",
//						"img src=\"이_문자열은_이미지_주소가_치환되기_전_임시_저장되는_문자열입니다\" ");
//				innerHTML = innerHTML.replaceAll("srcset=", "alt=");
//				for (int i = 0; i < 1000; i++)
//					innerHTML = innerHTML.replaceFirst("이_문자열은_이미지_주소가_치환되기_전_임시_저장되는_문자열입니다", "img" + i + ".jpg");
//
//				BufferedWriter writer = new BufferedWriter(new FileWriter(saveDir(pageNum) + "/index.html"));
//				writer.write(innerHTML);
//				writer.close();
				//
				//
				//
				//
				//
				//
				System.out.println("완료\n사진 다운로드 시작");
				for (int i = 0; i < 1000; i++) {
					imgNum = i; // 이미지 중복제거시 번호 수정용 변수 리셋
					try {
						// imageClass = driver.findElement(By.className("imageblock"));
						imageClass = blogView.findElement(By.tagName("img"));
					} catch (Exception e) {
						System.out.println("사진 다운로드 완료 : " + i-- + "개");
						break;
					}
					// imageClass = imageClass.findElement(By.tagName("img"));
					imgURL[i] = imageClass.getAttribute("src");// 사진 주소들 저장해두기
					// System.out.println("사진 " + i + " 주소: " + imgURL[i]);
					System.out.println("사진 " + i);
					for (int j = 0; j < i; j++) {// (제작예정)사진이 이전과 중복인 지 확인하기 - 모든 배열을 검사해 중복 사진일 경우 그 파일과 하나로 합친다.

						if (imgURL[i].equals(imgURL[j])) {// if 지금 다운받으려고하는 이미지 == 원래이미지
							imgNum = j; // then 이미지 번호를 j(이전 중복이미지)로 바꿔버린다.
							System.out.println("이미지 중복 발견 : img" + i + ".jpg는 img" + j + ".jpg와 같기 때문에 img" + j
									+ ".jpg파일로 통합하고 링크를 연결합니다.");
							break;
						}
					}

					try { // 링크 원본으로 치환 후 다운로드
						HiResURL = imgURL[i];

						if (imgURL[i].contains("daumcdn.net/cfile/tistory")) {
							HiResURL = HiResURL + "?original";
							System.out.println("유형: DAUMCDN 원본");
						} else if (imgURL[i].contains("daumcdn.net/thumb")) {

							HiResURL = HiResURL.split("%3A%2F%2F")[1];
							HiResURL = HiResURL.replace("%2Fimage%2F", "/original/");
							HiResURL = HiResURL.replace("cfile", "http://cfile");
							System.out.println("유형: Tistory 구서버 원본");
						} else
							System.out.println("유형: 원본 이미지");

						fileUrlReadAndDownload(HiResURL, "img" + imgNum + ".jpg", saveDir(pageNum));

					} catch (Exception e) {
						System.out.println("이미지 다운로드 오류 : " + imgNum);
						imgNum = 999;
					}

					JavascriptExecutor js_delimg = (JavascriptExecutor) driver;
					js_delimg.executeScript("var element = arguments[0]; element.parentNode.removeChild(element);",
							driver.findElement(By.tagName("img")));

					// 이 시점에서 imgurl[imgnum] 속 링크는 "img" + imgNum + ".jpg" 와 같다.

					///////////// html파일 속 이미지 링크를 로컬 링크로 바꾸는 부분
					innerHTML = innerHTML.replaceAll("srcset=", "alt="); // 크롬으로 열면 어째선지 sec보다 sreset 속 링크가 먼저 보여지는 듯..
					// for (int ii = 0; ii < 1000; ii++)
					try {
						innerHTML = innerHTML.replace("&amp;","&").replace(imgURL[imgNum], "img" + imgNum + ".jpg");
						//System.out.println("이미지 주소교체 완료 : "+imgNum+" / "+imgURL[imgNum]);
					} catch (Exception e) {
						System.out.println("이미지 주소를 교체할 수 없음.");
					}
					BufferedWriter writer = new BufferedWriter(new FileWriter(saveDir(pageNum) + "/index.html"));
					writer.write(innerHTML);
					writer.close();
					///////////// html파일 속 이미지 링크를 로컬 링크로 바꾸는 부분 끝

				} // for (int i = 0; i < 1000; i++) }
					//
					//
					//
					//
					//
					//
					//for (; true;); // 한개만 색인시 true
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