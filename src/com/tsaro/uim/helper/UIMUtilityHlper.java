package com.tsaro.uim.helper;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import oracle.communications.inventory.api.configuration.BaseConfigurationManager;
import oracle.communications.inventory.api.entity.InventoryConfigurationSpec;
import oracle.communications.inventory.api.entity.LogicalDevice;
import oracle.communications.inventory.api.entity.LogicalDeviceSpecification;
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.entity.ServiceConfigurationItem;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.api.entity.ServiceSpecification;
import oracle.communications.inventory.api.entity.Specification;
import oracle.communications.inventory.api.entity.SpecificationRel;
import oracle.communications.inventory.api.entity.common.InventoryConfigurationVersion;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.logicaldevice.LogicalDeviceManager;
import oracle.communications.inventory.api.logicaldevice.LogicalDeviceSearchCriteria;
import oracle.communications.inventory.api.service.ServiceConfigurationManager;
import oracle.communications.inventory.api.service.ServiceManager;
import oracle.communications.inventory.api.specification.SpecManager;
import oracle.communications.platform.persistence.CriteriaItem;
import oracle.communications.platform.persistence.CriteriaOperator;
import oracle.communications.platform.persistence.Finder;
import oracle.communications.platform.persistence.PersistenceHelper;

public class UIMUtilityHlper {
	
	
	public static Service findOrCreateService() {
		
		return null;
		
	}
	
	public static ServiceConfigurationVersion findOrCreateServiceConfiguration() {
		return null;
		
	}
	
	public static LogicalDevice findOrCreateLD(String logicalDeviceName,String specName) throws Exception {
		
	
		System.out.println("finding logical device"+logicalDeviceName);
		
		LogicalDeviceManager ldMgr=PersistenceHelper.makeLogicalDeviceManager();
		LogicalDeviceSearchCriteria ldSearchCriteria=ldMgr.makeLogicalDeviceSearchCriteria();
		
		CriteriaItem nameItem=ldSearchCriteria.makeCriteriaItem();
		nameItem.setValue(logicalDeviceName);
		nameItem.setOperator(CriteriaOperator.EQUALS);
		ldSearchCriteria.setName(nameItem);
		LogicalDeviceSpecification ldSpec=(LogicalDeviceSpecification) findSpec(specName);
		ldSearchCriteria.setLogicalDeviceSpecification(ldSpec);
		List<LogicalDevice> ldFindList=ldMgr.findLogicalDevice(ldSearchCriteria);

		if(!ldFindList.isEmpty()) {
			System.out.println(logicalDeviceName+" logical device found ");
			return ldFindList.get(0);
		
		}
		else {
			System.out.println(logicalDeviceName+" logical device not found");
			LogicalDevice ldTocreate=ldMgr.makeLogicalDevice();
			ldTocreate.setName(logicalDeviceName);
			ldTocreate.setSpecification(ldSpec);
			List<LogicalDevice> lDListToCreate=new ArrayList<>();
			lDListToCreate.add(ldTocreate);
			System.out.println("Creating logical device "+logicalDeviceName);
			List<LogicalDevice> ldCreatedListList=ldMgr.createLogicalDevice(lDListToCreate);
			if(!ldCreatedListList.isEmpty()) {
				System.out.println(logicalDeviceName+" logical device created ");
				return ldCreatedListList.get(0);
			}
		}
		
		return null;
		
	}
	
public static Service createRFSService(ServiceConfigurationVersion scv ) throws Exception {
		Service rfsService=null;
		ServiceManager serviceMgr=PersistenceHelper.makeServiceManager();
		System.out.println("Finding RFS Specification");
		ServiceSpecification rfsServicespec=(ServiceSpecification) findSpec("Access_RFS");
		Service  serviceRfs=serviceMgr.makeService(Service.class);
		//setting the  spec 
		serviceRfs.setSpecification(rfsServicespec);
		//setting the  name 
		serviceRfs.setName(scv.getService().getSpecification().getName()+"_RFS");
		
		Collection<Service> services = new ArrayList<Service>();
		services.add(serviceRfs);

		List<Service> createdServices = serviceMgr.createService(services);
		rfsService = createdServices.get(0);
		
		
		
		return rfsService;
	}

