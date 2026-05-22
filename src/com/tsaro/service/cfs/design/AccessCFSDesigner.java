package com.tsaro.service.cfs.design;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.impl.values.XmlObjectBase;
import org.apache.xmlbeans.impl.values.XmlStringImpl;
import org.w3c.dom.Node;

import com.tsaro.uim.helper.ChangeUIMUtilityHelper;
import com.tsaro.uim.helper.UIMUtilityHlper;

import oracle.communications.inventory.api.businessinteraction.BusinessInteractionManager;
import oracle.communications.inventory.api.common.BaseInvManager;
import oracle.communications.inventory.api.common.EntityUtils;
import oracle.communications.inventory.api.entity.AssignmentState;
import oracle.communications.inventory.api.entity.BusinessInteraction;
import oracle.communications.inventory.api.entity.BusinessInteractionItem;
import oracle.communications.inventory.api.entity.LogicalDevice;
import oracle.communications.inventory.api.entity.Party;
import oracle.communications.inventory.api.entity.Service;
import oracle.communications.inventory.api.entity.ServiceConfigurationItem;
import oracle.communications.inventory.api.entity.ServiceConfigurationVersion;
import oracle.communications.inventory.api.entity.ServiceStatus;
import oracle.communications.inventory.api.entity.common.ConsumableResource;
import oracle.communications.inventory.api.exception.ValidationException;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.api.framework.logging.LogFactory;
import oracle.communications.inventory.api.framework.security.impl.UserEnvironmentFactory;
import oracle.communications.inventory.api.party.PartyManager;
import oracle.communications.inventory.api.service.ServiceConfigurationManager;
import oracle.communications.inventory.api.service.ServiceManager;
import oracle.communications.inventory.extensibility.extension.util.ExtensionPointContext;
import oracle.communications.inventory.techpack.common.impl.CommonHelper;
import oracle.communications.inventory.xmlbeans.BusinessInteractionItemType;
import oracle.communications.inventory.xmlbeans.ParameterType;
import oracle.communications.platform.persistence.Finder;
import oracle.communications.platform.persistence.PersistenceHelper;

public class AccessCFSDesigner extends BaseInvManager {
	Log log = LogFactory.getLog(AccessCFSDesigner.class);

	public void design(ExtensionPointContext context,Log log) throws Exception {
		ServiceConfigurationVersion cfsConfig = (ServiceConfigurationVersion) context.getArguments()[0];
		BusinessInteractionItemType orderItem = (BusinessInteractionItemType) context.getArguments()[1];
		//cfsConfig.getService().setName(cfsConfig.getService().getId() + "_" + cfsConfig.getService().getSpecification().getName());
		// service Action is Create here
		
	    
		String serviceAction = orderItem.getService().getAction();
		
		System.out.println("*****serviceAction******:"+serviceAction);
		

		if (serviceAction != null && serviceAction.equalsIgnoreCase("add")|| serviceAction.equalsIgnoreCase("Create")) 
	     {
			designAdd(cfsConfig, orderItem);
			System.out.println("All Devices assigned successfully to the Access service");
		}
		else if (serviceAction != null && serviceAction.equalsIgnoreCase("Modify")|| serviceAction.equalsIgnoreCase("Change")) 
		{
			designChange(cfsConfig, orderItem,log);
			System.out.println("All Devices assigned successfully for change scenario");
		}
		
		else if (serviceAction != null && serviceAction.equalsIgnoreCase("suspend"))
		{
			designSuspend(cfsConfig,orderItem,log);
			System.out.println("******CFS and RFS Services suspended******");
		}
		
                
	}

