package com.shaokat.orderservice.service;

import com.shaokat.orderservice.common.Payment;
import com.shaokat.orderservice.common.TransactionRequest;
import com.shaokat.orderservice.common.TransactionResponse;
import com.shaokat.orderservice.entity.Order;
import com.shaokat.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    public TransactionResponse saveOrder(TransactionRequest request){
        String response = "";
        Order order = request.getOrder();
        Payment payment = request.getPayment();
        payment.setOrderId(order.getOrderId());
        payment.setAmount(order.getPrice());

        orderRepository.save(order);

        //rest call
        Payment paymentResponse = restTemplate.postForObject("http://Payment-Service/payment/doPayment",payment, Payment.class);
        response = paymentResponse.getPaymentStatus().equals("success")?"payment processing successful":"payment not successful order added in cart";
        return new TransactionResponse(order, paymentResponse.getAmount(), paymentResponse.getTransactionId(),response);

    }
}
