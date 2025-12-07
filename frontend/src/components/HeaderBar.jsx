import React from 'react';

const TYPES = [
  { label: 'All types', value: 'ALL' },
  { label: 'Land', value: 'LAND' },
  { label: 'Villa', value: 'VILLA' },
  { label: 'Apartment', value: 'APARTMENT' }
];

export function HeaderBar({ search, onSearchChange, typeFilter, onTypeChange, count, sidebarOpen, toggleSidebar }) {
  return (
    <header className="header">
      <div className="logo">On Map Real Estate</div>
      <div className="search-box">
        <input
          type="text"
          placeholder="Search city or code..."
          value={search}
          onChange={(e) => onSearchChange(e.target.value)}
        />
      </div>
      <div className="filters">
        <select value={typeFilter} onChange={(e) => onTypeChange(e.target.value)}>
          {TYPES.map((t) => (
            <option key={t.value} value={t.value}>
              {t.label}
            </option>
          ))}
        </select>
        <div className="chip">Live: {count}</div>
        <button className="toggle-btn" onClick={toggleSidebar} aria-label="Toggle sidebar">
          {sidebarOpen ? 'Hide' : 'Show'} list
        </button>
      </div>
    </header>
  );
}
