<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />

<%@page import="fr.paris.lutece.portal.service.spring.SpringContextService"%>
<%@page import="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean"%>
<jsp:useBean id="offer" scope="session" class="fr.paris.lutece.plugins.stock.modules.billetterie.web.OfferJspBean" />
<% 
offer.init( request, OfferJspBean.RIGHT_MANAGE_OFFERS);
%>
<%= offer.getManageOffers( request ) %>

<%@ include file="../../../../AdminFooter.jsp" %>