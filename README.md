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
