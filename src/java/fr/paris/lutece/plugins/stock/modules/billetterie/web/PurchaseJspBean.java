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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import fr.paris.lutece.plugins.stock.business.purchase.PurchaseFilter;
import fr.paris.lutece.plugins.stock.business.purchase.exception.PurchaseUnavailable;
import fr.paris.lutece.plugins.stock.commons.dao.PaginationProperties;
import fr.paris.lutece.plugins.stock.commons.exception.BusinessException;
import fr.paris.lutece.plugins.stock.commons.exception.FunctionnalException;
import fr.paris.lutece.plugins.stock.modules.billetterie.utils.constants.BilletterieConstants;
import fr.paris.lutece.plugins.stock.modules.tickets.business.NotificationDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ReservationDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ReservationFilter;
import fr.paris.lutece.plugins.stock.modules.tickets.business.SeanceDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.service.INotificationService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IPurchaseService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.ISeanceService;
import fr.paris.lutece.plugins.stock.modules.tickets.utils.constants.TicketsConstants;
import fr.paris.lutece.plugins.stock.service.IPurchaseSessionManager;
import fr.paris.lutece.plugins.stock.utils.DateUtils;
import fr.paris.lutece.plugins.stock.utils.ListUtils;
import fr.paris.lutece.plugins.stock.utils.NumberUtils;
import fr.paris.lutece.plugins.stock.utils.constants.StockConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.datatable.DataTableManager;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;


/**
 * This class provides the user interface to manage form features ( manage,
 * create, modify, remove)
 */
public class PurchaseJspBean extends AbstractJspBean
{
    /** The logger for this jspBean */
    public static final Logger LOGGER = Logger.getLogger( PurchaseJspBean.class );

    /** The constant String RESOURCE_TYPE */
    public static final String RESOURCE_TYPE = "STOCK";
    /** The constant String RIGHT_MANAGE_PURCHASES */
    public static final String RIGHT_MANAGE_PURCHASES = "PURCHASES_MANAGEMENT";

    // PARAMETERS
    /** The constant String PARAMETER_PURCHASE_ID */
    public static final String PARAMETER_PURCHASE_ID = "purchase_id";
    /** The constant String PARAMETER_PURCHASE_DUPLICATE */
    public static final String PARAMETER_PURCHASE_DUPLICATE = "duplicate";
    /** The constant String PARAMETER_PURCHASE_PRODUCT_NAME */
    public static final String PARAMETER_PURCHASE_PRODUCT_NAME = "productName";
    /** The constant String PARAMETER_PURCHASE_GENRE_LIST */
    public static final String PARAMETER_PURCHASE_GENRE_LIST = "purchase_genre_list";
    /** The constant String PARAMETER_PURCHASE_GENRE_LIST_DEFAULT */
    public static final String PARAMETER_PURCHASE_GENRE_LIST_DEFAULT = "purchase_genre_list_default";
    /** The constant String PARAMETER_BUTTON_DELETE */
    public static final String PARAMETER_BUTTON_DELETE = "delete";
    /** The constant String PARAMETER_FILTER_NAME */
    public static final String PARAMETER_FILTER_NAME = "filter_name";
    /** The constant String PARAMETER_FILTER_ID */
    public static final String PARAMETER_FILTER_ID = "filter_id";
    /** The constant String PARAMETER_FILTER_PARTNER_NAME */
    public static final String PARAMETER_FILTER_PARTNER_NAME = "filter_partner_name";
    /** The constant String PARAMETER_FILTER_PARTNER_NICKNAME */
    public static final String PARAMETER_FILTER_PARTNER_NICKNAME = "filter_partner_nickname";
    /** The constant String PARAMETER_FILTER_PURCHASE_TYPE */
    public static final String PARAMETER_FILTER_PURCHASE_TYPE = "filter_purchase_type";
    /** The constant String PARAMETER_FILTER_DATE_BEGIN */
    public static final String PARAMETER_FILTER_DATE_BEGIN = "filter_date_begin";
    /** The constant String PARAMETER_FILTER_DATE_END */
    public static final String PARAMETER_FILTER_DATE_END = "filter_date_end";
    /** The constant String PARAMETER_ORDER_BY_ID */
    public static final String PARAMETER_ORDER_BY_ID = "order_by_id";
    /** The constant String PARAMETER_ORDER_BY_LABEL */
    public static final String PARAMETER_ORDER_BY_LABEL = "order_by_label";
    /** The constant String PARAMETER_ORDER_BY_PLACE */
    public static final String PARAMETER_ORDER_BY_PLACE = "order_by_place";
    /** The constant String PARAMETER_ORDER_BY_TYPE */
    public static final String PARAMETER_ORDER_BY_TYPE = "order_by_type";
    /** The constant String PARAMETER_ORDER_ASC */
    public static final String PARAMETER_ORDER_ASC = "order_asc";
    /** The constant String PARAMETER_ORDER_DESC */
    public static final String PARAMETER_ORDER_DESC = "order_desc";
    /** The constant String PARAMETER_FILTER */
    public static final String PARAMETER_FILTER = "filter";
    // MARKS

