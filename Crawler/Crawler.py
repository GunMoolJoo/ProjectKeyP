from itertools import product
import requests as rq
from bs4 import BeautifulSoup
from datetime import datetime


import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore


# Firebase 다운 받은 키 위치 저장
cred = credentials.Certificate("C:\project\ProjectKeyP\gunmoojoo-29a642ad200c.json")
firebase_admin.initialize_app(cred)

db = firestore.client()

dic_ref = db.collection(u'users').document(u'aaa')

# 정보를 담을 빈 리스트
product_info=[]


now = datetime.now() # 현재시간을 알려준다.
print(now)

def craw():

    url = "https://news.naver.com/"

    #url에서 파싱하기
    res = rq.get(url)
    soup = BeautifulSoup(res.text,'lxml')

    # title값 가지고오기
    title =soup.head.title.text.strip()
    print(title)

    # 전체에서 가지고오기
    info=soup.select('p')
    
   
    keyword = "네이버"
    
    for i in info:
        if keyword in i.text.strip():
            product_info.append(i.text.strip())
            product_info.append("============================")
        
    return product_info
    

list =craw()
print(list)