/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.plugins.stock.business.purchase.exception.PurchaseUnavailable;
import fr.paris.lutece.plugins.stock.commons.ResultList;
import fr.paris.lutece.plugins.stock.commons.exception.BusinessException;
import fr.paris.lutece.plugins.stock.commons.exception.FunctionnalException;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ReservationDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ReservationFilter;
import fr.paris.lutece.plugins.stock.modules.tickets.business.SeanceDTO;
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
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.DelegatePaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


/**
 * This class provides the user interface to manage form features ( manage,
 * create, modify, remove)
 */
public class PurchaseJspBean  extends AbstractJspBean
{
    public static final Logger LOGGER = Logger.getLogger( PurchaseJspBean.class );
    
    public static final String RESOURCE_TYPE = "STOCK";
    public static final String RIGHT_MANAGE_PURCHASES = "PURCHASES_MANAGEMENT";

    // PARAMETERS
    public static final String PARAMETER_PURCHASE_ID = "purchase_id";
    public static final String PARAMETER_PURCHASE_DUPLICATE = "duplicate";
    public static final String PARAMETER_PURCHASE_PRODUCT_NAME = "productName";
    public static final String PARAMETER_PURCHASE_GENRE_LIST = "purchase_genre_list";
    public static final String PARAMETER_PURCHASE_GENRE_LIST_DEFAULT = "purchase_genre_list_default";
    public static final String PARAMETER_BUTTON_DELETE = "delete";
    public static final String PARAMETER_FILTER_NAME = "filter_name";
    public static final String PARAMETER_FILTER_ID = "filter_id";
    public static final String PARAMETER_FILTER_PARTNER_NAME = "filter_partner_name";
    public static final String PARAMETER_FILTER_PARTNER_NICKNAME = "filter_partner_nickname";
    public static final String PARAMETER_FILTER_PURCHASE_TYPE = "filter_purchase_type";
    public static final String PARAMETER_FILTER_DATE_BEGIN = "filter_date_begin";
    public static final String PARAMETER_FILTER_DATE_END = "filter_date_end";
    public static final String PARAMETER_ORDER_BY_ID = "order_by_id";
    public static final String PARAMETER_ORDER_BY_LABEL = "order_by_label";
    public static final String PARAMETER_ORDER_BY_PLACE = "order_by_place";
    public static final String PARAMETER_ORDER_BY_TYPE = "order_by_type";
    public static final String PARAMETER_ORDER_ASC = "order_asc";
    public static final String PARAMETER_ORDER_DESC = "order_desc";
    public static final String PARAMETER_FILTER = "filter";
    // MARKS
    public static final String MARK_PURCHASE = "purchase";
    public static final String MARK_TITLE = "title";
    public static final String MARK_LOCALE = "locale";
    public static final String MARK_PURCHASE_STATUT_CANCEL = "strStatutCancel";
    public static final String MARK_CURRENT_DATE = "currentDate";
    public static final String MARK_LIST_OFFER_GENRE = "offerGenre_list";
    public static final String MARK_OFFER_ID = "offer_id";
    public static final String MARK_PURCHASSE_ID = "purchase_id";
    public static final String MARK_QUANTITY_LIST = "quantity_list";
    public static final String MARK_ERRORS = "errors";
    // PROPERTIES
    public static final Integer NB_PLACES_MAX_INVITATION = AppPropertiesService.getPropertyInt(
            "stock-billetterie.nb_places_max.invitation", 2 );
    public static final Integer NB_PLACES_MAX_INVITATION_SPECTACLE = AppPropertiesService.getPropertyInt(
            "stock-billetterie.nb_places_max.invitation_enfant", 2 );
    public static final Integer NB_PLACES_MAX_TARIF_REDUIT = AppPropertiesService.getPropertyInt(
            "stock-billetterie.nb_places_max.tarif_reduit", 2 );

