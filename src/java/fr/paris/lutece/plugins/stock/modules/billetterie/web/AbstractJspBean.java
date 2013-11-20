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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Path;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import fr.paris.lutece.plugins.stock.commons.ResultList;
import fr.paris.lutece.plugins.stock.commons.dao.PaginationProperties;
import fr.paris.lutece.plugins.stock.commons.dao.PaginationPropertiesAdapterDataTable;
import fr.paris.lutece.plugins.stock.commons.dao.PaginationPropertiesImpl;
import fr.paris.lutece.plugins.stock.commons.exception.BusinessException;
import fr.paris.lutece.plugins.stock.commons.exception.FunctionnalException;
import fr.paris.lutece.plugins.stock.commons.exception.ValidationException;
import fr.paris.lutece.plugins.stock.modules.tickets.utils.constants.TicketsConstants;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.util.LocalizedDelegatePaginator;
import fr.paris.lutece.util.beanvalidation.BeanValidationUtil;
import fr.paris.lutece.util.datatable.DataTableManager;
import fr.paris.lutece.util.datatable.DataTablePaginationProperties;
import fr.paris.lutece.util.html.DelegatePaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;


/**
 * Abstract class for jsp bean
 * @author abataille
 */
public class AbstractJspBean extends PluginAdminPageJspBean
{
    private static final Logger LOGGER = Logger.getLogger( AbstractJspBean.class );

    public static final int N_DEFAULT_ITEMS_PER_PAGE = AppPropertiesService.getPropertyInt(
            TicketsConstants.PROPERTY_DEFAULT_ITEM_PER_PAGE, 50 );
    protected static final String ERROR_MESSAGE_KEY = "module.stock.billetterie.validation.error";
    protected static final String ERROR_TEMPLATE = "admin/plugins/stock/modules/billetterie/error.html";
    protected static final String FIELD_MESSAGE_PREFIX = "module.stock.billetterie.field.";
    protected static final String MARK_FILTER = "filter";
    protected static final String MARK_DATA_TABLE_MANAGER = "dataTableManager";
    protected static final String MARK_MESSAGE_LIST = "messageList";
    protected static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    protected static final String MARK_PAGINATOR = "paginator";
    protected static final String PARAMETER_FIND_BY_FILTER_NAME_METHOD = "findByFilter";
    private String _strCurrentPageIndex = StringUtils.EMPTY;
    protected int _nItemsPerPage;

    /**
     * 
     * Get the datatable save in the session
     * @param request the http request
     * @param key the key for the data table
     * @return the DataTableManager keep in session
     */
    @SuppressWarnings( "unchecked" )
    protected <T> DataTableManager<T> loadDataTableFromSession( HttpServletRequest request, String key )
    {
        DataTableManager<T> dataTable = null;

        Object object = request.getSession( ).getAttribute(
                StringUtils.isNotBlank( key ) ? key : MARK_DATA_TABLE_MANAGER );

        if ( object != null && object instanceof DataTableManager<?> )
        {
            try
            {
                dataTable = (DataTableManager<T>) object;
            }
            catch ( Exception e )
            {
                LOGGER.error( "Error during cast :" + e );
            }
        }

        return dataTable;
    }

    /**
     * Save a Data table manager into the http session with key. Can save
     * various data table.
     * @param request the http request with the user session
     * @param dataTable the datatable
     * @param key the datatable key
     * @param <T> Type of data
     */
    protected <T> void saveDataTableInSession( HttpServletRequest request, DataTableManager<T> dataTable, String key )
    {
        request.getSession( ).setAttribute( StringUtils.isNotBlank( key ) ? key : MARK_DATA_TABLE_MANAGER, dataTable );
    }

    /**
     * Return a paginator for the view using parameter in http request.
     * 
     * @param <T> the generic type
     * @param request http request
     * @param list bean list to paginate
     * @return paginator
     */
    protected <T> DelegatePaginator<T> getPaginator( HttpServletRequest request, ResultList<T> list )
    {

        _strCurrentPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        _nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                N_DEFAULT_ITEMS_PER_PAGE );
        String strBaseUrl = request.getRequestURL( )/*
                                                     * .append( "?" ).append(
                                                     * request.getQueryString( )
                                                     * )
                                                     */.toString( );
        LocalizedDelegatePaginator<T> paginator = new LocalizedDelegatePaginator<T>( list, _nItemsPerPage, strBaseUrl,
                Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex, list.getTotalResult( ), getLocale( ) );

