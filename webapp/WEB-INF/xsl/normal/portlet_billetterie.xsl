<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" indent="yes" />

	<xsl:template match="portlet">
		<div>
			<xsl:attribute name="class">portlet <xsl:value-of select="billetterie/typePortlet"/></xsl:attribute>
			<h3>
				<xsl:value-of disable-output-escaping="yes" select="portlet-name" />
			</h3>
			<div class="portlet-content">
				<div class="show_list_page">
					<xsl:choose>
						<xsl:when test="billetterie/typePortlet = 'aLaffiche'">
							<xsl:attribute name="href"><xsl:value-of select="url"/></xsl:attribute>
							<xsl:choose>
							<xsl:when test="billetterie[show]">
							<div id="carousel-show" class="carousel slide" data-ride="carousel">
								<ol class="carousel-indicators">
									<xsl:for-each select="billetterie/show">
										<li data-slide-to="{position() - 1}" data-target="#carousel-show">
											<xsl:if test="position() - 1 = 0"><xsl:attribute name="class">active</xsl:attribute></xsl:if>
										</li>
									</xsl:for-each>
	        					</ol>
        					
	        					<div role="listbox" class="carousel-inner">	
									<xsl:for-each select="billetterie/show">
										<div>
											<xsl:if test="position() - 1 = 0"><xsl:attribute name="class">item active</xsl:attribute></xsl:if>
											<xsl:if test="position() - 1 &gt; 0"><xsl:attribute name="class">item</xsl:attribute></xsl:if>
											<img class="img-responsive" >
												<xsl:attribute name="src"><xsl:value-of select="posterUrl"/></xsl:attribute>
											</img>
											<div class="carousel-caption">
												<h3>
													<a target="_top" >
														<xsl:attribute name="title"><xsl:value-of select="name"/></xsl:attribute>
														<xsl:attribute name="href"><![CDATA[jsp/site/Portal.jsp?page=billetterie&action=fiche-spectacle&product_id=]]><xsl:value-of select="id"/></xsl:attribute>
														<xsl:value-of select="name" />
													</a>
												</h3>
											</div>
										</div>
									</xsl:for-each>
								</div>
								<a data-slide="prev" role="button" href="#carousel-show" class="left carousel-control">
								<span aria-hidden="true" class="fa fa-chevron-left">
									<span class="sr-only">Precedent</span>
								</span>
								</a>
								<a data-slide="next" role="button" href="#carousel-show" class="right carousel-control">
									<span aria-hidden="true" class="fa fa-chevron-right">
										<span class="sr-only">Suivant</span>
									</span>
								</a>
							</div>
							<br style="clear:both" />
							<span class="lien-detail">
							<a>
								<xsl:attribute name="href">jsp/site/Portal.jsp?page=billetterie&amp;action=a-laffiche</xsl:attribute>
								Voir tous les spectacles en cours
							</a>
							</span>
							</xsl:when>
							<xsl:otherwise>
								Pas de spectacle en cours.
							</xsl:otherwise>
						</xsl:choose>

						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="href"><xsl:value-of select="url"/></xsl:attribute>
							<xsl:choose>
							<xsl:when test="billetterie[show]">
							<xsl:for-each select="billetterie/show">

								<div class="show_details">
									<xsl:attribute name="style">background-color: <xsl:value-of select="categoryColor"/>;</xsl:attribute>
									<a>
										<xsl:attribute name="title"><xsl:value-of select="name"/></xsl:attribute>
										<xsl:attribute name="href"><xsl:value-of select="url"/></xsl:attribute>
										<img>
											<xsl:attribute name="src"><xsl:value-of select="posterUrl"/></xsl:attribute>
											<xsl:attribute name="alt"><xsl:value-of select="name"/></xsl:attribute>
										</img>
									</a>
									<br/>
									<a class="en-savoir-plus">
										<xsl:attribute name="href"><![CDATA[jsp/site/Portal.jsp?page=billetterie&action=fiche-spectacle&product_id=]]><xsl:value-of select="id"/></xsl:attribute>
										<xsl:attribute name="title">Détails du spectacle</xsl:attribute>
										<xsl:value-of select="name" />
										<br/>
										EN SAVOIR	PLUS
									</a>
								</div>
							</xsl:for-each>
							<br style="clear:both" />
							<span class="lien-detail">
							<a>
								<xsl:attribute name="href">jsp/site/Portal.jsp?page=billetterie&amp;action=a-venir</xsl:attribute>
								Voir tous les spectacles à venir
							</a>
							</span>
							</xsl:when>
							<xsl:otherwise>
								Pas de spectacle à venir.
							</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</div>
			</div>
		</div>
	</xsl:template>

</xsl:stylesheet>
