import { useEffect, useState } from "react";
import api from "../../api/axios.js";
import OrderStatusBadge from "../../components/OrderStatusBadge.jsx";

// The next legal status for each current status, matching the backend's state machine.
const NEXT_STATUS = {
  PLACED: ["ACCEPTED", "REJECTED"],
  ACCEPTED: ["PREPARING"],
  PREPARING: ["OUT_FOR_DELIVERY"],
  OUT_FOR_DELIVERY: ["DELIVERED"],
};

export default function IncomingOrders() {
  const [orders, setOrders] = useState([]);
  const [message, setMessage] = useState("");

  const loadOrders = () => api.get("/restaurant-admin/orders").then(({ data }) => setOrders(data));

  useEffect(() => {
    loadOrders();
  }, []);

  const updateStatus = async (orderId, status) => {
    try {
      await api.put(`/restaurant-admin/orders/${orderId}/status`, { status });
      await loadOrders();
    } catch (err) {
      setMessage(err.response?.data?.message || "Could not update order status.");
    }
  };

  return (
    <div className="page">
      <h1>Incoming orders</h1>
      {message && <p className="error">{message}</p>}

      {orders.length === 0 && <p>No orders yet.</p>}

      <div className="order-list">
        {orders.map((o) => (
          <div key={o.id} className="card order-row-detail">
            <div>
              <strong>Order #{o.id}</strong>
              <p className="muted">{new Date(o.createdAt).toLocaleString()}</p>
              <ul className="plain-list">
                {o.items.map((item, idx) => (
                  <li key={idx}>{item.quantity} x {item.itemName}</li>
                ))}
              </ul>
              <p>Deliver to: {o.deliveryAddress}</p>
              <p><strong>Total: ₹{o.totalAmount}</strong></p>
            </div>
            <div>
              <OrderStatusBadge status={o.status} />
              <div className="form-actions">
                {(NEXT_STATUS[o.status] || []).map((next) => (
                  <button key={next} onClick={() => updateStatus(o.id, next)}>
                    Mark as {next.replaceAll("_", " ")}
                  </button>
                ))}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
