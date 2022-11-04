python 파일에서 pip install 이 필요한 파일은 해주셔야 합니다.
따로 적어두지는 않겠습니다.

!!!<java android또한 firebase와 연동하여서 firebase 연동이 안되면 실행이 되지 않을겁니다>!!!

//환경
connect => python (crawler, server)
GunMoolJoo => Android studio
KeywordSurfing => ppt

------------------------------------------------------------------------------------------
준비물 : 서버구동 툴(파이썬), 스마트폰(apk)

파이썬 파일 41번째 라인에 json파일의 경로를 작성해 주어야한다.
(현재 경로는 D:// 에 저장된 서비스키를 가져온다.)
<어플 사용자>
1. MyApplication파일 다운로드
2. Android studio를 실행시킨 후 핸드폰으로 에뮬레이터를 실행하거나 시뮬레이터 실행
3. 어플을 실행시킨 후 회원가입
4. 로그인 진행
5. 원하는 url, keyword 등록
6. 등록 화면 하단부분에 올라온 데이터 확인 ( 파이썬에서 데이터를 crawling한 후 데이터베이스에 올라갔을 경우 )
7. 데이터 클릭 시 해당 소스 url로 이동( 미구현 단계)

<관리자(crawling)>
1. 파이썬 파일을 IDE를 이용해 Open
2. pip를 이용해 firestore_admin 다운로드 ( firebase 관련 라이브러리 )
3. pip를 이용해 BeaytiFulSoup 다운로드 ( crawling 관련 라이브러리 )
4. 실행 시 데이터 crawling 시작

crawling 서버( Python )를 켜두고, 어플( Android )을 실행시켜 URL과 키워드를 입력하면 crawling되어 확인이 가능하다.


*** 설정 ***
환경설정이 필요합니다. 
api때문에 요약결과가 잘 안나올 수 있습니다.
디바이스 알람 설정 : 헨드폰 설정 - 어플리케이션 - 어플리케이션명 검색 -
