import { useEffect, useState } from "react";
import api from "../../api/axios.js";
import RestaurantCard from "../../components/RestaurantCard.jsx";

export default function Home() {
  const [restaurants, setRestaurants] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get("/public/restaurants")
      .then(({ data }) => setRestaurants(data))
      .finally(() => setLoading(false));
  }, []);

  const filtered = restaurants.filter((r) =>
    r.name.toLowerCase().includes(search.toLowerCase()) ||
    r.city.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="page">
      <h1>Restaurants near you</h1>
      <input
        className="search-box"
        placeholder="Search by restaurant or city..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      {loading && <p>Loading restaurants...</p>}
      {!loading && filtered.length === 0 && <p>No restaurants found.</p>}

      <div className="grid">
        {filtered.map((r) => (
          <RestaurantCard key={r.id} restaurant={r} />
        ))}
      </div>
    </div>
  );
}
