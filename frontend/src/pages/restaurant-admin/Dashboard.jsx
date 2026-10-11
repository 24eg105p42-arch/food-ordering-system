import { useEffect, useState } from "react";
import api from "../../api/axios.js";

export default function Dashboard() {
  const [restaurant, setRestaurant] = useState(null);
  const [notFound, setNotFound] = useState(false);
  const [form, setForm] = useState({ name: "", description: "", address: "", city: "", phone: "", imageUrl: "" });
  const [saving, setSaving] = useState(false);
  const [message, setMessage] = useState("");

  useEffect(() => {
    api.get("/restaurant-admin/restaurant")
      .then(({ data }) => setRestaurant(data))
      .catch(() => setNotFound(true));
  }, []);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleCreate = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      const { data } = await api.post("/restaurant-admin/restaurant", form);
      setRestaurant(data);
      setNotFound(false);
    } catch (err) {
      setMessage(err.response?.data?.message || "Could not create restaurant.");
    } finally {
      setSaving(false);
    }
  };

  const toggleOpen = async () => {
    const { data } = await api.patch("/restaurant-admin/restaurant/open-status", { open: !restaurant.openNow });
    setRestaurant(data);
  };

  if (!notFound && !restaurant) return <div className="page"><p>Loading...</p></div>;

  if (notFound) {
    return (
      <div className="page">
        <h1>Set up your restaurant</h1>
        <p className="muted">Create your restaurant profile to start adding menu items.</p>
        {message && <p className="error">{message}</p>}
        <form className="inline-form" onSubmit={handleCreate}>
          <input name="name" placeholder="Restaurant name" required value={form.name} onChange={handleChange} />
          <input name="description" placeholder="Short description" value={form.description} onChange={handleChange} />
          <input name="address" placeholder="Street address" required value={form.address} onChange={handleChange} />
          <input name="city" placeholder="City" required value={form.city} onChange={handleChange} />
          <input name="phone" placeholder="Phone" value={form.phone} onChange={handleChange} />
          <input name="imageUrl" placeholder="Image URL (optional)" value={form.imageUrl} onChange={handleChange} />
          <button type="submit" disabled={saving}>{saving ? "Creating..." : "Create restaurant"}</button>
        </form>
      </div>
    );
  }

  return (
    <div className="page">
      <h1>{restaurant.name}</h1>
      <p className="muted">{restaurant.address}, {restaurant.city}</p>
      <p>
        Status:{" "}
        <span className={`badge ${restaurant.approved ? "badge-open" : "badge-closed"}`}>
          {restaurant.approved ? "Approved by admin" : "Pending admin approval"}
        </span>
      </p>
      <p>
        <span className={`badge ${restaurant.openNow ? "badge-open" : "badge-closed"}`}>
          {restaurant.openNow ? "Open for orders" : "Closed"}
        </span>
        {" "}
        <button className="link-btn" onClick={toggleOpen}>
          {restaurant.openNow ? "Mark as closed" : "Mark as open"}
        </button>
      </p>
      {!restaurant.approved && (
        <p className="info-banner">
          Your restaurant is not visible to customers yet. A Super Admin needs to approve it first.
        </p>
      )}
    </div>
  );
}
