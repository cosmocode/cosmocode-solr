<!--

    Copyright 2010 CosmoCode GmbH

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text"/>

<xsl:template match="/response/result">
  <xsl:value-of select="'id,name,email,phone,website,superbrand,collections'"/>
  <xsl:text>&#10;</xsl:text>
  <xsl:apply-templates select="doc"/>
</xsl:template>

<xsl:template match="doc">
    <xsl:value-of select="./str[@name=&quot;id&quot;]" />
    <xsl:value-of select="','"/>
    <xsl:value-of select="./str[@name=&quot;labelname_t&quot;]" />
    <xsl:value-of select="','"/>
    <xsl:value-of select="./str[@name=&quot;labelemail_t&quot;]" />
    <xsl:value-of select="','"/>
    <xsl:value-of select="./str[@name=&quot;labelphone_t&quot;]" />
    <xsl:value-of select="','"/>
    <xsl:value-of select="./str[@name=&quot;labelwebsiteurl_t&quot;]" />
    <xsl:value-of select="','"/>
    <xsl:value-of select="./str[@name=&quot;superbrand_t&quot;]" />
    <xsl:value-of select="','"/>
    <xsl:value-of select="./str[@name=&quot;labelcollections_t&quot;]" />
    <xsl:text>&#10;</xsl:text>
</xsl:template>

</xsl:stylesheet>
