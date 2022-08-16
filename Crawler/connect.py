

### import ###################

from concurrent.futures import ThreadPoolExecutor
import concurrent.futures
from multiprocessing import Pool
import multiprocessing as mp
from threading import Thread
import threading
import time
import psutil
## psutil pip install psutil 로 깔아줘야함

import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

import requests as rq
from bs4 import BeautifulSoup
from datetime import datetime




### field ######################
# url 리스트
urlList = []


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



### function #################################



### 인코딩 되어있는 url을 디코딩해주는 함수 #########################
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


### crawling ######################################################
def crawler(url):
    ## url을 디코딩 해준 후 realurl에 넣어줌
    realUrl = unescape_firebase_key(url)

    # url에서 파싱하기
    res = rq.get(realUrl, headers={'User-Agent':'Mozilla/5.0'})
    soup = BeautifulSoup(res.text, 'lxml')

    # title값 가지고오기
    title = soup.head.title.text.strip()
    print(title)

    # a 태그만 가져오기
    crawling_data = soup.select('a')
                
    ## 크롤링된 데이터 반환
    return crawling_data


### get keyword #############################################################
def get_keyWord_in_url(url: str):
    db_ref_for_keyword = db.collection('crawlingUrl').document(url).get().to_dict()
    keyWordList = db_ref_for_keyword.get('keyWordList')
    ## 해당 url과 함께 입력받은 keyword 반환
    return keyWordList 


### find keyword in crawling data ############################################\
def find_keyword_in_crawling_data(url, crawling_data, keyword):
    ## 크롤링된 데이터들 중에 keyword를 포함한 문장이 있으면 데이터베이스에 update( 중복이 있으면 저장하지 않음 )
    for line in crawling_data:
        if keyword in line.text.strip():
            doc_ref = doc_ref = db.collection('crawlingUrl').document(url).collection(keyword).document('data')
            ## DB에 데이터 저장하기
            ## 저장할 데이터 확실하게 결정 후 수정 필요!
            
            ## 이거는 입력받은 url에서 찾은 데이터
            doc_ref.update({u'data' : firestore.ArrayUnion([{line.text.strip():line.get('href')}])})
            
            ## 본문을 요약한 후 DB에 넣을 데이터
            # use_TLDR_This()
            
            
            
## craw main text - 본문 크롤링하기  #################################
def craw_main_text():
    print()
    
    
## use  TLDR This (Open API)  #######################################
def use_TLDR_This():
    print()
    
    
    
### Multi Processing #################################################################    

############### porcess #########################################################


## porcess는 url에서 크롤링을 해준다.
## 크롤링된 데이터를 쓰레드를 통해서 키워드를 찾아준다.
def do_process_with_thread_craw(url: str):
    ## 크롤링된 데이터
    crawling_data = crawler(url)
    ## 찾을 키워드 리스트
    keyWordList = get_keyWord_in_url(url)
    print(*keyWordList)
    ### thread 실행
    do_thread_keyword_find(url, crawling_data, keyWordList)


############### thread ##########################################################
## thread는 크롤링된 데이터에서 keyword를 찾아서 DB에 찾은 데이터를 올려준다.
def do_thread_keyword_find(url, crwaling_data, keyWordList):
    thread_list = []
    
    with ThreadPoolExecutor(max_workers=8) as excutor:
        for keyWord in keyWordList:
            thread_list.append(excutor.submit(find_keyword_in_crawling_data, url, crwaling_data, keyWord))
        for excution in concurrent.futures.as_completed(thread_list):
            excution.result()


### When urlList in firestore change ################################################
def on_snapshot(doc_snapshot, changes, read_time):
    for doc in doc_snapshot:
        ##### urlList를 새롭게 업데이트 해주는 역할 #########
        
        ## 새로 변한 urlList
        newUrlList = doc.to_dict().get('urlList')
        
        ## urlList가 비어있는 경우
        if newUrlList == None:
            return
        
        #### urlList에 변화된 데이터 적용
        ## 삭제
        if len(urlList) > len(newUrlList):
            ## 삭제된 url 찾아주기
            del_url = list(set(urlList) - set(newUrlList))[0]
            ## urlList 재정의
            urlList.remove(del_url)
            
        ## 추가
        elif len(urlList) < len(newUrlList):
            ## 추가된 url 찾아주기
            add_url = list(set(newUrlList) - set(urlList))[0]
            ## urlList 재정의
            urlList.append(add_url)
        
        print('new url')
        



### main #########################

if __name__ == "__main__":
    print('Connection initialized')
    
    ## cpu 코어에 맞춰서 프로세싱 진행
    num_cores = mp.cpu_count()
    
    with Pool(processes=num_cores) as pool:
        pool.map(do_process_with_thread_craw, urlList)
    ### url이 추가되는지 계속해서 확인    
    doc_ref = db.collection('crawlingUrl').document('urlList')
    doc_ref.on_snapshot(on_snapshot)

    ## 메인 쓰레드가 멈추는 것을 방지하는 부분
    while True:
        ## 멀티 프로세싱을 계속해서 진행함
        with Pool(processes=num_cores) as pool:
            pool.map(do_process_with_thread_craw, urlList)
        time.sleep(3)
        print("wait...")

