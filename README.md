# Powerdock (2020 Setup update)

# Project folder structure

The Powerdock app uses these folders (create if not included)

src: contains Java source files

fxlib : contains the JavaFX library

classes : contains compiled JVM bytecode

templates : contains previous work

config : contains file with config data, like recents list for recents Menu

html: contains any html output from the program.

# Setup

Modify the Config.java file before compiling to ensure project directory is specified.

Default:

```
 String projectfolder = "";  //This is the current top level folder with the src and fxlib folders in it
 String templatesfolder = projectfolder+"/templates/";
 String recentsfolder = projectfolder+"/config/";
```
e.g. with src folder for source files etc

# Make sure you have JavaFX library available

To use JavaFX you need to invoke it as a module when you compile and run.  It is not longer automatically part of the Java SDK.

So first you need to download the JavaFX library.

see https://openjfx.io and https://gluonhq.com/products/javafx/

The openJDK download is specific for each OS.  The SDK library file when unzipped will be closer to 95MB.

When you have downloaded the JavaFX SDK package, locate the folder javafx-sdk-11.0.2 (or similar) and inside this, find the lib file with the javafx .jar files in it.  

Rename it to fxlib and put it into the root folder of the Powerdock project.

# Summary of what to do to get app with JavaFX running from source (as at 7 August 2020)

The next step is to set up the environment variables and make it easier to invoke the program.  This requires some terminal (bash) commands to be executed, in order.

In the parent folder (the root folder for the github):

```
export PATH_TO_FX='fxlib'
alias compilej='javac -cp classes -d classes src/*.java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.swing,javafx.web'
alias runprog='java --module-path $PATH_TO_FX --add-modules javafx.controls,javafx.swing,javafx.web -cp classes Main'
```

Then just type this to compile:

```
compilej 
```
and this to run:

```
runprog
```

# Simple instructions

## New Concepts

Choose New and Concept to create a new file structure.

## Adding or Moving Concept Boxes

Open up a Concept Box by double-clicking.  
To Add a New Concept inside an existing concept:
 (1) Click inside the 'Concepts Area' inside a Concept Box, then:
 
 (2) Choose new concept while the Concepts Area is selected.
 
To move an existing Concept inside another Concept:
(1) Open a Concept Box by double clicking 

(2) Highlight another Concept Box (anywhere) so that it is red.

(3) Click inside the open Concept Box's 'Concept Area', then choose "Concept" --> "Move to Target" from main menu.

## Save and Re-Open

Save and Re-Open existing Concept Structures (Templates) from the File Menu.
