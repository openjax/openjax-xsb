/* Copyright (c) 2006 JAX-SB
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.jaxsb.tutorial;

import java.math.BigDecimal;

import org.jaxsb.www.tutorial.invoice.xAA.Invoice;
import org.openjax.xml.datatype.Date;

public class MarshalHowTo {
  public static void main(final String[] args) {
    final Invoice invoice = new Invoice();
    invoice.setDate(new Invoice.Date(new Date(2003, 1, 7)));

    invoice.setNumber(new Invoice.Number(1458L));

    final Invoice.BillingAddress billingAddress = new Invoice.BillingAddress();
    billingAddress.setName(new Invoice.BillingAddress.Name("Ian Barking"));
    billingAddress.setAddress(new Invoice.BillingAddress.Address("123 Kennel Street"));
    billingAddress.setCity(new Invoice.BillingAddress.City("Dachshund City"));
    billingAddress.setPostalCode(new Invoice.BillingAddress.PostalCode(98765L));
    billingAddress.setCountry(new Invoice.BillingAddress.Country("US"));

    invoice.setBillingAddress(billingAddress);

    final Invoice.ShippingAddress shippingAddress = new Invoice.ShippingAddress();
    shippingAddress.setName(new Invoice.BillingAddress.Name("Retail Dept."));
    shippingAddress.setAddress(new Invoice.BillingAddress.Address("888 Dogbowl Street"));
    shippingAddress.setCity(new Invoice.BillingAddress.City("Pet City"));
    shippingAddress.setPostalCode(new Invoice.BillingAddress.PostalCode(98765L));
    shippingAddress.setCountry(new Invoice.BillingAddress.Country("US"));

    invoice.setShippingAddress(shippingAddress);

    final Invoice.BilledItems billedItems = new Invoice.BilledItems();

    final Invoice.BilledItems.Item item1 = new Invoice.BilledItems.Item();
    item1.setDescription(new Invoice.BilledItems.Item.Description("Studded Collar"));
    item1.setCode(new Invoice.BilledItems.Item.Code(45342L));
    item1.setQuantity(new Invoice.BilledItems.Item.Quantity(10L));
    item1.setPrice(new Invoice.BilledItems.Item.Price(BigDecimal.valueOf(11.95)));

    billedItems.addItem(item1);

    final Invoice.BilledItems.Item item2 = new Invoice.BilledItems.Item();
    item2.setDescription(new Invoice.BilledItems.Item.Description("K9 Pet Coat"));
    item2.setCode(new Invoice.BilledItems.Item.Code(25233L));
    item2.setQuantity(new Invoice.BilledItems.Item.Quantity(5L));
    item2.setPrice(new Invoice.BilledItems.Item.Price(BigDecimal.valueOf(25.01)));

    billedItems.addItem(item2);

    invoice.setBilledItems(billedItems);

    System.out.println(invoice);
  }
}