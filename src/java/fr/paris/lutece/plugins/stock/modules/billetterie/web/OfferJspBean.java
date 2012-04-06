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

import fr.paris.lutece.plugins.stock.business.offer.OfferFilter;
import fr.paris.lutece.plugins.stock.business.product.ProductFilter;
import fr.paris.lutece.plugins.stock.business.purchase.PurchaseFilter;
import fr.paris.lutece.plugins.stock.commons.ResultList;
import fr.paris.lutece.plugins.stock.commons.exception.FunctionnalException;
import fr.paris.lutece.plugins.stock.modules.billetterie.utils.constants.BilletterieConstants;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ReservationDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.SeanceDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.SeanceFilter;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ShowDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IPurchaseService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.ISeanceService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IShowService;
import fr.paris.lutece.plugins.stock.modules.tickets.utils.constants.TicketsConstants;
import fr.paris.lutece.plugins.stock.utils.DateUtils;
import fr.paris.lutece.plugins.stock.utils.ListUtils;
import fr.paris.lutece.plugins.stock.utils.constants.StockConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.constants.Parameters;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.DelegatePaginator;
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
 * This class provides the user interface to manage form features ( manage,
 * create, modify, remove)
 */
public class OfferJspBean  extends AbstractJspBean
{
    public static final Logger LOGGER = Logger.getLogger( OfferJspBean.class );
    
    // PARAMETERS
    public static final String PARAMETER_OFFER_ID = "offer_id";
    public static final String PARAMETER_OFFER_DUPLICATE = "duplicate";
    public static final String PARAMETER_OFFER_PRODUCT_ID = "productId";
    public static final String PARAMETER_OFFER_GENRE_LIST = "offer_genre_list";
    public static final String PARAMETER_OFFER_GENRE_LIST_DEFAULT = "offer_genre_list_default";
    public static final String PARAMETER_BUTTON_DELETE = "delete";
    public static final String PARAMETER_FILTER_NAME = "filter_name";
    public static final String PARAMETER_FILTER_ID = "filter_id";
    public static final String PARAMETER_FILTER_PARTNER_NAME = "filter_partner_name";
    public static final String PARAMETER_FILTER_PARTNER_NICKNAME = "filter_partner_nickname";
    public static final String PARAMETER_FILTER_OFFER_TYPE = "filter_offer_type";
    public static final String PARAMETER_FILTER_DATE_BEGIN = "filter_date_begin";
    public static final String PARAMETER_FILTER_DATE_END = "filter_date_end";
    public static final String PARAMETER_ORDER_BY_ID = "order_by_id";
    public static final String PARAMETER_ORDER_BY_LABEL = "order_by_label";
    public static final String PARAMETER_ORDER_BY_PLACE = "order_by_place";
    public static final String PARAMETER_ORDER_BY_TYPE = "order_by_type";
    public static final String PARAMETER_ORDER_ASC = "order_asc";
    public static final String PARAMETER_ORDER_DESC = "order_desc";
    public static final String PARAMETER_FILTER = "filter";
    public static final String PARAMETER_PRODUCT_ID = "product_id";

    public static final String RIGHT_MANAGE_OFFERS = "OFFERS_MANAGEMENT";
    public static final String RESOURCE_TYPE = "STOCK";

    // MARKS
    public static final String MARK_OFFER = "offer";
    public static final String MARK_TITLE = "title";
    public static final String MARK_LOCALE = "locale";
    public static final String MARK_OFFER_STATUT_CANCEL = "strStatutCancel";
    public static final String MARK_OFFER_STATUT_LOCK = "strStatutLock";
    public static final String MARK_CURRENT_DATE = "currentDate";
    public static final String MARK_ERRORS = "errors";
    private static final String MARK_LIST_OFFERS = "list_offers";
    private static final String MARK_LIST_PRODUCT = "product_list";
    private static final String MARK_LIST_OFFER_GENRE = "offerGenre_list";

    // JSP
    private static final String JSP_MANAGE_OFFERS = "jsp/admin/plugins/stock/modules/billetterie/ManageOffers.jsp";
    private static final String JSP_DO_DELETE_OFFER = "jsp/admin/plugins/stock/modules/billetterie/DoDeleteOffer.jsp";
    private static final String JSP_SAVE_OFFER = "SaveOffer.jsp";
    
    // TEMPLATES
    private static final String TEMPLATE_MANAGE_OFFERS = "admin/plugins/stock/modules/billetterie/manage_offers.html";
    private static final String TEMPLATE_SAVE_OFFER = "admin/plugins/stock/modules/billetterie/save_offer.html";


