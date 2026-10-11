import { useEffect, useState } from "react";
import api from "../../api/axios.js";

export default function AdminStats() {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    api.get("/admin/stats").then(({ data }) => setStats(data));
  }, []);

  if (!stats) return <div className="page"><p>Loading...</p></div>;

  return (
    <div className="page">
      <h1>Platform statistics</h1>
      <div className="grid stats-grid">
        <div className="card stat-card"><h2>{stats.totalUsers}</h2><p>Total users</p></div>
        <div className="card stat-card"><h2>{stats.totalRestaurants}</h2><p>Total restaurants</p></div>
        <div className="card stat-card"><h2>{stats.totalOrders}</h2><p>Total orders</p></div>
        <div className="card stat-card"><h2>{stats.deliveredOrders}</h2><p>Delivered orders</p></div>
        <div className="card stat-card"><h2>{stats.pendingOrders}</h2><p>Pending orders</p></div>
        <div className="card stat-card"><h2>₹{stats.totalRevenue}</h2><p>Total revenue (delivered)</p></div>
      </div>
    </div>
  );
}
