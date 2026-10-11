import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../../api/axios.js";
import OrderStatusBadge from "../../components/OrderStatusBadge.jsx";

export default function OrderTrack() {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [cancelling, setCancelling] = useState(false);
  const [message, setMessage] = useState("");

  const load = () => api.get(`/customer/orders/${id}`).then(({ data }) => setOrder(data));

  useEffect(() => {
    load();
  }, [id]);

  const handleCancel = async () => {
    setCancelling(true);
    try {
      await api.put(`/customer/orders/${id}/cancel`);
      await load();
    } catch (err) {
      setMessage(err.response?.data?.message || "Could not cancel this order.");
    } finally {
      setCancelling(false);
    }
  };

  if (!order) return <div className="page"><p>Loading order...</p></div>;

  return (
    <div className="page">
      <h1>Order #{order.id}</h1>
      <p className="muted">{order.restaurantName} — {new Date(order.createdAt).toLocaleString()}</p>

      <OrderStatusBadge status={order.status} />
      {message && <p className="error">{message}</p>}

      <h3>Items</h3>
      <ul className="plain-list">
        {order.items.map((item, idx) => (
          <li key={idx}>{item.quantity} x {item.itemName} — ₹{item.unitPrice}</li>
        ))}
      </ul>

      <p><strong>Delivery address:</strong> {order.deliveryAddress}</p>
      <p><strong>Total:</strong> ₹{order.totalAmount}</p>

      {order.status === "PLACED" && (
        <button onClick={handleCancel} disabled={cancelling}>
          {cancelling ? "Cancelling..." : "Cancel order"}
        </button>
      )}
    </div>
  );
}
