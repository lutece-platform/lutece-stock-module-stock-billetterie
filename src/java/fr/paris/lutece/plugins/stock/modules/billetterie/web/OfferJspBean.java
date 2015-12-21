/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
import fr.paris.lutece.plugins.stock.business.product.Product;
import fr.paris.lutece.plugins.stock.business.product.ProductFilter;
import fr.paris.lutece.plugins.stock.business.purchase.PurchaseFilter;
import fr.paris.lutece.plugins.stock.commons.ResultList;
import fr.paris.lutece.plugins.stock.commons.dao.PaginationProperties;
import fr.paris.lutece.plugins.stock.commons.exception.BusinessException;
import fr.paris.lutece.plugins.stock.commons.exception.FunctionnalException;
import fr.paris.lutece.plugins.stock.modules.billetterie.utils.constants.BilletterieConstants;
import fr.paris.lutece.plugins.stock.modules.tickets.business.Contact;
import fr.paris.lutece.plugins.stock.modules.tickets.business.NotificationDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.PartnerDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ReservationDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.SeanceDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.business.SeanceFilter;
import fr.paris.lutece.plugins.stock.modules.tickets.business.ShowDTO;
import fr.paris.lutece.plugins.stock.modules.tickets.service.INotificationService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IProviderService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IPurchaseService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.ISeanceService;
import fr.paris.lutece.plugins.stock.modules.tickets.service.IShowService;
import fr.paris.lutece.plugins.stock.modules.tickets.utils.constants.TicketsConstants;
import fr.paris.lutece.plugins.stock.modules.tickets.utils.export.TicketsExportUtils;
import fr.paris.lutece.plugins.stock.service.IPurchaseSessionManager;
import fr.paris.lutece.plugins.stock.service.ISubscriptionProductService;
import fr.paris.lutece.plugins.stock.utils.CsvUtils;
import fr.paris.lutece.plugins.stock.utils.DateUtils;
import fr.paris.lutece.plugins.stock.utils.ListUtils;
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
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.datatable.DataTableManager;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * This class provides the user interface to manage form features ( manage,
 * create, modify, remove)
 */
public class OfferJspBean extends AbstractJspBean
{
    public static final Logger LOGGER = Logger.getLogger( OfferJspBean.class );

    // PARAMETERS
    public static final String PARAMETER_OFFER_ID = "offer_id";
    public static final String PARAMETER_OFFERS_ID = "offers_id";
    public static final String PARAMETER_OFFER_DUPLICATE = "duplicate";
    public static final String PARAMETER_OFFER_PRODUCT_ID = "productId";
    public static final String PARAMETER_OFFER_ID_PRODUCT = "product.id";
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
    public static final String PARAMETER_REFRESH_CONTACT = "refresh_contact";
    public static final String RIGHT_MANAGE_OFFERS = "OFFERS_MANAGEMENT";
    public static final String RESOURCE_TYPE = "STOCK";

    // MARKS
    public static final String MARK_OFFER = "offer";
    public static final String MARK_PRODUCT = "product";
    public static final String MARK_TITLE = "title";
    public static final String MARK_LOCALE = "locale";
    public static final String MARK_OFFER_STATUT_CANCEL = "strStatutCancel";
    public static final String MARK_OFFER_STATUT_LOCK = "strStatutLock";
    public static final String MARK_CURRENT_DATE = "currentDate";
    public static final String MARK_ERRORS = "errors";
    public static final String MARK_CONTACT_LIST = "contact_list";
    public static final String MARK_PURCHASE = "purchase";
    public static final String MARK_BASE_URL = "base_url";
    public static final String MARK_USER_NAME = "userName";

    /** The constants for DataTableManager */
    public static final String MARK_DATA_TABLE_OFFER = "dataTableOffer";
    public static final String MARK_FILTER_OFFER = "filterOffer";
    public static final String MACRO_COLUMN_CHECKBOX_DELETE_OFFER = "columnCheckboxDeleteOffer";
    public static final String MACRO_COLUMN_PRODUCT_OFFER = "columnProductOffer";
    public static final String MACRO_COLUMN_STATUT_OFFER = "columnStatutOffer";
    public static final String MACRO_COLUMN_ACTIONS_OFFER = "columnActionsOffer";
    public static final String MACRO_COLUMN_NAME_OFFER = "columnNameOffer";
    public static final String MACRO_COLUMN_DATES_OFFER = "columnDatesOffer";
    private static final String MARK_LIST_PRODUCT = "product_list";
    private static final String MARK_LIST_OFFER_GENRE = "offerGenre_list";
    private static final String MARK_RESERVE = "RESERVATIONS";

