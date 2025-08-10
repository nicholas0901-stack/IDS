export default function StatCard({ title, value, delta }) {
  const positive = (delta ?? 0) >= 0;
  return (
    <div className="card shadow-sm rounded-3">
      <div className="card-body">
        <div className="text-muted text-xs uppercase tracking-wide">{title}</div>
        <div className="flex items-baseline gap-2 mt-1">
          <div className="text-2xl font-semibold">{value}</div>
          <span className={`badge ${positive ? 'text-bg-success' : 'text-bg-danger'}`}>
            {positive ? '+' : ''}{delta ?? 0}%
          </span>
        </div>
      </div>
    </div>
  );
}
