import { useEffect, useState } from "react";
import api from "../../api/axios.js";

export default function AdminCategories() {
  const [categories, setCategories] = useState([]);
  const [name, setName] = useState("");
  const [imageUrl, setImageUrl] = useState("");
  const [message, setMessage] = useState("");

  const load = () => api.get("/admin/categories").then(({ data }) => setCategories(data));

  useEffect(() => {
    load();
  }, []);

  const handleAdd = async (e) => {
    e.preventDefault();
    try {
      await api.post("/admin/categories", { name, imageUrl });
      setName("");
      setImageUrl("");
      await load();
    } catch (err) {
      setMessage(err.response?.data?.message || "Could not add category.");
    }
  };

  const handleDelete = async (id) => {
    await api.delete(`/admin/categories/${id}`);
    await load();
  };

  return (
    <div className="page">
      <h1>Food categories</h1>
      {message && <p className="error">{message}</p>}

      <form className="inline-form" onSubmit={handleAdd}>
        <input placeholder="Category name" required value={name} onChange={(e) => setName(e.target.value)} />
        <input placeholder="Image URL (optional)" value={imageUrl} onChange={(e) => setImageUrl(e.target.value)} />
        <button type="submit">Add category</button>
      </form>

      <ul className="plain-list">
        {categories.map((c) => (
          <li key={c.id}>
            {c.name} <button className="link-btn" onClick={() => handleDelete(c.id)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  );
}