    // JSP
    private static final String JSP_MANAGE_OFFERS = "jsp/admin/plugins/stock/modules/billetterie/ManageOffers.jsp";
    private static final String JSP_DO_DELETE_OFFER = "jsp/admin/plugins/stock/modules/billetterie/DoDeleteOffer.jsp";
    private static final String JSP_DO_MASSE_DELETE_OFFER = "jsp/admin/plugins/stock/modules/billetterie/DoMasseDeleteOffer.jsp";
    private static final String JSP_SAVE_OFFER = "SaveOffer.jsp";

    // TEMPLATES
    private static final String TEMPLATE_MANAGE_OFFERS = "admin/plugins/stock/modules/billetterie/manage_offers.html";
    private static final String TEMPLATE_SAVE_OFFER = "admin/plugins/stock/modules/billetterie/save_offer.html";
    private static final String TEMPLATE_NOTIFICATION_CREATE_OFFER = "admin/plugins/stock/modules/billetterie/notification_create_offer.html";

    // PAGE TITLES
    private static final String PROPERTY_PAGE_TITLE_MANAGE_OFFER = "module.stock.billetterie.list_offres.title";
    private static final String PROPERTY_PAGE_TITLE_CREATE_OFFER = "module.stock.billetterie.create_offer.title";
    private static final String PROPERTY_PAGE_TITLE_MODIFY_OFFER = "module.stock.billetterie.modify_offer.title";

    // MESSAGES
    private static final String MESSAGE_CONFIRMATION_DELETE_OFFER = "module.stock.billetterie.message.deleteOffer.confirmation";
    private static final String MESSAGE_CONFIRMATION_MASSE_DELETE_OFFER = "module.stock.billetterie.message.deleteOffer.masseConfirmation";
    private static final String MESSAGE_TITLE_CONFIRMATION_DELETE_OFFER = "module.stock.billetterie.message.title.deleteOffer.confirmation";
    private static final String MESSAGE_TITLE_CONFIRMATION_MASSE_DELETE_OFFER = "module.stock.billetterie.message.title.masseDeleteOffer.confirmation";
    private static final String MESSAGE_OFFER_STATUT_ISNT_CANCEL = "module.stock.billetterie.message.offer.statut.isnt.cancel";
    private static final String MESSAGE_OFFER_STATUT_ISNT_MASSE_CANCEL = "module.stock.billetterie.message.offer.statut.isnt.masseCancel";
    private static final String MESSAGE_DELETE_MASSE_OFFER_NO_OFFER_CHECK = "module.stock.billetterie.message.deleteMasseOffer.noCaseCheck";
    private static final String MESSAGE_SEARCH_PURCHASE_DATE = "module.stock.billetterie.message.search.purchase.date";
    private static final String MESSAGE_NOTIFICATION_OFFER_PRODUCT = "module.stock.billetterie.notification.offer.product";
    private static final String MESSAGE_ERROR_TICKETS_RANGE = "module.stock.billetterie.message.error.tickets.range";
    // BEANS
    private static final String BEAN_STOCK_TICKETS_SHOW_SERVICE = "stock-tickets.showService";
    private static final String BEAN_STOCK_TICKETS_SEANCE_SERVICE = "stock-tickets.seanceService";
    private static final String BEAN_STOCK_TICKETS_PARTNER_SERVICE = "stock-tickets.providerService";

    // ORDER FILTERS
    private static final String ORDER_FILTER_DATE = "date";
    private static final String ORDER_FILTER_PRODUCT_NAME = "product.name";
    private static final String ORDER_FILTER_TYPE_NAME = "typeName";

    // MEMBERS VARIABLES
    // @Inject
    // @Named( "stock-tickets.seanceService" )
    private ISeanceService _serviceOffer;

    // @Inject
    // @Named( "stock-tickets.showService" )
    private IShowService _serviceProduct;
    private IProviderService _servicePartner;
    private IPurchaseService _servicePurchase;
    private INotificationService _serviceNotification;
    private ISubscriptionProductService _subscriptionProductService;
    private IPurchaseSessionManager _purchaseSessionManager;
    private SeanceFilter _offerFilter;