    // PAGE TITLES
    private static final String PROPERTY_PAGE_TITLE_MANAGE_OFFER = "module.stock.billetterie.list_offres.title";
    private static final String PROPERTY_PAGE_TITLE_CREATE_OFFER = "module.stock.billetterie.create_offer.title";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_OFFER = "module.stock.billetterie.modify_offer.title";
    
    // MESSAGES
    private static final String MESSAGE_CONFIRMATION_DELETE_OFFER = "module.stock.billetterie.message.deleteOffer.confirmation";
    private static final String MESSAGE_TITLE_CONFIRMATION_DELETE_OFFER = "module.stock.billetterie.message.title.deleteOffer.confirmation";
    private static final String MESSAGE_OFFER_STATUT_ISNT_CANCEL = "module.stock.billetterie.message.offer.statut.isnt.cancel";
    private static final String MESSAGE_SEARCH_PURCHASE_DATE = "module.stock.billetterie.message.search.purchase.date";

    // BEANS
    private static final String BEAN_STOCK_TICKETS_SHOW_SERVICE = "stock-tickets.showService";
    private static final String BEAN_STOCK_TICKETS_SEANCE_SERVICE = "stock-tickets.seanceService";

    // ORDER FILTERS
    private static final String ORDER_FILTER_DATE = "date";
    private static final String ORDER_FILTER_PRODUCT_NAME = "product.name";
    private static final String ORDER_FILTER_TYPE_NAME = "type.name";
    
    // MEMBERS VARIABLES
    private int _nItemsPerPage;
    // @Inject
    // @Named( "stock-tickets.seanceService" )
    private ISeanceService _serviceOffer;
    // @Inject
    // @Named( "stock-tickets.showService" )
    private IShowService _serviceProduct;
    // @Inject
    private IPurchaseService _servicePurchase;
    private SeanceFilter _offerFilter;

    /**
     * Instantiates a new offer jsp bean.
     */
    public OfferJspBean(  )
    {
        super(  );

        _offerFilter = new SeanceFilter( );
        _serviceOffer = (ISeanceService) SpringContextService.getBean( BEAN_STOCK_TICKETS_SEANCE_SERVICE );
        _serviceProduct = (IShowService) SpringContextService.getBean( BEAN_STOCK_TICKETS_SHOW_SERVICE );
        _servicePurchase = SpringContextService.getContext( ).getBean( IPurchaseService.class );
    }

    /**
     * Builds the filter.
     * 
     * @param filter the filter
     * @param request the request
     */
    protected void buildFilter( OfferFilter filter, HttpServletRequest request )
    {
        populate( filter, request );
    }
    
    /**
     * Gets the offer filter.
     * 
     * @param request the request
     * @return the offer filter
     */
    private OfferFilter getOfferFilter( HttpServletRequest request )
    {
        // SORT
        String strSortedAttributeName = request.getParameter( Parameters.SORTED_ATTRIBUTE_NAME );

        // "filter" in request  ==> new filter. use old one otherwise
        // if ( request.getParameter( TicketsConstants.PARAMETER_FILTER ) !=
        // null )
        // {
            SeanceFilter filter = new SeanceFilter( );
            buildFilter( filter, request );
            _offerFilter = filter;
        // }

        if ( strSortedAttributeName != null )
        {
        	_offerFilter.getOrders( ).add( strSortedAttributeName );

            String strAscSort = request.getParameter( Parameters.SORTED_ASC );
            boolean bIsAscSort = Boolean.parseBoolean( strAscSort );
            _offerFilter.setOrderAsc( bIsAscSort );
        }

        return _offerFilter;
    }
    
    /**
     * Get the manage offers page
     *
     * @param request
     *            the request
     * @return the page with offers list
     */
    public String getManageOffers( HttpServletRequest request )
    {
        setPageTitleProperty( PROPERTY_PAGE_TITLE_MANAGE_OFFER );

        SeanceFilter filter = (SeanceFilter) getOfferFilter( request );
        if ( filter.getProductId( ) != null && filter.getProductId( ) > 0 )
        {
            ShowDTO show = this._serviceProduct.findById( filter.getProductId( ) );
            filter.setProductName( show.getName( ) );
        }
        if ( StringUtils.isNotEmpty( filter.getProductName( ) ) )
        {
            ProductFilter productFilter = new ProductFilter( );
            productFilter.setName( filter.getProductName( ) );
            List<ShowDTO> listShow = this._serviceProduct.findByFilter( productFilter );
            if ( !listShow.isEmpty( ) )
            {
                filter.setProductId( listShow.get( 0 ).getId( ) );
            }
        }

        // Fill the model
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_LOCALE, getLocale( ) );

