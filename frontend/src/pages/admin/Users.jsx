import { useEffect, useState } from "react";
import api from "../../api/axios.js";

export default function AdminUsers() {
  const [users, setUsers] = useState([]);

  const load = () => api.get("/admin/users").then(({ data }) => setUsers(data));

  useEffect(() => {
    load();
  }, []);

  const toggleActive = async (user) => {
    await api.patch(`/admin/users/${user.id}/active`, { active: !user.active });
    await load();
  };

  return (
    <div className="page">
      <h1>All users</h1>
      <table className="cart-table">
        <thead>
          <tr><th>Name</th><th>Email</th><th>Role</th><th>Status</th><th></th></tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.name}</td>
              <td>{u.email}</td>
              <td>{u.role}</td>
              <td>{u.active ? "Active" : "Disabled"}</td>
              <td>
                <button className="link-btn" onClick={() => toggleActive(u)}>
                  {u.active ? "Disable" : "Enable"}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
