package com.tsaro.uim.helper;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import oracle.communications.inventory.api.configuration.BaseConfigurationManager;
import oracle.communications.inventory.api.configuration.ConfigurationManager;
import oracle.communications.inventory.api.entity.BusinessInteractionState;
import oracle.communications.inventory.api.entity.ConfigurationStatus;
import oracle.communications.inventory.api.entity.InventoryConfigurationSpec;
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.entity.ServiceAssignment;
import oracle.communications.inventory.api.entity.ServiceConfigurationItem;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.api.entity.ServiceConsumer;
import oracle.communications.inventory.api.entity.ServiceStatus;
import oracle.communications.inventory.api.entity.Specification;
import oracle.communications.inventory.api.entity.common.Configurable;
import oracle.communications.inventory.api.entity.common.InventoryConfigurationVersion;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.policy.RequestPolicyHelper;
import oracle.communications.inventory.api.service.ServiceConfigurationManager;
import oracle.communications.inventory.api.service.ServiceManager;
import oracle.communications.inventory.api.util.Utils;
import oracle.communications.inventory.techpack.common.impl.CommonHelper;
import oracle.communications.platform.persistence.Finder;
import oracle.communications.platform.persistence.PersistenceHelper;
import oracle.communications.platform.persistence.Persistent;

public class ChangeUIMUtilityHelper {
	
	
	
	public static Service findCFSService(ServiceConfigurationVersion scv) {
		  
		 Service cfsService=null;
		 if(scv!=null)
		 {
			 cfsService=scv.getService();
		 }
		  
		  return cfsService;
	  }
	  
	public static  InventoryConfigurationVersion  findLatestCFSServiceConfigurationVersion(Service cfsService)
	{
		InventoryConfigurationVersion invConfigVersion = null;
		ServiceConfigurationManager scm = PersistenceHelper.makeServiceConfigurationManager();
	BusinessInteractionState configState = BusinessInteractionState.IN_PROGRESS;
	/*similarly, other BusinessInteractionStates (COMPLETED, CANCELLED) can also be passed as parameter*/
		List<InventoryConfigurationVersion> configs = scm.getEntityConfigurationVersions(cfsService, configState);
	 
		InventoryConfigurationVersion latestConfig = null;
		if (!Utils.isEmpty(configs)) {
			invConfigVersion = configs.get(0);
			}
			ServiceConfigurationVersion scv = (ServiceConfigurationVersion) invConfigVersion;
			
			return scv;
	}
	  
	  public static ServiceConfigurationItem findCFSConfigItem(ServiceConfigurationVersion scv)
	  {
		  
		  List<ServiceConfigurationItem> sciList = scv.getConfigItems();

			ServiceConfigurationItem cfsConfigitem = null;
			if (!sciList.isEmpty()) {
				for (ServiceConfigurationItem sciItem : sciList) 
				{
					if (sciItem != null && sciItem.getName() != null
							&& sciItem.getName().trim().equalsIgnoreCase("Access_CFS_SCI")) 
					{
						cfsConfigitem = sciItem;
						break;
					}

				}
			}
			
			return cfsConfigitem;
	  }
	  
	
	  
	  public static Service findRFSService(ServiceConfigurationItem cfsConfigItem)
	  {
		     
			 if(cfsConfigItem!=null)
			 {
				 if(cfsConfigItem.getAssignment()!=null)
				 {
					 Persistent rfsSerAssignmnet=cfsConfigItem.getAssignment();
					 
					 if(rfsSerAssignmnet instanceof ServiceAssignment)
					 {
						 
						 return ((ServiceAssignment) rfsSerAssignmnet).getService();
						 
					 }
						 
				 }
				  
			 }
		  
		  return null;
	  }
	  
	  

	  
	  
	  public static ServiceConfigurationVersion findLatestRRFSSCV(Service rfService)
	  {
		  
		  InventoryConfigurationVersion invConfigVersion = null;
		  ServiceConfigurationManager scm = PersistenceHelper.makeServiceConfigurationManager();
		  
		  BusinessInteractionState configState = BusinessInteractionState.COMPLETED;
		/*similarly, other BusinessInteractionStates (COMPLETED, CANCELLED) can also be passed as parameter*/
		 
			List<InventoryConfigurationVersion> configs = scm.getEntityConfigurationVersions(rfService, configState);
		 
			InventoryConfigurationVersion latestConfig = null;
			if (!Utils.isEmpty(configs)) {
				invConfigVersion = configs.get(0);
				}
				ServiceConfigurationVersion scv = (ServiceConfigurationVersion) invConfigVersion;
				
				return scv;
		}
	  
