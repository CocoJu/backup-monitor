import socket, os

def listFiles(d):    
    directory = os.walk(d)
    walkArr = ()
    dirArr = []   
    for dr in directory:
        walkArr = dr
        break        
    for d in walkArr[2]:
        #dirArr.append((walkArr[0]+'/'+d).replace('\\','/'))
        dirArr.append(d)
    return dirArr

def fileDate(dirs):
    fileDt=[]         
    for d in dirs:
        fileDt.append("Dir:"+d)
        for file in listFiles(d):           
            fileDt.append(file)
            fileDt.append(str(os.stat((d +'/'+file).replace('\\','/')).st_mtime))
            fileDt.append( os.stat((d +'/'+file).replace('\\','/')).st_size )           
    return fileDt

HOST = 'localhost'
PORT = 9090
SPLITTER = "<s>" 
 
sock = socket.socket()
sock.bind(("",PORT))
sock.listen(1)

while True:
    #print("enter in cycle")
    conn, addr = sock.accept()
    message = []
    stringBuffer = ""
    try:        
        data = conn.recv(32)
    except ConnectionResetError:
        #print("exception")
        continue
    #print(data.decode('utf8'))
    if data.decode('utf8').rfind("BEGIN")!= -1  or not data:        
        while True:
            recvString = conn.recv(128).decode('utf8')
            #print(recvString)
            #END! используется как флаг для сигнала прекращения приема данных
            if (recvString.rfind("END!")!= -1 or not data):
                if((not data) != True):
                    message.append(recvString.replace("END!",""))
                break
            message.append(recvString)      
        for m in message:
            if m!="":
                stringBuffer += m
        clearMessage = []
        clearMessage.clear()
        clearMessage = stringBuffer.split(SPLITTER)
        #print(clearMessage)
        conn.send(("Hostname:"+socket.gethostname()+SPLITTER).encode('utf8'))
        for field in fileDate(clearMessage):
            #print(field)
            conn.send((str(field)+SPLITTER).encode('utf8'))
    conn.close()