	public static void designAdd(ServiceConfigurationVersion scvofCFS, BusinessInteractionItemType itemtype)
			throws Exception {
		
		System.out.println("getting service");
		Service cfsService=scvofCFS.getService();
		
		Party party=null;
		
		PartyManager partyManager=PersistenceHelper.makePartyManager();
		Finder f=PersistenceHelper.makeFinder();
		     
		
		System.out.println("Party assign");
		
		//set of party service relation
		
		
		
		String mediaType = null;
		String peDeviceHostName = null;
		String switchDeviceHostName = null;
		String oltDeviceHostName = null;
		System.out.println("Reading BI parameter List from the request");
		if (null != itemtype && itemtype.getParameterList() != null) {
			List<ParameterType> paramList = itemtype.getParameterList();
			if (!paramList.isEmpty()) {

				for (ParameterType param : paramList) 
				{
					if (null != param && param.getName() != null) 
					{
						System.out.println("Reading the Parameter Values and storing them in a variable");
						if (param.getName().equals("MediaType"))
						
						{
							
							mediaType = param.getValue().newCursor().getTextValue();
//							param.getValue()-Returns XML object, not plain string
//							getDomNode()-Creates a cursor (pointer) to navigate inside that XML
//							getTextValue()-Extracts only the text inside the XML
							System.out.println("mediaType :"+mediaType);
						}
						if (param.getName().equals("PeDeviceName")) 
						{
							peDeviceHostName =param.getValue().newCursor().getTextValue();
							System.out.println("peDeviceHostName :"+peDeviceHostName);
						}
						if (param.getName().equals("SwitchHostName"))
						{
							switchDeviceHostName = param.getValue().newCursor().getTextValue();
							System.out.println("switchDeviceHostName :"+switchDeviceHostName);
						}
						
						if (param.getName().equals("OltDeviceName")) 
						{
							oltDeviceHostName = param.getValue().newCursor().getTextValue();
							System.out.println("oltDeviceHostName :"+oltDeviceHostName);
						}
					}
				}

			}
		}
        System.out.println("Creating RFS Service");
		Service rfsservice = UIMUtilityHlper.createRFSService(scvofCFS);
		System.out.println("RFS Service Created ");

		System.out.println("creating RFS Service Configuration");
		ServiceConfigurationVersion rfsConfig = UIMUtilityHlper.createRFSServiceConfiguration(rfsservice);
		System.out.println("RFS Service configuration is created under the current BI context");
		/*Collection<ServiceConfigurationVersion> rfsConfigCollection=new ArrayList<ServiceConfigurationVersion>();
		rfsConfigCollection.add(rfsConfig);
		BusinessInteractionManager biMgr = PersistenceHelper.makeBusinessInteractionManager();
		
		  List<BusinessInteractionItem>
		  biItelList=biMgr.findBusinessInteractionItemsForItem(scvofCFS);
		 
		if(!biItelList.isEmpty()) { BusinessInteraction
		parentBi=biItelList.get(0).getBusinessInteraction();
		 biMgr.associateBusinessInteractionToConfigVersions(parentBi, rfsConfigCollection); }
		 */

		
		
		System.out.println("Getting config items under CFS Service Configuration");
		List<ServiceConfigurationItem> sciList = scvofCFS.getConfigItems();

		ServiceConfigurationItem cfsConfigitem = null;
		if (!sciList.isEmpty())
		{
			for (ServiceConfigurationItem sciItem : sciList) {
				if (sciItem != null && sciItem.getName() != null
						&& sciItem.getName().equalsIgnoreCase("Access_CFS_SCI")) {
					cfsConfigitem = sciItem;
					break;
				}

			}
		}

		if (cfsConfigitem != null)
		{
			ServiceConfigurationManager scMgr = PersistenceHelper.makeServiceConfigurationManager();
			System.out.println("Assigning RFS Service to the config item of CFS Service Configuration");
			scMgr.assignResource(cfsConfigitem, rfsservice, null, null);
		}
		
		
		
		if (rfsConfig != null) 
		{
			ServiceConfigurationManager scrfsMgr = PersistenceHelper.makeServiceConfigurationManager();
			System.out.println("Getting Parent config item under RFS Service Configuration");
			ServiceConfigurationItem parentAccessDeviceItem = UIMUtilityHlper.findOrCreateParentServiceConfigurationItem(rfsConfig,
					"Access_Devices");
			
			if (null != parentAccessDeviceItem)
			{
				System.out.println("Getting child config items under Parent config item");
				//EntityUtils.setValue(parentAccessDeviceItem, "mediaType", mediaType);
				ServiceConfigurationItem peDeviceItem = UIMUtilityHlper.findOrCreateChildServiceConfigurationItem(parentAccessDeviceItem, "PeDevice");
				
                if (null != peDeviceItem) 
				  {
					if (null != peDeviceHostName)
						{
						    System.out.println("Finding peDevice ");
							LogicalDevice peDevice = UIMUtilityHlper.findOrCreateLD(peDeviceHostName, "peDevice");
							System.out.println("Assigning "+peDevice);
							scrfsMgr.assignResource(peDeviceItem, peDevice, null, null);
							System.out.println("Assignment status of peDevice:"+peDevice.getCurrentAssignment().getConsumer());
					
							
							//scrfsMgr.referenceEntity(arg0, peDeviceItem);
							EntityUtils.setValue(peDeviceItem, "peDeviceName", peDeviceHostName);
						}
					}
			  
			
			  if (null != mediaType)
			  {
                if (mediaType.equalsIgnoreCase("fiber")||mediaType.equalsIgnoreCase("Microwave")) 
					{
                	
                        
                        ServiceConfigurationItem switchDeviceItem = UIMUtilityHlper.findOrCreateChildServiceConfigurationItem(parentAccessDeviceItem, "SwitchDevice");
            			  if (null != switchDeviceItem) 
            				  {
            				  
            					if (null != switchDeviceHostName)
            					  {
            						System.out.println("Finding SwitchDevice ");
            						LogicalDevice switchDevice = UIMUtilityHlper.findOrCreateLD(switchDeviceHostName,"accessDevice");
            						System.out.println("Assigning "+switchDevice);
            						scrfsMgr.assignResource(switchDeviceItem, switchDevice, null, null);
            						EntityUtils.setValue(switchDeviceItem, "switchHostName", switchDeviceHostName);
                                  }
            					
            					if(mediaType.equalsIgnoreCase("fiber"))
            					{
            						EntityUtils.setValue(parentAccessDeviceItem, "mediaType", mediaType);
            						EntityUtils.setValue(parentAccessDeviceItem, "mwType","No");
            					}
            					else if(mediaType.equalsIgnoreCase("Microwave"))
            					{
            						EntityUtils.setValue(parentAccessDeviceItem, "mediaType", mediaType);
            						EntityUtils.setValue(parentAccessDeviceItem, "mwType","Yes");
            					}
            					 
            					
            					
                              }
                        	
					}
					  

                else if(mediaType.equalsIgnoreCase("GPON"))
				{
                	   
					ServiceConfigurationItem OLTDeviceItem = UIMUtilityHlper.findOrCreateChildServiceConfigurationItem(parentAccessDeviceItem, "OLTDevice");

					  if(null!=OLTDeviceItem)
					  {
						  
						  if(null!=oltDeviceHostName)
						  {
							  System.out.println("Finding OLTDevice ");
							  LogicalDevice oltDevice=UIMUtilityHlper.findOrCreateLD(oltDeviceHostName,"oltDevice");
      						  System.out.println("Assigning "+oltDevice);
                              scrfsMgr.assignResource(OLTDeviceItem, oltDevice, null, null);
      						EntityUtils.setValue(OLTDeviceItem, "OLTDeviceName", oltDeviceHostName);
      						EntityUtils.setValue(parentAccessDeviceItem, "mediaType", mediaType);
    						EntityUtils.setValue(parentAccessDeviceItem, "mwType","No");


						  }
					  }
		              
					
					
					
				}
				
				
					
			  }
			  
			}
			
		}
	}
	public static void designChange(ServiceConfigurationVersion scvofCFS, BusinessInteractionItemType itemtype,Log log)
			throws Exception 
	{
		
		
		String mediaType = null;
		String peDeviceHostName = null;
		String switchDeviceHostName = null;
		String oltDeviceHostName = null;
		System.out.println("Reading BI parameter List from the request");
		if (null != itemtype && itemtype.getParameterList() != null)
		{
			List<ParameterType> paramList = itemtype.getParameterList();
			if (!paramList.isEmpty()) {
				System.out.println("Reading the Parameter Values and storing them in a variable");
				for (ParameterType param : paramList) 
				{
					if (null != param && param.getName() != null) 
					{
						
						if (param.getName().equals("MediaType"))
						
						{
							
							mediaType = param.getValue().newCursor().getTextValue();
//							param.getValue()-Returns XML object, not plain string
//							getDomNode()-Creates a cursor (pointer) to navigate inside that XML
//							getTextValue()-Extracts only the text inside the XML
							System.out.println("mediaType :"+mediaType);
						}
						if (param.getName().equals("PeDeviceName")) 
						{
							peDeviceHostName =param.getValue().newCursor().getTextValue();
							System.out.println("peDeviceHostName :"+peDeviceHostName);
						}
						if (param.getName().equals("SwitchHostName"))
						{
							switchDeviceHostName = param.getValue().newCursor().getTextValue();
							System.out.println("switchDeviceHostName :"+switchDeviceHostName);
						}
						
						if (param.getName().equals("OltDeviceName")) 
						{
							oltDeviceHostName = param.getValue().newCursor().getTextValue();
							System.out.println("oltDeviceHostName :"+oltDeviceHostName);
						}
					}
				}

			}
		}
		String RequestExternalObjectId=null;
		String RequestServiceName=null;
		if(itemtype!=null && itemtype.getService()!=null)
		{
			RequestExternalObjectId=itemtype.getService().getExternalIdentity().getExternalObjectId();
			RequestServiceName=itemtype.getService().getName();
		}
		
		System.out.println("RequestExternalObjectId"+RequestExternalObjectId);
		
		String CFSExternalObjectId=scvofCFS.getService().getExternalObjectId();
		
		String CFSServiceName=scvofCFS.getService().getName();
		System.out.println("ExternalObjectId:"+CFSExternalObjectId);
		System.out.println("CFSServiceName:"+CFSServiceName);
		
		if(!RequestExternalObjectId.equals(CFSExternalObjectId))
		{
			log.validationException("No CFS Service found with given external object id :{0}",null , RequestExternalObjectId);
			
			
		}
		if(!RequestServiceName.equals(CFSServiceName))
		{
			log.validationException("No CFS Service found with Given Service name :{0}",null,RequestServiceName);
		}
		
		
		     //This will give CFS service configuration version 2 not the version which is created as part of "Create" action
		     System.out.println("scvofCFS :"+scvofCFS);
		
		     
		   
		     
		     
		     System.out.println("Finding cfs service ");
		     Service cfsService=ChangeUIMUtilityHelper.findCFSService(scvofCFS);
		     System.out.println("CFS Service found with name:"+cfsService.getName()+"and  id :"+cfsService.getId());
		     
		     
		     System.out.println("Finding latest CFS Service configuration version");
		     ServiceConfigurationVersion latestCFSServiceConfiguration=(ServiceConfigurationVersion) ChangeUIMUtilityHelper.findLatestCFSServiceConfigurationVersion(cfsService);
		     System.out.println("Latest CFS Service configuration found :"+latestCFSServiceConfiguration);
		     
		     
		     System.out.println("Finding CFS Service configuration Item"); 
		     ServiceConfigurationItem cfsServiceConfigurationItem=ChangeUIMUtilityHelper.findCFSConfigItem(latestCFSServiceConfiguration);
		     System.out.println("CFS service configuration Item found with name :"+cfsServiceConfigurationItem.getName());
		     
		     System.out.println("Finding RFS Service assigned to CFS Service Configuration Item");
		     Service rfsService=ChangeUIMUtilityHelper.findRFSService(cfsServiceConfigurationItem);
		     System.out.println("RFS Service found with name :"+rfsService.getName());
		     
		     
		     System.out.println("Finding RFS Service Configuration Version");
		     ServiceConfigurationVersion latestscvOfRFS=ChangeUIMUtilityHelper.findLatestRRFSSCV(rfsService);
		     System.out.println("RFS SCV found:"+latestscvOfRFS);
		     
		     System.out.println("Creating new RFS SCV");
		     ServiceConfigurationVersion newRFSSCV=ChangeUIMUtilityHelper.createNewRFSSCV(rfsService);
		     System.out.println("New RFS SCV created :"+newRFSSCV);
		     
		     
		
		     
		     if (newRFSSCV != null) 
		     {
					ServiceConfigurationManager scrfsMgr = PersistenceHelper.makeServiceConfigurationManager();
					System.out.println("Getting Parent config item under RFS Service Configuration");
					ServiceConfigurationItem parentAccessDeviceItem = UIMUtilityHlper.findOrCreateParentServiceConfigurationItem(newRFSSCV,
							"Access_Devices");
					
					
					if (null != parentAccessDeviceItem)
					{
						System.out.println("Getting child config items under Parent config item");
						//EntityUtils.setValue(parentAccessDeviceItem, "mediaType", mediaType);
						
						
						Collection<ServiceConfigurationItem> itemsToUnallocate = new ArrayList<>();
						ServiceConfigurationItem peDeviceItem = UIMUtilityHlper.findOrCreateChildServiceConfigurationItem(parentAccessDeviceItem, "PeDevice");
						
						
						
		                if (null != peDeviceItem) 
						  {
							if (null != peDeviceHostName)
								{
								System.out.println("Finding peDevice ");
								LogicalDevice peDevice = UIMUtilityHlper.findOrCreateLD(peDeviceHostName, "peDevice");
								Collection<ServiceConfigurationItem> PEList = new ArrayList<>();
									if (peDeviceItem != null && peDeviceItem.getAssignment() != null)
									{   
										System.out.println(peDeviceItem+ "already has an assignmnet");
										PEList.add(peDeviceItem);
									    
									    System.out.println("unallocating "+peDeviceItem);
										scrfsMgr.unallocateInventoryConfigurationItems(PEList);
										System.out.println("unallocated "+peDeviceItem);
									}
									
									
									
									
									
									System.out.println("Assigning "+peDevice);
									scrfsMgr.assignResource(peDeviceItem, peDevice, null, null);
									System.out.println("Assignment status of peDevice:"+peDevice.getCurrentAssignment().getConsumer());
							
									
									//scrfsMgr.referenceEntity(arg0, peDeviceItem);
									EntityUtils.setValue(peDeviceItem, "peDeviceName", peDeviceHostName);
								}
							}
		  			     
		  			  
							
					  
					  
					
					  if (null != mediaType)
					  {
		                if (mediaType.equalsIgnoreCase("fiber")||mediaType.equalsIgnoreCase("Microwave")) 
							{
		                	
		                        
		                        ServiceConfigurationItem switchDeviceItem = UIMUtilityHlper.findOrCreateChildServiceConfigurationItem(parentAccessDeviceItem, "SwitchDevice");
		            			  if (null != switchDeviceItem) 
		            				  {
		            				  
		            					if (null != switchDeviceHostName)
		            					  {
		            						System.out.println("Finding SwitchDevice ");
		            						LogicalDevice switchDevice = UIMUtilityHlper.findOrCreateLD(switchDeviceHostName,"accessDevice");
		            						
		            						Collection<ServiceConfigurationItem> SwitchList = new ArrayList<>();
		            						if (switchDeviceItem != null && switchDeviceItem.getAssignment() != null)
											{  
		            							System.out.println(switchDeviceItem+ "already has an assignmnet");
		            							SwitchList.add(switchDeviceItem);
											    
											    System.out.println("unallocating "+switchDeviceItem);
												scrfsMgr.unallocateInventoryConfigurationItems(SwitchList);
												System.out.println("unallocated "+switchDeviceItem);
											}
		            						
								            
		            						System.out.println("Assigning "+switchDevice);
		            						scrfsMgr.assignResource(switchDeviceItem, switchDevice, null, null);
		            						EntityUtils.setValue(switchDeviceItem, "switchHostName", switchDeviceHostName);
		                                  }
		            					
		            					
		            				  }
		            					
		            					ServiceConfigurationItem OLTDeviceItem = UIMUtilityHlper.findChildServiceConfigurationItem(parentAccessDeviceItem, "OLTDevice");

										  if(null!=OLTDeviceItem)
										  {
											  
											  if(null!=oltDeviceHostName)
											  {
												  Collection<ServiceConfigurationItem> OLTList = new ArrayList<>();
												  
					      						  
					      						if (OLTDeviceItem != null && OLTDeviceItem.getAssignment() != null)
												{
					      							System.out.println(OLTDeviceItem+ "already has an assignmnet");
					      							OLTList.add(OLTDeviceItem);
												    
												    System.out.println("unallocating "+OLTDeviceItem);
													scrfsMgr.unallocateInventoryConfigurationItems(OLTList);
													System.out.println("unallocated "+OLTDeviceItem);
												  }
					      						
				                        	

									            }
										  }
		            					
		            					if(mediaType.equalsIgnoreCase("fiber"))
		            					{
		            						EntityUtils.setValue(parentAccessDeviceItem, "mediaType", mediaType);
		            						EntityUtils.setValue(parentAccessDeviceItem, "mwType","No");
		            					}
		            					else if(mediaType.equalsIgnoreCase("Microwave"))
		            					{
		            						EntityUtils.setValue(parentAccessDeviceItem, "mediaType", mediaType);
		            						EntityUtils.setValue(parentAccessDeviceItem, "mwType","yes");
		            					}
		            					 
							}		
		            					
		                  else if(mediaType.equalsIgnoreCase("GPON"))
						  {
		                	
		                      ServiceConfigurationItem OLTDeviceItem = UIMUtilityHlper.findOrCreateChildServiceConfigurationItem(parentAccessDeviceItem, "OLTDevice");

							  if(null!=OLTDeviceItem)
							  {
								  
								  if(null!=oltDeviceHostName)
								  {
									  Collection<ServiceConfigurationItem> OLTList = new ArrayList<>();
									  System.out.println("Finding OLTDevice ");
									  LogicalDevice oltDevice=UIMUtilityHlper.findOrCreateLD(oltDeviceHostName,"oltDevice");
		      						  
		      						if (OLTDeviceItem != null && OLTDeviceItem.getAssignment() != null)
									{
		      							System.out.println(OLTDeviceItem+ "already has an assignmnet");
		      							OLTList.add(OLTDeviceItem);
									    
									    System.out.println("unallocating "+OLTDeviceItem);
										scrfsMgr.unallocateInventoryConfigurationItems(OLTList);
										System.out.println("unallocated "+OLTDeviceItem);
									}
		      						
		      						
		      						
									System.out.println("Assigning "+oltDevice);
		                            scrfsMgr.assignResource(OLTDeviceItem, oltDevice, null, null);
		      						EntityUtils.setValue(OLTDeviceItem, "OLTDeviceName", oltDeviceHostName);
		      						EntityUtils.setValue(parentAccessDeviceItem, "mediaType", mediaType);
		    						EntityUtils.setValue(parentAccessDeviceItem, "mwType","No");

		    						

								  }
							  }
							  
							  
							  ServiceConfigurationItem switchDeviceItem = UIMUtilityHlper.findChildServiceConfigurationItem(parentAccessDeviceItem, "SwitchDevice");
	            			  if (null != switchDeviceItem) 
	            				  {
	            				  
	            					if (null != switchDeviceHostName)
	            					  {
	            						
	            						Collection<ServiceConfigurationItem> SwitchList = new ArrayList<>();
	            						if (switchDeviceItem != null && switchDeviceItem.getAssignment() != null)
										{  
	            							System.out.println(switchDeviceItem+ "already has an assignmnet");
	            							SwitchList.add(switchDeviceItem);
										    
										    System.out.println("unallocating "+switchDeviceItem);
											scrfsMgr.unallocateInventoryConfigurationItems(SwitchList);
											System.out.println("unallocated "+switchDeviceItem);
										}
	            					
	            					
	                              }
							  
				              }
						
						
							
					  }
					  
					}
		
		
      
            }
		
	     }
	
      }
	