        // if date begin is after date end, add error
        List<String> errors = new ArrayList<String>( );
        if ( filter.getDateBegin( ) != null && filter.getDateEnd( ) != null
                && filter.getDateBegin( ).after( filter.getDateEnd( ) ) )
        {
            errors.add( I18nService.getLocalizedString( MESSAGE_SEARCH_PURCHASE_DATE, request.getLocale( ) ) );
        }

        model.put( MARK_ERRORS, errors );
        List<String> orderList = new ArrayList<String>( );
        orderList.add( ORDER_FILTER_DATE );
        orderList.add( ORDER_FILTER_PRODUCT_NAME );
        orderList.add( ORDER_FILTER_TYPE_NAME );
        filter.setOrders( orderList );
        filter.setOrderAsc( true );

        ResultList<SeanceDTO> listAllOffer = _serviceOffer
                .findByFilter( filter, getPaginationProperties( request ) );

        DelegatePaginator<SeanceDTO> paginator = getPaginator( request, listAllOffer );



        // the paginator
        model.put( TicketsConstants.MARK_NB_ITEMS_PER_PAGE, String.valueOf( _nItemsPerPage ) );
        model.put( TicketsConstants.MARK_PAGINATOR, paginator );
        model.put( MARK_LIST_OFFERS, paginator.getPageItems(  ) );
        // the filter
        model.put( TicketsConstants.MARK_FILTER, filter );
        // Combo
        ReferenceList offerGenreComboList = ListUtils.toReferenceList( _serviceOffer.findAllGenre( ),
                BilletterieConstants.ID, BilletterieConstants.NAME,
                StockConstants.EMPTY_STRING );
        model.put( MARK_LIST_OFFER_GENRE, offerGenreComboList );
        // offer statut
        model.put( MARK_OFFER_STATUT_CANCEL, TicketsConstants.OFFER_STATUT_CANCEL );
        model.put( MARK_OFFER_STATUT_LOCK, TicketsConstants.OFFER_STATUT_LOCK );
        // currentDate
        model.put( MARK_CURRENT_DATE, DateUtils.getCurrentDateString( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_OFFERS, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Returns the form for offer creation and modification
     * @param request The HTTP request
     * @return HTML Form
     */
    public String getSaveOffer( HttpServletRequest request )
    {
        SeanceDTO offer = null;
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_LOCALE, getLocale( ) );

        // Manage validation errors
        FunctionnalException ve = getErrorOnce( request );
        if ( ve != null )
        {
            offer = (SeanceDTO) ve.getBean( );
            model.put( BilletterieConstants.ERROR, getHtmlError( ve ) );
        }
        else
        {
            // No error, get offer if modify
            String strOfferId = request.getParameter( PARAMETER_OFFER_ID );
            String strDuplicate = request.getParameter( PARAMETER_OFFER_DUPLICATE );
            String strProductId = request.getParameter( PARAMETER_OFFER_PRODUCT_ID );
            
            if ( strOfferId != null )
            {
                setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY_OFFER );
                Integer nIdOffer = Integer.parseInt( strOfferId );
                offer = _serviceOffer.findSeanceById( nIdOffer );
                // Duplicate offer, set id to 0 to create a new offer
                if ( StringUtils.isNotEmpty( strDuplicate ) )
                {
                	offer.setId( null );
                    offer.setStatut( StringUtils.EMPTY );
                }
            }
            else
            {
                setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE_OFFER );
                // Create new Offer
                offer = new SeanceDTO( );
                // If creation and filter "Spectacle" exist, pre-select the spectacle
                if ( StringUtils.isNotBlank( strProductId ) )
                {
                    Integer nIdProduct = Integer.parseInt( strProductId );
                    List<ShowDTO> showList;
                	ProductFilter productFilter = new ProductFilter( );
                    productFilter.setIdProduct( nIdProduct );
                	showList = _serviceProduct.findByFilter( productFilter );
                	
                	if ( showList.size( ) == 1 )
                	{
                		offer.setProduct( showList.get( 0 ) );
                	}
                }
            }
        }
        
