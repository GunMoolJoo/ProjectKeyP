

### import ###################

from concurrent.futures import ThreadPoolExecutor
import concurrent.futures
from multiprocessing import Pool
import multiprocessing as mp
import time
from urllib import response

import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

import requests as rq
from bs4 import BeautifulSoup

from firebase_admin import messaging


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
            doc_ref = db.collection('crawlingUrl').document(url).collection(keyword)
            ## DB에 데이터 저장하기
            ## 저장할 데이터 확실하게 결정 후 수정 필요!
            
            ## db에 해당 데이터가 있는지 확인
            tag = True
            data = doc_ref.document('data').get().to_dict()
            array_data = data['data']
            for c_data in array_data:
                print(c_data)
                print(line.text.strip())
                if line.text.strip() in c_data:
                    tag = False
                    break
            
            ## db에 해당 크롤링 데이터가 없으면
            if tag:
                ## 이거는 입력받은 url에서 찾은 데이터
                doc_ref.document('data').update({'data' : firestore.ArrayUnion([{line.text.strip():line.get('href')}])})
                
                ## api 통신 오류 예외처리
                try:
                ## 본문내용 요약하기
                    summary_text = summary(line.get('href'))
                ## 본문을 요약한 후 DB에 넣을 데이터
                    doc_ref.document('summary').update({'summary' : firestore.ArrayUnion([{line.get('href'):summary_text}])})
                except Exception as e:
                    ## API 통신 오류
                    summary_text = '통신 오류'
                print('summary : {0}'.format(summary_text))
                
                ## 해당 키워드를 구독한 유저의 토큰정보 가져오기
                registration_tokens = doc_ref.document('user').get().to_dict().get('user')
                print('tokens : {0}'.format(registration_tokens))
                ## 키워드 알람 보내기
                sendMessage(url=line.get('href'), keyword=keyword, summary=summary_text, registration_tokens=registration_tokens)
            else:
                print('pass')            
                
            
            
            
            
            
##### craw main text - 본문 크롤링하기  #################################

## use  Papago API  #################################################
#### papago API url
papago_url = "https://openapi.naver.com/v1/papago/n2mt"
#### papago API key
naver_client_id = 'hVbkQXQChVEjEKvNEW1V'
naver_client_secret = 'ZWa2lTBimR'

#### 한글 -> 영어 번역하기 ###############################
def use_papago_for_kor_to_eng(trans_text):
    
    ## API에 전송할 데이터
    payload = {
        'source' : 'ko',
        'target' : 'en',
        'text' : trans_text,
    }
    
    headers = {
        "content-type": "application/json",
        "X-Naver-Client-Id": naver_client_id,
        "X-Naver-Client-Secret": naver_client_secret,
    }
    
    global papago_url
    
    ## API에 데이터 전송
    response = rq.request("POST", papago_url, json=payload, headers=headers)
    
    ## 영어로 번역된 문장 반환
    eng_text = response.json()['message']['result']['translatedText']
    
    return eng_text

#### 영어 -> 한글 번역하기 ###############################
def use_papago_for_eng_to_kor(trans_text):
## API에 전송할 데이터
    payload = {
        'source' : 'en',
        'target' : 'ko',
        'text' : trans_text,
    }
    
    headers = {
        "content-type": "application/json",
        "X-Naver-Client-Id": naver_client_id,
        "X-Naver-Client-Secret": naver_client_secret,
    }
    
    global papago_url
    
    ## API에 데이터 전송
    response = rq.request("POST", papago_url, json=payload, headers=headers)
    
    ## 한글로 번역된 문장 반환
    kor_text = response.json()['message']['result']['translatedText']
    
    return kor_text

## use  TLDR This (Open API)  #######################################
#### 본문 크롤링 ########################################
def use_TLDR_This_for_crawling_main_text(crawling_url):
    
    ## 사용할 기능의 url
    url = "https://tldrthis.p.rapidapi.com/v1/model/abstractive/summarize-url/"
    
    ## API로 보낼 정보
    payload = {
        "url" : crawling_url,
        "min_length": 100, # 최소 길이
        "max_length": 250, # 최대 길이
        "is_detailed":True # 한 문장으로 반환할 것인지 여부
    }
    
    headers = {
        'content-type': 'application/json',
        'X-RapidAPI-Key': '4a850f4a03mshc080ed0be83dc09p1e245cjsn804d4267423a',
        'X-RapidAPI-Host': 'tldrthis.p.rapidapi.com'
    }
    
    ## API로 url 정보 보내기
    response = rq.request("POST", url, json=payload, headers=headers)
    
    ## 본문 크롤링 반환받기
    json_data = response.json()
    content = json_data['article_text']
    return content

#### 본문내용 요약하기 ####################################
def use_TLDR_This_for_summary_main_text(content):
    
    ## 사용할 기능의 url
    url = 'https://tldrthis.p.rapidapi.com/v1/model/abstractive/summarize-text/'
    
    ## API로 보낼 정보
    payload = {
        'text' : content,
        'min_length' : 30,
        'max_length' : 100
    }
    
    headers = {
        'content-type': 'application/json',
        'X-RapidAPI-Key': '4a850f4a03mshc080ed0be83dc09p1e245cjsn804d4267423a',
        'X-RapidAPI-Host': 'tldrthis.p.rapidapi.com'
    }
    
    ## API로 본문 내용 보내기
    response = rq.request("POST", url, json=payload, headers=headers)
    
    ## 요약된 정보 반환받기
    json_data = response.json()
    summary = json_data['summary']
    return summary


################ summary ###################
def summary(crawling_url):
    ## crawling main text
    kor_main_text = use_TLDR_This_for_crawling_main_text(crawling_url)
    ## translate main text ( ko -> en )
    eng_main_text = use_papago_for_kor_to_eng(kor_main_text)
    ## summary main text
    eng_summary_text = use_TLDR_This_for_summary_main_text(eng_main_text)
    ## translate summary text ( en -> ko )
    kor_summary_text = use_papago_for_eng_to_kor(eng_summary_text)
    
    return kor_summary_text


### Multi Processing #################################################################    

############### porcess #########################################################

## 크롤링된 데이터를 쓰레드를 통해서 
## porcess는 url에서 크롤링을 해준다키워드를 찾아준다.
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
    
    with ThreadPoolExecutor(max_workers=100) as excutor:
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
        



##################  Firebase FCM Message Server ##############

### FCM API Server Key
APIKEY = "29a642ad200caddf283fa628dd7d4db1dd552f31"

### send messages (body, title) ############# 
def sendMessage(url, keyword, summary, registration_tokens):
    
    
    # 메시지 (data type)
    message = messaging.MulticastMessage(
        data={"url": url,"keyword": keyword,"summary": summary},
        tokens = registration_tokens,
    )
    
    response = messaging.send_multicast(message)
    
    # 전송 결과 출력
    print('{0} messages were sent successfully'.format(response.success_count))



### main #########################

if __name__ == "__main__":
    print('Connection initialized')
    
    ## cpu 코어에 맞춰서 프로세싱 진행
    num_cores = mp.cpu_count()
        
    ### url이 추가되는지 계속해서 확인    
    doc_ref = db.collection('crawlingUrl').document('urlList')
    doc_ref.on_snapshot(on_snapshot)

    ## 메인 쓰레드가 멈추는 것을 방지하는 부분
    while True:

        ## 멀티 프로세싱을 계속해서 진행함
        try:
            with Pool(processes=num_cores) as pool:
                pool.map(do_process_with_thread_craw, urlList)
        except Exception as e:
            print(e)
        print('wait...')
        time.sleep(10)
