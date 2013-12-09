<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.PartnerJspBean"%>
<jsp:useBean id="partner" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.PartnerJspBean" />
<% 
	partner.init( request, PartnerJspBean.RIGHT_MANAGE_PARTNERS);
	if(request.getParameter( "save" ) == null && request.getParameter( "cancel" ) == null){%>
	    <%@ include file="billetterie_header.inc.jsp" %>
	    <%= partner.getSavePartner( request ) %>
	   	<%@ include file="../../../../AdminFooter.jsp" %>
	 <%}
	else{
	    response.sendRedirect( partner.doSavePartner( request ) ); 
	}
%>

