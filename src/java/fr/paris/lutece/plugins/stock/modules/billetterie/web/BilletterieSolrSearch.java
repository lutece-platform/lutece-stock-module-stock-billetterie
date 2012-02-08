/*
 * Copyright (c) 2002-2008, Mairie de Paris
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

import fr.paris.lutece.plugins.stock.utils.DateUtils;
import fr.paris.lutece.portal.service.util.AppPathService;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


/**
 * Used for special solr queries
 *
 * @author abataille
 */
public class BilletterieSolrSearch extends HttpServlet {

    /**  
     *
     */
    private static final long serialVersionUID = -806886865678792151L;

    private static final Logger LOGGER = Logger.getLogger( BilletterieSolrSearch.class );

    /**
     * Get billetterie specific parameters and call Solr Module.
     * 
     * @param request the request
     * @param response the response
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException,
            IOException
    {
        StringBuilder sbReq = new StringBuilder( 100 );
        sbReq.append( AppPathService.getBaseUrl( request ) ).append( "jsp/site/Portal.jsp?page=search-solr&" )
                .append( request.getQueryString( ) );

        SimpleDateFormat sdfXml = new SimpleDateFormat( DateUtils.XML_DATE_FORMAT );
        String sShowDateStart = request.getParameter( "show_date_start" );
        String sShowDateEnd = request.getParameter( "show_date_end" );
        if ( sShowDateStart != null )
        {

            Timestamp showDateStart = DateUtils.getDate( sShowDateStart, true );
            String sXmlShowDateStart = sdfXml.format( showDateStart );
            sbReq.append( "&fq=end_date:[" ).append( sXmlShowDateStart ).append( " TO *]" );
        }
        if ( sShowDateEnd != null )
        {

            Timestamp showDateEnd = DateUtils.getDate( sShowDateEnd, true );
            String sXmlShowDateEnd = sdfXml.format( showDateEnd );

            sbReq.append( "&fq=start_date:[* TO " ).append( sXmlShowDateEnd ).append( "]" );
        }

        LOGGER.debug( "Requête SOLR de date, redirection vers " + sbReq.toString( ) );
        response.sendRedirect( sbReq.toString( ) );

    }

}