        // Combo
        List<String> orderList = new ArrayList<String>( );
        orderList.add( BilletterieConstants.NAME );
        ReferenceList productComboList = ListUtils.toReferenceList(
                _serviceProduct.getCurrentAndComeProduct( orderList ), BilletterieConstants.ID,
                BilletterieConstants.NAME,
                StockConstants.EMPTY_STRING );
        ReferenceList offerGenreComboList = ListUtils.toReferenceList( _serviceOffer.findAllGenre( ),
                BilletterieConstants.ID, BilletterieConstants.NAME,
                StockConstants.EMPTY_STRING );
        model.put( MARK_LIST_PRODUCT, productComboList );
        model.put( MARK_LIST_OFFER_GENRE, offerGenreComboList );

        // Add the JSP wich called this action
        model.put( StockConstants.MARK_JSP_BACK, request.getParameter( StockConstants.MARK_JSP_BACK ) );
        model.put( MARK_OFFER, offer );
        
        if ( offer.getId( ) != null )
        {
            model.put( MARK_TITLE, I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_MODIFY_OFFER, Locale.getDefault( ) ) );
        }
        else
        {
            model.put( MARK_TITLE, I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_CREATE_OFFER, Locale.getDefault( ) ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SAVE_OFFER, getLocale( ), model );

        return getAdminPage( template.getHtml( ) );
    }


    /**
     * Save a offer
     * @param request The HTTP request
     * @return redirection url
     */
    public String doSaveOffer( HttpServletRequest request )
    {
        if ( StringUtils.isNotBlank( request.getParameter( StockConstants.PARAMETER_BUTTON_CANCEL ) ) )
        {
            return doGoBack( request );
        }

        SeanceDTO offer = new SeanceDTO( );
        populate( offer, request );

        try
        {
            // Controls mandatory fields
            validate( offer );
            _serviceOffer.doSaveOffer( offer );
        }
        catch ( FunctionnalException e )
        {
            return manageFunctionnalException( request, e, JSP_SAVE_OFFER );
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

    /**
     * Returns the confirmation message to delete an offer
     * 
     * @param request The Http request
     * @return the html code message
     */
    public String getDeleteOffer( HttpServletRequest request )
    {
        String strOfferId = request.getParameter( PARAMETER_OFFER_ID );

        Integer nIdOffer;

        try
        {
            nIdOffer = Integer.parseInt( strOfferId );
        }
        catch ( NumberFormatException e )
        {
            LOGGER.debug( e );

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR, AdminMessage.TYPE_STOP );
        }

        Map<String, Object> urlParam = new HashMap<String, Object>( );
        urlParam.put( PARAMETER_OFFER_ID, nIdOffer );

        String strJspBack = JSP_MANAGE_OFFERS;

        PurchaseFilter filter = new PurchaseFilter( );
        filter.setIdOffer( nIdOffer );
        ResultList<ReservationDTO> bookingList = _servicePurchase.findByFilter( filter, null );
        // // BO-E07-RGE02 : Only offer with date before current date or offer
        // with statut "Cancel" can be delete
        SeanceDTO seance = _serviceOffer.findSeanceById( nIdOffer );

        if ( !bookingList.isEmpty( ) && !seance.getStatut( ).equals( TicketsConstants.OFFER_STATUT_CANCEL ) )
        {
            if ( !DateUtils.getDate( seance.getDate( ), false ).before( DateUtils.getCurrentDate( ) ) )
            {
                return AdminMessageService.getMessageUrl( request, MESSAGE_OFFER_STATUT_ISNT_CANCEL,
                        AdminMessage.TYPE_STOP );
            }
        }

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_DELETE_OFFER, null,
                MESSAGE_TITLE_CONFIRMATION_DELETE_OFFER, JSP_DO_DELETE_OFFER, BilletterieConstants.TARGET_SELF,
                AdminMessage.TYPE_CONFIRMATION,
                urlParam, strJspBack );
    }

    /**
     * Delete an offer
     * 
     * @param request The Http request
     * @return the html code message
     */
    public String doDeleteOffer( HttpServletRequest request )
    {
        String strOfferId = request.getParameter( PARAMETER_OFFER_ID );

        int nIdOffer;

        try
        {
            nIdOffer = Integer.parseInt( strOfferId );
        }
        catch ( NumberFormatException e )
        {
            LOGGER.debug( e );

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR,
                    AdminMessage.TYPE_STOP );
        }

        _serviceOffer.doDeleteOffer( nIdOffer );

        return doGoBack( request );
    }
}
