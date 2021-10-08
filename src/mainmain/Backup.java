package mainmain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.commons.lang3.ArrayUtils;

import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;

public class Backup {

	static String Version = "2021.05.20"; // 버전
	static int delayFileDL = 3000; // 첨부파일간 딜레이 (이 값을 2.5초 아래로 낮추면 티스토리 서버에게 IP밴 당할 수 있습니다)(기본:4000)
	static int delay = 3000; // 페이지 로딩 완료후 기다리는 시간 (이 값을 2.5초 아래로 낮추면 티스토리 서버에게 IP밴 당할 수 있습니다)(기본:2700)
	static int emptyPageCheckLimit = 40; // 이 횟수만큼 빈 페이지가 연속해서 나오면 색인을 종료합니다.
	static String myDir = "A:/Tistory/"; // A:/Tistory/ 색인이 저장될 절대 경로(비워둘 경우에는 상대경로로 저장됩니다)(기본:"")
	public static final String WEB_DRIVER_ID = "webdriver.chrome.driver"; // IE/크롬/파이어폭스 등등
	public static final String WEB_DRIVER_PATH = "chromedriver.exe"; // 드라이버의 위치를 지정하세요(기본: chromedriver.exe)
	static boolean isHiDpi125 = false;
	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	/** 시작페이지 startPage FirstPage 초기 페이지 색인을 시작하는 페이지 (기본:0) */
	static int pageNum = 0;
	static int pageNum_total = 99999; // 전체 페이지 수
	static int pageNum_sitemap = 0; // 사이트맵에서 내부적으로 사용하는 순서 ID
	/** 값을 설정하면 실행중 블로그 이름 또는 블로그 ID를 묻지 않습니다. (기본:"") */
	static String blogName = "";
	/** 암호걸린 게시글의 암호 해독 */
	static String password = "1111";

	static boolean Enable_Image_download = true;
	static boolean Enable_Thumbnail_Screenshot = true;
	static boolean Allow_Duplicate_Downloads = false;
	public static boolean Use_Sitemap = true;
	// static boolean Enable_Image_download = false;
	// static boolean Enable_Image_download = false;

	float jpegParams_setCompressionQuality = 0.3f; // 섬네일 미리보기 화질 결정. 0.1f -> 10% // 1f ->100%
	///////////////////////////////////////////////////////////////////////////////////////////////
	public static String loc[]; // sitemap.xml 태그의 loc 부분, PC버전 도메인(인터넷에 노출되는 메인 URL)
	// TODO: https://hongmeilin.tistory.com/m/38 여기 크롤링에서 스킵하게 설정
	String[] imgURL = new String[1000];
	/**
	 * 원래 img숫자.jpg로 파일을 관리했는데 실 파일명으로 저장하면서 이미지 링크 치환할 때 기존 저장한 파일명을 보존할 필요가 생겼다.
	 */
	public static String[] imageRealname = new String[1000];
	Log log = new Log();
	//////////////////////////////////////////////////////////////////////////////////////

	public static void main(String[] args) {////////// MAIN
		Log log = new Log();
		log.println("[tistory-dl] 참고: Chrome에서 사진이 전부 로딩되지 않거나 X박스 등으로 보여도 사진은 정상적으로 저장됩니다.");
		log.println("[tistory-dl] 참고: 실행 파일과 같은 디렉터리에 chromedriver.exe 파일이 있어야 합니다.");
		log.println("[tistory-dl] 참고: 실행 파일 경로 속 Backup 폴더에 데이터가 저장됩니다.");
		log.println("[tistory-dl] 참고: 블로그 본문 HTML 텍스트와 원본 사진, 첨부파일 백업이 가능합니다.");
		log.println("[tistory-dl] 참고: 티스토리 기본 블로그 주소 중 앞 부분(○○○.tistory.com)만 입력해주세요. ex) bxmpe.tistory.com이라면 bxmpe");
		// 크롬 다른이름으로 저장이 계속 뜨는 경우는 해당 폴더가 없어서 그러는 경우도 있습니다. - 추가
		log.println("\n[tistory-dl] tistory-dl" + Version + " - blog.Kamilake.com\n");

		log.print("블로그 주소 앞 부분을 입력해주세요 : ");
		Scanner scan = new Scanner(System.in);
		if (blogName.equals("")) {
			blogName = scan.nextLine();
		} else
			log.println(blogName);
		Backup backup = new Backup();
		backup.crawl();
		scan.close();
	}

