import { Link } from "react-router-dom";

export default function RestaurantCard({ restaurant }) {
  return (
    <Link to={`/restaurants/${restaurant.id}`} className="card restaurant-card">
      <img
        src={restaurant.imageUrl || "https://placehold.co/400x240?text=Restaurant"}
        alt={restaurant.name}
      />
      <div className="card-body">
        <h3>{restaurant.name}</h3>
        <p className="muted">{restaurant.city}</p>
        <span className={`badge ${restaurant.openNow ? "badge-open" : "badge-closed"}`}>
          {restaurant.openNow ? "Open" : "Closed"}
        </span>
      </div>
    </Link>
  );
}
