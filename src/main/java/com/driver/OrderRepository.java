package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {

    HashMap<String, Order> OrderList = new HashMap<>();
    HashMap<String, DeliveryPartner> PartnerList = new HashMap<>();
    HashMap<String, List<Order>> OrderPartnerMap = new HashMap<>();

    HashMap<String, String> OrderAssignedToPartner = new HashMap<>();

    public void addOrder(Order order) {
        String orderid = order.getId();
        OrderList.put(orderid, order);
    }

    public void addPartner(String partnerId) {
        PartnerList.put(partnerId, new DeliveryPartner(partnerId));
    }

    public void addOrderPartnerPair(String orderId, String partnerId) {
        if (!OrderList.containsKey(orderId) || !PartnerList.containsKey(partnerId)) {
            return;
        }
        if (!OrderAssignedToPartner.containsKey(orderId)) {
            Order orderObject = OrderList.get(orderId);
            // add to delivery partners list of orders
            List<Order> list;
            list = OrderPartnerMap.getOrDefault(partnerId, new ArrayList<>());
            list.add(orderObject);
            OrderPartnerMap.put(partnerId,list);

//        Now Increment the count of orders for delivery partner
            DeliveryPartner PartnerObject = PartnerList.get(partnerId);
            int orderCount = PartnerObject.getNumberOfOrders() + 1;
            PartnerObject.setNumberOfOrders(orderCount);
            // Now add this orderid and partner id to assigned list
            OrderAssignedToPartner.put(orderId, partnerId);
        }
    }

    public Order getOrderById(String orderId) {
        if (OrderList.containsKey(orderId)) {
            return OrderList.get(orderId);
        }
        return null;

    }

    public DeliveryPartner getPartnerById(String partnerId) {
        if (PartnerList.containsKey(partnerId)) {
            return PartnerList.get(partnerId);
        }
        return null;
    }

    public Integer getOrderCountByPartnerId(String partnerId) {
        if (PartnerList.containsKey(partnerId)) {
            return PartnerList.get(partnerId).getNumberOfOrders();
        }
        return 0;
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        if (OrderPartnerMap.containsKey(partnerId)) {
            // create a list of string and store order ids and return
            List<String> ans = new ArrayList<>();
            List<Order> list = OrderPartnerMap.get(partnerId);
            for (Order ord : list) {
                ans.add(ord.getId());
            }
            return ans;
        }
        return null;
    }

    public List<String> getAllOrders() {
        List<String> ans = new ArrayList<>();
        for (Map.Entry<String, Order> mapElement : OrderList.entrySet()) {
            ans.add(mapElement.getKey());
        }
        return ans;
    }

    public Integer getCountOfUnassignedOrders() {
        // total orders -assigned orders=unassigined orders count
        return OrderList.size() - OrderAssignedToPartner.size();
    }

    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {

        if (OrderPartnerMap.containsKey(partnerId)) {
            // convert the time into minutes and check with orderlist that partner has
            String[] arr = time.split(":");
            int min1 = Integer.parseInt(arr[0]) * 60;
            int min2=Integer.parseInt(arr[1]);
            int TimeLimit=min1+min2;
            int CntOfOrdersLeft=0;


            //get the orderlist of deliveryperson
            for (Order od:OrderPartnerMap.get(partnerId)){
                int DeliveryTimeOfOrder=od.getDeliveryTime();
                if(DeliveryTimeOfOrder>TimeLimit)
                    CntOfOrdersLeft++;

            }
            return CntOfOrdersLeft;
        }
        return 0;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        if(OrderPartnerMap.containsKey(partnerId)){
            int LastMaxTime=0;
            List<Order>list=OrderPartnerMap.get(partnerId);
            for(Order od:list){
                if(od.getDeliveryTime()>LastMaxTime)LastMaxTime=od.getDeliveryTime();
            }
            StringBuilder sb=new StringBuilder();
            //convert the minutes time into string
            int hours=LastMaxTime/60;
            int min=LastMaxTime%60;

            if(hours<10)sb.append(0);
            sb.append(hours);
            sb.append(":");
            if(min<10)sb.append(0);
            sb.append(min);
            return sb.toString();
        }
        return null;
    }

    public void deletePartnerById(String partnerId) {
        if(OrderPartnerMap.containsKey(partnerId)){
            // Get the order tobe made by parter, and unlist them as unassigned
            for(Order oid :OrderPartnerMap.get(partnerId)){
                String orderidentity=oid.getId();
                OrderAssignedToPartner.remove(orderidentity);
            }
            OrderPartnerMap.remove(partnerId);
        }else{
            OrderPartnerMap.remove(partnerId);

        }
    }

    public void deleteOrderById(String orderId) {
//        if order is  assigned to partner,then have to delete it in list of orders
          if(OrderAssignedToPartner.containsKey(orderId)){
              String  deliveryPartnerId=OrderAssignedToPartner.remove(orderId);
              List<Order>list=OrderPartnerMap.get(deliveryPartnerId);
              Order orderObject=OrderList.remove(orderId);
              list.remove(orderObject);

          }else{
              OrderList.remove(orderId);
          }
    }
}
