/*
 * Copyright (c) 2002-2018, Mairie de Paris
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.util.UriComponentsBuilder;

import fr.paris.lutece.portal.service.util.AppPathService;

/**
 * Used for special solr queries
 *
 * @author abataille
 */
public class BilletterieSolrSearch extends HttpServlet
{
    /**
     *
     */
    private static final long serialVersionUID = -806886865678792151L;
    private static final String MARK_QUOI = "quoi";
    private static final String MARK_OU = "ou";
    private static final String MARK_QUAND = "quand";
    private static final String MARK_QUERY = "query";
    private static final String MARK_CONF = "conf";
    private static final Logger LOGGER = Logger.getLogger( BilletterieSolrSearch.class );
    public static final String FQ_END_DATE_NOW_TO = "&fq=end_date:[NOW TO *]";
    public static final String FQ_TYPE_PRODUCT = "&fq=type:PRODUCT";

    /**
     * Get billetterie specific parameters and call Solr Module.
     *
     * @param request
     *            the request
     * @param response
     *            the response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
    {
        StringBuilder sbReq = new StringBuilder( "" );
        sbReq.append( AppPathService.getBaseUrl( request ) ).append( "jsp/site/Portal.jsp?page=search-solr" );

        StringBuilder sbFilter = new StringBuilder( "" );
        String sQuery = request.getParameter( MARK_QUERY );

        String sQuoi = request.getParameter( MARK_QUOI );
        String sOu = request.getParameter( MARK_OU );
        String sQuand = request.getParameter( MARK_QUAND );

        if ( StringUtils.isNotEmpty( sQuery ) )
        {
            sbReq.append( "&query=" + sQuery );
        }

        if ( StringUtils.isNotEmpty( sQuoi ) )
        {
            sbFilter.append( sQuoi );
        }

        if ( StringUtils.isNotEmpty( sOu ) )
        {
            sbFilter.append( sOu );
        }

        if ( StringUtils.isNotEmpty( sQuand ) )
        {
            sbFilter.append( sQuand );
        }

        if ( sbFilter.toString( ).isEmpty( ) && StringUtils.isEmpty( sQuery ) )
        {
            // Create default filter
            sbReq.append( "&query=*:*" );
        }
        else
        {
            sbReq.append( sbFilter.toString( ) );
        }
        sbReq.append( FQ_END_DATE_NOW_TO );
        sbReq.append( FQ_TYPE_PRODUCT );

        String sConf = request.getParameter( MARK_CONF );
        if ( StringUtils.isNotEmpty( sConf ) )
        {
            sbReq.append( "&conf=" ).append( sConf );
        }
        LOGGER.debug( "Requête SOLR de date, redirection vers " + sbReq.toString( ) );

        response.sendRedirect( encodeUri( sbReq.toString( ) ) );
    }

    protected static String encodeUri( String strUri ) throws IOException
    {
        return UriComponentsBuilder.fromUriString( strUri ).build( ).encode( "UTF-8" ).toUriString( );
    }
}
