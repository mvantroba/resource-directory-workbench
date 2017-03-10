package de.thk.rdw.rd.resources;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

import de.thk.rdw.rd.uri.UriUtils;
import de.thk.rdw.rd.uri.UriVariable;

public class RdResource extends CoapResource {

	private static final Logger LOGGER = Logger.getLogger(RdResource.class.getName());

	public RdResource() {
		super("rd");
		getAttributes().addResourceType("core.rd");
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		LOGGER.log(Level.INFO, "Registration request from {0}:{1}.",
				new Object[] { exchange.getSourceAddress().getHostAddress(), exchange.getSourcePort() });
		exchange.respond(preparePostResponse(exchange));
	}

	private Response preparePostResponse(CoapExchange exchange) {
		Map<UriVariable, String> variables = initVariablesFromExchange(exchange);
		EndpointResource newEndpoint;
		EndpointResource existingEndpoint;
		Response response;
		// Check if query contains mandatory variable endpoint name.
		if (variables.containsKey(UriVariable.END_POINT)) {
			newEndpoint = new EndpointResource(variables);
			existingEndpoint = findChildEndpointResource(newEndpoint);
			// Check if the endpoint is already registered.
			if (existingEndpoint == null) {
				add(newEndpoint);
				newEndpoint.updateResources(exchange.advanced().getRequest().getPayloadString());
				response = new Response(ResponseCode.CREATED);
				exchange.setLocationPath(newEndpoint.getURI());
				LOGGER.log(Level.INFO, "Added new endpoint: {0}.", new Object[] { newEndpoint.toString() });
			} else {
				existingEndpoint.updateVariables(variables);
				existingEndpoint.updateResources(exchange.advanced().getRequest().getPayloadString());
				response = new Response(ResponseCode.CHANGED);
				exchange.setLocationPath(existingEndpoint.getURI());
				LOGGER.log(Level.INFO, "Updated endpoint: {0}.", new Object[] { existingEndpoint.toString() });
			}
		} else {
			response = new Response(ResponseCode.BAD_REQUEST);
			response.setPayload(String.format("Uri variable \"%s\" is mandatory.", UriVariable.END_POINT));
		}
		return response;
	}

	private Map<UriVariable, String> initVariablesFromExchange(CoapExchange exchange) {
		Map<UriVariable, String> result = new EnumMap<>(UriVariable.class);
		Map<UriVariable, String> variables = UriUtils.parseUriQuery(exchange.getRequestOptions().getUriQuery());
		String endpoint = variables.get(UriVariable.END_POINT);
		String domain = variables.get(UriVariable.DOMAIN);
		String endpointType = variables.get(UriVariable.END_POINT_TYPE);
		String lifetime = variables.get(UriVariable.LIFE_TIME);
		String context = variables.get(UriVariable.CONTEXT);
		if (endpoint != null) {
			result.put(UriVariable.END_POINT, validateVariable(UriVariable.END_POINT, endpoint));
		}
		result.put(UriVariable.DOMAIN, validateVariable(UriVariable.DOMAIN, domain));
		result.put(UriVariable.END_POINT_TYPE, validateVariable(UriVariable.END_POINT_TYPE, endpointType));
		result.put(UriVariable.LIFE_TIME, validateVariable(UriVariable.LIFE_TIME, lifetime));
		result.put(UriVariable.CONTEXT, initContextFromRequest(exchange.advanced().getRequest(), context));
		return result;
	}

	private String validateVariable(UriVariable uriVariable, String value) {
		String result = null;
		if (value != null) {
			try {
				uriVariable.validate(value);
				result = value;
			} catch (IllegalArgumentException e) {
				LOGGER.log(Level.WARNING, e.getMessage());
			}
		}
		return result;
	}

	private String initContextFromRequest(Request request, String context) {
		String result = null;
		String scheme = null;
		String host = null;
		int port = -1;
		URI uri;
		if (context != null) {
			try {
				// Uri at which the server is available.
				uri = new URI(context);
				scheme = uri.getScheme();
				host = uri.getHost();
				port = uri.getPort();
			} catch (URISyntaxException e) {
				LOGGER.log(Level.WARNING, e.getMessage());
			}
		}
		// If the context parameter is not in the request, source scheme, host
		// and port are assumed.
		scheme = scheme != null ? scheme : getSchemeFromRequest(request);
		host = host != null ? host : request.getSource().getHostAddress();
		port = port > 0 ? port : request.getSourcePort();
		try {
			result = new URI(scheme, null, host, port, null, null, null).toString();
		} catch (URISyntaxException e) {
			LOGGER.log(Level.WARNING, e.getMessage());
		}
		return result;
	}

	private String getSchemeFromRequest(Request request) {
		String result = request.getScheme();
		if (result == null || result.isEmpty()) {
			if (request.getOptions().getUriPort() != null
					&& request.getOptions().getUriPort().intValue() == CoAP.DEFAULT_COAP_SECURE_PORT) {
				result = CoAP.COAP_SECURE_URI_SCHEME;
			} else {
				result = CoAP.COAP_URI_SCHEME;
			}
		}
		return result;
	}

	private EndpointResource findChildEndpointResource(EndpointResource endpointResource) {
		EndpointResource result = null;
		for (Resource child : getChildren()) {
			String childName = child.getName();
			String childDomain = ((EndpointResource) child).getDomain();
			if (childName != null && childName.equals(endpointResource.getName()) && childDomain != null
					&& childDomain.equals(endpointResource.getDomain())) {
				result = (EndpointResource) child;
				break;
			}
		}
		return result;
	}
}