    /**
     * Instantiates a new offer jsp bean.
     */
    public OfferJspBean(  )
    {
        super(  );

        _offerFilter = new SeanceFilter(  );
        _serviceOffer = (ISeanceService) SpringContextService.getBean( BEAN_STOCK_TICKETS_SEANCE_SERVICE );
        _serviceProduct = (IShowService) SpringContextService.getBean( BEAN_STOCK_TICKETS_SHOW_SERVICE );
        _servicePartner = (IProviderService) SpringContextService.getBean( BEAN_STOCK_TICKETS_PARTNER_SERVICE );
        _purchaseSessionManager = SpringContextService.getContext(  ).getBean( IPurchaseSessionManager.class );
        _servicePurchase = SpringContextService.getContext(  ).getBean( IPurchaseService.class );
        _serviceNotification = SpringContextService.getContext(  ).getBean( INotificationService.class );
        _subscriptionProductService = SpringContextService.getContext(  ).getBean( ISubscriptionProductService.class );
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
        SeanceFilter filter = new SeanceFilter(  );
        buildFilter( filter, request );
        _offerFilter = filter;

        // }
        if ( strSortedAttributeName != null )
        {
            _offerFilter.getOrders(  ).add( strSortedAttributeName );

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

        if ( ( filter.getProductId(  ) != null ) && ( filter.getProductId(  ) > 0 ) )
        {
            ShowDTO show = this._serviceProduct.findById( filter.getProductId(  ) );
            filter.setProductName( show.getName(  ) );
        }

        if ( StringUtils.isNotEmpty( filter.getProductName(  ) ) )
        {
            ProductFilter productFilter = new ProductFilter(  );
            productFilter.setName( filter.getProductName(  ) );

            List<ShowDTO> listShow = this._serviceProduct.findByFilter( productFilter );

            if ( !listShow.isEmpty(  ) )
            {
                filter.setProductId( listShow.get( 0 ).getId(  ) );
            }
        }

        // Fill the model
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_LOCALE, getLocale(  ) );

        // if date begin is after date end, add error
        List<String> errors = new ArrayList<String>(  );

        if ( ( filter.getDateBegin(  ) != null ) && ( filter.getDateEnd(  ) != null ) &&
                filter.getDateBegin(  ).after( filter.getDateEnd(  ) ) )
        {
            errors.add( I18nService.getLocalizedString( MESSAGE_SEARCH_PURCHASE_DATE, request.getLocale(  ) ) );
        }

        model.put( MARK_ERRORS, errors );

        List<String> orderList = new ArrayList<String>(  );
        orderList.add( ORDER_FILTER_DATE );
        orderList.add( ORDER_FILTER_PRODUCT_NAME );
        orderList.add( ORDER_FILTER_TYPE_NAME );
        filter.setOrders( orderList );
        filter.setOrderAsc( true );

        DataTableManager<SeanceDTO> dataTableToUse = getDataTable( request, filter );

        for ( SeanceDTO seance : dataTableToUse.getItems(  ) )
        {
            // Update quantity with quantity in session for this offer
            seance.setQuantity( _purchaseSessionManager.updateQuantityWithSession( seance.getQuantity(  ),
                    seance.getId(  ) ) );
        }

        model.put( MARK_DATA_TABLE_OFFER, dataTableToUse );

        // the paginator
        model.put( TicketsConstants.MARK_NB_ITEMS_PER_PAGE, String.valueOf( _nItemsPerPage ) );
        // the filter
        model.put( TicketsConstants.MARK_FILTER, filter );

        // Combo
        ReferenceList offerGenreComboList = ListUtils.toReferenceList( _serviceOffer.findAllGenre(  ),
                BilletterieConstants.ID, BilletterieConstants.NAME, StockConstants.EMPTY_STRING );
        model.put( MARK_LIST_OFFER_GENRE, offerGenreComboList );
        // offer statut
        model.put( MARK_OFFER_STATUT_CANCEL, TicketsConstants.OFFER_STATUT_CANCEL );
        model.put( MARK_OFFER_STATUT_LOCK, TicketsConstants.OFFER_STATUT_LOCK );
        // currentDate
        model.put( MARK_CURRENT_DATE, DateUtils.getCurrentDateString(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MANAGE_OFFERS, getLocale(  ), model );

        //opération nécessaire pour eviter les fuites de mémoires
        dataTableToUse.clearItems(  );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Get the DataTableManager object for the ShowDTO bean
     * @param request the http request
     * @param filter the filter
     * @return the data table to use
     */
    private <T> DataTableManager<T> getDataTable( HttpServletRequest request, SeanceFilter filter )
    {
        //si un objet est déjà présent en session, on l'utilise
        Method findMethod = null;

        try
        {
            findMethod = _serviceOffer.getClass(  )
                                      .getMethod( PARAMETER_FIND_BY_FILTER_NAME_METHOD, OfferFilter.class,
                    PaginationProperties.class );
        }
        catch ( Exception e )
        {
            LOGGER.error( "Erreur lors de l'obtention du data table : ", e );
        }

        DataTableManager<T> dataTableToUse = getAbstractDataTableManager( request, filter, MARK_DATA_TABLE_OFFER,
                JSP_MANAGE_OFFERS, _serviceOffer, findMethod );

        //si pas d'objet en session, il faut ajouter les colonnes à afficher
        if ( dataTableToUse.getListColumn(  ).isEmpty(  ) )
        {
            dataTableToUse.addFreeColumn( StringUtils.EMPTY, MACRO_COLUMN_STATUT_OFFER );
            dataTableToUse.addFreeColumn( StringUtils.EMPTY, MACRO_COLUMN_CHECKBOX_DELETE_OFFER );
            dataTableToUse.addColumn( "module.stock.billetterie.list_offres.filter.name", "name", true );
            dataTableToUse.addColumn( "module.stock.billetterie.list_offres.filter.product", "product.name", true );
            dataTableToUse.addColumn( "module.stock.billetterie.list_offres.type", "typeName", true );
            dataTableToUse.addFreeColumn( "module.stock.billetterie.save_offer.date", MACRO_COLUMN_DATES_OFFER );
            dataTableToUse.addFreeColumn( "module.stock.billetterie.save_offer.initialQuantity",
                "columnInitialQuantityOffer" );
            dataTableToUse.addColumn( "module.stock.billetterie.save_offer.quantity", "quantity", false );
            dataTableToUse.addFreeColumn( "module.stock.billetterie.save_offer.sessions.actions",
                MACRO_COLUMN_ACTIONS_OFFER );
        }

        saveDataTableInSession( request, dataTableToUse, MARK_DATA_TABLE_OFFER );

        return dataTableToUse;
    }

    /**
     * Returns the form for offer creation and modification
     * @param request The HTTP request
     * @return HTML Form
     */
    public String getSaveOffer( HttpServletRequest request )
    {
        SeanceDTO offer = null;
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_LOCALE, getLocale(  ) );

        // Manage validation errors
        FunctionnalException ve = getErrorOnce( request );

        if ( ve != null )
        {
            offer = (SeanceDTO) ve.getBean(  );
            model.put( BilletterieConstants.ERROR, getHtmlError( ve ) );
        }
        else
        {
            // No error, get offer if modify
            String strOfferId = request.getParameter( PARAMETER_OFFER_ID );
            String strDuplicate = request.getParameter( PARAMETER_OFFER_DUPLICATE );
            String strProductId = request.getParameter( PARAMETER_OFFER_PRODUCT_ID );
            ShowDTO selectedProduct;

            //Modification of an existing offer
            if ( StringUtils.isNotEmpty( strOfferId ) )
            {
                setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY_OFFER );

                Integer nIdOffer = Integer.parseInt( strOfferId );
                offer = _serviceOffer.findSeanceById( nIdOffer );

                int idProvider = 0;

                if ( request.getParameter( PARAMETER_REFRESH_CONTACT ) != null )
                {
                    //case of wanting to get the contact which can be link to the offer
                    populate( offer, request );

                    String strIdProduct = request.getParameter( PARAMETER_OFFER_ID_PRODUCT );
                    int nIdSelectedProvider = Integer.valueOf( strIdProduct );

                    if ( nIdSelectedProvider >= 0 )
                    {
                        selectedProduct = _serviceProduct.findById( nIdSelectedProvider );
                    }
                    else
                    {
                        selectedProduct = _serviceProduct.findById( offer.getProduct(  ).getId(  ) );
                    }
                }
                else
                {
                    //case of taking the contact linking to the offer
                    selectedProduct = _serviceProduct.findById( offer.getProduct(  ).getId(  ) );
                }
                idProvider = selectedProduct.getIdProvider();
                
                model.put( MARK_CONTACT_LIST, getContactComboList( idProvider, selectedProduct ) );

                // Duplicate offer, set id to 0 to create a new offer
                if ( StringUtils.isNotEmpty( strDuplicate ) )
                {
                    offer.setId( null );
                    offer.setStatut( StringUtils.EMPTY );
                }
            } //Create a new offer
            else
            {
                setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE_OFFER );
                // Create new Offer
                offer = new SeanceDTO(  );

                // If creation and filter "Spectacle" exist, pre-select the spectacle
                if ( StringUtils.isNotBlank( strProductId ) && !strProductId.equals( "0" ) )
                {
                    Integer nIdProduct = Integer.parseInt( strProductId );
                    offer.setProduct( _serviceProduct.findById( nIdProduct ) );
                }
                else
                {
                    populate( offer, request );
                }

                if ( ( request.getParameter( PARAMETER_OFFER_ID_PRODUCT ) != null ) &&
                        !request.getParameter( PARAMETER_OFFER_ID_PRODUCT ).equals( "-1" ) )
                {
                    Integer idProduct = Integer.valueOf( request.getParameter( PARAMETER_OFFER_ID_PRODUCT ) );

                    ShowDTO productChoose = _serviceProduct.findById( idProduct );
                    int idProvider = productChoose.getIdProvider(  );
                    model.put( MARK_CONTACT_LIST, getContactComboList( idProvider, productChoose ) );
                }
            }
        }

