#!/bin/bash
javac src/node/*.java src/transaction/*.java 
mv src/node/*.class bin/node
mv src/transaction/*.class bin/transaction
sudo python script.py
