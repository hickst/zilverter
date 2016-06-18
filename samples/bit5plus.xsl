<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema" version="1.0">

  <xsl:output method="text" />
  <xsl:strip-space elements="*" />


  <xsl:template match="/">
    <xsl:text>Subject,DateTime,COT,'+Bit5'
</xsl:text>
    <xsl:apply-templates />
  </xsl:template>


  <xsl:template match="item">
    <xsl:value-of select="ancestor::subject/@num" />,<xsl:text/>
    <xsl:value-of select="ancestor::subject/@dateTime" />,<xsl:text/>
    <xsl:value-of select="@num" />,<xsl:text/>
    <xsl:value-of select="@COT" />
    <xsl:for-each select="rt[@type='+Bit5']">
      <xsl:text>,</xsl:text>
      <xsl:value-of select="."/><xsl:text/>
    </xsl:for-each>
    <xsl:text>
</xsl:text>
  </xsl:template>

  <xsl:template match="rt" />

</xsl:stylesheet>
