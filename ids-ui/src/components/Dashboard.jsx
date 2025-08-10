import React, { useEffect, useState } from 'react';
import StatCard from './StatCard';
import SparkLine from './SparkLine';
import Donut from './Donut';
import BaselineTable from './BaselineTable';

export default function Dashboard() {
  const [baseline, setBaseline] = useState([]);
  const [msg, setMsg] = useState('');
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState('');

  const stats = [
    { title: 'New Alerts', value: 43, delta: 6 },
    { title: 'Resolved Today', value: 17, delta: -3 },
    { title: 'New Events', value: 7, delta: 3 },
    { title: 'Hosts Monitored', value: '27.3k', delta: 3 },
    { title: 'Avg Risk Score', value: 95, delta: 2 },
    { title: 'Rules', value: 621, delta: -1 },
  ];

  const donutA = [{ name: 'Normal', value: 72 }, { name: 'Flagged', value: 28 }];
  const donutB = [{ name: 'CPU', value: 47 }, { name: 'Auth', value: 33 }, { name: 'Net', value: 20 }];
  const spark = Array.from({ length: 28 }, (_, i) => ({ x: i, y: Math.round(20 + Math.sin(i / 2) * 10 + (i % 7)) }));

  const loadBaseline = async () => {
    setErr('');
    try {
      const r = await fetch('http://localhost:4567/baseline');
      const json = await r.json();              // expects [{event, mean, stddev}, ...]
      setBaseline(json);
    } catch (e) {
      setErr('Could not load baseline stats (check /baseline endpoint).');
    }
  };

  const analyze = async () => {
    setErr('');
    setMsg('');
    setLoading(true);
    try {
      const r = await fetch('http://localhost:4567/analyze', {
        method: 'POST',
        headers: { 'Content-Type': 'text/plain' },
        body: ''                                  // send text if you need
      });
      setMsg(await r.text());                     // “Analysis complete…”
      await loadBaseline();                       // refresh table after analysis
    } catch (e) {
      setErr('Analyze failed. Is the backend running on :4567 and CORS enabled?');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadBaseline(); }, []);

  return (
    <div className="container py-4">
      {/* stats */}
      <div className="row g-3 mb-4">
        {stats.map((s, i) => (
          <div className="col-6 col-md-4 col-lg-2" key={i}>
            <StatCard {...s} />
          </div>
        ))}
      </div>

      {/* charts */}
      <div className="row g-3 mb-4">
        <div className="col-lg-6 col-xl-4">
          <SparkLine data={spark} title="Event Volume Over Time" height={260}/>
        </div>
        <div className="col-lg-3 col-xl-4">
          <Donut data={donutA} title="Alert Distribution by Severity" height={260}/>
        </div>
        <div className="col-lg-3 col-xl-4">
          <Donut data={donutB} title="Event Types Breakdown" height={260}/>
        </div>
      </div>

      {/* table + actions */}
      <div className="row g-3">
        <div className="col-lg-8">
          <BaselineTable rows={baseline} />
        </div>

        <div className="col-lg-4">
          <div className="card mb-3">
            <div className="card-body">
              <h6 className="card-title mb-3">Run analysis</h6>
              <button className="btn btn-primary w-100" onClick={analyze} disabled={loading}>
                {loading ? (<><span className="spinner-border spinner-border-sm me-2" />Analyzing…</>) : 'Analyze Logs'}
              </button>
              {msg && <div className="alert alert-info mt-3 mb-0">{msg}</div>}
              {err && <div className="alert alert-danger mt-3 mb-0">{err}</div>}
            </div>
          </div>

          <div className="card">
            <div className="card-body">
              <h6 className="card-title">Read our documentation</h6>
              <p className="text-muted small mb-0">with code samples.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
