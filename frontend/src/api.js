const API_BASE = import.meta.env.VITE_API_BASE || '/api';

export async function fetchLatestTransactions({ limit = 50, type, query } = {}) {
  const params = new URLSearchParams();
  params.set('limit', limit);
  if (type && type !== 'ALL') params.set('type', type);
  if (query) params.set('q', query);
  const res = await fetch(`${API_BASE}/transactions/latest?${params.toString()}`);
  if (!res.ok) {
    throw new Error(`Failed to load transactions: ${res.status}`);
  }
  return res.json();
}
