import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts';

export default function SparkLine({ data, title='Development Activity' }) {
  return (
    <div className="card shadow-sm rounded-3 h-full">
      <div className="card-body">
        <h6 className="card-title mb-3">{title}</h6>
        <div style={{ width: '100%', height: 220 }}>
          <ResponsiveContainer>
            <LineChart data={data}>
              <XAxis dataKey="x" hide />
              <YAxis hide />
              <Tooltip />
              <Line type="monotone" dataKey="y" strokeWidth={2} dot={false} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
}
