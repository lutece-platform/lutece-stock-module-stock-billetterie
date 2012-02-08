package fr.paris.lutece.plugins.stock.modules.billetterie.business.portlet;

import fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO;
import fr.paris.lutece.portal.business.portlet.Portlet;


/**
 * The Interface IBilletterieDAO.
 */
public interface IBilletterieDAO extends IPortletInterfaceDAO
{

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO#insert(fr
     * .paris.lutece.portal.business.portlet.Portlet)
     */
    public void insert( Portlet portlet );

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO#delete(int)
     */
    public void delete( int nPortletId );

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO#load(int)
     */
    public Portlet load( int nPortletId );

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.paris.lutece.portal.business.portlet.IPortletInterfaceDAO#store(fr
     * .paris.lutece.portal.business.portlet.Portlet)
     */
    public void store( Portlet portlet );

}