 private static Specification findSpec(String specName ) {
	Specification spec=null;
	Finder finder=PersistenceHelper.makeFinder();
	Collection<Specification> specList=finder.findByName(Specification.class, specName);
	if(!specList.isEmpty()) {
		return specList.iterator().next();
	}
	return spec;
}

public static ServiceConfigurationVersion createRFSServiceConfiguration(Service rfsser) throws ValidationException {
	ServiceConfigurationManager  scvMgr=
		    PersistenceHelper.makeServiceConfigurationManager();
	ServiceConfigurationVersion scv=scvMgr.makeConfigurationVersion(rfsser);
	InventoryConfigurationSpec invSpec =(InventoryConfigurationSpec) findSpec("Access_RFS_Configuration");
	scv.setName(invSpec.getName()+"_RFS");
	
	scv.setEffDate(new Date());
	InventoryConfigurationVersion createdConfig = 
			scvMgr.createConfigurationVersion(rfsser, scv,invSpec);
	
	
	return (ServiceConfigurationVersion) createdConfig;
	
}

    public static InventoryConfigurationSpec getConfigItemSpecByName(ServiceConfigurationItem parentConfigItem,
		String configItemName) {
    	
    	System.out.println("finding child config specification");

	//log.debug("", "Inside VIL_UIMHelper.getConfigItemSpecByName - parentConfigItem: " + parentConfigItem+ ", configItemName: " + configItemName);
	InventoryConfigurationSpec sChild = null;
	SpecManager specManager = PersistenceHelper.makeSpecManager();

	List<SpecificationRel> specRels = null;
	try {
		specRels = specManager.getSpecificationRels(parentConfigItem.getConfigSpec(), null, true, 0);
	} catch (Exception ex1) {
		//debug("Error......" + ex1.getLocalizedMessage());
	}
	if (specRels != null) {

		for (SpecificationRel relItems : specRels) {
			if (relItems != null) {
				Specification child = relItems.getChild();
				if (child != null && child.getName().equalsIgnoreCase(configItemName.trim())) {
					sChild = (InventoryConfigurationSpec) child;
				}
			}
		}
	}
	//log.debug("", "Exiting VIL_UIMHelper.getConfigItemSpecByName - sChild: " + sChild);
	System.out.println(" child config specification found");
	return sChild;
}

    
    public static InventoryConfigurationSpec getConfigItemSpecFromSCV(ServiceConfigurationVersion scv,String configItemName) {
    	
    	System.out.println("finding parent config specification");

        InventoryConfigurationSpec pChild = null;
        SpecManager specManager = PersistenceHelper.makeSpecManager();

        List<SpecificationRel> specRels = null;

        try {
            
            specRels = specManager.getSpecificationRels(scv.getConfigSpec(), null, true, 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (specRels != null) {
            for (SpecificationRel relItems : specRels) {
                if (relItems != null) {
                    Specification child = relItems.getChild();
                    if (child != null &&
                        child.getName().equalsIgnoreCase(configItemName.trim())) {

                       pChild=(InventoryConfigurationSpec) child;
                    }
                }
            }
        }

        return pChild;
    }



	public static ServiceConfigurationItem findOrCreateParentServiceConfigurationItem(ServiceConfigurationVersion scv,
			String parentConfigItemName) throws ValidationException 
	{
		
		if (scv.getConfigItems() != null)
		{
			System.out.println("finding parent config item"+parentConfigItemName);
			for (ServiceConfigurationItem serConItem : scv.getConfigItems()) 
			{
				if (null != serConItem && null != serConItem.getName()&& serConItem.getName().equalsIgnoreCase(parentConfigItemName)) 
				{
					System.out.println(parentConfigItemName+"config item found");
					return serConItem;
					
					//If found method ends here
				}
                           
			}
			
			
		}
		     //else(scv.getConfigItems()==null)->this means list is not created 
		    
		    //If not found control comes out of the loop and execution continues
			System.out.println(parentConfigItemName+"config item not found......creating");
			/*if else is added here This runs ONLY when configItems is null
		    NOT when item is "not found"*/
			ServiceConfigurationItem parentItem=(ServiceConfigurationItem) scv.getConfigItemTypeConfig();
			ServiceConfigurationManager scvMgr=PersistenceHelper.makeServiceConfigurationManager();
			InventoryConfigurationSpec scvItemSpec=getConfigItemSpecFromSCV(scv,parentConfigItemName);
			
			//create child config items
			
			Collection<?> itemList=scvMgr.createConfigurationItems(parentItem, scvItemSpec,1);//creating 1 config item
			
			if(!itemList.isEmpty())
			{
				ServiceConfigurationItem newItem=(ServiceConfigurationItem) itemList.iterator().next();
				
				newItem.setName(parentConfigItemName);
				newItem.setLabel(parentConfigItemName);
				
				System.out.println(parentConfigItemName+"config item created");
				return newItem;
			}
			
			
		

		return null;
	}
	
	public static ServiceConfigurationItem findOrCreateChildServiceConfigurationItem(ServiceConfigurationItem parentSCIItem,
			String childSCItemName) throws ValidationException {
		
		

		if (parentSCIItem.getChildConfigItems()!= null) {
			System.out.println("finding child configuration item"+childSCItemName);
			for (ServiceConfigurationItem serConItem : parentSCIItem.getChildConfigItems())
			{
				if (null != serConItem && null != serConItem.getName()
						&& serConItem.getName().equalsIgnoreCase(childSCItemName)) 
				{
					System.out.println(childSCItemName+"config item found");
					return serConItem;
				}
				
				

			}
		

		}
			System.out.println(childSCItemName+"config item not found......creating");	
			ServiceConfigurationManager scvMgr=PersistenceHelper.makeServiceConfigurationManager();
			InventoryConfigurationSpec scvItemSpec=getConfigItemSpecByName(parentSCIItem, childSCItemName);
			
			
			
			//create child config items
			
			Collection<?> itemList=scvMgr.createConfigurationItems(parentSCIItem, scvItemSpec,1);//creating 1 config item
			
			if(!itemList.isEmpty())
			{
				ServiceConfigurationItem newItem=(ServiceConfigurationItem) itemList.iterator().next();
				
				newItem.setName(childSCItemName);
				newItem.setLabel(childSCItemName);
				
				System.out.println(childSCItemName+"config item created");
				return newItem;
			}
			
			
		

		return null;
	}
	
	
	
	
	public static ServiceConfigurationItem findChildServiceConfigurationItem(ServiceConfigurationItem parentSCIItem,
			String childSCItemName) throws ValidationException {
		
		

		if (parentSCIItem.getChildConfigItems()!= null) 
		{
			System.out.println("finding child configuration item"+childSCItemName);
			for (ServiceConfigurationItem serConItem : parentSCIItem.getChildConfigItems())
			{
				if (null != serConItem && null != serConItem.getName()
						&& serConItem.getName().equalsIgnoreCase(childSCItemName)) 
				{
					System.out.println(childSCItemName+"config item found");
					return serConItem;
					
				}
				
				

			}
		

		}
			

		return null;
	}
	
	
	
	//creating config items through service configuration version (SCV->ParentConfigItem->ChildConfigItem)
	/*public static ServiceConfigurationItem createParentConfigItem(ServiceConfigurationVersion scv,String configItemName) throws ValidationException
	{
		
		ServiceConfigurationItem parentItem=(ServiceConfigurationItem) scv.getConfigItemTypeConfig();
		ServiceConfigurationManager scvMgr=PersistenceHelper.makeServiceConfigurationManager();
		InventoryConfigurationSpec scvItemSpec=(InventoryConfigurationSpec) findSpec(configItemName);
		
		//create child config items
		
		Collection<?> itemList=scvMgr.createConfigurationItems(parentItem, scvItemSpec,1);//creating 1 config item
		
		if(!itemList.isEmpty())
		{
			ServiceConfigurationItem newItem=(ServiceConfigurationItem) itemList.iterator().next();
			
			newItem.setName(configItemName);
			newItem.setLabel(configItemName);
			
			return newItem;
		}
		
		
		
		return null;
	}*/
	
	
	
	
	
	
	
	
	
	
	//creating config items by Parent config item(ParentConfigItem->ChildConfigItem)
	/*public static ServiceConfigurationItem createChildConfigItem(ServiceConfigurationItem parentItem,String configItemName) throws ValidationException
	{
		ServiceConfigurationManager scvMgr=PersistenceHelper.makeServiceConfigurationManager();
		InventoryConfigurationSpec scvItemSpec=(InventoryConfigurationSpec) findSpec(configItemName);
		
		//create child config items
		
		Collection<?> itemList=scvMgr.createConfigurationItems(parentItem, scvItemSpec,1);//creating 1 config item
		
		if(!itemList.isEmpty())
		{
			ServiceConfigurationItem newItem=(ServiceConfigurationItem) itemList.iterator().next();
			
			newItem.setName(configItemName);
			newItem.setLabel(configItemName);
			
			return newItem;
		}
		
		
		
		return null;
	}*/

	  
}