    /** The constants for DataTableManager */
    public static final String MARK_DATA_TABLE_PURCHASE = "dataTablePurchase";
    public static final String MARK_FILTER_PURCHASE = "filterPurchase";
    public static final String MACRO_COLUMN_ACTIONS_PURCHASE = "columnActionsPurchase";
    public static final String MACRO_COLUMN_NAME_PURCHASE = "columnNamePurchase";
    public static final String MACRO_COLUMN_DATE_PURCHASE = "columnDatePurchase";
    public static final String MACRO_COLUMN_OFFER_TYPE_PURCHASE = "columnOfferTypePurchase";
    public static final String MACRO_COLUMN_AGENT_PURCHASE = "columnAgentPurchase";

    /** The constant String MARK_PURCHASE */
    public static final String MARK_PURCHASE = "purchase";
    /** The constant String MARK_TITLE */
    public static final String MARK_TITLE = "title";
    /** The constant String MARK_LOCALE */
    public static final String MARK_LOCALE = "locale";
    /** The constant String MARK_PURCHASE_STATUT_CANCEL */
    public static final String MARK_PURCHASE_STATUT_CANCEL = "strStatutCancel";
    /** The constant String MARK_CURRENT_DATE */
    public static final String MARK_CURRENT_DATE = "currentDate";
    /** The constant String MARK_LIST_OFFER_GENRE */
    public static final String MARK_LIST_OFFER_GENRE = "offerGenre_list";
    /** The constant String MARK_OFFER_ID */
    public static final String MARK_OFFER_ID = "offer_id";
    /** The constant String MARK_PURCHASSE_ID */
    public static final String MARK_PURCHASSE_ID = "purchase_id";
    /** The constant String MARK_QUANTITY_LIST */
    public static final String MARK_QUANTITY_LIST = "quantity_list";
    /** The constant String MARK_ERRORS */
    public static final String MARK_ERRORS = "errors";
    /** The constant String MARK_BASE_URL */
    public static final String MARK_BASE_URL = "base_url";
    // PROPERTIES
    /** The constant Integer NB_PLACES_MAX_INVITATION */
    public static final Integer NB_PLACES_MAX_INVITATION = AppPropertiesService.getPropertyInt(
            "stock-billetterie.nb_places_max.invitation", 2 );
    /** The constant Integer NB_PLACES_MAX_INVITATION_SPECTACLE */
    public static final Integer NB_PLACES_MAX_INVITATION_SPECTACLE = AppPropertiesService.getPropertyInt(
            "stock-billetterie.nb_places_max.invitation_enfant", 2 );
    /** The constant Integer NB_PLACES_MAX_TARIF_REDUIT */
    public static final Integer NB_PLACES_MAX_TARIF_REDUIT = AppPropertiesService.getPropertyInt(
            "stock-billetterie.nb_places_max.tarif_reduit", 2 );

