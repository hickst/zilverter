#!/bin/sh
if [ $# -lt 2 ]; then
  echo "Usage: zilverter XSLT-file zil-file"
  exit 1
fi


xslfile=${1}
zilfile=${2}
base=`basename $zilfile .zil`
xmlfile=${base}.xml
csvfile=${base}.csv

# generate an XML file from the .zil file
java -cp .:jdom.jar ZilVerter $zilfile >$xmlfile

# extract the desired data from the XML file using the given XSLT file
CP=.:xalan.jar:xercesImpl.jar:xmlParserAPIs.jar
java -cp ${CP} org.apache.xalan.xslt.Process -XSL $xslfile -IN $xmlfile -OUT $csvfile
