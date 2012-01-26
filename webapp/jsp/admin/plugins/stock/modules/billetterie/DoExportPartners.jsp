<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.PartnerJspBean"%>
<jsp:useBean id="partner" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.PartnerJspBean" />

<% 
	partner.init( request, PartnerJspBean.RIGHT_MANAGE_PARTNERS);
   	String strResult =  partner.doExportPartners( request, response );
  	if ( !response.isCommitted(  ) )
    {
		response.sendRedirect( strResult );
	}
%>