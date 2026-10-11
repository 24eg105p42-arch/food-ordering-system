import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../api/axios.js";
import { useCart } from "../../context/CartContext.jsx";

export default function Checkout() {
  const { cart, refreshCart, clearCart } = useCart();
  const navigate = useNavigate();

  const [addresses, setAddresses] = useState([]);
  const [selectedAddressId, setSelectedAddressId] = useState("");
  const [paymentMethod, setPaymentMethod] = useState("COD");
  const [newAddress, setNewAddress] = useState({ label: "Home", line1: "", line2: "", city: "", state: "", pincode: "" });
  const [error, setError] = useState("");
  const [placing, setPlacing] = useState(false);

  useEffect(() => {
    refreshCart();
    api.get("/customer/addresses").then(({ data }) => {
      setAddresses(data);
      if (data.length > 0) setSelectedAddressId(data[0].id);
    });
  }, [refreshCart]);

  const handleAddAddress = async (e) => {
    e.preventDefault();
    const { data } = await api.post("/customer/addresses", { ...newAddress, isDefault: addresses.length === 0 });
    setAddresses([...addresses, data]);
    setSelectedAddressId(data.id);
    setNewAddress({ label: "Home", line1: "", line2: "", city: "", state: "", pincode: "" });
  };

  const handlePlaceOrder = async () => {
    if (!selectedAddressId) {
      setError("Please choose or add a delivery address.");
      return;
    }
    setPlacing(true);
    setError("");
    try {
      const { data } = await api.post("/customer/orders", {
        addressId: selectedAddressId,
        paymentMethod,
      });
      await clearCart();
      navigate(`/orders/${data.id}`);
    } catch (err) {
      setError(err.response?.data?.message || "Could not place the order.");
    } finally {
      setPlacing(false);
    }
  };

  if (!cart) return <div className="page"><p>Loading...</p></div>;

  return (
    <div className="page">
      <h1>Checkout</h1>
      {error && <p className="error">{error}</p>}

      <section className="checkout-section">
        <h3>Delivery address</h3>
        {addresses.map((a) => (
          <label key={a.id} className="radio-row">
            <input
              type="radio"
              name="address"
              checked={String(selectedAddressId) === String(a.id)}
              onChange={() => setSelectedAddressId(a.id)}
            />
            {a.label}: {a.line1}, {a.city}, {a.state} - {a.pincode}
          </label>
        ))}

        <form className="inline-form" onSubmit={handleAddAddress}>
          <h4>Add a new address</h4>
          <input placeholder="Label (Home/Work)" value={newAddress.label}
                 onChange={(e) => setNewAddress({ ...newAddress, label: e.target.value })} />
          <input placeholder="Address line 1" required value={newAddress.line1}
                 onChange={(e) => setNewAddress({ ...newAddress, line1: e.target.value })} />
          <input placeholder="Address line 2 (optional)" value={newAddress.line2}
                 onChange={(e) => setNewAddress({ ...newAddress, line2: e.target.value })} />
          <input placeholder="City" required value={newAddress.city}
                 onChange={(e) => setNewAddress({ ...newAddress, city: e.target.value })} />
          <input placeholder="State" required value={newAddress.state}
                 onChange={(e) => setNewAddress({ ...newAddress, state: e.target.value })} />
          <input placeholder="Pincode" required value={newAddress.pincode}
                 onChange={(e) => setNewAddress({ ...newAddress, pincode: e.target.value })} />
          <button type="submit">Save address</button>
        </form>
      </section>

      <section className="checkout-section">
        <h3>Payment method</h3>
        {["COD", "UPI", "CARD"].map((method) => (
          <label key={method} className="radio-row">
            <input
              type="radio"
              name="payment"
              checked={paymentMethod === method}
              onChange={() => setPaymentMethod(method)}
            />
            {method === "COD" ? "Cash on Delivery" : method}
          </label>
        ))}
      </section>

      <div className="cart-total">
        <strong>Total: ₹{cart.total}</strong>
        <button onClick={handlePlaceOrder} disabled={placing}>
          {placing ? "Placing order..." : "Place order"}
        </button>
      </div>
    </div>
  );
}
