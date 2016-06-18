<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema" version="1.0">

  <xsl:output method="text" />
  <xsl:strip-space elements="*" />

  <xsl:template match="/">
    <xsl:text>Subject,DateTime,Refresh
</xsl:text>
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="zil/subject">
    <xsl:value-of select="@num" />,<xsl:text/>
    <xsl:value-of select="@dateTime" />,<xsl:text/>
    <xsl:value-of select="@refresh" /><xsl:text>
</xsl:text>
  </xsl:template>

</xsl:stylesheet>
