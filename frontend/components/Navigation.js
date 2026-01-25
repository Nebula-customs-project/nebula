'use client'

import Link from 'next/link'
import Image from 'next/image'
import { usePathname } from 'next/navigation'
import { Car, ShoppingCart, MapPin, User, Settings, LogOut } from 'lucide-react'
import { useAuth } from '@/hooks/useAuth'

export default function Navigation() {
  const pathname = usePathname();
  const { isAuthenticated } = useAuth();

  const navigation = [
    { href: '/', label: 'Home', icon: null },
    { href: '/cars', label: 'Cars', icon: Car },
    { href: '/car-configurator', label: 'Car Configurator', icon: Settings },
    { href: '/world-drive', label: 'World Drive', icon: MapPin },
    { href: '/merchandise', label: 'Merchandise', icon: ShoppingCart },
    { href: '/my-car', label: 'My Car', icon: User },
  ];



  return (
    <nav className="bg-white/5 bg-gradient-to-b from-gray-900/40 to-gray-900/20 backdrop-blur-2xl text-white fixed top-0 left-0 right-0 z-[100] shadow-2xl border-b border-white/10">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          <Link href="/" className="flex items-center active:scale-95 transition-transform duration-200">
            <Image
              src="/assets/nebula-logo-final.png"
              alt="Nebula"
              width={400}
              height={100}
              className="h-56 w-auto object-contain translate-y-[2px] -translate-x-[100px]"
              priority
            />
          </Link>
          <div className="flex items-center gap-4">
            {navigation
              .filter(item => item.href !== '/my-car' || isAuthenticated)
              .map(item => {
                const Icon = item.icon;
                const isActive = pathname === item.href;
                return (
                  <Link
                    key={item.href}
                    href={item.href}
                    className={`flex items-center gap-2 px-5 py-2.5 rounded-full transition-all duration-300 backdrop-blur-md border ${isActive
                      ? 'bg-red-600/90 border-red-500/50 shadow-[0_0_20px_rgba(220,38,38,0.4)]'
                      : 'bg-white/5 border-white/10 hover:bg-white/10 hover:border-white/20 hover:shadow-[0_0_15px_rgba(255,255,255,0.1)]'
                      }`}
                  >
                    {Icon && <Icon className="w-4 h-4" />}
                    <span className="hidden md:inline font-medium whitespace-nowrap">{item.label}</span>
                  </Link>
                );
              })}
            {!isAuthenticated && (
              <Link
                href="/login"
                className={`flex items-center gap-2 px-5 py-2.5 rounded-full transition-all duration-300 backdrop-blur-md border ${pathname === '/login'
                  ? 'bg-red-600/90 border-red-500/50 shadow-[0_0_20px_rgba(220,38,38,0.4)]'
                  : 'bg-white/5 border-white/10 hover:bg-white/10 hover:border-white/20 hover:shadow-[0_0_15px_rgba(255,255,255,0.1)]'
                  }`}
              >
                <LogOut className="w-4 h-4" />
                <span className="hidden md:inline font-medium">Login</span>
              </Link>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}