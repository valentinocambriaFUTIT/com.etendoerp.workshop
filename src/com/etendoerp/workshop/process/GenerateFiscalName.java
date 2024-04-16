package com.etendoerp.workshop.process;


import org.hibernate.criterion.Restrictions;
import org.openbravo.dal.service.OBCriteria;
import org.openbravo.dal.service.OBDal;
import org.openbravo.model.common.businesspartner.BusinessPartner;
import org.openbravo.model.common.businesspartner.Category;
import org.openbravo.scheduling.ProcessBundle;
import org.openbravo.model.common.enterprise.Organization;
import org.openbravo.service.db.DalBaseProcess;

import java.util.List;

public class GenerateFiscalName extends DalBaseProcess {

    private final static String EMPLOYEE = "Employee";
    @Override
    protected void doExecute(ProcessBundle bundle) throws Exception {
        try{

            final OBCriteria<Organization> organizationOBCriteria = OBDal.getInstance().createCriteria(Organization.class)
                    .add(Restrictions.ilike(Organization.PROPERTY_SEARCHKEY, "F&B España, S.A"));
            Organization organization = (Organization) organizationOBCriteria.setMaxResults(1).uniqueResult();
            String organitazionId = organization.getId();

            final OBCriteria<Category> categoryOBCriteria = OBDal.getInstance().createCriteria(Category.class)
                    .add(Restrictions.ilike(Organization.PROPERTY_SEARCHKEY, EMPLOYEE));
            Category category = (Category) categoryOBCriteria.setMaxResults(1).uniqueResult();
            String categoryId = category.getId();

            final OBCriteria<BusinessPartner> businessPartnerOBCriteria = OBDal.getInstance()
                    .createCriteria(BusinessPartner.class).add(Restrictions
                            .eq(BusinessPartner.PROPERTY_ORGANIZATION + ".id", organitazionId))
                    .add(Restrictions.eq(BusinessPartner.PROPERTY_BUSINESSPARTNERCATEGORY + ".id", categoryId));
            List<BusinessPartner> businessPartnerList = businessPartnerOBCriteria.list();

            if (businessPartnerList != null){
                for (BusinessPartner bpartner : businessPartnerList){
                    String aux = bpartner.getName();
                    String aux2 = bpartner.getSearchKey();
                    bpartner.setName2(aux + " - " + aux2);
                }
            }

        } catch (Exception e){

        }
    }
}
