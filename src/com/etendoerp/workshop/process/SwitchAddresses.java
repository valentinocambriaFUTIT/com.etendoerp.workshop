package com.etendoerp.workshop.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONObject;
import org.hibernate.criterion.Restrictions;
import org.openbravo.client.application.process.BaseProcessActionHandler;
import org.openbravo.dal.core.OBContext;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.common.businesspartner.BusinessPartner;
import org.openbravo.model.common.businesspartner.Location;

import java.util.List;
import java.util.Map;

public class SwitchAddresses extends BaseProcessActionHandler {
    private static final Logger log = LogManager.getLogger();

    @Override
    protected JSONObject doExecute(Map<String, Object> parameters, String content) {

        JSONObject jsonRequest = new JSONObject();
        OBContext.setAdminMode(true);
        try {
            jsonRequest = new JSONObject(content);
            final String recordId = jsonRequest.getString("inpcBpartnerLocationId");
            final String bpartnerId = jsonRequest.getString("C_BPartner_ID");
            List<Location> locations = OBDal.getInstance().createCriteria(Location.class)
                    .add(Restrictions.eq(Location.PROPERTY_BUSINESSPARTNER + ".id",  bpartnerId)).list();
            String description = jsonRequest.getString("inpdescription");
            BusinessPartner businessPartner = (BusinessPartner) OBDal.getInstance()
                    .get(BusinessPartner.class, bpartnerId);

            for (Location locat : locations){
                Boolean isShipToAddress = locat.isShipToAddress();
                locat.setShipToAddress(!isShipToAddress);
                locat.setInvoiceToAddress(isShipToAddress);
            }
            String description_message = "Se ha cambiado la descripcion";
            String description_bpartner = businessPartner.getDescription();
            if (description_bpartner == null){
                businessPartner.setDescription(description_message);
            }
        } catch (Exception e){
            OBDal.getInstance().rollbackAndClose();
            log.error(e);
        }
        return jsonRequest;
    }
}
