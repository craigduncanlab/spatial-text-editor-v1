# Powerdock

# general folder structure

The Powerdock app uses these folders (create if not included)

src: contains Java source files

classes : contains compiled JVM bytecode

templates : contains previous work

config : contains file with config data, like recents list for recents Menu

html: contains any html output from the program.

# Setup

Modify the Config.java file before compiling to ensure project directory is specified.

# Compiling .java files to bytecode

Ensure there is a classes folder inside the main powerdock project folder

Within the powerdock folder:

`javac -d classes src/*.java`

# Running from bytecode

Within the main powerdock folder type and run from command line:

`java -cp classes Main`

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
