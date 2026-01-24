"use client";
import FloatingCart from '@/components/FloatingCart';
import { useAuth } from '@/hooks/useAuth';

export default function MerchandiseLayout({ children }) {
  const { isAuthenticated } = useAuth();
  return (
    <>
      {children}
      {isAuthenticated && <FloatingCart />}
    </>
  );
}
