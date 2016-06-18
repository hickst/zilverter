#!/bin/sh
if [ $# -lt 3 ]; then
  echo "Usage: xform xslfile infile outfile"
  exit 1
fi

CP=.:xalan.jar:xercesImpl.jar:xmlParserAPIs.jar
java -cp ${CP} org.apache.xalan.xslt.Process -XSL $1 -IN $2 -OUT $3
