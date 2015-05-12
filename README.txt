
    * Download the the tarball.
    * Put the tarball in a directory you've created for this class, say, ~/cs223
    * cd ~/cs223
    * gunzip project1.tar.gz
    * tar xvf project1.tar
    * cd project/lockmgr
    * make
    * copy your submissions directory to ./submissions
    * cd ../transaction
    * Think of a random number between 2000 and 5000, say, 2100, and change the value of RMIREGPORT in first line of Makefile from 1099 to this number. This is the port number where your rmiresgistry will be listening. Giving it a random number will ensure that your rmiresgistry doesn't conflict with somebody else's.
    * cd ../test.part1
    * cp -r ../../submissions/* ../transaction
    * rm -r ../transaction/Client.java
    * cp -f Client.java ../transaction/
    * cd ../transaction
    * make clean
    * make server
    * make client
    * rmiregistry -J-classpath -J.. 2100 &
    * cd ../test.part1
    * setenv CLASSPATH .:gnujaxp.jar
    * /usr/bin/javac RunTests.java
    * java -DrmiPort=1099 RunTests MASTER.xml

Make sure the "java" command and the "rm" command in RunTests.java and Client.java are properly set based on your shell environment. The results will be put in ./results/grades.txt. If you want, you can modify the file project/test.part1/MASTER.xml to change the scripts you want to test. You are STRONGLY suggested to run the scripts ONE BY ONE.

Structure: (1) RunTest.java parses the file MASTER.xml. For each line, it activates "Client.java" by passing the script name under the "scripts" directory. (2) Client.java starts the necessary RMI modules. Then it reads and parses the script file, and interpret each line to take the corresponding action.
