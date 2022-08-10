### 초기 세팅 ###
# pip install --upgrade firebase-admin
# 터미널에 위 코드를 입력

### cloud firestore 를 사용하기 위한 초기 코드 ###
from tokenize import String
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

# Use a service account
cred = credentials.Certificate('D:\gunmoojoo-29a642ad200c.json')
firebase_admin.initialize_app(cred)


db = firestore.client()

doc_ref = db.collection(u'users').document(u'aaa')

for i in doc_ref.get():
  print(i)


print(a)



# # Create an Event for notifying main thread.
# import threading


# callback_done = threading.Event()

# # Create a callback on_snapshot function to capture changes
# def on_snapshot(doc_snapshot, changes, read_time):
#     for doc in doc_snapshot:
#         print(f'Received document snapshot: {doc.id}')
#     callback_done.set()

# doc_ref = db.collection(u'cities').document(u'SF')

# # Watch the document
# doc_watch = doc_ref.on_snapshot(on_snapshot)