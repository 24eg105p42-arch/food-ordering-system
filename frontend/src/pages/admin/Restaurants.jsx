import { useEffect, useState } from "react";
import api from "../../api/axios.js";

export default function AdminRestaurants() {
  const [restaurants, setRestaurants] = useState([]);

  const load = () => api.get("/admin/restaurants").then(({ data }) => setRestaurants(data));

  useEffect(() => {
    load();
  }, []);

  const approve = async (id) => {
    await api.put(`/admin/restaurants/${id}/approve`);
    await load();
  };

  return (
    <div className="page">
      <h1>All restaurants</h1>
      <table className="cart-table">
        <thead>
          <tr><th>Name</th><th>City</th><th>Open</th><th>Approved</th><th></th></tr>
        </thead>
        <tbody>
          {restaurants.map((r) => (
            <tr key={r.id}>
              <td>{r.name}</td>
              <td>{r.city}</td>
              <td>{r.openNow ? "Yes" : "No"}</td>
              <td>{r.approved ? "Yes" : "No"}</td>
              <td>
                {!r.approved && <button className="link-btn" onClick={() => approve(r.id)}>Approve</button>}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
