package de.thk.ct.rd.resources.lookup;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.LinkFormat;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import de.thk.ct.rd.resources.GroupResource;
import de.thk.ct.rd.resources.RdGroupResource;

/**
 * The {@link CoapResource} type which allows to perform a group lookup. List of
 * groups can be retrieved by sending a GET request to this resource.
 * 
 * @author Martin Vantroba
 *
 */
public class LookupGroupResource extends CoapResource {

	private RdGroupResource rdGroupResource = null;

	/**
	 * Creates a {@link LookupGroupResource} with name according to the
	 * {@link LinkFormat} and initializes the {@link RdGroupResource} which is
	 * needed to retrieve list of groups.
	 * 
	 * @param rdGroupResource
	 *            Resource to which groups can be registered
	 */
	public LookupGroupResource(RdGroupResource rdGroupResource) {
		super(RdLookupType.GROUP.getType());
		this.rdGroupResource = rdGroupResource;
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		StringBuilder resultPayload = new StringBuilder();

		for (Resource resource : rdGroupResource.getChildren()) {
			if (resource instanceof GroupResource) {
				resultPayload.append(LinkFormat.serializeResource(resource));
			}
		}

		if (resultPayload.length() > 0) {
			exchange.respond(ResponseCode.CONTENT, resultPayload.toString(), MediaTypeRegistry.APPLICATION_LINK_FORMAT);
		} else {
			exchange.respond(ResponseCode.NOT_FOUND);
		}
	}
}
