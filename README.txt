                             Zilverter README
                             ================

DISCLAIMER
----------

This distribution of this software is regulated by the accompanying
LICENSE files. By obtaining, using, and/or copying this work, you agree
that you have read, understood, and will comply with the terms of the
license.

Although every effort has been taken to ensure that viruses are not
present in the software, there are inherent dangers in the use of any
software available for downloading on the Internet. You are cautioned to
always check downloaded software for viruses before using it on your
system.


OVERVIEW
--------

Zilverter is a Java program that creates an XML file from a DMDX .zil
output file. Once you have converted the .zil file into XML, you can then
use the vast array of open-source and commercial XML tools to manipulate
your data.

To do the conversion, Zilverter depends on several JAR files (which are in
the distribution) and should work with any version of Java 1.4.0 or later.
Oracle does not permit the re-distribution of Java, so you have to download
it from their site, if it is not already installed on your machine. The
instructions below tell you how to install on Windows 2000 or XP (the
process is very similar for other systems and there is even a shell script
that runs Zilverter under Unix. (The Unix version may be easier to
use than the Windows version, since Zilverter was developed on Linux).

Zilverter will take your .zil file and create a 'marked up' version. This
is the XML file (which will have whatever name you specified). You can view
the XML file in a text editor or a recent version of Internet
Explorer. What you will see is a set of nested tags that identify each
piece of data from the .zil file. Once the data has been labelled in this
way, you can extract part of it and format it in many different ways with an
XSLT program. A sample XSLT file is provided (bit5plus.xsl) that describes the
desired output. You can view this XSLT file with a text editor and modify
it to do other similar tasks.


SPECIFICS
---------

Download a Java runtime (version 1.4.0 or later should work) available from:

   https://java.com

and follow the instructions to install it.

Unzip the zilverter.zip distribution into your working area.


SAMPLES
-------

To convert a sample .zil file and run a sample transformation, open a
shell command prompt and type:

   >zilverter bit5plus.xsl a.zil a.xml a.csv

This command runs the zilverter.bat script (which is viewable with a text
editor). The script reads the 'a.zil' sample data file and creates the
'a.xml' file from it. It also uses the 'bit5plus.xsl' XSLT program to
extract data from the XML file into the CSV file called 'a.csv' (which can
be imported into an Excel spreadsheet). The creation of the CSV file is an
example of transforming XML using XSLT and is particular to a specific
experiment that we run at the University of Arizona.

'bit5plus.xsl' is the sample XSLT file included in the distribution. It
extracts and formats some of the data from the XML file based on XSL
patterns. XSLT is a large and powerful language which can perform many
kinds of extraction and formatting tasks. We can not provide instructions
on how to write XSLT programs here, instead we refer you to the many
tutorials and articles available on the Internet, or one of the many
excellent books on the subject.

The 'xform' script runs only the code necessary to apply an XSLT program
to a existing XML file. For example, given an existing XML file 'a.xml'
and the sample XSLT file 'bit5plus.xsl', you can run the XSL against the
XML (to create a transformed output file) by executing the command:

   >xform bit5plus.xsl a.xml transformed.txt

With a little XSLT knowledge you can create your own XSLT files using the
'bit5plus.xsl' file as a template. With XSL you can search for, extract,
and transform data from your .zil files in a variety of different ways.

Once you have used Zilverter to create XML files from your .zil data
files, you can also use the ever-growing number of open-source and
commercial tools to manipulate the data in a variety of useful ways.

IMPORTANT NOTE:
Zilverter expects a comment line to be indicated by a leading !
We have discovered that DMDX may generate error messages that do not
conform to this expectation.  There is no easy way to handle this
programatically.  We therefore advise you to add ! to the beginning of
any such line and any lines of ************* surrounding the error:

!**********************************************
! I AM A PROPERLY FIXED ERROR MESSAGE
!**********************************************

