<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean"%>

<jsp:useBean id="offer" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean" />
<% 
    offer.init( request, OfferJspBean.RIGHT_MANAGE_OFFRES);
   	String strResult =  offer.doExportCSV(request, response);
  	if ( !response.isCommitted(  ) )
    {
		response.sendRedirect( strResult );
	}
%>