    private static final String MARK_LIST_PURCHASES = "list_purchases";
    // JSP
    private static final String JSP_MANAGE_PURCHASES = "jsp/admin/plugins/stock/modules/billetterie/ManagePurchase.jsp";
    private static final String JSP_DO_DELETE_PURCHASE = "jsp/admin/plugins/stock/modules/billetterie/DoDeletePurchase.jsp";
    private static final String JSP_MANAGE_OFFERS = "jsp/admin/plugins/stock/modules/billetterie/ManageOffers.jsp";
    
    // TEMPLATES
    private static final String TEMPLATE_MANAGE_PURCHASES = "admin/plugins/stock/modules/billetterie/manage_purchases.html";
    private static final String TEMPLATE_SAVE_PURCHASE = "admin/plugins/stock/modules/billetterie/save_purchase.html";


    // PAGE TITLES
    private static final String PROPERTY_PAGE_TITLE_MANAGE_PURCHASE = "module.stock.billetterie.list_purchase.title";
    private static final String PROPERTY_PAGE_TITLE_CREATE_PURCHASE = "module.stock.billetterie.create_purchase.title";
    
    
    // MESSAGES
    private static final String MESSAGE_CONFIRMATION_DELETE_PURCHASE = "module.stock.billetterie.message.deletePurchase.confirmation";
    private static final String MESSAGE_INSUFFICIENT_PLACE_REMAINING = "module.stock.billetterie.message.error.insufficient_place_remaining";
    private static final String MESSAGE_TITLE_CONFIRMATION_DELETE_PURCHASE = "module.stock.billetterie.message.title.deletePurchase.confirmation";
    private static final String MESSAGE_SEARCH_OFFER_DATE = "module.stock.billetterie.message.search.offer.date";
    private static final String MESSAGE_SEARCH_PURCHASE_DATE = "module.stock.billetterie.message.search.purchase.date";
    
    // MEMBERS VARIABLES
    // Paginator
    private int _nDefaultItemsPerPage;
    private String _strCurrentPageIndex;
    private int _nItemsPerPage;
    // @Inject
    private IPurchaseService _servicePurchase;
    // @Inject
    // @Named( "stock-tickets.seanceService" )
    private ISeanceService _serviceOffer;
    // @Inject
    private IPurchaseSessionManager _purchaseSessionManager;

    private ReservationFilter _purchaseFilter;

