package com.sybase365.mobiliser.web.btpn.common.behaviours;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.request.target.resource.ResourceStreamRequestTarget;
import org.apache.wicket.util.resource.IResourceStream;

/**
 * This is used to download the file.
 * 
 * @author Vikram Gunda
 */
public abstract class AJAXDownload extends AbstractAjaxBehavior {

	private static final long serialVersionUID = 1L;

	private boolean addAntiCache;

	public AJAXDownload() {
		this(true);
	}

	public AJAXDownload(boolean addAntiCache) {
		super();
		this.addAntiCache = addAntiCache;
	}

	/**
	 * Call this method to initiate the download.
	 */
	public void initiate(AjaxRequestTarget target) {
		String url = getCallbackUrl().toString();

		if (addAntiCache) {
			url = url + (url.contains("?") ? "&" : "?");
			url = url + "antiCache=" + System.currentTimeMillis();
		}

		// the timeout is needed to let Wicket release the channel
		target.appendJavascript("setTimeout(\"window.location.href='" + url + "'\", 100);");
	}

	public void onRequest() {
		getComponent().getRequestCycle().setRequestTarget(
			new ResourceStreamRequestTarget(getResourceStream(), getFileName()));
	}

	/**
	 * Override this method for a file name which will let the browser prompt with a save/open dialog.
	 * 
	 * @see ResourceStreamRequestTarget#getFileName()
	 */
	protected abstract String getFileName();

	/**
	 * Hook method providing the actual resource stream.
	 */
	protected abstract IResourceStream getResourceStream();
}
