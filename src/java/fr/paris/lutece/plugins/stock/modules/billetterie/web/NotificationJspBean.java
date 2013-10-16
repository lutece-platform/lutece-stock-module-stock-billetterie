/*
 * Copyright (c) 2002-2013, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.stock.modules.billetterie.web;

import fr.paris.lutece.plugins.stock.business.purchase.PurchaseFilter;
import fr.paris.lutece.plugins.stock.commons.exception.FunctionnalException;
import fr.paris.lutece.plugins.stock.modules.billetterie.utils.constants.BilletterieConstants;
import fr.paris.lutece.plugins.stock.modules.tickets.business.NotificationDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ReservationDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.SeanceDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.service.INotificationService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IPurchaseService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.ISeanceService;
import fr.paris.lutece.plugins.stock.modules.tickets.utils.constants.TicketsConstants;
import fr.paris.lutece.plugins.stock.utils.constants.StockConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * This class provides the user interface to manage notifications
 */
public class NotificationJspBean  extends AbstractJspBean
{
    public static final Logger LOGGER = Logger.getLogger( NotificationJspBean.class );
    
    public static final String RESOURCE_TYPE = "STOCK";
    public static final String RIGHT_MANAGE_NOTIFICATIONS = "OFFERS_MANAGEMENT";

    // MARKS
    public static final String MARK_NOTIFICATION = "notification";
    public static final String MARK_TITLE = "title";
    public static final String MARK_OFFER_ID = "offer_id";
    public static final String MARK_CANCEL = "cancel";
    public static final String MARK_LOCK = "lock";
    public static final String MARK_NOTIFICATION_ACTION = "notificationAction";
    public static final String MARK_AVERTISSEMENT = "avertissement";
    public static final String MARK_SEANCE = "seance";
    public static final String MARK_LIST_RESERVATION = "liste_reservations";
    public static final String MARK_TARIF_REDUIT_ID = "idTarifReduit";
    public static final String MARK_BASE_URL = "base_url";

    // BEAN
    private static final String BEAN_STOCK_TICKETS_SEANCE_SERVICE = "stock-tickets.seanceService";

    // JSP
    private static final String JSP_MANAGE_OFFERS = "jsp/admin/plugins/stock/modules/billetterie/ManageOffers.jsp";
    private static final String JSP_SEND_NOTIFICATION = "SendNotification.jsp";
    
    // TEMPLATES
    private static final String TEMPLATE_SEND_NOTIFICATION = "admin/plugins/stock/modules/billetterie/send_notification.html";
    private static final String TEMPLATE_MAIL_LOCK = "admin/plugins/stock/modules/billetterie/mail_lock.html";
    private static final String TEMPLATE_MAIL_CANCEL = "admin/plugins/stock/modules/billetterie/mail_cancel.html";

    // PAGE TITLES
    private static final String PROPERTY_PAGE_TITLE_SEND_NOTIFICATION = "module.stock.billetterie.send_notification.title";
    private static final String PROPERTY_MAIL_SEPARATOR = "mail.list.separator";

    // MESSAGES
    private static final String MESSAGE_AVERTISSEMENT_CANCEL_OFFER = "module.stock.billetterie.message.cancelOffer.avertissement";
    private static final String MESSAGE_AVERTISSEMENT_SEND_OFFER = "module.stock.billetterie.message.sendOffer.avertissement";
    private static final String MESSAGE_NOTIFICATION_CANCEL_SUBJECT = "module.stock.billetterie.notification.cancel.subject";
    private static final String MESSAGE_NOTIFICATION_BOOKING_LIST_SUBJECT = "module.stock.billetterie.notification.bookingList.subject";

    // ORDER FILTER
    private static final String ORDER_FILTER_USER_NAME = "userName";
    
    // @Inject
    // @Named( "stock-tickets.seanceService" )
    private ISeanceService _serviceOffer;
    // @Inject
    private IPurchaseService _servicePurchase;
    // @Inject
    private INotificationService _serviceNotification;

