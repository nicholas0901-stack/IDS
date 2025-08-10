export default function BaselineTable({ rows = [] }) {
  return (
    <div className="card shadow-sm rounded-3">
      <div className="card-body">
        <h6 className="card-title mb-3">Baseline Statistics</h6>
        <div className="table-responsive">
          <table className="table table-sm align-middle mb-0">
            <thead className="table-light">
              <tr><th>Event</th><th className="text-end">Mean</th><th className="text-end">Std Dev</th></tr>
            </thead>
            <tbody>
              {rows.map((r, i) => (
                <tr key={i}>
                  <td>{r.event}</td>
                  <td className="text-end">{r.mean}</td>
                  <td className="text-end">{r.stddev}</td>
                </tr>
              ))}
              {rows.length === 0 && (
                <tr><td colSpan="3" className="text-center text-muted py-4">No data yet</td></tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
