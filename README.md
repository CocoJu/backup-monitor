### BackupMonitor-Server
I using this programm on the work for simple monitoring creation backups of 1c programm (accountancy) on servers at local network, i control date creation, size file and existence it of specified directory. Main program receives data from specified servers and show it on the web page.
On the monitored servers run python script: bmonclient.py, which send response to main server.
List monitored servers specified at the config.xml file. It file parsed of sax parser, and each server be polled via sockets.
Rather, at this project I learned a little bit java language, than its specific use at work, but we using it :).
Understand how work sockets, http server, http headers, sax parser. Oh, what I have done :)
