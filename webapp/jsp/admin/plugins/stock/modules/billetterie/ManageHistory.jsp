<%@ page errorPage="../../../../ErrorPage.jsp" %>

<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean"%>
<jsp:include page="../../../../AdminHeader.jsp" />

<jsp:useBean id="history" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.HistoryJspBean" />
<% 
	history.init( request, OfferJspBean.RIGHT_MANAGE_OFFRES);
%>
<%= history.getManageHistory( request ) %>

<%@ include file="../../../../AdminFooter.jsp" %>