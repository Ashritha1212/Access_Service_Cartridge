package com.tsaro.custom.service.map;

import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.extensibility.extension.util.ExtensionPointRuleContext;

public class MapServiceActions {
	
	public String mapCustomServiceActionsToUIMActions(ExtensionPointRuleContext context,Log log)
	{
		
		String actionFromRequest=  (String) context.getArguments()[2];
		System.out.println("actionFromRequest :"+actionFromRequest);
		String convertedAction=null;
		if (actionFromRequest != null)
		{
			if(actionFromRequest.equalsIgnoreCase("Add")||actionFromRequest.equalsIgnoreCase("Create"))
			{
				return "create";
			}
			else if(actionFromRequest.equalsIgnoreCase("Modify") || actionFromRequest.equalsIgnoreCase("Update") || actionFromRequest.equalsIgnoreCase("Change"))
			{
				return "change";
			}
			else if(actionFromRequest.equalsIgnoreCase("Suspend"))
			{
				return "suspendWithConfiguration";
				//if no CTA concept return "suspend"
			}
			else if(actionFromRequest.equalsIgnoreCase("Resume"))
			{
				return "resumeWithConfiguration";
				//if no CTA concept return "resume"
			}
			else if(actionFromRequest.equalsIgnoreCase("Delete")||actionFromRequest.equalsIgnoreCase("Disconnect"))
			{
				return "disconnect";
			}
		}
		return convertedAction;
	}

}





//if (null!= actionFromRequest)
