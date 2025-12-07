import React from 'react';

export function Sidebar({ children, open }) {
  return <aside className={`sidebar ${open ? 'open' : 'closed'}`}>{children}</aside>;
}
