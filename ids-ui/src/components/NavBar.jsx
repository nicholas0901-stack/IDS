import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.bundle.min.js';

export default function NavBar() {
  return (
    <nav className="navbar navbar-expand-lg navbar-light bg-white border-bottom shadow-sm">
      <div className="container">
        <a className="navbar-brand fw-bold" href="#">Dashboard</a>

        {/* Toggle button for mobile */}
        <button
          className="navbar-toggler"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarNav"
          aria-controls="navbarNav"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        {/* Menu links */}
        <div className="collapse navbar-collapse" id="navbarNav">
          <ul className="navbar-nav ms-auto">
            <li className="nav-item">
              <a className="nav-link active" href="#">Home</a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="#">Interface</a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="#">Components</a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="#">Pages</a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="#">Forms</a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="#">Gallery</a>
            </li>
            <li className="nav-item">
              <a className="nav-link" href="#">Documentation</a>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  );
}
