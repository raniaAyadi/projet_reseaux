



# bittorent project
___

This is a school project, In this project we implement a peer in a bittorent centralised network, communication is tcp based, protocol is specified by the assignement.



### Dependencies
+ [Apache commons-cli 1.4 ](https://commons.apache.org/proper/commons-cli/download_cli.cgi)
+ [jasypt-1.9.0](http://www.java2s.com/Code/Jar/j/Downloadjasypt190jar.htm)

### Upload listener
+ listens to any new changes on a specified directory, if a new entry is detected, the upload listener automatically adds the file to the list of seeded files by the peer

### File downloader
+ to instanciate a file download use the UserAction.startLeech method

### MyConfig
+ this class tries to configure the app using bowth configuration file and passed arguments, if it fails, application will exit after displaying errors

### Usage
+ To start the application, just instantiate an ApplicationContext object passing it the args argument, the app tries then to configure it self using argumets and config file (which can be
specified as an argumet)
+ *Supported arguments:* to view help use the --help argument, in case of invalid arguments, program will display help and exit automatically.
+ Insert image display help here
+ To activate upload listener, just specify the listen directory as upload-path in the configuration file

# General Notes
+ i have included all files from the svn repo
+ i have successfully tested the file downloader (client) on any type of files including  binary files
+ i have implemented new file access methods in the Storage module, this lets you read and write in bytes wich is needed to support binary files
server can use FileTracker.readPiece and writePiece which are index based file access (instead of manually calculating offset when a piece is required by index)
+ i havent been able to use the decoder when initiating request, in fact: when parsing binary data, decoding bytes as caracters using a certain charset is not only iniffecient (read the hole stream, put it in a string, then parse that string instead
of parsing the stream directly) but also can cause problems: data payload may contain spaces (escape char), or even ':' (separators), one possible way to fix this is to set payload size in the regex before compiling it, problem is that
size in caracter count is different than size in byte count (depends on java string encoding)
+ i have renamed peer to ApplicationContext (sounds more technical, and can help us impress the jury :p)
+ still missing integration of logger, error handling, dynamic choice of piece size, update tracker ...
+ Instead of manually parsing xml config file, we can use the java.util.Properties which supports xml streams but also property files which are much more simpler to use,
it also provides out of the box a key value store, so no need to manually construct the map, just load the config file
+ checkout MyConfig based as i sad on java.util.Properties and also uses apache commons cli to parse program arguments, and if it seems like a better choise, we will use
it instead of Config class



 
