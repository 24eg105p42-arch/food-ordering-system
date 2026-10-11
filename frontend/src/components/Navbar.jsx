import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";
import { useCart } from "../context/CartContext.jsx";

export default function Navbar() {
  const { user, logout } = useAuth();
  const { cart } = useCart();
  const navigate = useNavigate();

  const itemCount = cart?.items?.reduce((sum, i) => sum + i.quantity, 0) || 0;

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <Link to="/" className="brand">🍔 FoodHub</Link>

      <div className="nav-links">
        {!user && (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}

        {user?.role === "CUSTOMER" && (
          <>
            <Link to="/">Restaurants</Link>
            <Link to="/cart">Cart ({itemCount})</Link>
            <Link to="/orders">My Orders</Link>
          </>
        )}

        {user?.role === "RESTAURANT_ADMIN" && (
          <>
            <Link to="/restaurant-admin">Dashboard</Link>
            <Link to="/restaurant-admin/menu">Menu</Link>
            <Link to="/restaurant-admin/orders">Orders</Link>
          </>
        )}

        {user?.role === "SUPER_ADMIN" && (
          <>
            <Link to="/admin/restaurants">Restaurants</Link>
            <Link to="/admin/categories">Categories</Link>
            <Link to="/admin/users">Users</Link>
            <Link to="/admin/stats">Stats</Link>
          </>
        )}

        {user && (
          <>
            <span className="user-chip">{user.name}</span>
            <button className="link-btn" onClick={handleLogout}>Logout</button>
          </>
        )}
      </div>
    </nav>
  );
}
