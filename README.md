# tistory-dl

Kakao의 블로그 플랫폼 Tistory에서 데이터를 받아오거나 자신의 블로그를 백업할 수 있는 툴입니다.

Selenium(Chromedriver에서만 테스트)을 베이스로 블로그 전체의 글을 복사해 저장하도록 만들어졌습니다.

Wordpress 또는 Tistory 자체에 글을 그대로 복원할 수 있습니다.

## 기능
1. 전체/부분 페이지 스크린샷
(스크롤캡처, 댓글 전부 열고 캡처)
1. 전체 페이지 이미지 원본 다운로드
1. 전체/부분 HTML 다운로드
### 커밋이랑 코드가 왜이렇게 지저분해요?
공개할 생각이 전혀 없이 그냥 제가 제 블로그 백업하려고 만들어뒀는데 점점 커져서 뭔가 혼자 쓰기는 아까워졌기에 공개로 전환했어요 ㅠㅠ
# 앞으로 할 일
CLI에서 플래그를 받아 자동화할 수 있게 만들고 싶은데 요즘 바빠서 시간이 없네요ㅠ

# 사용방법
먼저, chromedriver.exe와 tistory-dl.jar 파일이 필요합니다.

아 그리고 자바도 필요합니다. 저는 자바 8(빌드 1.8.0_241-b07)에서 만들었는데 아마 다른 버전도 문제 없을 것 같아요.

필수 명령은 하나도 없으며, `--name`이 없다면 실행 중 물어봅니다.

<pre> Usage: tistory-dl [options]
  Options:
    --WEB_DRIVER_ID
      사용할 웹드라이버 (IE/크롬/파이어폭스 등등)
      Default: webdriver.chrome.driver
    --WEB_DRIVER_PATH
      웹드라이버 위치
      Default: chromedriver.exe
    --allow-duplicate-downloads
      이미 존재하는 파일을 덮어씁니다.
      Default: false
    --delay
      페이지 로딩 완료후 기다리는 시간 (이 값을 2.5초 아래로 낮추면 티스토리 서버에게 IP밴 당할 수 있습니다)
      Default: 3000
    --delayFileDL
      첨부파일간 딜레이 (이 값을 2초 아래로 낮추면 티스토리 서버에게 IP밴 당할 수 있습니다)
      Default: 3000
    --emptyPageCheckLimit
      이 횟수만큼 빈 페이지가 연속해서 나오면 완료된 것으로 간주하고 색인을 종료합니다.
      Default: 150
    --help
      지금 보고있는 도움말을 출력합니다.
    --hidpi
      HiDpi(125%+) 모드에서 스크롤캡처의 호환성을 체크합니다.
      Default: false
    --jpegParams-setCompressionQuality
      섬네일 미리보기 화질 설정. 0.1 -> 10% // 1.0 ->100%
      Default: 0.3
    --name, -n
      블로그 이름(○○○.tistory.com)| 지정하지 않으면 실행중 묻습니다.
      Default: <empty string>
    --output, -o
      파일이 저장될 경로
      Default: <empty string>
    --pageNum
      해당 번호부터 색인을 시작합니다.
      Default: 0
    --pageNum_total
      전체 페이지 수
      Default: 99999
    --password
      암호로 보호된 게시글의 암호
      Default: 1111
    --skip-image-download
      게시글 속 모든 사진을 다운로드하지 않습니다.
      Default: false
    --skip-thumbnail-screenshot
      스크롤캡처 이미지를 저장하지 않습니다.
      Default: false
    --test, -t
      테스트용으로 잠깐 만든 플래그
      Default: 0
    --use-sitemap
      티스토리 내장 사이트맵 사용
      Default: true
    --debug, --verbose, -v
      상세한 로그를 출력합니다.
      Default: false
      </pre>
