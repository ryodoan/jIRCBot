Hive13 IRC Bot
==============
Wikipage: http://wiki.hive13.org/Hive13_IRC_Bot

This is the bot that hanges out in irc.freenode.net #hive13.  The bot is written in Java with a MySQL database for 
logging messages and a PHP website for displaying said log.

Building
--------
The bot is developed in eclipse, and if the repository is retrieved at the root it contains all libraries (other 
than the JDK) needed to build the bot.  I have tried to extensively comment the code to make everyone's lives easier.
If you have already retrieved the project using Git, you can open it in Eclipse by going to 
File->Import->General->Existing Projects Into Workspace, then selecting the projects root directory.

Project Structure
-----------------
- /lib - Libraries the bot needs to run
- /res - Resource files associated with the bot, currently the MySQL database construction scripts and the PHP website.
- /src - Source code for the bot.
- /jIRCBot.properties - A properties file for basic configuration (Bot name, IRC Server, IRC Channel, password for connection, MySQL database names, etc,etc..)  This file also contains prototyped properties that have not been implemented yet, namely the ones related to the plugins.

Code Structure
--------------
