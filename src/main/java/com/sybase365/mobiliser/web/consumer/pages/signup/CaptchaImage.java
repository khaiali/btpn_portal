package com.sybase365.mobiliser.web.consumer.pages.signup;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;

public class CaptchaImage extends NonCachingImage {

    private static final String CAPTCHA_PRODUCER = "captchaProducer";
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(CaptchaImage.class);

    // inject via Spring
    @SpringBean
    private DefaultKaptcha captchaProducer;

    // private DefaultKaptcha captchaProducer;
    public CaptchaImage(String id) {
	super(id);

	setImageResource(new DynamicImageResource() {

	    public byte[] getImageData() {
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		try {
		    BufferedImage bi = getImageCaptchaService();
		    try {
			ImageIO.write(bi, "JPEG", os);
		    } catch (IOException e) {
			LOG.error(
				"# An error occurred while displaying captcha image",
				e);
		    }

		    return os.toByteArray();
		} catch (Exception e) {
		    throw new RuntimeException(e);
		}

	    };

	    private BufferedImage getImageCaptchaService() {

		Request request = RequestCycle.get().getRequest();
		HttpServletRequest httpRequest = ((WebRequest) request)
			.getHttpServletRequest();

		String capText = captchaProducer.createText();

		// store the text in the session
		httpRequest.getSession().setAttribute(
			Constants.KAPTCHA_SESSION_KEY, capText);

		// create the image with the text
		BufferedImage bi = captchaProducer.createImage(capText);

		return bi;

	    }
	});

    }
}