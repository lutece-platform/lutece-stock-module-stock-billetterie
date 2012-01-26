package fr.paris.lutece.plugins.stock.modules.billetterie.business.portlet;

import fr.paris.lutece.portal.business.portlet.Portlet;
import fr.paris.lutece.util.sql.DAOUtil;

public class BilletteriePortletDAO implements IBilletterieDAO {

	// Constants
	private static final String SQL_QUERY_SELECT = "SELECT id_portlet, number_show,type_content_portlet FROM billetterie_portlet WHERE id_portlet = ? ";
	private static final String SQL_QUERY_INSERT = "INSERT INTO billetterie_portlet ( id_portlet, number_show,type_content_portlet ) VALUES ( ?, ?,? )";
	private static final String SQL_QUERY_DELETE = "DELETE FROM billetterie_portlet WHERE id_portlet = ? ";
	private static final String SQL_QUERY_UPDATE = "UPDATE billetterie_portlet SET id_portlet = ?, number_show = ?,type_content_portlet=? WHERE id_portlet = ? ";

	/**
	 * Delete record from table
	 * 
	 * @param nPortletId
	 *            The indentifier of the Portlet
	 */
	public void delete(int nPortletId) {
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_DELETE);
		daoUtil.setInt(1, nPortletId);

        daoUtil.executeUpdate();
		daoUtil.free();
	}

	/**
	 * Insert a new record in the table.
	 * 
	 * @param portlet
	 *            The Instance of the Portlet
	 */
	public void insert(Portlet portlet) {

		PortletBilletterie p = (PortletBilletterie) portlet;
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_INSERT);
		daoUtil.setInt(1, p.getId());
		daoUtil.setInt(2, p.getnShow());
        daoUtil.setString( 3, p.getTypeContentPortlet( ) );
		daoUtil.executeUpdate();
		daoUtil.free();
	}

	/**
	 * load the data of dbpagePortlet from the table
	 * 
	 * @param nIdPortlet
	 *            The indentifier of the portlet
	 * @return portlet The instance of the object portlet
	 */
	public Portlet load(int nIdPortlet) {
		PortletBilletterie portlet = new PortletBilletterie();
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_SELECT);
		daoUtil.setInt(1, nIdPortlet);
		daoUtil.executeQuery();
		if (daoUtil.next()) {
			portlet.setId(daoUtil.getInt(1));
			portlet.setnShow(daoUtil.getInt(2));
            portlet.setTypeContentPortlet( daoUtil.getString( 3 ) );
		}
		daoUtil.free();

		return portlet;
	}
	/**
	 * Update the record in the table
	 * 
	 * @param portlet
	 *            The reference of the portlet
	 */
	public void store(Portlet portlet) {

		PortletBilletterie portl = (PortletBilletterie) portlet;
		DAOUtil daoUtil = new DAOUtil(SQL_QUERY_UPDATE);
		daoUtil.setInt(1, portl.getId());
		daoUtil.setInt(2, portl.getnShow());
        daoUtil.setString( 3, portl.getTypeContentPortlet( ) );
		daoUtil.setInt(4, portl.getId());
		daoUtil.executeUpdate();
		daoUtil.free();

	}

}