        return paginator;
    }

    /**
     * Return a bean for pagination in service/dao using parameter in http
     * request
     * @param request http request
     * @return paginator
     */
    protected PaginationProperties getPaginationProperties( HttpServletRequest request )
    {
        String strPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX, _strCurrentPageIndex );
        int nCurrentPageIndex = 1;
        if ( StringUtils.isNotEmpty( strPageIndex ) )
        {
            nCurrentPageIndex = Integer.valueOf( strPageIndex );
        }
        int nItemsPerPage = Paginator.getItemsPerPage( request, Paginator.PARAMETER_ITEMS_PER_PAGE, _nItemsPerPage,
                N_DEFAULT_ITEMS_PER_PAGE );

        return new PaginationPropertiesImpl( ( nCurrentPageIndex - 1 ) * nItemsPerPage, nItemsPerPage );
    }

    /**
     * Validate a bean using jsr 303 specs.
     * 
     * @param <T> the bean type
     * @param bean to validate
     * @throws ValidationException exception containing informations about
     *             errors and the bean
     */
    protected <T> void validateBilletterie( T bean ) throws ValidationException
    {
        Set<ConstraintViolation<T>> constraintViolations = BeanValidationUtil.validate( bean );
        if ( constraintViolations.size( ) > 0 )
        {
            ValidationException ve = new ValidationException( bean );
            for ( ConstraintViolation<T> constraintViolation : constraintViolations )
            {
                ve.addConstraintViolation( constraintViolation );
            }
            throw ve;
        }
    }

    /**
     * Return localized message
     * @param key i18n key
     * @return localized message
     */
    protected String getMessage( String key )
    {
        return I18nService.getLocalizedString( key, this.getLocale( ) );
    }

    /**
     * Return localized message with args
     * @param key i18n key
     * @param args args
     * @return localized message
     */
    protected String getMessage( String key, String... args )
    {
        return I18nService.getLocalizedString( key, args, this.getLocale( ) );
    }

    /**
     * Get validation error from session and remove from it
     * @param request http request
     * @return validation exception
     */
    protected FunctionnalException getErrorOnce( HttpServletRequest request )
    {
        FunctionnalException fe = (FunctionnalException) request.getSession( ).getAttribute(
                TicketsConstants.PARAMETER_ERROR );
        if ( fe != null )
        {
            request.getSession( ).removeAttribute( TicketsConstants.PARAMETER_ERROR );
        }
        return fe;
    }

    /**
     * Return html code for error message
     * @param e functionnal exception
     * @return html
     */
    protected String getHtmlError( FunctionnalException e )
    {
        Map<String, Object> model = new HashMap<String, Object>( );
        List<String> messageList = new ArrayList<String>( );
        try
        {
            throw e;
        }
        // Validation error
        catch ( ValidationException ve )
        {
            String typeName = ve.getBean( ).getClass( ).getSimpleName( );

            // Add a validation error message using value, field name and
            // provided
            // message
            for ( ConstraintViolation<?> constraintViolation : ve.getConstraintViolationList( ) )
            {
                String fieldName = getMessage( FIELD_MESSAGE_PREFIX + typeName + "."
                        + correctPath( constraintViolation.getPropertyPath( ) ) );
                messageList.add( getMessage( ERROR_MESSAGE_KEY,
                        String.valueOf( constraintViolation.getInvalidValue( ) ), fieldName,
                        constraintViolation.getMessage( ) ) );
            }
        }
        // Business error
        catch ( BusinessException be )
        {
            messageList.add( getMessage( be.getCode( ), be.getArguments( ) ) );
        }

        model.put( MARK_MESSAGE_LIST, messageList );

        HtmlTemplate template = AppTemplateService.getTemplate( ERROR_TEMPLATE, getLocale( ), model );
        return template.getHtml( );
    }

    /**
     * Ensure path given which use indexing value like [%d] is cleaning
     * @param propertyPath the property path to clean
     * @return the right i18n key
     */
    private String correctPath( Path propertyPath )
    {
        String path = propertyPath.toString( );

        Pattern pattern = Pattern.compile( "\\[\\d*\\]" );
        Matcher matcher = pattern.matcher( path );
        if ( matcher.find( ) )
        {
            path = path.substring( 0, matcher.start( ) )
                    + path.substring( matcher.start( ) + matcher.group( ).length( ), path.length( ) );
        }

        return path;
    }

    /**
     * Manage functionnal exception. Save it into session
     * 
     * @param request the request
     * @param e the e
     * @param targetUrl the target url
     * @return the string
     */
    protected String manageFunctionnalException( HttpServletRequest request, FunctionnalException e, String targetUrl )
    {

        request.getSession( ).setAttribute( TicketsConstants.PARAMETER_ERROR, e );
        return targetUrl;
    }

    /**
     * Get the correct filter to use with data table manager
     * @param request the http request
     * @param filter the bean filter get with request
     * @param markFilter the key of the filter
     * @param dataTable the datatable to use
     * @param <T> the bean filter type
     * @return the filter to use
     */
    protected <T> T getFilterToUse( HttpServletRequest request, T filter, String markFilter,
            DataTableManager<?> dataTable )
    {

        @SuppressWarnings( "unchecked" )
        T filterFromSession = (T) request.getSession( ).getAttribute( markFilter );
        //1) est-ce qu'une recherche vient d'être faite ? 2) est-ce qu'un filtre existe en session ? 3) est-ce que le filtre en session est d'un type héritant du fitre fournit en parametre ?
        T filterToUse = request.getParameter( TicketsConstants.MARK_FILTER ) != null || filterFromSession == null
                || !filterFromSession.getClass( ).isAssignableFrom( filter.getClass( ) ) ? dataTable
                .getAndUpdateFilter( request, filter ) : filterFromSession;
        return filterToUse;
    }

    /**
     * Get the correct data table manager
     * @param request the http request
     * @param markFilter the key of the filter
     * @param jspManage the jsp file to manage the beans
     * @param <T> the bean filter type
     * @return the DataTableManager
     */
    protected <T> DataTableManager<T> getDataTableToUse( HttpServletRequest request, String markFilter, String jspManage )
    {
        DataTableManager<T> dataTableFromSession = loadDataTableFromSession( request, markFilter );
        DataTableManager<T> dataTablePartner = dataTableFromSession != null ? dataTableFromSession
                : new DataTableManager<T>( jspManage, StringUtils.EMPTY, 10, true );

        return dataTablePartner;
    }

    @SuppressWarnings( "unchecked" )
    protected <T> DataTableManager<T> getAbstractDataTableManager( HttpServletRequest request, Object filter,
            String keyDataTable, String jspManage, Object service, Method findByFilter )
    {

        //si un objet est déjà présent en session, on l'utilise
        DataTableManager<T> dataTableToUse = getDataTableToUse( request, keyDataTable, jspManage );

        //determination de l'utilisation d'un nouveau filtre (recherche) ou de celui présent en session (changement de page)
        Object filterToUse = getFilterToUse( request, filter, MARK_FILTER, dataTableToUse );
        BeanUtils.copyProperties( filterToUse, filter );

        //mise à jour de la pagination dans le data table pour l'afficahge de la page courante et du nombre d'items
        DataTablePaginationProperties updatePaginator = dataTableToUse.getAndUpdatePaginator( request );

        //obtention manuel des beans à afficher
        PaginationPropertiesAdapterDataTable paginationProperties = new PaginationPropertiesAdapterDataTable(
                updatePaginator );

        ResultList<T> listAllBean = null;
        try
        {
            listAllBean = (ResultList<T>) findByFilter.invoke( service, filterToUse, paginationProperties );
        }
        catch ( Exception e )
        {
            LOGGER.error( "Erreur lors de l'obtention de la liste des beans : " + e );
        }
        request.getSession( ).setAttribute( MARK_FILTER, filterToUse );
        dataTableToUse.setItems( listAllBean, listAllBean.getTotalResult( ) );

        return dataTableToUse;
    }

}
