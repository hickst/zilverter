@ECHO OFF

if "%3" == "" goto :USAGE

SET xslfile=%1%
SET xmlfile=%2%
SET outfile=%3%

SET CP=.;xalan.jar;xercesImpl.jar;xmlParserAPIs.jar
java -cp %CP% org.apache.xalan.xslt.Process -XSL %xslfile% -IN %xmlfile% -OUT %outfile%

goto :EXIT

:USAGE
  @echo Usage: xform XSL-file XML-file outfile

:EXIT
