package com.san.paypal.service;

import java.util.ArrayList;
import java.util.List;

import com.paypal.api.payments.*;
import org.springframework.stereotype.Service;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.san.paypal.config.Client;
import com.san.paypal.config.PaypalPaymentIntent;
import com.san.paypal.config.PaypalPaymentMethod;

/**   
* @author xsansan  
* @date 2018年9月12日 
* Description:  
*/
@Service
public class PaypalService {

	APIContext apiContext = new APIContext(Client.clientID, Client.clientSecret, Client.mode);
	
    public Payment createPayment(
            Double total, 
            String currency, 
            PaypalPaymentMethod method, 
            PaypalPaymentIntent intent, 
            String description, 
            String cancelUrl, 
            String successUrl) throws PayPalRESTException {


    	// 支付金额
        Amount amount = new Amount();
        // 设置币种
        amount.setCurrency(currency);
        //设置金额
        amount.setTotal(String.format("%.2f", total));


        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setCity("合肥");
        shippingAddress.setLine1("高新区");
        shippingAddress.setPhone("12345678999");
        shippingAddress.setDefaultAddress(true);
        shippingAddress.setPostalCode("230000");
        shippingAddress.setCountryCode("C2");
        shippingAddress.setState("CN-AH");

        Item item = new Item();
        item.setPrice("200.01");
        item.setName("商品名？");
        item.setDescription("描述");
//        item.setCategory("DIGITAL");
        item.setCurrency("USD");
        item.setQuantity("1"); // 数量

        Item item2 = new Item();
        item2.setPrice("299.99");
        item2.setName("商品名2？");
        item2.setDescription("商品名2");

//        item.setCategory("DIGITAL");
        item2.setCurrency("USD");
        item2.setQuantity("1");  // 数量


        List<Item> i = new ArrayList<Item>();
        i.add(item);
        i.add(item2);

        ItemList itemList = new ItemList();
        itemList.setShippingAddress(shippingAddress);
        itemList.setItems(i);

        // 交易信息
        Transaction transaction = new Transaction();
        //交易描述
        transaction.setDescription(description);
//        transaction.setItemList()
        // 将交易金额添加到交易信息中
        transaction.setAmount(amount);
        transaction.setItemList(itemList);


        // Add transaction to a list   添加交易新消息到一个列表中 （一个交易信息应该只包含单件商品）
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);



        // Set payer details  设置支付细节
        Payer payer = new Payer();
        // 设置支付方式
        payer.setPaymentMethod(method.toString());


        // Set redirect URLs  设置重定向路径， 成功或失败返回的路径
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);


        // Add payment details  所以付款细节
        Payment payment = new Payment();
        // 设置意图  ：  出售
        payment.setIntent(intent.toString());
        // 设置支付细节
        payment.setPayer(payer);

        // 设置支付信息
        payment.setTransactions(transactions);
        // 设置重定向路径
        payment.setRedirectUrls(redirectUrls);

        System.out.println(transaction);


        return payment.create(apiContext);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
        Payment payment = new Payment();
        payment.setId(paymentId);
        
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        
        return payment.execute(apiContext, paymentExecute);
    }
}
