import React from 'react';
import dayjs from 'dayjs';

export function TransactionsList({ transactions }) {
  return (
    <div className="list">
      {transactions.map((tx) => (
        <div key={tx.id} className="list-item">
          <div>
            <p className="city">{tx.city}</p>
            <p className="meta">
              {tx.cityCode} • {tx.type} • {dayjs(tx.time).format('HH:mm:ss')}
            </p>
          </div>
          <div className="price">SAR {Number(tx.price).toLocaleString()}</div>
        </div>
      ))}
    </div>
  );
}
