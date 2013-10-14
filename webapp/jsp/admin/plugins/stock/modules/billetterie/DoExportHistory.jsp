<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean"%>
<jsp:useBean id="history" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.HistoryJspBean" />

<% 
	history.init( request, OfferJspBean.RIGHT_MANAGE_OFFRES);
   	String strResult =  history.doExportHistory(request, response);
  	if ( !response.isCommitted(  ) )
    {
		response.sendRedirect( strResult );
	}
%>