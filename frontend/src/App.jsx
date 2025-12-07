import React, { useMemo, useState } from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MapView } from './components/MapView.jsx';
import { TransactionsList } from './components/TransactionsList.jsx';
import { useTransactions } from './hooks/useTransactions.js';
import { HeaderBar } from './components/HeaderBar.jsx';
import { Sidebar } from './components/Sidebar.jsx';

const queryClient = new QueryClient();

function AppContent() {
  const [search, setSearch] = useState('');
  const [typeFilter, setTypeFilter] = useState('ALL');
  const [sidebarOpen, setSidebarOpen] = useState(true);

  const { data, isLoading, error } = useTransactions(
    { limit: 200, type: typeFilter, query: search },
    5000
  );

  const transactions = useMemo(() => data || [], [data]);

  return (
    <div className="layout">
      <HeaderBar
        search={search}
        onSearchChange={setSearch}
        typeFilter={typeFilter}
        onTypeChange={setTypeFilter}
        count={transactions.length}
        sidebarOpen={sidebarOpen}
        toggleSidebar={() => setSidebarOpen((v) => !v)}
      />
      <div className={`map-shell ${sidebarOpen ? '' : 'sidebar-closed'}`}>
        <MapView transactions={transactions} />
        <Sidebar open={sidebarOpen}>
          <div className="panel-title">Latest Transactions</div>
          {isLoading && <p className="muted">Loading…</p>}
          {error && <p className="error">Failed to load transactions.</p>}
          {!isLoading && !error && <TransactionsList transactions={transactions} />}
        </Sidebar>
      </div>
    </div>
  );
}

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <AppContent />
    </QueryClientProvider>
  );
}
