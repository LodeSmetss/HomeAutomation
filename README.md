This is an initial version of a java home automation system

This was originally written in python and ran on a raspberry pi

Java version is created for developing Java skills only and can run on a desktop computer which communicates to a WAGO remote IO Module over ethernet using Modbus protocol.

Hence, this version lacks proper exception handling and the ability to communicate with other device types such as KNX devices or devices which can communicate via open source API's such as Daikin AC, Somfy blinds etc...

# Requirements

- Gson is required. You can find the repo here https://github.com/google/gson
- JAVA 17 is required to run this project. preview features should be enabled

# Compile

To compile on the commandline:
javac -cp bin:gson.jar src/HomeAutomation.java

