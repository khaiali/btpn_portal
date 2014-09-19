package com.sybase365.mobiliser.web.demomerchant.util;

public class ProductItemFactory {
    public static ProductItem getItem(int id) {
	ProductItem item = new ProductItem();
	item.setId(id);
	switch (id) {
	case 99:
	    item.setName("Gone With The Wind");
	    item.setCost(500);
	    break;
	case 100:
	    item.setName("Casablanca (1943)");
	    item.setCost(700);
	    break;
	}

	return item;
    }
}
