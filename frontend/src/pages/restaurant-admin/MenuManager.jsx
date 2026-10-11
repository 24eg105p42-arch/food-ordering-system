import { useEffect, useState } from "react";
import api from "../../api/axios.js";

const emptyForm = { name: "", description: "", price: "", imageUrl: "", veg: true, categoryId: "" };

export default function MenuManager() {
  const [menu, setMenu] = useState([]);
  const [categories, setCategories] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [message, setMessage] = useState("");

  const loadMenu = () => api.get("/restaurant-admin/menu").then(({ data }) => setMenu(data));

  useEffect(() => {
    loadMenu();
    api.get("/public/categories").then(({ data }) => setCategories(data));
  }, []);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm({ ...form, [name]: type === "checkbox" ? checked : value });
  };

  const resetForm = () => {
    setForm(emptyForm);
    setEditingId(null);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    const payload = { ...form, price: Number(form.price), categoryId: Number(form.categoryId) };
    try {
      if (editingId) {
        await api.put(`/restaurant-admin/menu/${editingId}`, payload);
      } else {
        await api.post("/restaurant-admin/menu", payload);
      }
      resetForm();
      await loadMenu();
    } catch (err) {
      setMessage(err.response?.data?.message || "Could not save the item.");
    }
  };

  const handleEdit = (item) => {
    setEditingId(item.id);
    setForm({
      name: item.name,
      description: item.description || "",
      price: item.price,
      imageUrl: item.imageUrl || "",
      veg: item.veg,
      categoryId: categories.find((c) => c.name === item.categoryName)?.id || "",
    });
  };

  const handleDelete = async (id) => {
    await api.delete(`/restaurant-admin/menu/${id}`);
    await loadMenu();
  };

  const toggleAvailability = async (item) => {
    await api.patch(`/restaurant-admin/menu/${item.id}/availability`, { available: !item.available });
    await loadMenu();
  };

  return (
    <div className="page">
      <h1>Manage menu</h1>
      {message && <p className="error">{message}</p>}

      <form className="inline-form" onSubmit={handleSubmit}>
        <h3>{editingId ? "Edit item" : "Add a new item"}</h3>
        <input name="name" placeholder="Item name" required value={form.name} onChange={handleChange} />
        <input name="description" placeholder="Description" value={form.description} onChange={handleChange} />
        <input name="price" type="number" step="0.01" min="0" placeholder="Price" required value={form.price} onChange={handleChange} />
        <input name="imageUrl" placeholder="Image URL (optional)" value={form.imageUrl} onChange={handleChange} />
        <select name="categoryId" required value={form.categoryId} onChange={handleChange}>
          <option value="">Select category</option>
          {categories.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
        </select>
        <label className="checkbox-row">
          <input type="checkbox" name="veg" checked={form.veg} onChange={handleChange} /> Vegetarian
        </label>
        <div className="form-actions">
          <button type="submit">{editingId ? "Save changes" : "Add item"}</button>
          {editingId && <button type="button" className="link-btn" onClick={resetForm}>Cancel</button>}
        </div>
      </form>

      <h3>Your items</h3>
      <table className="cart-table">
        <thead>
          <tr><th>Name</th><th>Category</th><th>Price</th><th>Available</th><th></th></tr>
        </thead>
        <tbody>
          {menu.map((item) => (
            <tr key={item.id}>
              <td>{item.name}</td>
              <td>{item.categoryName}</td>
              <td>₹{item.price}</td>
              <td>
                <button className="link-btn" onClick={() => toggleAvailability(item)}>
                  {item.available ? "Available" : "Unavailable"}
                </button>
              </td>
              <td>
                <button className="link-btn" onClick={() => handleEdit(item)}>Edit</button>{" "}
                <button className="link-btn" onClick={() => handleDelete(item.id)}>Delete</button>
              </td>
            </tr>
          ))}
          {menu.length === 0 && <tr><td colSpan={5}>No items yet. Add your first one above.</td></tr>}
        </tbody>
      </table>
    </div>
  );
}
