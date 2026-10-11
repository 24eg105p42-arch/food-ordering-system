import { useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useCart } from "../../context/CartContext.jsx";

export default function Cart() {
  const { cart, refreshCart, updateQuantity, removeItem } = useCart();
  const navigate = useNavigate();

  useEffect(() => {
    refreshCart();
  }, [refreshCart]);

  if (!cart) return <div className="page"><p>Loading cart...</p></div>;

  return (
    <div className="page">
      <h1>Your cart</h1>

      {cart.items.length === 0 && (
        <p>Your cart is empty. <Link to="/">Browse restaurants</Link>.</p>
      )}

      {cart.items.length > 0 && (
        <>
          <p className="muted">From {cart.restaurantName}</p>
          <table className="cart-table">
            <thead>
              <tr><th>Item</th><th>Price</th><th>Qty</th><th>Subtotal</th><th></th></tr>
            </thead>
            <tbody>
              {cart.items.map((item) => (
                <tr key={item.cartItemId}>
                  <td>{item.name}</td>
                  <td>₹{item.price}</td>
                  <td>
                    <input
                      type="number"
                      min={1}
                      value={item.quantity}
                      onChange={(e) => updateQuantity(item.cartItemId, Number(e.target.value))}
                      className="qty-input"
                    />
                  </td>
                  <td>₹{item.subtotal}</td>
                  <td>
                    <button className="link-btn" onClick={() => removeItem(item.cartItemId)}>Remove</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="cart-total">
            <strong>Total: ₹{cart.total}</strong>
            <button onClick={() => navigate("/checkout")}>Proceed to checkout</button>
          </div>
        </>
      )}
    </div>
  );
}
