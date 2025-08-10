import { PieChart, Pie, Cell, Tooltip, ResponsiveContainer } from 'recharts';

export default function Donut({ data = [], title = 'Chart title', height = 220 }) {
  const COLORS = ['#16a34a', '#60a5fa', '#f59e0b', '#ef4444', '#8b5cf6'];

  return (
    <div className="card shadow-sm rounded-3 h-100">
      <div className="card-body">
        <h6 className="card-title mb-3">{title}</h6>
        <div style={{ width: '100%', height }}>
          <ResponsiveContainer>
            <PieChart>
              <Pie data={data} dataKey="value" nameKey="name" innerRadius="55%" outerRadius="85%">
                {data.map((_, i) => (
                  <Cell key={i} fill={COLORS[i % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip formatter={(v, n, p) => [`${v} (${(p.percent * 100).toFixed(0)}%)`, p.payload.name]} />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
}
