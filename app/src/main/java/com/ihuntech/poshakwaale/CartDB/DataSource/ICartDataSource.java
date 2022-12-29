package com.ihuntech.poshakwaale.CartDB.DataSource;

import com.ihuntech.poshakwaale.CartDB.Model.Cart;

import java.util.List;

import io.reactivex.Flowable;

public interface ICartDataSource {
    Flowable<List<Cart>> getCartItems();

    Flowable<List<Cart>> getCartItemById(int cartItemId);

    int countCartItems();

    void emptyCart();

    void insertToCart(Cart...carts);

    void updateCart(Cart...carts);

    void deleteCartItem(Cart cart);


}