    /**
     * Instantiates a new notification jsp bean.
     */
    public NotificationJspBean(  )
    {
        super( );
        _serviceOffer = (ISeanceService) SpringContextService.getBean( BEAN_STOCK_TICKETS_SEANCE_SERVICE );
        _servicePurchase = SpringContextService.getContext( ).getBean( IPurchaseService.class );
        _serviceNotification = SpringContextService.getContext( ).getBean( INotificationService.class );
    }

    /**
     * Returns the form for notification creation
     * @param request The HTTP request
     * @return HTML Form
     */
    public String getSendNotification( HttpServletRequest request )
    {
        NotificationDTO notification;
        Map<String, Object> model = new HashMap<String, Object>( );

        // Manage validation errors
        FunctionnalException ve = getErrorOnce( request );
        if ( ve != null )
        {
        	notification = ( NotificationDTO ) ve.getBean( );
            model.put( BilletterieConstants.ERROR, getHtmlError( ve ) );
        }
        else
        {
            setPageTitleProperty( PROPERTY_PAGE_TITLE_SEND_NOTIFICATION );
            // Create new notification
            notification = new NotificationDTO( );
        }
        
        String strIdOffer = request.getParameter( MARK_OFFER_ID );
        String strCancel = request.getParameter( MARK_CANCEL );
        String strLock = request.getParameter( MARK_LOCK );
        // If the notification is about an offer
        if ( StringUtils.isNotEmpty( strIdOffer ) )
        {
        	Integer idOffer = Integer.parseInt( strIdOffer );
        	
            SeanceDTO seance = this._serviceOffer.findSeanceById( idOffer );
        	notification.setIdOffer( idOffer );
            List<String> orderList = new ArrayList<String>( );
            PurchaseFilter filter = new PurchaseFilter( );
            orderList.add( ORDER_FILTER_USER_NAME );
            filter.setOrders( orderList );
            filter.setOrderAsc( true );
            filter.setIdOffer( idOffer );
            List<ReservationDTO> listReservations = this._servicePurchase.findByFilter( filter, null );
        	
        	// If the action is to cancel the offer
        	if ( StringUtils.isNotEmpty( strCancel ) || ( notification.getNotificationAction( ) != null && notification.getNotificationAction( ).equals( TicketsConstants.OFFER_STATUT_CANCEL ) ) )
            {
            	notification.setNotificationAction( TicketsConstants.OFFER_STATUT_CANCEL );
            	// Set the default recipientsTo
                StringBuilder recipientsTo = new StringBuilder( 100 );
            	for ( ReservationDTO purchase : listReservations )
            	{
                    recipientsTo.append( purchase.getEmailAgent( ) ).append(
                            AppPropertiesService.getProperty( PROPERTY_MAIL_SEPARATOR ) );
            	}
                notification.setRecipientsTo( recipientsTo.toString( ) );
            	// Set the default subject
                notification.setSubject( getMessage( MESSAGE_NOTIFICATION_CANCEL_SUBJECT, seance.getProduct( )
                        .getName( ), seance.getDate( ), seance.getHour( ) ) );
            	// Set the default message
                Map<String, Object> modelMail = new HashMap<String, Object>( );
                modelMail.put( MARK_BASE_URL, AppPathService.getBaseUrl( request ) );
                modelMail.put( MARK_SEANCE, seance );
                HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MAIL_CANCEL, request.getLocale( ), modelMail );
            	notification.setMessage( template.getHtml( ) );
        	}
        	// Action to send notification to partner and lock the offer
        	else if ( StringUtils.isNotEmpty( strLock ) || ( notification.getNotificationAction( ) != null && notification.getNotificationAction( ).equals( TicketsConstants.OFFER_STATUT_LOCK ) ) )
            {
            	notification.setNotificationAction( TicketsConstants.OFFER_STATUT_LOCK );
            	// Set the default recipientsTo
            	notification.setRecipientsTo( seance.getProduct( ).getProviderMail( ) ); 
                // Set the default subject
                notification.setSubject( getMessage( MESSAGE_NOTIFICATION_BOOKING_LIST_SUBJECT, seance
                        .getProduct( ).getName( ) + " " + seance.getDate( ) + " - " + seance.getHour( ) ) );
                // Set the default message
                Map<String, Object> modelMail = new HashMap<String, Object>( );
                modelMail.put( MARK_BASE_URL, AppPathService.getBaseUrl( request ) );
                modelMail.put( MARK_SEANCE, seance );
                modelMail.put( MARK_TARIF_REDUIT_ID, TicketsConstants.OFFER_TYPE_REDUCT_ID );
                modelMail.put( MARK_LIST_RESERVATION, listReservations );
                HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MAIL_LOCK, request.getLocale( ), modelMail );
            	notification.setMessage( template.getHtml( ) );	
        	}
        	// Else action to send notification
        	else
        	{
            	// Set the default recipientsTo
                StringBuilder recipientsTo = new StringBuilder( );
            	for ( ReservationDTO purchase : listReservations )
            	{
                    recipientsTo.append( purchase.getEmailAgent( ) ).append(
                            AppPropertiesService.getProperty( PROPERTY_MAIL_SEPARATOR ) );
            	}
                notification.setRecipientsTo( recipientsTo.toString( ) );
        	}
        }

        // If the action is to cancel the offer
        if ( StringUtils.isNotEmpty( strCancel )
                || ( notification.getNotificationAction( ) != null && notification.getNotificationAction( ).equals(
                        TicketsConstants.OFFER_STATUT_CANCEL ) ) )
        {
            model.put( MARK_AVERTISSEMENT,
                    I18nService.getLocalizedString( MESSAGE_AVERTISSEMENT_CANCEL_OFFER, request.getLocale( ) ) );
        }
        // Action to send notification to partner and lock the offer
        else if ( StringUtils.isNotEmpty( strLock )
                || ( notification.getNotificationAction( ) != null && notification.getNotificationAction( ).equals(
                        TicketsConstants.OFFER_STATUT_LOCK ) ) )
        {
            model.put( MARK_AVERTISSEMENT,
                    I18nService.getLocalizedString( MESSAGE_AVERTISSEMENT_SEND_OFFER, request.getLocale( ) ) );
        }

        // Add the JSP wich called this action
        model.put( StockConstants.MARK_JSP_BACK, request.getParameter( StockConstants.MARK_JSP_BACK ) );
        model.put( MARK_NOTIFICATION, notification );

        model.put( MARK_TITLE, I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_SEND_NOTIFICATION, Locale.getDefault( ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SEND_NOTIFICATION, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }


    /**
     * Send a notification
     * @param request The HTTP request
     * @return redirection url
     */
    public String doSendNotification( HttpServletRequest request )
    {
        if ( StringUtils.isNotBlank( request.getParameter( StockConstants.PARAMETER_BUTTON_CANCEL ) ) )
        {
            return doGoBack( request );
        }

        NotificationDTO notification = new NotificationDTO( );
        populate( notification, request );

        try
        {
            // Controls mandatory fields
            validateBilletterie( notification );
            if ( notification.getIdOffer( ) != null && notification.getIdOffer( ) > 0 )
            {
                SeanceDTO seance = this._serviceOffer.findSeanceById( notification.getIdOffer( ) );
	            if ( notification.getNotificationAction( ).equals( TicketsConstants.OFFER_STATUT_LOCK ) )
	            {
	            	seance.setStatut( TicketsConstants.OFFER_STATUT_LOCK );
	            	this._serviceOffer.update( seance );
	            }
	            else if ( notification.getNotificationAction( ).equals( TicketsConstants.OFFER_STATUT_CANCEL ) )
	            {
	            	seance.setStatut( TicketsConstants.OFFER_STATUT_CANCEL );
	            	this._serviceOffer.update( seance );
	            }
            }
            _serviceNotification.send( notification );
        }
        catch ( FunctionnalException e )
        {
            return manageFunctionnalException( request, e, JSP_SEND_NOTIFICATION );
        }

        return doGoBack( request );
    }

    /**
     * Return the url of the JSP which called the last action
     * @param request The Http request
     * @return The url of the last JSP
     */
    private String doGoBack( HttpServletRequest request )
    {
        String strJspBack = request.getParameter( StockConstants.MARK_JSP_BACK );

        return StringUtils.isNotBlank( strJspBack ) ? ( AppPathService.getBaseUrl( request ) + strJspBack )
                : AppPathService.getBaseUrl( request ) + JSP_MANAGE_OFFERS;
    }
}