    // BEANS
    private static final String BEAN_STOCK_TICKETS_SEANCE_SERVICE = "stock-tickets.seanceService";

    // JSP
    private static final String JSP_MANAGE_PURCHASES = "jsp/admin/plugins/stock/modules/billetterie/ManagePurchase.jsp";
    private static final String JSP_DO_DELETE_PURCHASE = "jsp/admin/plugins/stock/modules/billetterie/DoDeletePurchase.jsp";
    private static final String JSP_MANAGE_OFFERS = "jsp/admin/plugins/stock/modules/billetterie/ManageOffers.jsp";
    private static final String JSP_SAVE_PURCHASE = "SavePurchase.jsp";
    private static final String JSP_DO_NOTIFY_PURCHASE = "jsp/admin/plugins/stock/modules/billetterie/DoNotifyPurchase.jsp";

    // TEMPLATES
    private static final String TEMPLATE_MANAGE_PURCHASES = "admin/plugins/stock/modules/billetterie/manage_purchases.html";
    private static final String TEMPLATE_SAVE_PURCHASE = "admin/plugins/stock/modules/billetterie/save_purchase.html";
    private static final String TEMPLATE_NOTIFICATION_BOOKING = "admin/plugins/stock/modules/billetterie/notification_booking.html";

    // PAGE TITLES
    private static final String PROPERTY_PAGE_TITLE_MANAGE_PURCHASE = "module.stock.billetterie.list_purchase.title";
    private static final String PROPERTY_PAGE_TITLE_CREATE_PURCHASE = "module.stock.billetterie.create_purchase.title";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_PURCHASE = "module.stock.billetterie.modify_purchase.title";

    // MESSAGES
    private static final String MESSAGE_CONFIRMATION_DELETE_PURCHASE = "module.stock.billetterie.message.deletePurchase.confirmation";
    private static final String MESSAGE_INSUFFICIENT_PLACE_REMAINING = "module.stock.billetterie.message.error.insufficient_place_remaining";
    private static final String MESSAGE_TITLE_CONFIRMATION_DELETE_PURCHASE = "module.stock.billetterie.message.title.deletePurchase.confirmation";
    private static final String MESSAGE_SEARCH_OFFER_DATE = "module.stock.billetterie.message.search.offer.date";
    private static final String MESSAGE_SEARCH_PURCHASE_DATE = "module.stock.billetterie.message.search.purchase.date";
    private static final String MESSAGE_NOTIFICATION_BOOKING_SUBJECT = "module.stock.billetterie.notification.booking.subject";
    private static final String MESSAGE_NOTIFY_PURCHASE_CONFIRMATION = "module.stock.billetterie.message.notifyPurchase.confirmation";
    private static final String MESSAGE_TITLE_NOTIFY_PURCHASE_CONFIRMATION = "module.stock.billetterie.message.title.notifyPurchase.confirmation";

    // ORDER FILTERS
    private static final String ORDER_FILTER_DATE = "date";
    private static final String ORDER_FILTER_OFFER_TYPE_NAME = "offer.type.name";
    private static final String ORDER_FILTER_OFFER_DATE = "offer.date";
    private static final String ORDER_FILTER_OFFER_PRODUCT_NAME = "offer.product.name";

    // MEMBERS VARIABLES
    // @Inject
    private IPurchaseService _servicePurchase;
    // @Inject
    // @Named( "stock-tickets.seanceService" )
    private ISeanceService _serviceOffer;
    // @Inject
    private IPurchaseSessionManager _purchaseSessionManager;

    private ReservationFilter _purchaseFilter;

    private INotificationService _serviceNotification;

