ARCHIVE=zilverter.zip
DOCS = README.txt LICENSE LICENSE.xerces LICENSE.jdom
JARS = jdom.jar xalan.jar xercesImpl.jar xmlParserAPIs.jar
SAMPLES = a.zil bit5plus.xsl
SCRIPTS = zilverter.sh zilverter.bat xform.sh xform.bat
CP=.:jdom.jar

what:
	@echo ""
	@echo "Make what?"
	@echo ""
	@echo "build	- compile the Java code"
	@echo "distrib  - create JAR file of runtime files for distribution"
	@echo ""
	@echo "testa	- run the zilverter code on a test file A"
	@echo "testb	- run the zilverter code on a test file B"
	@echo "testc	- run the zilverter code on a test file C"
	@echo "testd	- run the zilverter code on a test file D"
	@echo ""
	@echo "clean	- remove all editor file"
	@echo "runclean - remove all editor, XML & CSV files"
	@echo "distclean - remove all editor, XML & CSV, and class files"
	@echo ""

build:
	javac -d . -classpath $(CP) ZilVerter.java

testa:
	@java -cp $(CP) ZilVerter catprod3_a_herc.zil

testb:
	@java -cp $(CP) ZilVerter catprod3_b_herc.zil

testc:
	@java -cp $(CP) ZilVerter A0025.zil

testd:
	@java -cp $(CP) ZilVerter A1293.zil

distrib: build
	zip $(ARCHIVE) ZilVerter.class $(JARS) $(DOCS) $(SCRIPTS) $(SAMPLES)


distclean: clean runclean
	rm -f *.class $(ARCHIVE)

clean:
	rm -f *.~1~ *.~2~ *.~3~ *.~4~

runclean:
	rm -f *.csv *.xml
