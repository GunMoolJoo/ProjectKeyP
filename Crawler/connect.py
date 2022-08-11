import threading
import time


import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore


import requests as rq
from bs4 import BeautifulSoup
from datetime import datetime

# 디코딩할때 사용해줄 도구
_escape = {'&': '&&',
       '$': '_D',
       '#': '_H',
       '[': '_O',
       ']': '_C',
       '/': '_S',
       '.': '_P'}

_unescape = {e: u for u, e in _escape.items()}

# Use a service account
cred = credentials.Certificate('D:\gunmoojoo-29a642ad200c.json')
firebase_admin.initialize_app(cred)

db = firestore.client()


## url, keyword 받아오기

# 중복 없는 url 리스트
urlList = []

# 실행중인 크롤러 쓰레드 리스트
## {threadId : id, tag : True}
crawler = []





# 디코딩 unescape_firebase_key(text) text에 집어넣으면 바꿔준다
def escape_firebase_key(text):
    return text.translate(str.maketrans(_escape))
def unescape_firebase_key(text):
    chunks = []
    i = 0
    while True:
        a = text[i:].find('_') # text에서 문자 찾아라.
        if a == -1: #없다면
            return ''.join(chunks + [text[i:]]) #chunks를 리턴해줘 chunks부터 끝까지
        else: #있다면
            if text[i+a:i+a+2] == "_S": #만약 시작점부터 _S가 있다면
                chunks.append(text[i:i+a])
                chunks.append('/')      #chunks에 /를 추가해라
                i += a+2                #검사 시작구간 늘려
            else:
                s = text[i+a:i+a+2] #_발견 후 3칸 까지 검사해서 s에 넣어
                if s in _unescape:  
                    chunks.append(text[i:i+a])
                    chunks.append(_unescape[s])
                    i += a+2
                else:
                    raise RuntimeError('Cannot unescape')


## 크롤링 쓰레드가 진행될 때 tag 값을 True로 쓰레드를 종료할 때는 False로 바꾸어서 관리
def craw(url, keyWordList):
    while True:
        ## url을 디코딩 해준 후 realurl에 넣어줌
        realUrl = unescape_firebase_key(url)

        # url에서 파싱하기
        res = rq.get(realUrl)
        soup = BeautifulSoup(res.text, 'lxml')

        # title값 가지고오기
        title = soup.head.title.text.strip()
        print(title)

        # 전체에서 가지고오기
        info = soup.select('p')


        ## 키워드 검사하기
        for i in info:
            for keyword in keyWordList:
                if keyword in i.text.strip():
                    # product_info.append(i.text.strip())
                    # 크롤링된 데이터를 DB에 넣어줌
                    doc_ref = db.collection('crawlingUrl').document(url).collection(keyword).document('data')
                    doc_ref.update({'data' : firestore.ArrayUnion([{'time':datetime.now(),'crawlingData' : i.text.strip()}])})
                
                


    


def on_snapshot(doc_snapshot, changes, read_time):
    for doc in doc_snapshot:
        ### urlList에서 url이 추가되거나 삭제되었을 때 각각의 기능 구현
        
        ## 새로 변한 urlList
        newUrlList = doc.to_dict().get('urlList')
        
        if newUrlList == None:
            return
        
        
        ## 삭제
        if len(urlList) > len(newUrlList):
            del_url = list(set(urlList) - set(newUrlList))[0]
            urlList.pop(del_url)
            ## del_url 의 url을 사용하여 해당 url의 크롤링 쓰레드 종료
            ## 쓰레드 구현과 함께 추가 예정
            
        ## 추가
        elif len(urlList) < len(newUrlList):
            add_url = list(set(newUrlList) - set(urlList))[0]
            urlList.append(add_url)
            
            ## add_url 의 url을 사용하여 해당 url의 크롤링 쓰레드 실행 
            print(add_url)
            ## 수정 필요 document(add_url)
            db_ref = db.collection('crawlingUrl').document(add_url).get().to_dict()
            keyWordList = db_ref.get('keyWordList')
            print(*keyWordList)
            craw(add_url, keyWordList)
            
            
        ## 키워드 추가, 삭제가 일어날 경우
        ## 추가 예정
        
        
        
    callback_done.set()


## 이벤트 대기

callback_done = threading.Event()

print('Connection initialized')


### url이 추가되는지 계속해서 확인    
doc_ref = db.collection('crawlingUrl').document('urlList')

doc_watch = doc_ref.on_snapshot(on_snapshot)

## 메인 쓰레드가 멈추는 것을 방지하는 부분
while True:
    time.sleep(1)
    print("wait...")
    
    
    
    
    
    # def on_snapshot(doc_snapshot, changes, read_time):
#     for doc in doc_snapshot:
#         keyWordList = doc.to_dict().get(url)
#         print(keyWordList)
        
#         # 키워드 추가되면 수행될 기능
#         ## 키워드 리스트가 바뀌면 크롤러 쓰레드에 크롤링할 키워드 리스트를 업데이트 해줘야함.
        
#     callback_done.set()