export default function FoodCard({ item, onAdd }) {
  return (
    <div className="card food-card">
      <img src={item.imageUrl || "https://placehold.co/300x200?text=Food"} alt={item.name} />
      <div className="card-body">
        <div className="food-card-header">
          <span className={`veg-dot ${item.veg ? "veg" : "non-veg"}`} title={item.veg ? "Veg" : "Non-veg"} />
          <h4>{item.name}</h4>
        </div>
        <p className="muted">{item.description}</p>
        <div className="food-card-footer">
          <strong>₹{item.price}</strong>
          <button disabled={!item.available} onClick={() => onAdd(item)}>
            {item.available ? "Add to cart" : "Unavailable"}
          </button>
        </div>
      </div>
    </div>
  );
}
