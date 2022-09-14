python 파일에서 pip install 이 필요한 파일은 해주셔야 합니다.
따로 적어두지는 않겠습니다.

java android또한 firebase와 연동하여서 firebase 연동이 안되면 실행이 되지 않을겁니다....ㅎㅎ..


connect => python (crawler, server)
GunMoolJoo => Android studio

KeywordSurfing => ppt

<어플 사용자>
1. MyApplication파일을 다운로드 받습니다.
2. Android studio를 실행시킨후 핸드폰으로 에뮬레이터를 실행하거나 시뮬레이터를 실행합니다.
3. 어플을 실행시킨 후 회원 가입을 합니다.
4. 로그인을 합니다.
5. 원하는 url과 키워드를 등록합니다.
6. 등록화면 하단 부분에 올라온 데이터를 확인합니다.(이 부분은 파이썬에서 데이터를 크롤링한 후 데이터베이스에 올라갔을 경우)
7. 데이터를 클릭 한 경우 해당 url로 넘어갑니다.(아직 미구현 단계)

<관리자(크롤링)>
1. 파이썬을 킵니다.
2. firebase에서 키를 할당받아 파이썬에 넣어 줍니다.(이 부분은 키 삽입 해야할 부분을 파이썬에 주석처리 해놓음)
3. 파이썬에서 firestore_admin을 다운로드 받습니다.(이 경우는 저희가 firebase를 이용하여서 입니다.)
4. 파이썬 BeaytiFulSoup을 다운받아줍니다.
5. 실행시키면 데이터 크롤링이 시작 됩니다.
