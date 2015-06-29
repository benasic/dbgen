Work in progress...
29.6.2015
For now program have basic functionality for generating data.
In next few days program will be upgraded with bug fixes and performance optimization.

How to import project into Eclipse:

Preconditions:
    1.Java 8u45 installed
    2.Git 1.9.5 or newer installed

1.In Eclipse switch to Git perspective:
    Windows->Open Perspective->Other->Git
2.Copy HTTPS clone URL from GitHub
3.Paste URI in Git Repository windows in Eclipse
    3.1 Clone Wizard Repository will start
    3.2 Follow steps - you can leave Authentication fields empty
4.Switch back to Java perspective
5.Create new Java Project with cloned git repository location:
    5.1 New->Java Project
    5.2 Enter desired project name
    5.3 For location you must select git repository location!!!
    5.4 Wizard will setup all necessary files for correct run
6.Run program:
    6.1 Chose DbGen when prompted for main entry point
