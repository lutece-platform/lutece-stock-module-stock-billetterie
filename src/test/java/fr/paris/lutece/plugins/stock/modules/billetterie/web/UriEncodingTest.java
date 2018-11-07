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

import org.junit.Assert;
import org.junit.Test;

/**
 * UriEncodingTest
 */
public class UriEncodingTest
{

    /**
     * test URI encoding
     * 
     * @throws Exception
     *             e
     */
    @Test
    public void testEncodeUri( ) throws Exception
    {
        String req = "http://localhost:8080/lutece/jsp/site/Portal.jsp?page=search-solr&sort_name=title_string&sort_order=asc&query=(test AND categorie:(A OR B)))&fq=tarif_reduit_string:true&fq=end_date:[* TO 2009-05-14:12:12Z]&bar=*:*";
        String out = BilletterieSolrSearch.encodeUri( req );
        String ref = "http://localhost:8080/lutece/jsp/site/Portal.jsp?page=search-solr&sort_name=title_string&sort_order=asc&query=(test%20AND%20categorie:(A%20OR%20B)))&fq=tarif_reduit_string:true&fq=end_date:%5B*%20TO%202009-05-14:12:12Z%5D&bar=*:*"; // ref
                                                                                                                                                                                                                                                              // generated
                                                                                                                                                                                                                                                              // with
                                                                                                                                                                                                                                                              // spring's
                                                                                                                                                                                                                                                              // 3.1.0.RELEASE
                                                                                                                                                                                                                                                              // org.springframework.web.util.UriUtils:encodeUri(String,
                                                                                                                                                                                                                                                              // String)
        Assert.assertEquals( ref, out );
    }

}
