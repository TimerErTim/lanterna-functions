# Lanterna-Functions
This is a feature extending library for Lanterna, a 100% pure Java terminal-like text based GUI.
## Introduction
Aim of this project is to provide a library with extra functions for the [Lanterna](https://github.com/mabe02/lanterna) library. This repository was created to provide features needed in the [Text-Fighter](https://github.com/hhaslam11/Text-Fighter/graphs/contributors) project to provide a console-like GUI. It may be updated in the future to provide extra functionality.
## Dependencies
In order to use this library you will obviously have to add [Lanterna](https://github.com/mabe02/lanterna) to your project. This can be done with Maven as shown on Lanterna's [README](https://github.com/mabe02/lanterna/blob/master/README.md) or Gradle using the same dependency information. Alternatively you can download the .jar files [here](https://repo1.maven.org/maven2/com/googlecode/lanterna/lanterna/3.0.3/lanterna-3.0.3.jar) and add it to your project as library like you would with Ant.
### Version
**The version of Lanterna used is _[3.0.3](https://mvnrepository.com/artifact/com.googlecode.lanterna/lanterna/3.0.3)_, so take this into account when using Gradle or Maven. Compatibility is not guaranteed with other versions (however it is still very much possible).**
## Usage
Download the .jar file from the [release](https://github.com/TimerErTim/Lanterna-Functions/releases) page and add it to your project as library. You may have to extract it prior to using it in order to have independent source and javadoc files. In the future you should be able to get it from Maven Central.
# Feature List
- TerminalConsole
    - Mainly acts like 'System.out.println' and 'System.in'
### TODO
- Menu choices in the TerminalConsole
- Softwrapping and automatic resizing
- Automatic scrolling and animated text