        // Combo
        List<String> orderList = new ArrayList<String>(  );
        orderList.add( BilletterieConstants.NAME );

        ReferenceList productComboList = ListUtils.toReferenceList( _serviceProduct.getCurrentAndComeProduct( orderList ),
                BilletterieConstants.ID, BilletterieConstants.NAME, StockConstants.EMPTY_STRING );
        ReferenceList offerGenreComboList = ListUtils.toReferenceList( _serviceOffer.findAllGenre(  ),
                BilletterieConstants.ID, BilletterieConstants.NAME, StockConstants.EMPTY_STRING );
        model.put( MARK_LIST_PRODUCT, productComboList );
        model.put( MARK_LIST_OFFER_GENRE, offerGenreComboList );

        // Add the JSP wich called this action
        model.put( StockConstants.MARK_JSP_BACK, request.getParameter( StockConstants.MARK_JSP_BACK ) );
        model.put( MARK_OFFER, offer );

        if ( offer.getId(  ) != null )
        {
            model.put( MARK_TITLE,
                I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_MODIFY_OFFER, Locale.getDefault(  ) ) );
        }
        else
        {
            model.put( MARK_TITLE,
                I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_CREATE_OFFER, Locale.getDefault(  ) ) );
        }

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SAVE_OFFER, getLocale(  ), model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Get only the selected contacts in the product, not all the contacts from the provider of the product
     * @param idProvider
     * @param product
     * @return The ReferenceList
     */
    private ReferenceList getContactComboList( int idProvider, ShowDTO product )
    {
        PartnerDTO findById = _servicePartner.findById( idProvider );
        List<Contact> contactList = findById.getContactList(  );
        Integer[] selectedContacts =  product.getIdContact(  );
        List<Contact> resultContactList = new ArrayList<Contact>(  );
       
        for ( Contact contact : contactList )
        {
            for ( Integer contactId : selectedContacts )
            {
                if ( contactId == contact.getId(  ) )
                {
                    resultContactList.add( contact );
                }
            }
        }
        ReferenceList contactComboList = ListUtils.toReferenceList( resultContactList,
                BilletterieConstants.ID, BilletterieConstants.NAME, StockConstants.EMPTY_STRING );

        return contactComboList;
    }

    /**
     * Save a offer
     * @param request The HTTP request
     * @return redirection url
     */
    public String doSaveOffer( HttpServletRequest request )
    {
        if ( request.getParameter( StockConstants.PARAMETER_BUTTON_CANCEL ) != null )
        {
            return doGoBack( request );
        }

        SeanceDTO offer = new SeanceDTO(  );
        populate( offer, request );

        // make sur you set the initial quantity only in the first save offer, not in modify offer
        try
        {
            // Controls mandatory fields
            validateBilletterie( offer );
            if ( offer.getMinTickets(  ) > offer.getMaxTickets(  ) )
            {
                throw new BusinessException( offer, MESSAGE_ERROR_TICKETS_RANGE );
            }
            
            _serviceOffer.doSaveOffer( offer );
        }
        catch ( FunctionnalException e )
        {
            return manageFunctionnalException( request, e, JSP_SAVE_OFFER );
        }

        if ( StringUtils.isBlank( request.getParameter( PARAMETER_OFFER_ID ) ) )
        {
            doNotifyCreateOffer( request, offer );
        }

        return doGoBack( request );
    }

    /**
     * Send notification for user who subscribed to the product link with an
     * offer.
     * @param offer the offer create
     * @param request The Http request
     */
    public void doNotifyCreateOffer( HttpServletRequest request, SeanceDTO offer )
    {
        //get all subscription for product
        Product product = _serviceProduct.findById( offer.getProduct(  ).getId(  ) ).convert(  );

        List<String> listUserEmail = _subscriptionProductService.getListEmailSubscriber( Integer.toString( 
                    offer.getProduct(  ).getId(  ) ) );

        //Generate mail content
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_OFFER, offer );
        model.put( MARK_PRODUCT, product );
        model.put( MARK_BASE_URL, AppPathService.getBaseUrl( request ) );

        // Create mail object
        HtmlTemplate template;
        NotificationDTO notificationDTO;

        for ( String strUserEmail : listUserEmail )
        {
            model.put( MARK_USER_NAME, strUserEmail );
            template = AppTemplateService.getTemplate( TEMPLATE_NOTIFICATION_CREATE_OFFER, request.getLocale(  ), model );

            notificationDTO = new NotificationDTO(  );
            notificationDTO.setRecipientsTo( strUserEmail );

            String[] args = new String[] { product.getName(  ), };
            notificationDTO.setSubject( I18nService.getLocalizedString( MESSAGE_NOTIFICATION_OFFER_PRODUCT, args,
                    request.getLocale(  ) ) );
            notificationDTO.setMessage( template.getHtml(  ) );

            // Send it
            _serviceNotification.send( notificationDTO );
        }
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
                                                    : ( AppPathService.getBaseUrl( request ) + JSP_MANAGE_OFFERS );
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

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR,
                AdminMessage.TYPE_STOP );
        }

        Map<String, Object> urlParam = new HashMap<String, Object>(  );

        String strJspBack = JSP_MANAGE_OFFERS;

        ArrayList<Integer> offersIdToDelete = new ArrayList<Integer>(  );
        offersIdToDelete.add( nIdOffer );

        String error = errorWithDeleteOffer( request, urlParam, offersIdToDelete );

        if ( error != null )
        {
            return error;
        }

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRMATION_DELETE_OFFER, null,
            MESSAGE_TITLE_CONFIRMATION_DELETE_OFFER, JSP_DO_DELETE_OFFER, BilletterieConstants.TARGET_SELF,
            AdminMessage.TYPE_CONFIRMATION, urlParam, strJspBack );
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

    /**
     * Returns the confirmation message to delete an offer
     *
     * @param request The Http request
     * @return the html code message
     */
    public String getMasseDeleteOffer( HttpServletRequest request )
    {
        ArrayList<Integer> offersIdToDelete = new ArrayList<Integer>(  );
        String[] parameterValues = request.getParameterValues( PARAMETER_OFFERS_ID );

        // if no case checked
        if ( parameterValues == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_DELETE_MASSE_OFFER_NO_OFFER_CHECK,
                AdminMessage.TYPE_STOP );
        }

        try
        {
            for ( String id : parameterValues )
            {
                offersIdToDelete.add( Integer.parseInt( id ) );
            }
        }
        catch ( NumberFormatException e )
        {
            LOGGER.debug( e );

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR,
                AdminMessage.TYPE_STOP );
        }

        boolean masse = offersIdToDelete.size(  ) > 1;

        String strJspBack = JSP_MANAGE_OFFERS;

        Map<String, Object> urlParam = new HashMap<String, Object>(  );
        String error = errorWithDeleteOffer( request, urlParam, offersIdToDelete );

        if ( error != null )
        {
            return error;
        }

        return AdminMessageService.getMessageUrl( request,
            masse ? MESSAGE_CONFIRMATION_MASSE_DELETE_OFFER : MESSAGE_CONFIRMATION_DELETE_OFFER, null,
            MESSAGE_TITLE_CONFIRMATION_MASSE_DELETE_OFFER, masse ? JSP_DO_MASSE_DELETE_OFFER : JSP_DO_DELETE_OFFER,
            BilletterieConstants.TARGET_SELF, AdminMessage.TYPE_CONFIRMATION, urlParam, strJspBack );
    }

    /**
     * Delete offers on masse
     *
     * @param request The Http request which contains the offer checked
     * @return the html code message
     */
    public String doMasseDeleteOffer( HttpServletRequest request )
    {
        ArrayList<Integer> listOffer = new ArrayList<Integer>(  );

        try
        {
            for ( Object key : request.getParameterMap(  ).keySet(  ) )
            {
                //ensure the key parameter it rightly an offer, can be offer_id1, offer_id2, ... or "offer_id" with only one offer
                if ( ( (String) key ).startsWith( PARAMETER_OFFER_ID ) )
                {
                    listOffer.add( Integer.parseInt( request.getParameter( (String) key ) ) );
                }
            }
        }
        catch ( NumberFormatException e )
        {
            LOGGER.debug( e );

            return AdminMessageService.getMessageUrl( request, StockConstants.MESSAGE_ERROR_OCCUR,
                AdminMessage.TYPE_STOP );
        }

        _serviceOffer.doMasseDeleteOffer( listOffer );

        return doGoBack( request );
    }

    /**
     * Return a error message if it's not possible to delete the offer given,
     * with deleteOffer or masseDeleteOffer
     * @param request The Http request
     * @param urlParam the url where the id offer to delete must be set
     * @param offersIdToDelete the list which contains the offer to delete
     * @return null if there aren't error, the message otherwise
     */
    private String errorWithDeleteOffer( HttpServletRequest request, Map<String, Object> urlParam,
        ArrayList<Integer> offersIdToDelete )
    {
        boolean masse = offersIdToDelete.size(  ) > 1;
        PurchaseFilter filter = new PurchaseFilter(  );
        int i = 0;

        for ( Integer nIdOffer : offersIdToDelete )
        {
            urlParam.put( masse ? ( PARAMETER_OFFER_ID + i++ ) : PARAMETER_OFFER_ID, nIdOffer );
            filter.setIdOffer( nIdOffer );

            ResultList<ReservationDTO> bookingList = _servicePurchase.findByFilter( filter, null );

            // // BO-E07-RGE02 : Only offer with date before current date or offer
            // with statut "Cancel" can be delete
            SeanceDTO seance = _serviceOffer.findSeanceById( nIdOffer );

            if ( !bookingList.isEmpty(  ) && !seance.getStatut(  ).equals( TicketsConstants.OFFER_STATUT_CANCEL ) )
            {
                if ( !DateUtils.getDate( seance.getDate(  ), false ).before( DateUtils.getCurrentDate(  ) ) )
                {
                    return AdminMessageService.getMessageUrl( request,
                        masse ? MESSAGE_OFFER_STATUT_ISNT_MASSE_CANCEL : MESSAGE_OFFER_STATUT_ISNT_CANCEL,
                        AdminMessage.TYPE_STOP );
                }
            }
        }

        return null;
    }

    public void doExportPurchase( HttpServletRequest request, HttpServletResponse response )
    {
        ResultList<ReservationDTO> reservationModel = new ResultList<ReservationDTO>(  );
        PurchaseFilter filter = new PurchaseFilter(  );
        String parameter = request.getParameter( PARAMETER_OFFER_ID );
        int idOffer = Integer.valueOf( parameter );
        filter.setIdOffer( idOffer );
        reservationModel.addAll( _servicePurchase.findByFilter( filter ) );

        try
        {
            //Génère le CSV
            String strFormatExtension = AppPropertiesService.getProperty( TicketsConstants.PROPERTY_CSV_EXTENSION );
            String strFileName = AppPropertiesService.getProperty( TicketsConstants.PROPERTY_CSV_PURCHASE_NAME ) + "." +
                strFormatExtension;
            TicketsExportUtils.addHeaderResponse( request, response, strFileName, strFormatExtension );

            OutputStream os = response.getOutputStream(  );

            //say how to decode the csv file, with utf8
            byte[] bom = new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF }; // BOM values
            os.write( bom ); // adds BOM 
            CsvUtils.ecrireCsv( MARK_RESERVE, reservationModel, os );

            os.flush(  );
            os.close(  );
        }
        catch ( IOException e )
        {
            AppLogService.error( e );
        }
    }
}
