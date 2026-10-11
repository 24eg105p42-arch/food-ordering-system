import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import api from "../../api/axios.js";

export default function Orders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get("/customer/orders").then(({ data }) => setOrders(data)).finally(() => setLoading(false));
  }, []);

  if (loading) return <div className="page"><p>Loading your orders...</p></div>;

  return (
    <div className="page">
      <h1>My orders</h1>
      {orders.length === 0 && <p>You haven't placed any orders yet.</p>}

      <div className="order-list">
        {orders.map((o) => (
          <Link key={o.id} to={`/orders/${o.id}`} className="card order-row">
            <div>
              <strong>{o.restaurantName}</strong>
              <p className="muted">{new Date(o.createdAt).toLocaleString()}</p>
            </div>
            <div>
              <span className={`badge badge-${o.status === "DELIVERED" ? "open" : "closed"}`}>{o.status}</span>
              <p>₹{o.totalAmount}</p>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}
