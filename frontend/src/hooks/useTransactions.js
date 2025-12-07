import { useQuery } from '@tanstack/react-query';
import { fetchLatestTransactions } from '../api';

export function useTransactions(filters, refetchMs = 5000) {
  return useQuery({
    queryKey: ['transactions', filters],
    queryFn: () => fetchLatestTransactions(filters),
    refetchInterval: refetchMs,
    refetchOnWindowFocus: false,
  });
}
