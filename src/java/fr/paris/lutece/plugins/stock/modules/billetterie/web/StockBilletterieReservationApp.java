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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.stock.business.purchase.PurchaseFilter;
import fr.paris.lutece.plugins.stock.business.purchase.exception.PurchaseUnavailable;
import fr.paris.lutece.plugins.stock.commons.exception.BusinessException;
import fr.paris.lutece.plugins.stock.commons.exception.FunctionnalException;
import fr.paris.lutece.plugins.stock.commons.exception.TechnicalException;
import fr.paris.lutece.plugins.stock.modules.billetterie.utils.constants.BilletterieConstants;
import fr.paris.lutece.plugins.stock.modules.tickets.business.NotificationDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ReservationDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.SeanceDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.UnauthentifiedPurchaserDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.service.INotificationService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IPurchaseService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.ISeanceService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.PurchaseService;
import fr.paris.lutece.plugins.stock.modules.tickets.utils.constants.TicketsConstants;
import fr.paris.lutece.plugins.stock.service.IPurchaseSessionManager;
import fr.paris.lutece.plugins.stock.utils.DateUtils;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;


/**
 * Pages for billetterie front
 * 
 */
public class StockBilletterieReservationApp extends AbstractXPageApp implements XPageApplication
{
    private static final String PAGE_BOOKING = "reservation";
    private static final String PAGE_TICKETING = "billetterie";

    private static final String JSP_DO_DELETE_RESERVATION = "jsp/site/plugins/stock/modules/billetterie/DoDeleteReservation.jsp";
    private static final String JSP_PORTAL = "jsp/site/Portal.jsp";

    private static final String PROPERTY_NOTIFICATION_REQUEST_RECIPIENT = "stock-billetterie.mail.senderEmail";
    // I18n
    private static final String TITLE_MY_BOOKINGS = "module.stock.billetterie.my_bookings.title";
    private static final String TITLE_CONFIRM_BOOKING = "module.stock.billetterie.confirm_booking.title";
    private static final String MESSAGE_NB_PLACES_INVALID = "module.stock.billetterie.message.error.invalid_nb_places";
    private static final String MESSAGE_INSUFFICIENT_PLACE_REMAINING = "module.stock.billetterie.message.error.insufficient_place_remaining";
    private static final String MESSAGE_CONFIRM_DELETE_PURCHASE_TITLE = "module.stock.billetterie.message.confirm.delete.purchase.title";
    private static final String MESSAGE_CONFIRM_DELETE_PURCHASE = "module.stock.billetterie.message.confirm.delete.purchase";
    private static final String MESSAGE_NOTIFICATION_BOOKING_SUBJECT = "module.stock.billetterie.notification.booking.subject";
    private static final String MESSAGE_NOTIFICATION_REQUEST_SUBJECT = "module.stock.billetterie.notification.request.subject";
    // Parameters
    private static final String PARAMETER_SEANCE_DATE = "seance_date";
    private static final String PARAMETER_SHOW_NAME = "show_name";
    private static final String PARAMETER_SHOW_ID = "show_id";
    private static final String PARAMETER_NEXT_BOOKING_LIST = "nextBookingList";
    private static final String PARAMETER_BOOKING_LIST = "bookingList";
    private static final String PARAMETER_PAST_BOOKING_LIST = "pastBookingList";
    private static final String PARAMETER_NB_PLACES = "nb_places";
    private static final String PARAMETER_SEANCE_TYPE_NAME = "seance_type_name";
    private static final String PARAMETER_SEANCE_ID = "seance_id";
    private static final String PARAMETER_PURCHASE_ID = "purchase_id";
    private static final String PARAMETER_ACTION = "action";
    private static final String PARAMETER_PRODUCT_ID = "product_id";
    private static final String PARAMETER_DATE_SEANCE = "date_seance";
    private static final String PARAMETER_BOOKING_CHECK = "booking_check";
    private static final String PARAMETER_AUTHENTIFIED_USER = "authentified_user";
    private static final String PARAMETER_DATE_SCEANCE = "date_sceance";
    private static final String PARAMETER_PAGE = "page";
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    // Actions
    private static final String ACTION_MY_BOOKINGS = "mes-reservations";
    private static final String ACTION_BOOK = "reserver";
    private static final String ACTION_DELETE_BOOKING = "delete-purchase";
    private static final String ACTION_SHOW_DETAILS = "fiche-spectacle";
    // Marks
    private static final String MARK_BASE_URL = "base_url";
    private static final String MARK_USER = "user";
    private static final String MARK_PURCHASER = "purchaser";
    private static final String MARK_PAGINATOR = "paginator";
    // Templates
    private static final String TEMPLATE_DIR = "skin/plugins/stock/modules/billetterie/";
    private static final String TEMPLATE_NOTIFICATION_BOOKING = "notification_booking.html";
    private static final String TEMPLATE_MY_BOOKINGS = "my_bookings.html";
    private static final String TEMPLATE_CONFIRM_BOOKING = "confirm_booking.html";
    private static final String TEMPLATE_NOTIFICATION_REQUEST = "notification_request.html";

