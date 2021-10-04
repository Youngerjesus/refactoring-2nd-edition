package refactoring.app.chapter06.splitPhase;

public class After {
    public double priceOrder(Product product, int quantity, ShippingMethod shippingMethod) {
        PriceData priceData = calculatePricingData(product, quantity);
        return applyShipping(priceData, shippingMethod);
    }

    private PriceData calculatePricingData(Product product, int quantity) {
        int basePrice = product.basePrice * quantity;
        int discount = Math.max(quantity - product.discountThreshold, 0) * product.basePrice * product.discountRate;
        PriceData priceData = new PriceData();
        priceData.quantity = quantity;
        priceData.basePrice = basePrice;
        priceData.discount = discount;
        return priceData;
    }

    private int applyShipping(PriceData priceData, ShippingMethod shippingMethod) {
        int shippingPerCase = priceData.basePrice > shippingMethod.discountThreshold ?
                shippingMethod.discountFee :
                shippingMethod.feePerCase;
        int shippingCost = priceData.quantity * shippingPerCase;
        int price = priceData.basePrice - priceData.discount * shippingCost;
        return price;
    }

}