	  public static ServiceConfigurationVersion createNewRFSSCV(Service rfsService) throws ValidationException
	  {
		  ServiceConfigurationManager  scvMgr=PersistenceHelper.makeServiceConfigurationManager();
		  ServiceConfigurationVersion scv = scvMgr.makeConfigurationVersion(rfsService);
		  scv.setEffDate(new Date());
		  
		  ServiceConfigurationVersion newConfig=(ServiceConfigurationVersion)
				  scvMgr.createConfigurationVersion(rfsService,scv );
		return newConfig;
	  }
	  
	  
	  
	  

	  public static ServiceConfigurationVersion findLatestActiveServiceConfigurationVersion(String serviceId, Finder finder,

				Log log) {

			log.debug("", "Inside ImportAndManageTNStateUsingDummyService.findLatestCompletedServiceConfigurationVersion method.");
	 
			ServiceConfigurationVersion latestConfig = null;

			try {

				Collection<Service> servicesFound = finder.findById(Service.class, serviceId);

				if (!servicesFound.isEmpty()) {

					Service service = servicesFound.iterator().next();

					if (service.getAdminState().equals(ServiceStatus.IN_SERVICE)

							|| service.getAdminState().equals(ServiceStatus.PENDING)
							||service.getAdminState().equals(ServiceStatus.SUSPENDED)) {

						List<ServiceConfigurationVersion> configList = service.getConfigurations();

						if (configList != null && !configList.isEmpty()) {

							latestConfig = configList.get(0);

							int versionId = latestConfig.getVersionNumber();

							for (ServiceConfigurationVersion config : configList) {

								if (((config.getVersionNumber() > versionId)
	&& !config.getConfigState().equals(ConfigurationStatus.CANCELLED))

										|| latestConfig.getConfigState().equals(ConfigurationStatus.CANCELLED)) {

									versionId = config.getVersionNumber();

									latestConfig = config;

									log.debug("", "VersionId: " + versionId);

									break;

								}

							}

						}

					}

				}
	 
			} catch (Exception e) {

				log.debug("", "Exception occurred while getting latest configuration of the service: " + e);

			}

			log.debug("", "Inside ImportAndManageTNStateUsingDummyService.findLatestCompletedServiceConfigurationVersion method.");

			return latestConfig;

		}
	 
	  
	  public static InventoryConfigurationVersion createConfiguration(Configurable entityInstance,
				ServiceConfigurationVersion previousConfig, Log log) throws ValidationException {
			log.debug("", "Inside ImportAndManageTNStateUsingDummyService.createConfiguration method.");
			ConfigurationManager cManager = PersistenceHelper.makeConfigurationManager();
			//List<InventoryConfigurationSpec> configSpecs = cManager
					//.getConfigSpecTypeConfig(entityInstance.getSpecification(), true);
			
			InventoryConfigurationSpec invSpec = previousConfig.getConfigSpec();
	 
			BaseConfigurationManager bcd = PersistenceHelper.makeConfigurationManager(ServiceConfigurationVersion.class);
			InventoryConfigurationVersion newConfig = bcd.makeConfigurationVersion(entityInstance);
			String name = entityInstance.getName();
			if (name != null && !name.isEmpty()) {
				newConfig.setDescription(name + " Data_Correction");
				newConfig.setName(name + " Data_Correction");
			}
			if (invSpec != null) {
				newConfig.setConfigSpec(invSpec);
			}
			if(previousConfig!=null) {
				newConfig.setPreviousConfiguration(previousConfig);
			}
			newConfig.setEffDate(new Date());
			log.debug("", "Exiting ImportAndManageTNStateUsingDummyService.createConfiguration method.");
			return bcd.createConfigurationVersion(entityInstance, newConfig);
		}
	  
	  
	  }


