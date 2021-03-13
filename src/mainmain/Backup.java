package mainmain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Scanner;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class Backup {
	Log log = new Log();
	
	static int delayFileDL = 10000; // 첨부파일을 다운로드하는 동안 기다리는 시간(다운로드 완료 시간 이상으로 설정하세요)(기본:4000)
	static int delay = 2000; // 페이지 로딩 완료후 기다리는 시간 (이 값을 2.5초 아래로 낮추면 티스토리 서버에게 IP밴 당할 수 있습니다)(기본:2700)
	static int emptyPageCheckLimit = 30; // 이 횟수만큼 빈 페이지가 연속해서 나오면 색인을 종료합니다.
	static String myDir = "A:/Tistory/"; // 색인이 저장될 절대 경로(비워둘 경우에는 상대경로로 저장됩니다)(기본:"")
	public static final String WEB_DRIVER_ID = "webdriver.chrome.driver"; // IE/크롬/파이어폭스 등등
	public static final String WEB_DRIVER_PATH = "chromedriver.exe"; // 드라이버의 위치를 지정하세요(기본: chromedriver.exe)

	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	/** 시작페이지 startPage FirstPage 초기 페이지 색인을 시작하는 페이지 (기본:0) */
	static int pageNum = 100;
	
	/** 시값을 설정하면 실행중 블로그 이름 또는 블로그 ID를 묻지 않습니다. (기본:"") */
	static String blogName = "myskrpatch";
	
	/** 암호걸린 게시글의 암호 해독 */
	static String password = "1111";
	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////

	//TODO: https://hongmeilin.tistory.com/m/38 여기 크롤링에서 스킵하게 설정
	// String blogTitle = new String();
	//static String blogName = new String();
	// String pageTitle = new String();
	// String[] imgTitle = new String[1000];
	String[] imgURL = new String[1000];
	/** 원래 img숫자.jpg로 파일을 관리했는데 실 파일명으로 저장하면서 이미지 링크 치환할 때 기존 저장한 파일명을 보존할 필요가 생겼다. */
	public static String[] imageRealname = new String[1000];

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

	public static void main(String[] args) {////////// MAIN
		Log log = new Log();
		log.println("참고: Chrome에서 사진이 전부 로딩되지 않거나 X박스 등으로 보여도 사진은 정상적으로 저장됩니다.");
		log.println("참고: 실행 파일과 같은 디렉터리에 chromedriver.exe 파일이 있어야 합니다.");
		log.println("참고: 실행 파일 경로 속 Backup 폴더에 데이터가 저장됩니다.");
		log.println("참고: 블로그 본문 HTML 텍스트와 원본 사진, 첨부파일 백업이 가능합니다.");
		log.println("참고: 티스토리 기본 블로그 주소 중 앞 부분(○○○.tistory.com)만 입력해주세요. ex) bxmpe.tistory.com이라면 bxmpe");

		log.println("\nHyper Tistory Backup v0.2-alpha  -  Kamilake.com\n");

		log.print("블로그 주소 앞 부분을 입력해주세요 : ");
		Scanner scan = new Scanner(System.in);
		if(blogName.equals("")) {
		blogName = scan.nextLine();
		} else log.println(blogName);
		Backup backup = new Backup();
		backup.crawl();
		scan.close();
	}

	// WebDriver
	private WebDriver driver;

	private WebElement imageClass;
	private WebElement attachment; //첨부파일
	private WebElement blogView;

	// Properties
	

	// private String base_url = "https://papago.naver.com/?sk=ja";

	@SuppressWarnings("deprecation")
	public Backup() {
		super();

		System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--window-size=1000,4000");
		options.setCapability("ignoreProtectedModeSettings", true);
		
		String downloadFilepath = (myDir+"DownloadTemp").replace("/","\\");
		HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
		chromePrefs.put("profile.default_content_settings.popups", 0);
		chromePrefs.put("download.default_directory", downloadFilepath);
		options.setExperimentalOption("prefs", chromePrefs);
		DesiredCapabilities cap = DesiredCapabilities.chrome();
		cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, false);
		cap.setCapability(ChromeOptions.CAPABILITY, options);
		driver = new ChromeDriver(cap);

		
		
		//driver = new ChromeDriver(options);

	}

	public void crawl() {
		
		Save save = new Save();
		Download dl = new Download();
		int imgNum = 0; // 다운로드할 이미지 번호를 지정(임시로만 사용) 중복이미지 필터링에 사용된다.
		String HiResURL = ""; // 원본이미지 주소를 저장하게 될 공간
		String targetBlock = "imageblock"; // imageblock or fileblock 첨부파일이 저장되어 있는 html 구문 
		
		try {
			driver.get("https://" + blogName + ".tistory.com/m/");
			log.println("Tistory에 로그인하거나 Enter키를 눌러 넘어갑니다.");
			System.in.read();
			/** 이 변수는 연속되는 빈 페이지를 확인할 때 사용됩니다.이 값이 임계값(emptyPageCheckLimit)에 도달하면 색인이 종료됩니다. */
			int emptyPageCount = 0; 
			// Thread.sleep(5000);
			for (/* int pageNum = 1 */;/* pageNum <= 블로그끝 */; pageNum++) { // 블로그 게시글 하나를 색인하는 for문
				log.println("검색중인 페이지 : " + pageNum);
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
				log.println("=========="+pageNum+"==========");
				log.print("[메타데이터] 페이지 찾는 중...");
				JavascriptExecutor js_dellike = (JavascriptExecutor) driver;
				try {
					js_dellike.executeScript("var element = arguments[0]; element.parentNode.removeChild(element);",
							driver.findElement(By.className("container_postbtn")));
				} catch (Exception e) {
					
					
					try {
						blogView = driver.findElement(By.className("tit_error"));
						if(blogView.getAttribute("innerHTML").equals("존재하지 않는 <span class=\"br_line\"><br></span>페이지 입니다.")) {
							// 좋아요 공감 삭제가 실패한다는 뜻은 해당 페이지가 없다는 뜻.
							log.println("빈 페이지 건너뛰기 (연속 " + emptyPageCount++ + "번째)");

							log.print(""+(delay+1000)+"ms 대기중...");
							try {
								Thread.sleep(delay+1000);
							} catch (InterruptedException ee) {// 다운로드
							}
							
							if (emptyPageCount == emptyPageCheckLimit) {
								log.println("\n\n백업이 모두 완료되었습니다.");
								return; // 종료.
							}
							} else {

								//TODO: if(과도트래픽 조건 확인) 과도트래픽이면 대기
								log.println("빈 페이지가 아닌 다른 에러 페이지입니다.\nEnter 키를 눌러서 이어서 진행하거나 Ctrl+C 키로 종료합니다.");
								System.in.read();
							}
							continue;
						
						
						
					} catch (Exception e2) {
						// TODO: 자세한확인조건 추가필요 -> 완료((비밀번호 게시글)표시)
						//비밀번호 게시글일때
						WebElement passwordInput;
						try {
							passwordInput= driver.findElement(By.id("password"));
							passwordInput.sendKeys(password);
							passwordInput.sendKeys(Keys.ENTER);
							log.print("(비밀번호 게시글) ");
							
							// 비밀번호가 맞는지 확인하는 로직이 필요하다.
							//아래는 테스트 안됨 - 비번 체크 로직
							try {
							blogView = driver.findElement(By.className("blogview_content")); // 아무거나 하나 열어서 열리나 보기
							} catch (Exception e_pwerror)
							{
								log.println("[메타데이터] 암호가 맞지 않습니다. #error");
								//log.println(e_pwerror);
								continue;
							}
							
							//위는 테스트 안됨 - 비번 체크 로직
							
						} catch (Exception e3) {
							log.println("[빈 페이지도, 비밀번호 게시글도, 에러 페이지도 아닌 다른 페이지입니다.(트래픽 차단 등)\nEnter 키를 눌러서 이어서 진행하거나 Ctrl+C 키로 종료합니다.");
							System.in.read();
						}

					}
					
					
					
				}
				log.println("완료");
				emptyPageCount = 0;
				try {
					OutputStream title = new FileOutputStream(save.saveDir(pageNum) + "/Title_Info.txt");
					WebElement titleElement;
					titleElement = driver.findElement(By.className("blogview_tit"));
					titleElement.findElement(By.className("tit_blogview")); // 작동하지 않는다. h2 클래스를 찾으면 될 듯.
					log.println("[제목] " + titleElement.getText().replace("\n", "\n[제목] "));
					byte[] by = titleElement.getText().getBytes();
					title.write(by);
					title.close();
				} catch (Exception e) {
					log.println("[제목] 예상했던 곳에 제목이 적혀있지 않습니다.\n 티스토리가 변경되었거나 이 프로그램에 문제가 있습니다.");
					//TODO: 파일 건너뛰었다는 로그 찍기
					e.printStackTrace();
					log.print(""+(delay+1000)+"ms 대기중...");
					try {
						Thread.sleep(delay+1000);
					} catch (InterruptedException ee) {// 다운로드
					}
					continue; // 다음으로 건너뛰기
				}
				log.print("[제목] "+delay+"ms 대기중...");
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
				//log.println(saveDir(pageNum));
				//log.print("[메타데이터] HTML 다운로드...");
				// File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
				// File dest_scrFile = new File(save.saveDir(pageNum) + "/preview.jpg");
				// copyFileUsingStream(scrFile, dest_scrFile);
				// // container_postbtn #post_button_group (좋아요 공감 버튼)삭제 - 추후 GUI화하면 옵션으로 제공
				//log.println("완료");
				
				log.print("[메타데이터] 섬네일 생성...");
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
				log.println("완료\n[사진] 사진 다운로드 시작");
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
							log.println("[사진] 이미지 중복 발견 : img" + i + " 파일은 img" + j + " 파일과 같기 때문에 img" + j
									+ " 파일로 통합하고 링크를 연결합니다.");
							break;
						}
					}

					try { // 링크 원본으로 치환 후 다운로드
						HiResURL = imgURL[i];

						if (imgURL[i].contains("daumcdn.net/cfile/tistory")) {
							HiResURL = HiResURL + "?original";
							log.println("[사진] 유형: DAUMCDN 원본");
						} else if (imgURL[i].contains("daumcdn.net/thumb")) {

							HiResURL = HiResURL.split("%3A%2F%2F")[1];
							HiResURL = HiResURL.replace("%2Fimage%2F", "/original/");
							HiResURL = HiResURL.replace("cfile", "http://cfile");
							log.println("[사진] 유형: Tistory 구서버 원본");
						} else
							log.println("[사진] 유형: 화면에 보이는 이미지");
							//TODO : 버그-> 외부링크 다운로드하면 가끔 x박스로 뜬다. url 리맵핑 개선 필요

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
						//innerHTML = innerHTML.replace("&amp;","&").replace(imgURL[imgNum], "img" + imgNum + ".jpg");
						
						innerHTML = innerHTML.replace("src=\"//","src=\"https://"); //myskrpatch를 보니까 주소가 <img src="//ac.namu.la/aa.png"> 로 되어있던...;;;;
						innerHTML = innerHTML.replace("&amp;","&").replace(imgURL[imgNum], imageRealname[imgNum]);
						log.println("교체대상 : "+imgURL[imgNum]);
						log.println("교체전주소 : "+"img" + imgNum + ".jpg");
						log.println("교체후주소 : "+imageRealname[imgNum]);
						
						//imageRealname
						//log.println("이미지 주소교체 완료 : "+imgNum+" / "+imgURL[imgNum]);
					} catch (Exception e) {
						log.println("[사진] 이미지 주소를 교체할 수 없음: "+imgNum);
					}

					log.print("[메타데이터] HTML 다운로드...");
					BufferedWriter writer = new BufferedWriter(new FileWriter(save.saveDir(pageNum) + "/index.html"));
					writer.write(innerHTML);
					writer.close();
					log.println("완료");
					
				} // for (int i = 0; i < 1000; i++) } 이미지검색기 종료






					///////////// 첨부파일 다운로드 영역 시작
				for (int i = 0; i < 1000; i++) { ///이미지 다운로드가 아니라 첨부파일 다운로더
					JavascriptExecutor js_del_nonfile = (JavascriptExecutor) driver;

					try {
						log.print("[첨부파일] 블록 찾는 중...");
						
						try {// 신파일 구파일 체크 | 신버전 티스토리에서 첨부한 첨부파일은 HTML 양식이 다르다.
							attachment = driver.findElement(By.className("imageblock")); //구파일
							targetBlock = "imageblock"; //구파일이면 타겟을 구파일로 설정
							log.println("완료 (Tistory Old)");
						} catch (Exception e) {
							try {
								attachment = driver.findElement(By.className("fileblock")); //신파일
								targetBlock = "fileblock"; //신파일이면 타겟을 신파일로 설정
								log.println("완료 (Tistory New)");
							} catch (Exception e2) {
								attachment = driver.findElement(By.tagName("figure123123123123123123")); //구글드라이브
								targetBlock = "googledrive"; //주의! : 클래스가 아님
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
					log.println("[첨부파일] URL: "+attachment.getAttribute("href"));
					log.println("[첨부파일] 파일명: "+attachment.getText());
					driver.navigate().to(attachment.getAttribute("href")); //파일 새 탭으로 열기
					log.print("[첨부파일] "+delayFileDL+"ms 대기중...");
					try {
						Thread.sleep(delayFileDL); //다운완료까지 대기
					} catch (InterruptedException ee) {// 다운로드
					}
					


					log.println("완료");
					//폴더 참조
					
					
					
					 //파일 이동
					log.println("[첨부파일] 파일 이동 시작");
			        File tempDir = new File((myDir+"DownloadTemp").replace("/","\\"));
			        File permanentDir = new File((save.saveDir(pageNum)).replace("/","\\"));
					save.moveFile(tempDir, permanentDir);
					log.println("[첨부파일] 파일 이동 완료");
					
					
					
					
					js_del_nonfile.executeScript("var element = arguments[0]; element.parentNode.removeChild(element);",
							driver.findElement(By.className(targetBlock)));
					log.println("[첨부파일] 저장한 링크 삭제 완료");
					} catch(Exception e) {  //imageblock은 있는데 그 안에 a href가 없을 경우 쓸모없는 블록이므로 날려버리기
					log.println("없음");
					//e.printStackTrace();
					try {
						//log.println("href 없당"+e);
						js_del_nonfile.executeScript("var element = arguments[0]; element.parentNode.removeChild(element);",
								driver.findElement(By.className(targetBlock)));
						log.println("[첨부파일] 쓸모없는 블록 삭제완료");
					} catch (Exception e2) {
						log.println("[첨부파일] 쓸모없는 블록 삭제실패: ");
						e2.printStackTrace();
					}
					}	//imageblock은 있는데 그 안에 a href가 없을 경우 쓸모없는 블록이므로 날려버리기 end				
					
					
				}// for (int i = 0; i < 1000; i++) /첨부파일 다운로드 닫기

				///////////// 첨부파일 다운로드 영역 종료
				
				
			} // 블로그 게시글 하나를 색인하는 for문 닫기

		} catch (Exception e) {

			e.printStackTrace();
			log.println(e);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException ee) {
			}
		}

		driver.close();

		return;
	}

}