import { createContext, useContext, useState, useCallback } from "react";
import api from "../api/axios.js";
import { useAuth } from "./AuthContext.jsx";

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const { user } = useAuth();
  const [cart, setCart] = useState(null);

  const refreshCart = useCallback(async () => {
    if (!user || user.role !== "CUSTOMER") return;
    const { data } = await api.get("/customer/cart");
    setCart(data);
  }, [user]);

  const addToCart = async (foodItemId, quantity = 1) => {
    const { data } = await api.post("/customer/cart/items", { foodItemId, quantity });
    setCart(data);
    return data;
  };

  const updateQuantity = async (cartItemId, quantity) => {
    const { data } = await api.put(`/customer/cart/items/${cartItemId}`, { quantity });
    setCart(data);
  };

  const removeItem = async (cartItemId) => {
    const { data } = await api.delete(`/customer/cart/items/${cartItemId}`);
    setCart(data);
  };

  const clearCart = async () => {
    const { data } = await api.delete("/customer/cart");
    setCart(data);
  };

  return (
    <CartContext.Provider
      value={{ cart, refreshCart, addToCart, updateQuantity, removeItem, clearCart }}
    >
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  return useContext(CartContext);
}
