
import threading
import time


import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore


import requests as rq
from bs4 import BeautifulSoup
from datetime import datetime


# Use a service account
cred = credentials.Certificate('D:\gunmoojoo-29a642ad200c.json')
firebase_admin.initialize_app(cred)

db = firestore.client()


## url, keyword 받아오기

# 중복 없는 url 리스트
urlList = []



def craw(url, keyWordList):

    realurl = 'https://news.naver.com/'

    # url에서 파싱하기
    res = rq.get(realurl)
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
                doc_ref = db.collection('url').document(url).collection(keyword).document('data')
                doc_ref.update({'data' : firestore.ArrayUnion([{'title':i.text.strip(),'time':datetime.now()}])})
                
                
    

    


def on_snapshot(doc_snapshot, changes, read_time):
    for doc in doc_snapshot:
        ## 데이터가 변화했을 때 url에 변화가 있는지 체크한 후
        ### url이 추가되거나 삭제되었을 때 각각의 기능 구현
        
        add_urlList = list(doc.to_dict().keys())
        
        
        
        ## 삭제
        if len(urlList) > len(add_urlList):
            del_url = list(set(urlList) - set(add_urlList))[0]
            urlList.pop(del_url)
            ## del_url 의 url을 사용하여 해당 url의 크롤링 쓰레드 종료
            
        ## 추가
        elif len(urlList) < len(add_urlList):
            add_url = list(set(add_urlList) - set(urlList))[0]
            urlList.append(add_url)
            ## add_url 의 url을 사용하여 해당 url의 크롤링 쓰레드 실행 
            keyWordList = doc.to_dict().get(add_url)
            craw(add_url, keyWordList)
        
    callback_done.set()


## 이벤트 대기

callback_done = threading.Event()

print('Connection initialized')

    
doc_ref = db.collection('user').document('User id')

doc_watch = doc_ref.on_snapshot(on_snapshot)

## 메인 쓰레드가 멈추는 것을 방지하는 부분
while True:
    time.sleep(1)
    print('wait...')
    
    
    
    
    
    # def on_snapshot(doc_snapshot, changes, read_time):
#     for doc in doc_snapshot:
#         keyWordList = doc.to_dict().get(url)
#         print(keyWordList)
        
#         # 키워드 추가되면 수행될 기능
#         ## 키워드 리스트가 바뀌면 크롤러 쓰레드에 크롤링할 키워드 리스트를 업데이트 해줘야함.
        
#     callback_done.set()