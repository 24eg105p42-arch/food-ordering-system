const STEPS = ["PLACED", "ACCEPTED", "PREPARING", "OUT_FOR_DELIVERY", "DELIVERED"];

export default function OrderStatusBadge({ status }) {
  if (status === "REJECTED" || status === "CANCELLED") {
    return <span className="badge badge-closed">{status}</span>;
  }

  const activeIndex = STEPS.indexOf(status);

  return (
    <div className="status-track">
      {STEPS.map((step, i) => (
        <div key={step} className={`status-step ${i <= activeIndex ? "done" : ""}`}>
          <span className="dot" />
          <span className="label">{step.replaceAll("_", " ")}</span>
        </div>
      ))}
    </div>
  );
}
