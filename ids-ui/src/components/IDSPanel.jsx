import React, { useState } from 'react';

export default function IDSPanel() {
  const [data, setData] = useState('');
  const [result, setResult] = useState('');
  const [baselineData, setBaselineData] = useState([]);

  const analyze = async () => {
    const res = await fetch('http://localhost:4567/analyze', {
      method: 'POST',
      headers: { 'Content-Type': 'text/plain' },
      body: data
    });
    const text = await res.text();
    setResult(text);

    // Fetch and show baseline
    fetchBaseline();
  };

  const fetchBaseline = async () => {
    const res = await fetch('http://localhost:4567/baseline');
    const data = await res.json();
    setBaselineData(data);
  };

  return (
    <div style={{ padding: '20px' }}>
      <h1>Intrusion Detection System</h1>
      <textarea
        rows={5}
        cols={50}
        value={data}
        onChange={e => setData(e.target.value)}
        placeholder="Enter packet/activity data..."
      />
      <br />
      <button onClick={analyze} style={{ marginTop: '10px' }}>
        Analyze
      </button>

      <h3>Analysis Result:</h3>
      <pre>{result}</pre>

      {baselineData.length > 0 && (
        <>
          <h3>Baseline Statistics</h3>
          <table border="1" cellPadding="10" style={{ marginTop: '10px' }}>
            <thead>
              <tr>
                <th>Event</th>
                <th>Mean</th>
                <th>Std Dev</th>
              </tr>
            </thead>
            <tbody>
              {baselineData.map((row, index) => (
                <tr key={index}>
                  <td>{row.event}</td>
                  <td>{row.mean}</td>
                  <td>{row.stddev}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </div>
  );
}
