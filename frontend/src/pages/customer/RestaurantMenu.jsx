import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../../api/axios.js";
import FoodCard from "../../components/FoodCard.jsx";
import { useCart } from "../../context/CartContext.jsx";
import { useAuth } from "../../context/AuthContext.jsx";
import { useNavigate } from "react-router-dom";

export default function RestaurantMenu() {
  const { id } = useParams();
  const { user } = useAuth();
  const { addToCart } = useCart();
  const navigate = useNavigate();

  const [restaurant, setRestaurant] = useState(null);
  const [menu, setMenu] = useState([]);
  const [message, setMessage] = useState("");

  useEffect(() => {
    api.get(`/public/restaurants/${id}`).then(({ data }) => setRestaurant(data));
    api.get(`/public/restaurants/${id}/menu`).then(({ data }) => setMenu(data));
  }, [id]);

  const handleAdd = async (item) => {
    if (!user) {
      navigate("/login");
      return;
    }
    if (user.role !== "CUSTOMER") {
      setMessage("Only customer accounts can order food.");
      return;
    }
    try {
      await addToCart(item.id, 1);
      setMessage(`${item.name} added to cart.`);
    } catch (err) {
      setMessage(err.response?.data?.message || "Could not add item to cart.");
    }
  };

  if (!restaurant) return <div className="page"><p>Loading...</p></div>;

  return (
    <div className="page">
      <h1>{restaurant.name}</h1>
      <p className="muted">{restaurant.address}, {restaurant.city}</p>
      {message && <p className="info-banner">{message}</p>}

      <div className="grid">
        {menu.map((item) => (
          <FoodCard key={item.id} item={item} onAdd={handleAdd} />
        ))}
        {menu.length === 0 && <p>This restaurant hasn't added any food items yet.</p>}
      </div>
    </div>
  );
}
