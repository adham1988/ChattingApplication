# Server

This repository contains the server program for an E2EE chat application.

The program was made as part of an Object Oriented Analysis, Design and Implementaton (OOADI) course at Aalborg University.

The [client](https://gitlab.com/20gr552/ooadi/client) program is needed to communicate through the server.
 
The server can run on the same machine as both clients, or within the same network. It will exchange public keys with the first two clients, and must be restarted if a new chat needs to take place. 

**To compile:**
user@user:~/server/$ make

**To run:**
user@user:~/server/$ make run

**To quit server program and close socket:**
**Cltr+C**

**To clean:**
user@user:~/server/$ make clean

