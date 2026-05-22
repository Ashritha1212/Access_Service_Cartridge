package com.tsaro.uim;
 
import java.util.List;
 
import org.apache.xmlbeans.XmlException;
 
import oracle.communications.inventory.api.entity.BusinessInteraction;
import oracle.communications.inventory.api.entity.BusinessInteractionAttachment;
import oracle.communications.inventory.api.framework.logging.Log;
import oracle.communications.inventory.extensibility.extension.util.ExtensionPointContext;
import oracle.communications.inventory.xmlbeans.BusinessInteractionItemType;
import oracle.communications.inventory.xmlbeans.InteractionDocument;
import oracle.communications.inventory.xmlbeans.ParameterType;
import oracle.communications.platform.exception.ValidationException;
 
public class ValidateAccessCaptureRequest {
	@SuppressWarnings("unlikely-arg-type")
	public void validateRequest(ExtensionPointContext context,Log log) throws ValidationException, XmlException {
		log.info("ValidateAccessCaptureRequest", "validateRequest started ");
		BusinessInteraction businessInteraction=(BusinessInteraction) context.getArguments()[0];
		BusinessInteractionAttachment biAttachament=(BusinessInteractionAttachment) context.getArguments()[1];
		if(businessInteraction==null) {
			throw new ValidationException("businessInteraction is empty" );
		}
		String requestXml=biAttachament.convertContentToString();
		InteractionDocument doc = InteractionDocument.Factory.parse(requestXml);
		
		List<BusinessInteractionItemType>  biItemTypeList=doc.getInteraction().getBody().getItemList();
		if(!biItemTypeList.isEmpty()) {
			
			boolean mediaTypeFound = false;
			boolean peDeviceFound = false;
			boolean switchFound = false;
			boolean oltFound = false;

			for (BusinessInteractionItemType biItemType : biItemTypeList) {
			    if (biItemType != null && biItemType.getParameterList() != null) {

			        List<ParameterType> paramList = biItemType.getParameterList();

			        for (ParameterType param : paramList) {
			            if (param != null && param.getName() != null) {

			                String name = param.getName().trim();

			                if ("MediaType".equals(name)) {
			                    mediaTypeFound = true;
			                } else if ("PeDeviceName".equals(name)) {
			                    peDeviceFound = true;
			                } else if ("SwitchHostName".equals(name)) {
			                    switchFound = true;
			                } else if ("OltDeviceName".equals(name)) {
			                    oltFound = true;
			                }
			            }
			        }
			    }
			}

			

			if (!mediaTypeFound) {
				System.out.println("MediaType Mandatory");
			    throw new ValidationException("MediaType mandatory");
			}
			if (!peDeviceFound) {
				System.out.println("PeDeviceName Mandatory");
			    throw new ValidationException("PeDeviceName mandatory");
			}
			if (!switchFound) {
				System.out.println("SwitchHostName Mandatory");
			    throw new ValidationException("SwitchHostName mandatory");
			}
			if (!oltFound) {
				System.out.println("OltDeviceName Mandatory");
			    throw new ValidationException("OltDeviceName mandatory");
			}
		}
		log.info("ValidateAccessCaptureRequest", "validateRequest ended ");
	}
		
}

 