	public static void designSuspend(ServiceConfigurationVersion scvofCFS, BusinessInteractionItemType itemtype,Log log) throws ValidationException
	{
		
		
		String action=itemtype.getService().getAction();
		System.out.println("**********Inside designSuspend*******");
		//System.out.println("scvofCFS"+scvofCFS);
		
		try {
		Finder serviceFinder=PersistenceHelper.makeFinder(); 
		ServiceManager sMgr=PersistenceHelper.makeServiceManager();
		ServiceConfigurationItem cfsConfigItem=null;
		if(scvofCFS!=null)
		{
			cfsConfigItem=ChangeUIMUtilityHelper.findCFSConfigItem(scvofCFS);
			System.out.println("*******cfsConfigItem*******:"+cfsConfigItem);
			Service rfsService=null;
			if(cfsConfigItem!=null)
			{
				rfsService=ChangeUIMUtilityHelper.findRFSService(cfsConfigItem);
					
			   ServiceConfigurationVersion latestRFSServiceConfiguration=null;
				if(rfsService!=null&&(!rfsService.getAdminState().equals(ServiceStatus.PENDING)))
				{
					
					System.out.println("******supending RFS Service******");
					sMgr.suspendService(rfsService);
					System.out.println("******rfsService******:"+rfsService);
					latestRFSServiceConfiguration=ChangeUIMUtilityHelper.findLatestActiveServiceConfigurationVersion(rfsService.getId(),serviceFinder,log);
					System.out.println("******latestRFSServiceConfiguration******:"+latestRFSServiceConfiguration);
					ServiceConfigurationVersion newRFSSCV=null;
					if(latestRFSServiceConfiguration!=null)
					{
						
						newRFSSCV=(ServiceConfigurationVersion) ChangeUIMUtilityHelper.createConfiguration(rfsService,latestRFSServiceConfiguration,log);
					    System.out.println("******newRFSSCV******:"+newRFSSCV);
					    
					    if (newRFSSCV != null) 
					     {
								ServiceConfigurationManager scrfsMgr = PersistenceHelper.makeServiceConfigurationManager();
								System.out.println("******Getting Parent config item under RFS Service Configuration******");
								ServiceConfigurationItem parentAccessDeviceItem = UIMUtilityHlper.findOrCreateParentServiceConfigurationItem(newRFSSCV,
										"Access_Devices");
								
								
								if (null != parentAccessDeviceItem)
								{
									EntityUtils.setValue(parentAccessDeviceItem, "serviceAction", action);
								}
					       }
					
				      }
				
			        }
		
			      }
		
		         }
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	
	
	
	
	public static void suspend(ExtensionPointContext context,Log log) throws ValidationException 
	{
		
		System.out.println("******Inside suspend******");
		
		Service cfsService=(Service) context.getArguments()[0];
		
		System.out.println("******cfsService****** :"+cfsService);
		try {
		ServiceManager sMgr=PersistenceHelper.makeServiceManager();
		Finder serviceFinder=PersistenceHelper.makeFinder();
		//sMgr.suspendService(cfsService);
		
		ServiceConfigurationVersion newCFSSCV=null;
		if(cfsService!=null)
		{
		
			ServiceConfigurationVersion latestCFSServiceConfiguration=ChangeUIMUtilityHelper.findLatestActiveServiceConfigurationVersion(cfsService.getId(),serviceFinder,log);
			System.out.println("*****Latest CFS Service configuration found****** :"+latestCFSServiceConfiguration);
			if(latestCFSServiceConfiguration!=null)
			{
				newCFSSCV=(ServiceConfigurationVersion) ChangeUIMUtilityHelper.createConfiguration(cfsService,latestCFSServiceConfiguration,log);
				System.out.println("******newCFSSCV******:"+newCFSSCV);
				
			}
		}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	     
	}

}		



	



