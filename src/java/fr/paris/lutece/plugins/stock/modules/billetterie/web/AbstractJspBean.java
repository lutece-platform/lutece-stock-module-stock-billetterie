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

import fr.paris.lutece.plugins.stock.commons.ResultList;
import fr.paris.lutece.plugins.stock.commons.dao.PaginationProperties;
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
import fr.paris.lutece.util.html.DelegatePaginator;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.html.Paginator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;

import org.apache.commons.lang.StringUtils;



/**
 * Abstract class for jsp bean
 * @author abataille
 */
public class AbstractJspBean extends PluginAdminPageJspBean
{

    public static final int N_DEFAULT_ITEMS_PER_PAGE = AppPropertiesService.getPropertyInt(
            TicketsConstants.PROPERTY_DEFAULT_ITEM_PER_PAGE, 50 );
    protected static final String MARK_FILTER = "filter";
    protected static final String MARK_PAGINATOR = "paginator";
    protected static final String MARK_NB_ITEMS_PER_PAGE = "nb_items_per_page";
    private String _strCurrentPageIndex = "";
    private int _nItemsPerPage;

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
        String strPageIndex = Paginator.getPageIndex( request, Paginator.PARAMETER_PAGE_INDEX,
                _strCurrentPageIndex );
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
    protected <T> void validate( T bean ) throws ValidationException
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
                String fieldName = getMessage( "module.stock.billetterie.field." + typeName + "."
                        + constraintViolation.getPropertyPath( ) );
                messageList.add( getMessage( "module.stock.billetterie.validation.error",
                        String.valueOf( constraintViolation.getInvalidValue( ) ), fieldName,
                        constraintViolation.getMessage( ) ) );
            }
        }
        // Business error
        catch ( BusinessException be )
        {
            messageList.add( getMessage( be.getCode( ), be.getArguments( ) ) );
        }
        
        model.put( "messageList", messageList );

        HtmlTemplate template = AppTemplateService.getTemplate( "admin/plugins/stock/modules/billetterie/error.html",
                getLocale( ), model );
        return template.getHtml( );
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

}
