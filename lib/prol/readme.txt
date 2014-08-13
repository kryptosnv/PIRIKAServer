============================
* PPPPP RRRRR OOOOO L      * 
* P   P R   R O   O L      *  
* PPPPP RRRRR O   O L      *
* P     R  R  O   O L      *
* P     R   R OOOOO LLLLLL *
*                          * 
============================

An Embeddable Java Prolog engine
© Copyright Igor A. Maznitsa 2008-2010. All rights reserved.
version: 1.1.3
-------------------------------------------------------------------------------------
The archive contains the Prol (it is an Embeddable Java Prolog engine) as the compiled JAR and documentation with examples of the use. The library can be used absolutely free for any kind of projects without any restrictions.
The last version of the engine can be found on the site http://www.igormaznitsa.com
For any questions about the engine do not hesitate to contact me via my email igor.maznitsa@igormaznitsa.com

The engine works under and Java 1.6, because it very actively uses stack, you have to remember make corrections of the maximum stack size in your Java machine through the -Xss<Stack_size> parameter of the JVM command string (as an example the e_life game needs the -Xss10m value)

History
--------------------
1.1.3
* Engine: The PredicateSynonims annotation has been renamed to correct english view and now it has the PredicateSynonyms name
* Prol NotePad: minor improvements and bugfixing
* Core library: the ifork/1 predicate has been added, it works like fork/1 but if there is any fail goal in the parallel goal list then all other goals will be interrupted
* Graphics library: bindaction/2, removeallactions/0, removeaction/1 have been added, the predicates allow to register actions which can be called by a user through the form menu

1.1.2
* Parser: Fixed the bug in float number parsing
* Prol NotePad: Fixed the bug which had made errors for non-asc symbols during a file loading.
* Prol NotePad: Improved usability

1.1.1
* Refactoring, speed increased in two times
* Bugfixing
* More testing (97 unit tests)
* !!/0 (local cut) added
* tracing predicates were removed, now the engine includes TraceListener interface to trace goals
* Now a goal works with direct data for speed so you have to be accurate with the goal term data. To isolate data there is new class the IsolatedGoal.

1.0.5
* Bugfixing
* The trigger mechanism was added (it can be used with the regtrigger/3 predicate)
* Corrected the assert/1 alias, now it is the alias for assertz/1 (I was wrong before and used it as asserta/1)

1.0.4
* Bugfixing
* The Multithreading support added: fork/1, async/1, lock/1, unlock/1, trylock/1, waitasync/0

1.0.3
* Bugfixing
* facts/1 predicate added into the prol core library (it allows to get only facts from the knowledge base)
* PreparedGoal class added

1.0.2
* Minor bugfixing

1.0.1
* Bugfixing
* The core prol library was exapnded with new predicates
* String and graphic prol libraries were added
* A small notepad to edit and execute prol scripts was embedded into the jar

1.0.0a
* Initing version