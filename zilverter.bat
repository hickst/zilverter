@ECHO OFF

if "%4" == "" goto :USAGE

SET xslfile=%1%
SET zilfile=%2%
SET xmlfile=%3%
SET csvfile=%4%

REM generate an XML file from the .zil file
java -cp .;jdom.jar ZilVerter %zilfile% > %xmlfile%

REM extract the desired data from the XML file using the given XSLT file
SET CP=.;xalan.jar;xercesImpl.jar;xmlParserAPIs.jar
java -cp %CP% org.apache.xalan.xslt.Process -XSL %xslfile% -IN %xmlfile% -OUT %csvfile%

goto :EXIT

:USAGE
  @echo Usage: zilverter XSLT-file zil-file xml-file csv-file

:EXIT
