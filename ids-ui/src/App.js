import NavBar from './components/NavBar';
import Dashboard from './components/Dashboard';

export default function App() {
  return (
    <div className="bg-light min-vh-100">
      <NavBar />
      <main className="container py-4">
        <h2 className="h3 fw-bold mb-4">Dashboard</h2>
        <Dashboard />
      </main>
    </div>
  );
}