	// WebDriver
	public static WebDriver driver;

	public WebElement imageClass;
	public WebElement attachment; // 첨부파일
	public WebElement blogView;

	// Properties

	// private String base_url = "https://papago.naver.com/?sk=ja";
	String downloadFilepath = "";

	@SuppressWarnings("deprecation")
	public Backup() {
		super();

		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--window-size=600,3000");
		options.setCapability("ignoreProtectedModeSettings", true);
		String downloadFilepath = "";
		if (myDir.equals("")) { // 크롬이 절대경로만 받는다.. 사실 절대경로만 받는 건 아니고 htb 실행경로랑 크롬 실행위치가 좀 달라서...
			downloadFilepath = (System.getProperty("user.dir") + "/" + "DownloadTemp/").replace("/", "\\");
		} else {
			downloadFilepath = (myDir + "DownloadTemp/").replace("/", "\\");
		}

		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.default_directory", downloadFilepath);
		chromePrefs.put("download.prompt_for_download", "false");
		options.setExperimentalOption("prefs", chromePrefs);
		DesiredCapabilities cap = DesiredCapabilities.chrome();
		cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, false);
		cap.setCapability(ChromeOptions.CAPABILITY, options);
		driver = new ChromeDriver(cap);

		// driver = new ChromeDriver(options);

	}

	public void crawl() {
		Save save = new Save();
		Download dl = new Download();
		Optimize opt = new Optimize();
		int imgNum = 0; // 다운로드할 이미지 번호를 지정(임시로만 사용) 중복이미지 필터링에 사용된다.
		String HiResURL = ""; // 원본이미지 주소를 저장하게 될 공간
		String targetBlock = "imageblock"; // imageblock or fileblock 첨부파일이 저장되어 있는 html 구문
		try {
			driver.get("https://" + blogName + ".tistory.com/m/");

			/**
			 * 이 변수는 연속되는 빈 페이지를 확인할 때 사용. 이 값이 임계값(emptyPageCheckLimit)에 도달하면 색인 종료
			 */
			int emptyPageCount = 0;

			if (isHiDpi125) {
				log.println("* Hidpi 배율이 커스텀 125%인 사람은 지금 수동으로 브라우저의 배율을 80%로 설정해주세요.");
				log.println("* 그렇지 않으면 AShot 전체 스크린샷에서 오른쪽과 아래가 잘려 나옵니다");
				log.println("Tistory에 로그인하거나 Enter키를 눌러 넘어갑니다.");
				System.in.read();
				// WebElement html = driver.findElement(By.tagName("html"));
				// html.sendKeys(Keys.chord(Keys.CONTROL, Keys.SUBTRACT ));
				// driver.findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.CONTROL,
				// Keys.SUBTRACT ));
				// driver.findElement(By.tagName("html")).sendKeys(Keys.chord(Keys.CONTROL,
				// Keys.SUBTRACT ));
				// JavascriptExecutor executor = (JavascriptExecutor)driver;
				// executor.executeScript("document.body.style.zoom = '80%'");
			}

			DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
			Document sitemap = null;
			NodeList nList = null;
			/**
			 * 페이지 원본 URL 저장(XML 파싱으로 얻은 주소)
			 */
			if (Use_Sitemap) {
				sitemap = dBuilder.parse("https://" + blogName + ".tistory.com/sitemap.xml");
				sitemap.getDocumentElement().normalize();
				System.out.println("Root element: " + sitemap.getDocumentElement().getNodeName());
				nList = sitemap.getElementsByTagName("url");
				pageNum_total = nList.getLength();
				System.out.println("게시글 수 : " + pageNum_total);
				loc = new String[pageNum_total - 1 + 1]; // 게시글 수만큼 선언해두기
				for (int i = 0; i != pageNum_total; i++) {
					System.out.println("######################");
					log.println("i=" + i);
					Node nNode = nList.item(i); // xml파일 전체에서"url"항목의 pageNum_sitemap++번째의 노드를 가져오기
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						// System.out.println(eElement.getTextContent());
						loc[i] = opt.getTagValue("loc", eElement);
						System.out.println("loc  : " + loc[i]);

						try {
							System.out.println("lastmod  : " + opt.getTagValue("lastmod", eElement));
							System.out.println("getMobileURL  : " + opt.getMobileURL(loc[i]));
							System.out.println("getPostID  : " + opt.getPostID(loc[i]));
						} catch (NullPointerException e) {
							System.out.println("글이 아님  : " + loc[i]);
							loc[i] = loc[i]+".무시무시"; // 카테고리페이지나 메인페이지 같은 경우는 무시를 위한 플래그 지정
						} catch (ArrayIndexOutOfBoundsException e2) {
							System.out.println("글이지만 주소를 파싱할 수 없음  : " + loc[i]);
							loc[i] = loc[i]+".무시무시"; // TODO: 추후 다른 방법으로 저장할 길을 모색해야겠다
						}
						// pageNum = (loc.split("/")[loc.split("/").length - 1]); //
						// loc에서 얻은 URL을 기준으로 페이지 번호를 탐색하는 건데 이게 FULL TEXT로 이루어진 주소에서는 당연히 오작동하겠지
						// 왜만들었을까;; Deprecated
						// if(loc) {
						// .
						// }
					} // if end
				}
				ArrayUtils.reverse(loc);
				// System.out.println(Arrays.toString(loc)); 모든주소 출력
			} else
				loc = new String[1]; // 사이트맵 안 쓰는 경우 대충 아무거나 채워넣기
			// Thread.sleep(5000);
			pageNum = -1; // 아래에서 0으로 바뀜
			for (/* int pageNum = 1 */;/* pageNum <= 블로그끝 */;) { // 블로그 게시글 하나를 색인하는 for문
				if (Use_Sitemap) {
					// nList;
					// System.out.println("게시글 수 : " + pageNum_total);

				} else {
				}
				pageNum++;

				if (pageNum == pageNum_total) {
					log.println("\n\n\n[tistory-dl] 백업이 모두 완료되었습니다.");
					driver.close();
					return; // 종료.
				}
				log.println("[tistory-dl] 검색중인 페이지 : " + (pageNum + 1) + "/" + pageNum_total + " ("
						+ String.format("%.2f",
								(float) ((float) (pageNum == 0 ? 1 : pageNum) / (float) (pageNum_total == 0 ? 1 : pageNum_total)) * 100.0)
						+ "%) [ID:" + opt.getPostID(loc[pageNum]) + "]");

				// 이미 다운로드한 페이지인지 확인하는 부분 시작
				if (Allow_Duplicate_Downloads == false) {

					if (save.isDirExists(pageNum)) {
						log.println("[tistory-dl] 페이지 " + pageNum + " 건너뛰기.");
						emptyPageCount = 0;
						continue;
					}
				}
				// 이미 다운로드한 페이지인지 확인하는 부분 끝.

				// 무시해야 하는 페이지인지 확인하는 부분 시작
				if (loc[pageNum].contains(".무시무시")) {
					log.println("[tistory-dl] 페이지 " + loc + " 무시.");
					emptyPageCount = 0;
					continue;
				}
				// 무시해야 하는 페이지인지 확인하는 부분 끝.
				if (Use_Sitemap) {
					driver.navigate().to(opt.getMobileURL(loc[pageNum]));
					log.println((opt.getMobileURL(loc[pageNum])));
				} else
					driver.navigate().to("https://" + blogName + ".tistory.com/m/" + pageNum); // TODO: aa

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
				log.println("[tistory-dl] ==========" + pageNum + "==========");
				log.print("[메타데이터] 페이지 찾는 중...");
				JavascriptExecutor js_dellike = (JavascriptExecutor) driver;
				try {
					js_dellike.executeScript("var element = arguments[0]; element.parentNode.removeChild(element);",
							driver.findElement(By.className("container_postbtn")));
				} catch (Exception e) {

					try {
						blogView = driver.findElement(By.className("tit_error"));
						if (blogView.getAttribute("innerHTML").equals("존재하지 않는 <span class=\"br_line\"><br></span>페이지 입니다.")) {
							// 좋아요 공감 삭제가 실패한다는 뜻은 해당 페이지가 없다는 뜻.
							log.println("빈 페이지 건너뛰기 (연속 " + emptyPageCount++ + "번째)");

							log.print("" + (delay + 1000) + "ms 대기중...");
							try {
								Thread.sleep(delay + 1000);
							} catch (InterruptedException ee) {// 다운로드
							}

							if (emptyPageCount == emptyPageCheckLimit) {
								log.println("\n\n\n[tistory-dl] 백업이 모두 완료되었습니다.");
								driver.close();
								return; // 종료.
							}
						} else {

							// TODO: if(과도트래픽 조건 확인) 과도트래픽이면 대기
							log.println("[tistory-dl] 빈 페이지가 아닌 다른 에러 페이지입니다.\nEnter 키를 눌러서 이어서 진행하거나 Ctrl+C 키로 종료합니다.");
							System.in.read();
						}
						continue;

					} catch (Exception e2) {
						// TODO: 자세한확인조건 추가필요 -> 완료((비밀번호 게시글)표시)
						// 비밀번호 게시글일때
						WebElement passwordInput;
						try {
							passwordInput = driver.findElement(By.id("password"));
							passwordInput.sendKeys(password);
							passwordInput.sendKeys(Keys.ENTER);
							log.print("(비밀번호 게시글) ");

							// 비밀번호가 맞는지 확인하는 로직이 필요하다.
							// 아래는 테스트 안됨 - 비번 체크 로직
							try {
								blogView = driver.findElement(By.className("blogview_content")); // 아무거나 하나 열어서 열리나 보기
							} catch (Exception e_pwerror) {
								log.println("[메타데이터] 암호가 맞지 않습니다. #error");
								// log.println(e_pwerror);
								continue;
							}

							// 위는 테스트 안됨 - 비번 체크 로직

						} catch (Exception e3) {
							e3.printStackTrace();
							log.println("[빈 페이지도, 비밀번호 게시글도, 에러 페이지도 아닌 다른 페이지입니다.(트래픽 차단 등)\nEnter 키를 눌러서 이어서 진행하거나 Ctrl+C 키로 종료합니다.");
							//System.in.read();
							opt.delay(10000);
						}

					}

				}
				log.println("완료");
				emptyPageCount = 0;
				try {
					OutputStream title = new FileOutputStream(save.saveDir(pageNum) + "/Metadata.txt");
					WebElement titleElement;
					String metaData = "";
					metaData += "Timestamp: [" + LocalDateTime.now() + "]\n";
					metaData += "URL: " + opt.getMobileURL(loc[pageNum]) + "\n";
					metaData += "Enable_Image_download: " + ((Enable_Image_download ? "Enabled" : "Disabled") + "\n");
					metaData += "Enable_Thumbnail_Screenshot: " + ((Enable_Thumbnail_Screenshot ? "Enabled" : "Disabled") + "\n");
					metaData += "HTB " + Version + "\n" + "blog.Kamilake.com";

					titleElement = driver.findElement(By.className("blogview_tit"));
					// titleElement.findElement(By.className("tit_blogview")); // 작동하지 않는다. h2 클래스를
					// 찾으면 될 듯.
					log.println("[제목] " + titleElement.getText().replace("\n", "\n[제목] "));
					byte[] by = (titleElement.getText() + "\n\n---\n\n" + metaData).getBytes();
					title.write(by);
					title.close();
				} catch (Exception e) {
					log.println("[제목] 예상했던 곳에 제목이 적혀있지 않습니다.\n 티스토리가 변경되었거나 이 프로그램에 문제가 있습니다.");
					// TODO: 파일 건너뛰었다는 로그 찍기
					e.printStackTrace();
					log.print("" + (delay + 1000) + "ms 대기중...");
					try {
						Thread.sleep(delay + 1000);
					} catch (InterruptedException ee) {// 다운로드
					}
					continue; // 다음으로 건너뛰기
				}
				log.print("[제목] " + delay + "ms 대기중...");
				try {
					Thread.sleep(delay);
				} catch (InterruptedException ee) {// 과도트래픽 방지
				}
				log.println("완료");
				//
				//
				//
				//
				//
				//
				// log.println(saveDir(pageNum));
				// log.print("[메타데이터] HTML 다운로드...");
				// File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				// File dest_scrFile = new File(save.saveDir(pageNum) + "/preview.jpg");
				// copyFileUsingStream(scrFile, dest_scrFile);
				// // container_postbtn #post_button_group (좋아요 공감 버튼)삭제 - 추후 GUI화하면 옵션으로 제공
				// log.println("완료");

				try {
					opt.delClass("kakao_head"); // 스크롤캡쳐시 상단바가 보이기 때문
					opt.delClass("blogview_head"); // 스크롤캡쳐시 상단바가 보이기 때문
					/* 잡다구리 삭제 */
					opt.delClass("adsenseMobileAd1"); // 구글광고
					opt.delId("bannerWrap"); // 카카오광고
					opt.delId("area_ad"); // 카카오광고
					
					opt.delClass("section_differ");
					opt.delClass("viewpaging_wrap");
					opt.delClass("section_relation");
					opt.delClass("cmt_write"); // 댓글작성칸 삭제
					// opt.delId("comment"))); // 댓글삭제
					/* 잡다구리 삭제 끝 */
				} catch (Exception e) {
					// TODO: handle exception
					// 잡다한 서식 삭제 중 문제 생기면 오는 곳.
				}
				try { // 댓글펼치기 누르기
					for (int comment_i = 0; comment_i < 100; comment_i++) {
						log.print("[댓글] 댓글 펼치는 중..." + comment_i);
						// driver.findElement(By.className("link_cmtmore")).click();
						((JavascriptExecutor) driver).executeScript("document.getElementsByClassName(\"link_cmtmore\")[0].click();");
						opt.delay(delayFileDL);
						log.println("");
					}
				} catch (Exception e) {
					log.println("완료");
				}
				log.print("[메타데이터] HTML 저장...");
				// blogview_content (본문 블록)찾아서 복제
				blogView = driver.findElement(By.className("blogview_content"));
				String innerHTML = blogView.getAttribute("innerHTML"); // 사진 모두 찾고 치환시작
				log.println("완료");
				if (Enable_Thumbnail_Screenshot) {
					log.print("[메타데이터] 섬네일 생성...");
					// isDisplayed();
					// 모바일상단바 = driver.findElement(By.className("cont_blog b_scroll"));
					// 모바일상단바.isDisplayed();

					try {
						Screenshot 스크롤캡쳐 = new AShot().shootingStrategy(ShootingStrategies.viewportPasting(10)).takeScreenshot(driver);
						final ImageWriter imgwriter = ImageIO.getImageWritersByFormatName("jpg").next();
						// specifies where the jpg image has to be written
						imgwriter.setOutput(new FileImageOutputStream(new File(save.saveDir(pageNum) + "/" + "Thumbnail.jpg")));

						JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
						jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
						jpegParams.setCompressionQuality(jpegParams_setCompressionQuality); // 섬네일 미리보기 화질 결정. 0.1f -> 10% // 1f ->100%

						// writes the file with given compression level
						// from your JPEGImageWriteParam instance
						imgwriter.write(null, new IIOImage(스크롤캡쳐.getImage(), null, null), jpegParams); // TODO: 60000픽셀 이상 저장 못한다. 예외 발생시
																																																																																					// 직접 기록하는 코드 필요

						// 원본으로 저장하는법 -> //ImageIO.write(스크롤캡쳐.getImage(), "webp", new
						// File(".\\fullimage.webp"));
						log.println("완료");
					} catch (Exception e) {
						// TODO: handle exception
						log.println("실패 : ");
						e.printStackTrace();
					}
				}
				//
				//
				//
				//
				//
				//
				if (Enable_Image_download) {
					log.println("[사진] 사진 다운로드 시작");
					for (int i = 0; i < 1000; i++) {
						imgNum = i; // 이미지 중복제거시 번호 수정용 변수 리셋
						try {
							// imageClass = driver.findElement(By.className("imageblock"));
							imageClass = blogView.findElement(By.tagName("img"));
						} catch (Exception e) {
							log.println("[사진] 사진 다운로드 완료 : " + i-- + "개");
							break;
						}
						// imageClass = imageClass.findElement(By.tagName("img"));
						imgURL[i] = imageClass.getAttribute("src");// 사진 주소들 저장해두기
						// log.println("사진 " + i + " 주소: " + imgURL[i]);
						log.println("[사진] 이미지 번호: " + i);
						for (int j = 0; j < i; j++) {// (제작예정)사진이 이전과 중복인 지 확인하기 - 모든 배열을 검사해 중복 사진일 경우 그 파일과 하나로 합친다.

							if (imgURL[i].equals(imgURL[j])) {// if 지금 다운받으려고하는 이미지 == 원래이미지
								imgNum = j; // then 이미지 번호를 j(이전 중복이미지)로 바꿔버린다.
								log.println("[사진] img" + i + " == img" + j + ". img" + j + "파일에 병합");
								break;
							}
						}

						try { // 링크 원본으로 치환 후 다운로드
							HiResURL = imgURL[i];

							if (imgURL[i].contains("daumcdn.net/cfile/tistory")) {
								HiResURL = HiResURL + "?original";
								log.println("[사진] 유형: DAUMCDN 원본");
							} else if (imgURL[i].contains("daumcdn.net/thumb")) {
								// log.println("----------구서버 ->" + imgURL[i] + " contains? -->" +
								// imgURL[i].contains("daumcdn.net/thumb"));
								HiResURL = HiResURL.split("%3A%2F%2F")[1];
								HiResURL = HiResURL.replace("%2Fimage%2F", "/original/");
								HiResURL = HiResURL.replace("cfile", "http://cfile");
								HiResURL = HiResURL.replace("t1.daumcdn", "http://t1.daumcdn");
								HiResURL = URLDecoder.decode(HiResURL, "UTF-8");

								log.println("[사진] 유형: Tistory 구서버 원본");
							} else
								log.println("[사진] 유형: 화면에 보이는 이미지");
							// TODO : 버그-> 외부링크 다운로드하면 가끔 x박스로 뜬다. url 리맵핑 개선 필요

							dl.fileUrlReadAndDownload(HiResURL, "img" + imgNum, save.saveDir(pageNum), imgNum);

						} catch (Exception e) {
							log.println("[사진] 이미지 다운로드 오류 : " + imgNum);
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
							// innerHTML = innerHTML.replace("&amp;","&").replace(imgURL[imgNum], "img" +
							// imgNum + ".jpg");

							innerHTML = innerHTML.replace("src=\"//", "src=\"https://"); // myskrpatch를 보니까 주소가 <img
																																																																				// src="//ac.namu.la/aa.png"> 로 되어있던...;;;;
							innerHTML = innerHTML.replace("&amp;", "&").replace(imgURL[imgNum], imageRealname[imgNum]);
							log.println("교체대상 : " + imgURL[imgNum]);
							log.println("교체전주소 : " + "img" + imgNum + ".jpg");
							log.println("교체후주소 : " + imageRealname[imgNum]); // TODO: 이미지이름 한글일때 제대로 안나온다 시놀 도커 마크서버 참고

							// imageRealname
							// log.println("이미지 주소교체 완료 : "+imgNum+" / "+imgURL[imgNum]);
						} catch (Exception e) {
							log.println("[사진] 이미지 주소를 교체할 수 없음: " + imgNum);
						}
					} // for (int i = 0; i < 1000; i++) } 이미지검색기 종료
				} // endif Enable_Image_download
				log.print("[메타데이터] HTML 다운로드...");
				BufferedWriter writer = new BufferedWriter(new FileWriter(save.saveDir(pageNum) + "/index.html"));
				writer.write(innerHTML);
				writer.close();
				log.println("완료");
				//////////// 첨부파일 다운로드 영역 시작
				for (int i = 0; i < 1000; i++) { /// 이미지 다운로드가 아니라 첨부파일 다운로더
					JavascriptExecutor js_del_nonfile = (JavascriptExecutor) driver;

					try {
						log.print("[첨부파일] 블록 찾는 중...");

						try {// 신파일 구파일 체크 | 신버전 티스토리에서 첨부한 첨부파일은 HTML 양식이 다르다.
							attachment = driver.findElement(By.className("imageblock")); // 구파일
							targetBlock = "imageblock"; // 구파일이면 타겟을 구파일로 설정
							log.println("완료 (Tistory Old)");
						} catch (Exception e) {
							try {
								attachment = driver.findElement(By.className("fileblock")); // 신파일
								targetBlock = "fileblock"; // 신파일이면 타겟을 신파일로 설정
								log.println("완료 (Tistory New)");
							} catch (Exception e2) {
								attachment = driver.findElement(By.tagName("figure123123123123123123")); // 구글드라이브
								targetBlock = "googledrive"; // 주의! : 클래스가 아님
								log.println("완료 (Drive)");
							}

						}

					} catch (Exception e) {
						log.println("블록 없음");
						log.println("[첨부파일] 다운로드 완료 : " + i-- + "개");
						break;
					}

					try {
						log.print("[첨부파일] 하이퍼링크 찾는 중...");
						attachment = attachment.findElement(By.tagName("a"));
						log.println("완료");
						log.println("[첨부파일] URL: " + attachment.getAttribute("href"));
						log.println("[첨부파일] 파일명: " + attachment.getText());
						driver.navigate().to(attachment.getAttribute("href")); // 파일 새 탭으로 열기
						// log.print("[첨부파일] " + delayFileDL + "ms 대기중...");
						// try {
						// Thread.sleep(delayFileDL); // 다운완료까지 대기
						// } catch (InterruptedException ee) {// 다운로드
						// }
						log.println(downloadFilepath);
						Download.observeCompleteDL(downloadFilepath, 10, TimeUnit.SECONDS);
						log.println("완료");
						opt.delay(1000); // 크롬이 다운로드 실패 찍지 않게. 없어도 별문제는 안생긴다.
						// 폴더 참조
						// 파일 이동
						log.println("[첨부파일] 파일 이동 시작");
						File tempDir = new File((myDir + "DownloadTemp").replace("/", "\\"));
						File permanentDir = new File((save.saveDir(pageNum)).replace("/", "\\"));
						save.moveFile(tempDir, permanentDir);
						log.println("[첨부파일] 파일 이동 완료");

						js_del_nonfile.executeScript("var element = arguments[0]; element.parentNode.removeChild(element);",
								driver.findElement(By.className(targetBlock)));
						log.println("[첨부파일] 저장한 링크 삭제 완료");
					} catch (Exception e) { // imageblock은 있는데 그 안에 a href가 없을 경우 쓸모없는 블록이므로 날려버리기
						log.println("없음");
						// e.printStackTrace();
						try {
							// log.println("href 없당"+e);
							js_del_nonfile.executeScript("var element = arguments[0]; element.parentNode.removeChild(element);",
									driver.findElement(By.className(targetBlock)));
							log.println("[첨부파일] 쓸모없는 블록 삭제완료");
						} catch (Exception e2) {
							log.println("[첨부파일] 쓸모없는 블록 삭제실패: ");
							e2.printStackTrace();
						}
					} // imageblock은 있는데 그 안에 a href가 없을 경우 쓸모없는 블록이므로 날려버리기 end

				} // for (int i = 0; i < 1000; i++) /첨부파일 다운로드 닫기

				///////////// 첨부파일 다운로드 영역 종료

			} // 블로그 게시글 하나를 색인하는 for문 닫기

		} catch (Exception e) {

			e.printStackTrace();
			log.println(e);
		}

		driver.close();

		return;
	}

}