    private static final String ENCODING_UTF_8 = "utf-8";

    private final ISeanceService _offerService = (ISeanceService) SpringContextService.getContext( ).getBean(
            ISeanceService.class );
    private final IPurchaseService _purchaseService = (IPurchaseService) SpringContextService.getContext( ).getBean(
            IPurchaseService.class );
    private final INotificationService _notificationService = (INotificationService) SpringContextService.getContext( )
            .getBean( INotificationService.class );
    private final IPurchaseSessionManager _purchaseSessionManager = (IPurchaseSessionManager) SpringContextService
            .getContext( ).getBean( IPurchaseSessionManager.class );

    /**
     * Return page with action specified.
     * 
     * @param request the request
     * @param nMode the n mode
     * @param plugin the plugin
     * @return the page
     * @throws UserNotSignedException the user not signed exception
     * @throws SiteMessageException the site message exception
     */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin ) throws UserNotSignedException,
            SiteMessageException
    {

        XPage page = new XPage( );
        Locale locale = request.getLocale( );

        String strAction = request.getParameter( PARAMETER_ACTION );

        if ( ACTION_BOOK.equals( strAction ) )
        {
            page = getConfirmBooking( page, request, locale );
        }
        else if ( ACTION_MY_BOOKINGS.equals( strAction ) )
        {
            page = getMyBookings( page, request, locale );
        }
        else if ( ACTION_DELETE_BOOKING.equals( strAction ) )
        {
            page = getDeleteBooking( page, request, locale );
        }

        return page;
    }

    /**
     * Returns xpage for confirm booking.
     * 
     * @param page xpage
     * @param request http request
     * @param locale locale
     * @return xpage
     * @throws SiteMessageException Confirm booking page cannot be shown
     */
    private XPage getConfirmBooking( XPage page, HttpServletRequest request, Locale locale )
            throws SiteMessageException
    {

        String[] seanceIdList = request.getParameterValues( PARAMETER_SEANCE_ID );
        String[] seanceTypeNameList = request.getParameterValues( PARAMETER_SEANCE_TYPE_NAME );
        String[] numberPlacesList = request.getParameterValues( PARAMETER_NB_PLACES );
        String showId = request.getParameter( PARAMETER_SHOW_ID );

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( PARAMETER_SHOW_ID, showId );

        FunctionnalException fe = getErrorOnce( request );

        List<ReservationDTO> bookingList = (List<ReservationDTO>) request.getSession( ).getAttribute(
                PARAMETER_BOOKING_LIST );

        String bookingCheck;
        boolean bAuthentified = false;
        UnauthentifiedPurchaserDTO purchaser = null;

        if ( fe == null )
        {
            // If user has already a booking in waiting state, remove it
            if ( bookingList != null )
            {
                for ( ReservationDTO reservation : bookingList )
                {
                    _purchaseSessionManager.release( request.getSession( ).getId( ), reservation );
                }
            }

            // Set user informations
            LuteceUser user = this.getUser( request );
            // Create booking list
            boolean bPlacesInvalid = true;
            bookingList = new ArrayList<ReservationDTO>( );
            // Avoid mixing purchase session (with two opened tabs for example)
            bookingCheck = UUID.randomUUID( ).toString( );
            try
            {
                int i = 0;
                ReservationDTO booking;
                int nbPlaces;
                for ( String seanceId : seanceIdList )
                {
                    // Check validity of parameter
                    if ( StringUtils.isNumeric( numberPlacesList[i] ) && Integer.valueOf( numberPlacesList[i] ) > 0 )
                    {
                        bPlacesInvalid = false;

                        // Create booking object
                        nbPlaces = Integer.valueOf( numberPlacesList[i] );
                        if ( nbPlaces > 0 )
                        {
                            booking = new ReservationDTO( );
                            booking.getOffer( ).setId( Integer.valueOf( seanceId ) );
                            booking.getOffer( ).setTypeName( seanceTypeNameList[i] );
                            booking.setQuantity( nbPlaces );
                            booking.setDate( DateUtils.getCurrentDateString( ) );
                            booking.setHeure( DateUtils.getHourFr( DateUtils.getCurrentDate( ) ) );
                            if ( user != null )
                            {
                                booking.setUserName( user.getName( ) );
                                booking.setEmailAgent( user.getUserInfo( LuteceUser.HOME_INFO_ONLINE_EMAIL ) );
                                booking.setFirstNameAgent( user.getUserInfo( LuteceUser.NAME_GIVEN ) );
                                booking.setNameAgent( user.getUserInfo( LuteceUser.NAME_FAMILY ) );

                                bAuthentified = true;

                                // Reserve tickets
                                try
                                {
                                    _purchaseSessionManager.reserve( request.getSession( ).getId( ), booking );
                                }
                                catch ( PurchaseUnavailable e )
                                {
                                    throw new BusinessException( null, MESSAGE_INSUFFICIENT_PLACE_REMAINING );
                                }
                            }
                            bookingList.add( booking );

                        }
                    }

                    i++;
                }

                if ( bPlacesInvalid )
                {
                    throw new BusinessException( null, MESSAGE_NB_PLACES_INVALID );
                }

                // Save booking into session
                request.getSession( ).setAttribute( PARAMETER_BOOKING_LIST, bookingList );
                request.getSession( ).setAttribute( PARAMETER_BOOKING_CHECK, bookingCheck );
            }
            catch ( BusinessException e )
            {
                String htmlError = getHtmlError( e, request );
                model.put( BilletterieConstants.ERROR, htmlError );
                UrlItem targetUrl = new UrlItem( JSP_PORTAL );
                targetUrl.addParameter( PARAMETER_PAGE, PAGE_TICKETING );
                targetUrl.addParameter( PARAMETER_ACTION, ACTION_SHOW_DETAILS );
                targetUrl.addParameter( PARAMETER_PRODUCT_ID, showId );
                SiteMessageService.setMessage( request, e.getCode( ), SiteMessage.TYPE_STOP, targetUrl.getUrl( ) );
            }
        }
        // Manage errors
        else
        {
            model.put( TicketsConstants.PARAMETER_ERROR, getHtmlError( fe, request ) );
            bookingCheck = (String) request.getSession( ).getAttribute( PARAMETER_BOOKING_CHECK );
            bAuthentified = StringUtils.isNotBlank( bookingList.get( 0 ).getEmailAgent( ) );

            // try to retrieve DTO if unauthentied user
            Object errorBean = fe.getBean( );
            if ( !bAuthentified && errorBean != null && errorBean instanceof UnauthentifiedPurchaserDTO )
            {
                purchaser = (UnauthentifiedPurchaserDTO) errorBean;
            }
        }

        // Generates template
        String showName = request.getParameter( PARAMETER_SHOW_NAME );

        model.put( PARAMETER_BOOKING_LIST, bookingList );
        model.put( PARAMETER_SEANCE_DATE, request.getParameter( PARAMETER_SEANCE_DATE ) );
        model.put( PARAMETER_SHOW_NAME, showName );
        model.put( PARAMETER_BOOKING_CHECK, bookingCheck );
        model.put( PARAMETER_AUTHENTIFIED_USER, bAuthentified );

        // Add DTO when unauthentified
        if ( !bAuthentified )
        {
            model.put( MARK_PURCHASER, purchaser != null ? purchaser : new UnauthentifiedPurchaserDTO( ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DIR + TEMPLATE_CONFIRM_BOOKING, locale, model );

        page.setContent( template.getHtml( ) );
        String pageTitle = getMessage( TITLE_CONFIRM_BOOKING, request, showName );
        page.setPathLabel( pageTitle );
        page.setTitle( pageTitle );
        return page;
    }

    /**
     * Action for saving booking. Called by JSP.
     * 
     * @param request http request
     * @param response http response
     * @return url to go
     * @throws SiteMessageException the site message exception
     */
    public String doSaveReservation( HttpServletRequest request, HttpServletResponse response )
            throws SiteMessageException
    {
        String returnUrl = null;
        // Check mixing booking (with two tabs and two booking opened
        if ( request.getParameter( PARAMETER_BOOKING_CHECK ) == null
                || !request.getParameter( PARAMETER_BOOKING_CHECK ).equals(
                        request.getSession( ).getAttribute( PARAMETER_BOOKING_CHECK ) ) )
        {
            SiteMessageService.setMessage( request, PurchaseService.MESSAGE_ERROR_PURCHASE_SESSION_EXPIRED,
                    SiteMessage.TYPE_ERROR, JSP_PORTAL );
        }
        else
        {
            List<ReservationDTO> bookingList = (List<ReservationDTO>) request.getSession( ).getAttribute(
                    PARAMETER_BOOKING_LIST );

            if ( Boolean.valueOf( request.getParameter( PARAMETER_AUTHENTIFIED_USER ) ) )
            {
                try
                {
                    bookingList = _purchaseService.doSavePurchaseList( bookingList, request.getSession( ).getId( ) );
                    sendBookingNotification( bookingList, request );
                    // Go to page "mes reservations"
                    UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_PORTAL );
                    url.addParameter( PARAMETER_PAGE, PAGE_BOOKING );
                    url.addParameter( PARAMETER_ACTION, ACTION_MY_BOOKINGS );
                    returnUrl = url.getUrl( );
                }
                catch ( FunctionnalException e )
                {
                    if ( bookingList != null )
                    {
                        // If error we display show page
                        SeanceDTO seance = _offerService.findSeanceById( bookingList.get( 0 ).getOffer( ).getId( ) );
                        UrlItem targetUrl = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_PORTAL );
                        targetUrl.addParameter( PARAMETER_PAGE, PAGE_TICKETING );
                        targetUrl.addParameter( PARAMETER_ACTION, ACTION_SHOW_DETAILS );
                        targetUrl.addParameter( PARAMETER_PRODUCT_ID, seance.getProduct( ).getId( ) );
                        return manageFunctionnalException( request, e, targetUrl.getUrl( ) );
                    }
                    else
                    {
                        SiteMessageService.setMessage( request, PurchaseService.MESSAGE_ERROR_PURCHASE_SESSION_EXPIRED,
                                SiteMessage.TYPE_ERROR, JSP_PORTAL );
                    }
                }
            }
            else
            {
                UnauthentifiedPurchaserDTO purchaser = new UnauthentifiedPurchaserDTO( );
                populate( purchaser, request );

                try
                {
                    validate( purchaser );
                    sendRequestNotification( bookingList, purchaser, request );
                    // Go to portal
                    UrlItem url = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_PORTAL );
                    returnUrl = url.getUrl( );
                }
                catch ( FunctionnalException e )
                {
                    UrlItem targetUrl = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_PORTAL );
                    targetUrl.addParameter( PARAMETER_PAGE, PAGE_BOOKING );
                    targetUrl.addParameter( PARAMETER_ACTION, ACTION_BOOK );
                    targetUrl.addParameter( PARAMETER_SHOW_ID, request.getParameter( PARAMETER_SHOW_ID ) );
                    targetUrl.addParameter( PARAMETER_SHOW_NAME, request.getParameter( PARAMETER_SHOW_NAME ) );
                    targetUrl.addParameter( PARAMETER_SEANCE_DATE, request.getParameter( PARAMETER_SEANCE_DATE ) );
                    return manageFunctionnalException( request, e, targetUrl.getUrl( ) );
                }
            }
        }
        return returnUrl;
    }

    /**
     * Action for cancel saving booking. Called by JSP.
     * 
     * @param request http request
     * @param response http response
     * @return url to go
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public String doCancelSaveReservation( HttpServletRequest request, HttpServletResponse response )
            throws UnsupportedEncodingException
    {
        List<ReservationDTO> bookingList = (List<ReservationDTO>) request.getSession( ).getAttribute(
                PARAMETER_BOOKING_LIST );
        String seanceDate = request.getParameter( PARAMETER_DATE_SEANCE );
        String showId = request.getParameter( PARAMETER_PRODUCT_ID );
        Integer nShowId;
        try
        {
            nShowId = Integer.valueOf( showId );
        }
        catch ( NumberFormatException e )
        {
            return AppPathService.getBaseUrl( request ) + JSP_PORTAL;
        }

        UrlItem targetUrl = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_PORTAL );
        targetUrl.addParameter( PARAMETER_PAGE, PAGE_TICKETING );
        targetUrl.addParameter( PARAMETER_ACTION, ACTION_SHOW_DETAILS );
        targetUrl.addParameter( PARAMETER_PRODUCT_ID, nShowId );
        targetUrl.addParameter( PARAMETER_DATE_SCEANCE, URLEncoder.encode( seanceDate, ENCODING_UTF_8 ) );

        if ( bookingList != null && !bookingList.isEmpty( ) )
        {
            try
            {
                _purchaseService.doCancelPurchaseList( bookingList, request.getSession( ).getId( ) );
            }
            catch ( FunctionnalException e )
            {
                // If error we display show page
                return manageFunctionnalException( request, e, targetUrl.getUrl( ) );
            }
        }

        return targetUrl.getUrl( );
    }

    /**
     * Returns page for managing user bookings.
     * 
     * @param page xpage
     * @param request http request
     * @param locale local
     * @return xpage
     * @throws UserNotSignedException the user not signed exception
     */
    private XPage getMyBookings( XPage page, HttpServletRequest request, Locale locale ) throws UserNotSignedException
    {
        LuteceUser user = getUser( request );
        if ( user == null )
        {
            throw new UserNotSignedException( );
        }

        // Get user bookings
        Date today = new Date( );
        PurchaseFilter purchaseFilter = new PurchaseFilter( );
        purchaseFilter.setUserName( user.getName( ) );
        purchaseFilter.setDateBeginOffer( new Timestamp( today.getTime( ) ) );

        // Dispatch booking list into two differents lists (old and to come)
        List<ReservationDTO> toComeBookingList = _purchaseService.findByFilter( purchaseFilter, null );

        purchaseFilter.setDateBeginOffer( null );
        purchaseFilter.setDateEndOffer( new Timestamp( today.getTime( ) ) );
        List<ReservationDTO> oldBookingList = _purchaseService.findByFilter( purchaseFilter,
                getPaginationProperties( request ) );

        // Generates template
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( PARAMETER_NEXT_BOOKING_LIST, toComeBookingList );
        model.put( PARAMETER_PAST_BOOKING_LIST, oldBookingList );

        model.put( MARK_USER, getUser( request ) );

        String strNbItemPerPage = request.getParameter( PARAMETER_NB_ITEMS_PER_PAGE );
        String strDefaultNbItemPerPage = DEFAULT_RESULTS_PER_PAGE;
        strNbItemPerPage = ( strNbItemPerPage != null ) ? strNbItemPerPage : strDefaultNbItemPerPage;

        int nNbItemsPerPage = Integer.parseInt( strNbItemPerPage );
        String strCurrentPageIndex = request.getParameter( PARAMETER_PAGE_INDEX );
        strCurrentPageIndex = ( strCurrentPageIndex != null ) ? strCurrentPageIndex : DEFAULT_PAGE_INDEX;

        UrlItem url = new UrlItem( "jsp/site/Portal.jsp?page=reservation&action=mes-reservations" );

        Paginator<ReservationDTO> paginator = new Paginator<ReservationDTO>( oldBookingList, nNbItemsPerPage,
                url.getUrl( ), PARAMETER_PAGE_INDEX, strCurrentPageIndex );

        model.put( MARK_PAGINATOR, paginator );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DIR + TEMPLATE_MY_BOOKINGS, locale, model );

        page.setContent( template.getHtml( ) );
        String pageTitle = getMessage( TITLE_MY_BOOKINGS, request );
        page.setPathLabel( pageTitle );
        page.setTitle( pageTitle );

        return page;
    }

    /**
     * Returns page for deleting user booking.
     * 
     * @param page xpage
     * @param request http request
     * @param locale local
     * @return xpage
     * @throws SiteMessageException the site message exception
     */
    public XPage getDeleteBooking( XPage page, HttpServletRequest request, Locale locale ) throws SiteMessageException
    {
        String purchaseId = request.getParameter( PARAMETER_PURCHASE_ID );
        if ( StringUtils.isEmpty( purchaseId ) || !StringUtils.isNumeric( purchaseId ) )
        {
            throw new TechnicalException( "Paramètre " + PARAMETER_SEANCE_ID + " invalide   : " + purchaseId );
        }

        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( PARAMETER_PURCHASE_ID, purchaseId );

        SiteMessageService.setMessage( request, MESSAGE_CONFIRM_DELETE_PURCHASE, null,
                MESSAGE_CONFIRM_DELETE_PURCHASE_TITLE, JSP_DO_DELETE_RESERVATION, null, SiteMessage.TYPE_CONFIRMATION,
                model );

        return null;
    }

    /**
     * Action for deleting booking. Called by JSP.
     * @param request http request
     * @param response http response
     * @return url to go
     */
    public String doDeleteReservation( HttpServletRequest request, HttpServletResponse response )
    {
        String purchaseId = request.getParameter( PARAMETER_PURCHASE_ID );
        if ( StringUtils.isEmpty( purchaseId ) || !StringUtils.isNumeric( purchaseId ) )
        {
            throw new TechnicalException( "Paramètre " + PARAMETER_SEANCE_ID + " invalide : " + purchaseId );
        }

        _purchaseService.doDeletePurchase( Integer.valueOf( purchaseId ) );

        UrlItem returnUrl = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_PORTAL );
        returnUrl.addParameter( PARAMETER_PAGE, PAGE_BOOKING );
        returnUrl.addParameter( PARAMETER_ACTION, ACTION_MY_BOOKINGS );
        return returnUrl.getUrl( );
    }

    /**
     * Send booking notification.
     * 
     * @param bookingList the booking list
     * @param request the request
     * @return the notification dto
     */
    private NotificationDTO sendBookingNotification( List<ReservationDTO> bookingList, HttpServletRequest request )
    {

        //Generate mail content
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( PARAMETER_BOOKING_LIST, bookingList );
        model.put( MARK_BASE_URL, AppPathService.getBaseUrl( request ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DIR + TEMPLATE_NOTIFICATION_BOOKING,
                request.getLocale( ), model );

        // Create mail object
        NotificationDTO notificationDTO = new NotificationDTO( );
        ReservationDTO reservation = bookingList.get( 0 );
        notificationDTO.setRecipientsTo( reservation.getEmailAgent( ) );
        notificationDTO.setSubject( getMessage( MESSAGE_NOTIFICATION_BOOKING_SUBJECT, request, reservation.getOffer( )
                .getName( ) ) );
        notificationDTO.setMessage( template.getHtml( ) );

        // Send it
        _notificationService.send( notificationDTO );

        return notificationDTO;
    }

    /**
     * Send request notification
     * 
     * @param bookingList the booking list
     * @param purchaser the purchaser
     * @param request the request
     * @return the notification dto
     */
    private NotificationDTO sendRequestNotification( List<ReservationDTO> bookingList,
            UnauthentifiedPurchaserDTO purchaser, HttpServletRequest request )
    {
        //Generate mail content
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( PARAMETER_BOOKING_LIST, bookingList );
        model.put( MARK_PURCHASER, purchaser );
        model.put( MARK_BASE_URL, AppPathService.getBaseUrl( request ) );
        model.put( PARAMETER_SEANCE_DATE, request.getParameter( PARAMETER_SEANCE_DATE ) );
        String showName = request.getParameter( PARAMETER_SHOW_NAME );
        model.put( PARAMETER_SHOW_NAME, showName );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_DIR + TEMPLATE_NOTIFICATION_REQUEST,
                request.getLocale( ), model );

        // Create mail object
        NotificationDTO notificationDTO = new NotificationDTO( );
        notificationDTO.setRecipientsTo( AppPropertiesService.getProperty( PROPERTY_NOTIFICATION_REQUEST_RECIPIENT ) );
        notificationDTO.setSubject( getMessage( MESSAGE_NOTIFICATION_REQUEST_SUBJECT, request, showName ) );
        notificationDTO.setMessage( template.getHtml( ) );

        // Send it
        _notificationService.send( notificationDTO );

        return notificationDTO;
    }
}