    /**
     * Instantiates a new purchase jsp bean.
     */
    public PurchaseJspBean( )
    {
        super( );

        _purchaseFilter = new ReservationFilter( );
        _servicePurchase = SpringContextService.getContext( ).getBean( IPurchaseService.class );
        _serviceOffer = (ISeanceService) SpringContextService.getBean( BEAN_STOCK_TICKETS_SEANCE_SERVICE );
        _purchaseSessionManager = SpringContextService.getContext( ).getBean( IPurchaseSessionManager.class );
        _serviceNotification = SpringContextService.getContext( ).getBean( INotificationService.class );
    }

    /**
     * Builds the filter.
     * 
     * @param filter the filter
     * @param request the request
     */
    protected void buildFilter( ReservationFilter filter, HttpServletRequest request )
    {
        populate( filter, request );
    }

    /**
     * Gets the purchase filter.
     * 
     * @param request the request
     * @return the purchase filter
     */
    private ReservationFilter getPurchaseFilter( HttpServletRequest request )
    {
        // SORT
        String strSortedAttributeName = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );

        // "filter" in request  ==> new filter. use old one otherwise
        if ( request.getParameter( TicketsConstants.PARAMETER_FILTER ) != null )
        {
            ReservationFilter filter = new ReservationFilter( );
            buildFilter( filter, request );
            _purchaseFilter = filter;
        }

        if ( strSortedAttributeName != null )
        {
            _purchaseFilter.getOrders( ).add( strSortedAttributeName );

            String strAscSort = request.getParameter( Parameters.SORTED_ASC );
            boolean bIsAscSort = Boolean.parseBoolean( strAscSort );
            _purchaseFilter.setOrderAsc( bIsAscSort );
        }