    /**
     * Instantiates a new purchase jsp bean.
     */
    public PurchaseJspBean(  )
    {
        super(  );

        _purchaseFilter = new ReservationFilter( );
        _servicePurchase = SpringContextService.getContext( ).getBean( IPurchaseService.class );
        _serviceOffer = (ISeanceService) SpringContextService.getBean( "stock-tickets.seanceService" );
        _purchaseSessionManager = SpringContextService.getContext( ).getBean( IPurchaseSessionManager.class );
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
     * @param request
     *            the request
     * @return the page with purchases list
     */
    public String getManagePurchases( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_PURCHASE );

        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nDefaultItemsPerPage = AppPropertiesService.getPropertyInt( TicketsConstants.PROPERTY_DEFAULT_ITEM_PER_PAGE,
                50 );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                _nDefaultItemsPerPage );

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
        orderList.add( "date" );
        orderList.add( "offer.product.name" );
        orderList.add( "offer.date" );
        orderList.add( "offer.type.name" );
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
                filter.setUserName( reservation.getUserName( ) );
            }
        	filter.setIdOffer( seance.getId( ) );
        	filter.setProductName( seance.getProduct( ).getName( ) );
        	filter.setIdGenre( seance.getIdGenre( ) );
        	filter.setDateBeginOffer( DateUtils.getDate( seance.getDate( ), false ) );
        	filter.setDateEndOffer( DateUtils.getDate( seance.getDate( ), false ) );
        }

        ResultList<ReservationDTO> listAllPurchase = _servicePurchase
                .findByFilter( filter, getPaginationProperties( request ) );

        DelegatePaginator<ReservationDTO> paginator = getPaginator( request, listAllPurchase );

        // Fill the model
        model.put( MARK_LOCALE, getLocale( ) );

        // the paginator
        model.put( TicketsConstants.MARK_NB_ITEMS_PER_PAGE, "" + _nItemsPerPage );
        model.put( TicketsConstants.MARK_PAGINATOR, paginator );
        model.put( MARK_LIST_PURCHASES, paginator.getPageItems(  ) );
        // Combo
        ReferenceList offerGenreComboList = ListUtils.toReferenceList( _serviceOffer.findAllGenre( ), "id", "name",
                StockConstants.EMPTY_STRING );
        model.put( MARK_LIST_OFFER_GENRE, offerGenreComboList );
        // offer statut cancel
        model.put( MARK_PURCHASE_STATUT_CANCEL, TicketsConstants.OFFER_STATUT_CANCEL );
        // the filter
        model.put( TicketsConstants.MARK_FILTER, filter );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_PURCHASES, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Returns the form for purchase creation and modification
     * @param request The HTTP request
     * @return HTML Form
     */
    public String getSavePurchase( HttpServletRequest request )
    {
        ReservationDTO purchase = null;
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_LOCALE, getLocale( ) );

        String strIdOffer;
        
        // Manage validation errors
        FunctionnalException ve = getErrorOnce( request );
        if ( ve != null )
        {
            purchase = (ReservationDTO) ve.getBean( );
            model.put( "error", getHtmlError( ve ) );
            strIdOffer = purchase.getOffer( ).getId( ).toString( );
        }
        else
        {
        	strIdOffer = request.getParameter( MARK_OFFER_ID );
            setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE_PURCHASE );
            // Create new Purchase
            purchase = new ReservationDTO( );
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
    		ReferenceList quantityList = new ReferenceList( );
    		for ( Integer i = 1; i <= quantity; i++ )
    		{
        		ReferenceItem refItem = new ReferenceItem( );
        		refItem.setCode( i.toString( ) );
        		refItem.setName( i.toString( ) );
        		quantityList.add( refItem );
    		}
    		model.put( MARK_QUANTITY_LIST, quantityList );
        }

        // Add the JSP wich called this action
        model.put( StockConstants.MARK_JSP_BACK, JSP_MANAGE_OFFERS );
        model.put( MARK_PURCHASE, purchase );
        model.put( MARK_TITLE, I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_CREATE_PURCHASE, Locale.getDefault( ) ) );

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
        if ( StringUtils.isNotBlank( request.getParameter( "cancel" ) ) )
        {
            return doGoBack( request );
        }

        ReservationDTO purchase = new ReservationDTO( );
        populate( purchase, request );
        purchase.setDate( DateUtils.getCurrentDateString( ) );

        try
        {
            // Controls mandatory fields
            validate( purchase );
            // Reserve tickets
            try
            {
                _purchaseSessionManager.reserve( request.getSession( ).getId( ), purchase );
            }
            catch ( PurchaseUnavailable e )
            {
                throw new BusinessException( purchase, MESSAGE_INSUFFICIENT_PLACE_REMAINING );
            }
            
            ReservationDTO saveReservation = _servicePurchase.doSavePurchase( purchase, request.getSession( ).getId( ) );

        }
        catch ( FunctionnalException e )
        {
            return manageFunctionnalException( request, e, "SavePurchase.jsp" );
        }

        String strArg = "?" + MARK_OFFER_ID + "=" + purchase.getOffer( ).getId( );
        String strArgum = "&" + MARK_PURCHASSE_ID + "=" + purchase.getId( );
        
        return AppPathService.getBaseUrl( request ) + JSP_MANAGE_PURCHASES + strArg + strArgum;
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

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR, AdminMessage.TYPE_STOP );
        }

        Map<String, Object> urlParam = new HashMap<String, Object>( );
        urlParam.put( PARAMETER_PURCHASE_ID, nIdPurchase );

        String strJspBack = JSP_MANAGE_PURCHASES;

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_DELETE_PURCHASE, null,
                MESSAGE_TITLE_CONFIRMATION_DELETE_PURCHASE, JSP_DO_DELETE_PURCHASE, "_self",
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
}
