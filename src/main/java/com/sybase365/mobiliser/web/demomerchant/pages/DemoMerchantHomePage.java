package com.sybase365.mobiliser.web.demomerchant.pages;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import com.sybase365.mobiliser.web.demomerchant.util.CartItem;
import com.sybase365.mobiliser.web.demomerchant.util.ProductItem;
import com.sybase365.mobiliser.web.demomerchant.util.ProductItemFactory;
import com.sybase365.mobiliser.web.util.MobiliserWebSession;

@SuppressWarnings("all")
public class DemoMerchantHomePage extends BaseDemoMerchantPage {

    private static final long serialVersionUID = 1L;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory
	    .getLogger(DemoMerchantHomePage.class);

    public DemoMerchantHomePage() {
	super();
	LOG.info("Created new DemoMerchantHomePage");
    }

    @Override
    protected void initOwnPageComponents() {
	add(new Link("buy1") {
	    @Override
	    public void onClick() {
		buyItem(99);
	    }
	});

	add(new Link("buy2") {
	    @Override
	    public void onClick() {
		buyItem(100);

	    }
	});

	add(new Link("forward") {
	    @Override
	    public void onClick() {
		setResponsePage(DemoMerchantPayPage.class);
	    }
	});

	createCartView();

    }

    private void createCartView() {
	addOrReplace(new ListView("cartItems",
		((MobiliserWebSession) getSession()).getCartItems()) {
	    @Override
	    protected void populateItem(ListItem item) {
		CartItem itemCart = (CartItem) item.getDefaultModelObject();
		item.addOrReplace(new Label("name", itemCart.getProduct()
			.getName()));
		item.addOrReplace(new Label("count", String.valueOf(itemCart
			.getCount())));
		item.addOrReplace(new Label("cost", formateAmount(itemCart
			.getProduct().getCost())));

	    }
	});
	addOrReplace(new Label("sum", formateAmount(calculaetSum())));

    }

    private long calculaetSum() {
	long sum = 0;
	for (CartItem item : ((MobiliserWebSession) getSession())
		.getCartItems()) {
	    sum = sum + item.getCount() * item.getProduct().getCost();
	}
	return sum;
    }

    private void buyItem(int id) {
	for (CartItem it : ((MobiliserWebSession) getSession()).getCartItems()) {
	    if (it.getProduct().getId() == id) {
		it.setCount(it.getCount() + 1);
		createCartView();
		return;
	    }
	}
	ProductItem pItem = ProductItemFactory.getItem(id);
	CartItem item = new CartItem();
	item.setProduct(pItem);
	item.setCount(1);
	((MobiliserWebSession) getSession()).getCartItems().add(item);
	createCartView();
    }

    private String formateAmount(long amount) {

	return amount / 100 + " " + "\u20AC";

    }

}