        return _purchaseFilter;
    }

    /**
     * Get the manage purchases page
     * 
     * @param request the request
     * @return the page with purchases list
     */
    public String getManagePurchases( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_PURCHASE );

        Map<String, Object> model = new HashMap<String, Object>( );
        ReservationFilter filter = getPurchaseFilter( request );

        // if date begin is after date end, add error
        List<String> error = new ArrayList<String>( );
        if ( filter.getDateBeginOffer( ) != null && filter.getDateEndOffer( ) != null
                && filter.getDateBeginOffer( ).after( filter.getDateEndOffer( ) ) )
        {
            error.add( I18nService.getLocalizedString( MESSAGE_SEARCH_OFFER_DATE, request.getLocale( ) ) );
        }
        if ( filter.getDateBegin( ) != null && filter.getDateEnd( ) != null
                && filter.getDateBegin( ).after( filter.getDateEnd( ) ) )
        {
            error.add( I18nService.getLocalizedString( MESSAGE_SEARCH_PURCHASE_DATE, request.getLocale( ) ) );
        }
        model.put( MARK_ERRORS, error );

        // OrderList for purchase list
        List<String> orderList = new ArrayList<String>( );
        orderList.add( ORDER_FILTER_DATE );
        orderList.add( ORDER_FILTER_OFFER_PRODUCT_NAME );
        orderList.add( ORDER_FILTER_OFFER_DATE );
        orderList.add( ORDER_FILTER_OFFER_TYPE_NAME );
        filter.setOrders( orderList );
        filter.setOrderAsc( true );

        String strOfferId = request.getParameter( MARK_OFFER_ID );
        String purchaseId = request.getParameter( MARK_PURCHASSE_ID );
        if ( strOfferId != null )
        {
            SeanceDTO seance = this._serviceOffer.findSeanceById( Integer.parseInt( strOfferId ) );
            if ( StringUtils.isNotEmpty( purchaseId ) && NumberUtils.validateInt( purchaseId ) )
            {
                ReservationDTO reservation = this._servicePurchase.findById( Integer.parseInt( purchaseId ) );
                filter.setUserName( reservation.getNameAgent( ) );
            }
            filter.setIdOffer( seance.getId( ) );
            filter.setProductName( seance.getProduct( ).getName( ) );
            filter.setIdGenre( seance.getIdGenre( ) );
            filter.setDateBeginOffer( DateUtils.getDate( seance.getDate( ), false ) );
            filter.setDateEndOffer( DateUtils.getDate( seance.getDate( ), false ) );
        }

        //Obtention des objets sauvegardés en session
        DataTableManager<ReservationDTO> dataTableToUse = getDataTable( request, filter );
        model.put( MARK_DATA_TABLE_PURCHASE, dataTableToUse );

        // Fill the model
        model.put( MARK_LOCALE, getLocale( ) );

        // Combo
        ReferenceList offerGenreComboList = ListUtils.toReferenceList( _serviceOffer.findAllGenre( ),
                BilletterieConstants.ID, BilletterieConstants.NAME, StockConstants.EMPTY_STRING );
        model.put( MARK_LIST_OFFER_GENRE, offerGenreComboList );
        // offer statut cancel
        model.put( MARK_PURCHASE_STATUT_CANCEL, TicketsConstants.OFFER_STATUT_CANCEL );
        // the filter
        model.put( TicketsConstants.MARK_FILTER, filter );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_PURCHASES, getLocale( ), model );
        //opération nécessaire pour eviter les fuites de mémoires
        dataTableToUse.clearItems( );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Get the DataTableManager object for the ReservationDTO bean
     * @param request the http request
     * @param filter the filter
     * @param <T> the type of the bean
     * @return the data table to use
     */
    private <T> DataTableManager<T> getDataTable( HttpServletRequest request, PurchaseFilter filter )
    {
        //si un objet est déjà présent en session, on l'utilise
        Method findMethod = null;
        try
        {
            findMethod = _servicePurchase.getClass( ).getMethod( PARAMETER_FIND_BY_FILTER_NAME_METHOD,
                    PurchaseFilter.class, PaginationProperties.class );
        }
        catch ( Exception e )
        {
            LOGGER.error( "Erreur lors de l'obtention du data table : ", e );
        }
        DataTableManager<T> dataTableToUse = getAbstractDataTableManager( request, filter, MARK_DATA_TABLE_PURCHASE,
                JSP_MANAGE_PURCHASES, _servicePurchase, findMethod );

        //si pas d'objet en session, il faut ajouter les colonnes à afficher
        if ( dataTableToUse.getListColumn( ).isEmpty( ) )
        {
            dataTableToUse.addFreeColumn( "module.stock.billetterie.list_purchase.table.product",
                    MACRO_COLUMN_NAME_PURCHASE );
            dataTableToUse.addFreeColumn( "module.stock.billetterie.list_purchase.table.dateOffer",
                    MACRO_COLUMN_DATE_PURCHASE);
            dataTableToUse.addColumn( "module.stock.billetterie.list_purchase.table.datePurchase", "date", false );
            dataTableToUse.addFreeColumn( "module.stock.billetterie.list_purchase.table.typeOffer",
                    MACRO_COLUMN_OFFER_TYPE_PURCHASE );
            dataTableToUse.addFreeColumn( "module.stock.billetterie.list_purchase.table.userName",
                    MACRO_COLUMN_AGENT_PURCHASE );
            dataTableToUse.addColumn( "module.stock.billetterie.list_purchase.table.quantity", "quantity", false );
            dataTableToUse.addFreeColumn( "module.stock.billetterie.list_purchase.table.actions",
                    MACRO_COLUMN_ACTIONS_PURCHASE);
        }
        saveDataTableInSession( request, dataTableToUse, MARK_DATA_TABLE_PURCHASE );
        return dataTableToUse;
    }

    /**
     * Returns the form for purchase creation and modification
     * @param request The HTTP request
     * @return HTML Form
     */
    public String getSavePurchase( HttpServletRequest request, HttpServletResponse response )
    {
        ReservationDTO purchase = null;
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_LOCALE, getLocale( ) );

        String strIdOffer;
        String strIdPurchase = request.getParameter( MARK_PURCHASSE_ID );

        // Manage validation errors
        FunctionnalException ve = getErrorOnce( request );
        if ( ve != null )
        {
            purchase = (ReservationDTO) ve.getBean( );
            model.put( BilletterieConstants.ERROR, getHtmlError( ve ) );
            strIdOffer = purchase.getOffer( ).getId( ).toString( );
        }
        else
        {
            strIdOffer = request.getParameter( MARK_OFFER_ID );
            setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE_PURCHASE );
            // Create new Purchase
            purchase = new ReservationDTO( );
        }

        // modification page only
        if ( strIdPurchase != null )
        {
            Integer nIdPurchase = Integer.parseInt( strIdPurchase );

            //get the info of the purchase
            purchase = _servicePurchase.findById( nIdPurchase );

            strIdOffer = purchase.getOfferId( ).toString( );

            model.put( MARK_PURCHASSE_ID, nIdPurchase );

        }

        if ( strIdOffer != null )
        {
            Integer idOffer = Integer.parseInt( strIdOffer );
            SeanceDTO seance = this._serviceOffer.findSeanceById( idOffer );
            purchase.setOffer( seance );

            Integer quantity;
            if ( seance.getTypeName( ).equals( TicketsConstants.OFFER_TYPE_INVITATION ) )
            {
                quantity = NB_PLACES_MAX_INVITATION;
            }
            else if ( seance.getTypeName( ).equals( TicketsConstants.OFFER_TYPE_INVITATION_SPECTACLE_ENFANT ) )
            {
                quantity = NB_PLACES_MAX_INVITATION_SPECTACLE;
            }
            else
            {
                quantity = NB_PLACES_MAX_TARIF_REDUIT;
            }
            // Libère la réservation prévue sur la page de réservation
            _purchaseSessionManager.release( request.getSession( ).getId( ), purchase );
            // Update quantity with quantity in session for this offer
            if ( quantity > seance.getQuantity( ) )
            {
                quantity = seance.getQuantity( );
            }
            quantity = _purchaseSessionManager.updateQuantityWithSession( quantity, idOffer );
            seance.setQuantity( quantity );

            ReferenceList quantityList = new ReferenceList( );
            for ( Integer i = 1; i <= quantity; i++ )
            {
                ReferenceItem refItem = new ReferenceItem( );
                refItem.setCode( i.toString( ) );
                refItem.setName( i.toString( ) );
                quantityList.add( refItem );
            }
            model.put( MARK_QUANTITY_LIST, quantityList );

            // Reserve tickets
            try
            {
                if ( quantity == 0 )
                {
                    throw new PurchaseUnavailable( purchase.getId( ), MESSAGE_INSUFFICIENT_PLACE_REMAINING );
                }

                if ( purchase.getQuantity( ) == null )
                {
                    purchase.setQuantity( quantity );
                }
                _purchaseSessionManager.reserve( request.getSession( ).getId( ), purchase );
            }
            catch ( PurchaseUnavailable e )
            {
                try
                {
                    response.sendRedirect( AdminMessageService.getMessageUrl( request,
                            MESSAGE_INSUFFICIENT_PLACE_REMAINING, AdminMessage.TYPE_STOP ) );
                }
                catch ( IOException e1 )
                {
                    AppLogService.error( e1 );
                }
                return null;
            }
        }

        // Add the JSP wich called this action
        model.put( StockConstants.MARK_JSP_BACK, JSP_MANAGE_OFFERS );
        model.put( MARK_PURCHASE, purchase );

        if ( strIdPurchase != null || purchase.getId( ) != null )
        {
            setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY_PURCHASE );
            model.put( MARK_TITLE,
                    I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_MODIFY_PURCHASE, Locale.getDefault( ) ) );
        }
        else
        {
            model.put( MARK_TITLE,
                    I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_CREATE_PURCHASE, Locale.getDefault( ) ) );

        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SAVE_PURCHASE, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }

    /**
     * Save a purchase
     * @param request The HTTP request
     * @return redirection url
     */
    public String doSavePurchase( HttpServletRequest request )
    {
        ReservationDTO purchase = new ReservationDTO( );
        String strIdPurchase = request.getParameter( PARAMETER_PURCHASE_ID );

        if ( null != request.getParameter( StockConstants.PARAMETER_BUTTON_CANCEL ) )
        {
            if ( strIdPurchase != null )
            {

                return AppPathService.getBaseUrl( request ) + JSP_MANAGE_PURCHASES;

            }
            else
            {
                return doCancelPurchase( request, purchase );
            }

        }

        if ( StringUtils.isNotBlank( strIdPurchase ) )
        {
            //case of modification
            Integer nIdPurchase = Integer.parseInt( strIdPurchase );
            purchase = _servicePurchase.findById( nIdPurchase );
            populate( purchase, request );

            _servicePurchase.update( purchase );
        }
        else
        {
            //case of creation
            populate( purchase, request );
            purchase.setDate( DateUtils.getCurrentDateString( ) );
        }
        try
        {
            // Controls mandatory fields
            validateBilletterie( purchase );

            if ( StringUtils.isBlank( strIdPurchase ) )
            {
                try
                {

                    // Reserve tickets
                    // Libère la réservation prévue sur la page de réservation
                    _purchaseSessionManager.release( request.getSession( ).getId( ), purchase );

                    // Réserve avec les nouvelles valeurs saisies par l'utilisateur
                    _purchaseSessionManager.reserve( request.getSession( ).getId( ), purchase );

                }
                catch ( PurchaseUnavailable e )
                {
                    throw new BusinessException( purchase, MESSAGE_INSUFFICIENT_PLACE_REMAINING );
                }

                _servicePurchase.doSavePurchase( purchase, request.getSession( ).getId( ) );
            }

        }
        catch ( FunctionnalException e )
        {
            return manageFunctionnalException( request, e, JSP_SAVE_PURCHASE );
        }

        UrlItem redirection = new UrlItem( AppPathService.getBaseUrl( request ) + JSP_MANAGE_PURCHASES );
        redirection.addParameter( MARK_OFFER_ID, purchase.getOffer( ).getId( ) );
        redirection.addParameter( MARK_PURCHASSE_ID, purchase.getId( ) );

        return redirection.getUrl( );
    }

    /**
     * Return the url of the JSP which called the last action and release
     * reservation from session
     * @param request The Http request
     * @param purchase the purchase to cancel
     * @return The url of the last JSP
     */
    private String doCancelPurchase( HttpServletRequest request, ReservationDTO purchase )
    {
        // Libère la réservation prévue sur la page de réservation
        _purchaseSessionManager.release( request.getSession( ).getId( ), purchase );

        String strJspBack = request.getParameter( StockConstants.MARK_JSP_BACK );

        return StringUtils.isNotBlank( strJspBack ) ? ( AppPathService.getBaseUrl( request ) + strJspBack )
                : AppPathService.getBaseUrl( request ) + JSP_MANAGE_PURCHASES;
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
                : AppPathService.getBaseUrl( request ) + JSP_MANAGE_PURCHASES;
    }

    /**
     * Returns the confirmation message to delete an purchase
     * 
     * @param request The Http request
     * @return the html code message
     */
    public String getDeletePurchase( HttpServletRequest request )
    {
        String strPurchaseId = request.getParameter( PARAMETER_PURCHASE_ID );

        Integer nIdPurchase;

        try
        {
            nIdPurchase = Integer.parseInt( strPurchaseId );
        }
        catch ( NumberFormatException e )
        {
            LOGGER.debug( e );

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR,
                    AdminMessage.TYPE_STOP );
        }

        Map<String, Object> urlParam = new HashMap<String, Object>( );
        urlParam.put( PARAMETER_PURCHASE_ID, nIdPurchase );

        String strJspBack = JSP_MANAGE_PURCHASES;

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_DELETE_PURCHASE, null,
                MESSAGE_TITLE_CONFIRMATION_DELETE_PURCHASE, JSP_DO_DELETE_PURCHASE, BilletterieConstants.TARGET_SELF,
                AdminMessage.TYPE_CONFIRMATION, urlParam, strJspBack );
    }

    /**
     * Delete an purchase
     * 
     * @param request The Http request
     * @return the html code message
     */
    public String doDeletePurchase( HttpServletRequest request )
    {
        String strPurchaseId = request.getParameter( PARAMETER_PURCHASE_ID );

        int nIdPurchase;

        try
        {
            nIdPurchase = Integer.parseInt( strPurchaseId );
        }
        catch ( NumberFormatException e )
        {
            LOGGER.debug( e );

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR,
                    AdminMessage.TYPE_STOP );
        }

        _servicePurchase.doDeletePurchase( nIdPurchase );

        return doGoBack( request );
    }

    /**
     * Returns the confirmation message to notify purchaser
     * 
     * @param request The Http request
     * @return the html code message
     */
    public String getNotifyPurchase( HttpServletRequest request )
    {

        String strPurchaseId = request.getParameter( PARAMETER_PURCHASE_ID );

        Integer nIdPurchase;

        try
        {
            nIdPurchase = Integer.parseInt( strPurchaseId );
        }
        catch ( NumberFormatException e )
        {
            LOGGER.debug( e );

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR,
                    AdminMessage.TYPE_STOP );
        }
        ReservationDTO purchase = _servicePurchase.findById( nIdPurchase );

        Object[] args = new Object[] { purchase.getEmailAgent( ), purchase.getOffer( ).getProduct( ).getName( ),
                purchase.getOffer( ).getDate( ), purchase.getOffer( ).getHour( ), purchase.getOffer( ).getTypeName( ),
                purchase.getQuantity( ), };

        Map<String, Object> urlParam = new HashMap<String, Object>( );
        urlParam.put( PARAMETER_PURCHASE_ID, nIdPurchase );

        String strJspBack = JSP_MANAGE_PURCHASES;

        return AdminMessageService.getMessageUrl( request, MESSAGE_NOTIFY_PURCHASE_CONFIRMATION, args,
                MESSAGE_TITLE_NOTIFY_PURCHASE_CONFIRMATION, JSP_DO_NOTIFY_PURCHASE, BilletterieConstants.TARGET_SELF,
                AdminMessage.TYPE_CONFIRMATION, urlParam, strJspBack );
    }

    /**
     * Send booking notification.
     * 
     * @param request The Http request
     * @return the html code message
     */
    public String doNotifyPurchase( HttpServletRequest request )
    {
        String strPurchaseId = request.getParameter( PARAMETER_PURCHASE_ID );

        Integer nIdPurchase;

        try
        {
            nIdPurchase = Integer.parseInt( strPurchaseId );
        }
        catch ( NumberFormatException e )
        {
            LOGGER.debug( e );

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR,
                    AdminMessage.TYPE_STOP );
        }

        ReservationDTO purchase = _servicePurchase.findById( nIdPurchase );

        //Generate mail content
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_PURCHASE, purchase );
        model.put( MARK_BASE_URL, AppPathService.getBaseUrl( request ) );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_NOTIFICATION_BOOKING, request.getLocale( ),
                model );

        // Create mail object
        NotificationDTO notificationDTO = new NotificationDTO( );
        notificationDTO.setRecipientsTo( purchase.getEmailAgent( ) );
        String[] args = new String[] { purchase.getOffer( ).getName( ), };
        notificationDTO.setSubject( I18nService.getLocalizedString( MESSAGE_NOTIFICATION_BOOKING_SUBJECT, args,
                request.getLocale( ) ) );
        notificationDTO.setMessage( template.getHtml( ) );

        // Send it
        _serviceNotification.send( notificationDTO );

        return doGoBack( request );
    }
}
