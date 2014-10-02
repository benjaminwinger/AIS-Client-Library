# README #

AIS Client Library

(C) 2014 Dracode Software

Version: 1.0

Authors: Benjamin Winger

A library that allows applications to connect to the [Android Indexing Service](https://github.com/dracode/Android-Indexing-Service) (AIS).

## Compiling ##
AIS Client Library is built using the Apache Maven build tool. Simply download the source and execute

```
#!bash

mvn clean install
```
This will generate a jar file in the target directory which can be used in other applications.
Compiled jar files are also uploaded with each release.

## Current Features ##
The current version of AIS Client Library includes the following features:

* Searching file content in a single file using boolean or fuzzy search
* Searching file content in multiple files using boolean or fuzzy search
* Searching file names in multiple files using boolean or fuzzy search
* Manually calling for a file to be indexed if for some reason it was not already
* Checking if the Client Library was able to connect to the search service
* Cancelling searches that are in progress

## Known Issues ##
none.

### Contact ###
For more information, contact Benjamin Winger (winger.benjamin@gmail.com).
