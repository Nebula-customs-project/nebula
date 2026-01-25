"use client";

import { useState, useRef, useEffect } from "react";
import Link from "next/link";
import Image from "next/image";
import { usePathname, useRouter } from "next/navigation";
import {
  Car,
  ShoppingCart,
  MapPin,
  User,
  Settings,
  LogOut,
  ChevronDown,
  LayoutDashboard,
} from "lucide-react";
import { useAuth } from "@/hooks/useAuth";

export default function Navigation() {
  const pathname = usePathname();
  const router = useRouter();
  const { user, isAuthenticated, logout } = useAuth();
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsDropdownOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const navigation = [
    { href: '/', label: 'Home', icon: Car },
    { href: '/cars', label: 'Cars', icon: Car },
    { href: '/car-configurator', label: 'Car Configurator', icon: Settings },
    { href: '/world-drive', label: 'World Drive', icon: MapPin },
    { href: '/merchandise', label: 'Merchandise', icon: ShoppingCart },
    { href: '/wishlist', label: 'Wishlist', icon: User },
    { href: '/my-nebula-car', label: 'My Nebula Car', icon: User },
  ];

  const getDashboardLink = () => {
    return user?.role === "ADMIN" ? "/admin-dashboard" : "/user-dashboard";
  };

  return (
    <nav className="bg-white/5 bg-gradient-to-b from-gray-900/40 to-gray-900/20 backdrop-blur-2xl text-white fixed top-0 left-0 right-0 z-[100] shadow-2xl border-b border-white/10">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          <Link
            href="/"
            className="flex items-center active:scale-95 transition-transform duration-200"
          >
            <Image
              src="/assets/nebula-logo-final.png"
              alt="Nebula"
              width={400}
              height={100}
              className="h-56 w-auto object-contain translate-y-[2px] -translate-x-[100px]"
              priority
            />
          </Link>
          <div className="flex gap-1">
            {navigation
              .filter(item => item.href !== '/my-nebula-car' || isAuthenticated)
              .map(item => {
                const Icon = item.icon;
                const isActive = pathname === item.href;
                return (
                  <Link
                    key={item.href}
                    href={item.href}
                    className={`flex items-center gap-2 px-4 py-2 rounded-lg transition ${isActive ? 'bg-red-600' : 'hover:bg-gray-800'
                      }`}
                  >
                    <Icon className="w-4 h-4" />
                    <span className="hidden md:inline">{item.label}</span>
                  </Link>
                );
              })}
            {isAuthenticated ? (
              <div className="relative" ref={dropdownRef}>
                <button
                  onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                  className="flex items-center gap-2 px-5 py-2.5 rounded-full transition-all duration-300 backdrop-blur-md border bg-white/5 border-white/10 hover:bg-white/10 hover:border-white/20 hover:shadow-[0_0_15px_rgba(255,255,255,0.1)]"
                >
                  <User className="w-4 h-4" />
                  <span className="hidden md:inline font-medium">
                    {getDisplayName()}
                  </span>
                  <ChevronDown
                    className={`w-4 h-4 transition-transform duration-200 ${isDropdownOpen ? "rotate-180" : ""}`}
                  />
                </button>

                {/* Dropdown Menu */}
                {isDropdownOpen && (
                  <div className="absolute right-0 mt-2 w-48 bg-gray-900/95 backdrop-blur-xl rounded-xl border border-white/20 shadow-2xl overflow-hidden z-50">
                    <div className="px-4 py-3 border-b border-white/10">
                      <p className="text-sm font-medium text-white">
                        {getDisplayName()}
                      </p>
                      <p className="text-xs text-gray-400 truncate">
                        {user?.email}
                      </p>
                    </div>
                    <div className="py-1">
                      <Link
                        href={getDashboardLink()}
                        onClick={() => setIsDropdownOpen(false)}
                        className="flex items-center gap-3 px-4 py-2.5 text-sm text-gray-200 hover:bg-white/10 transition-colors"
                      >
                        <LayoutDashboard className="w-4 h-4" />
                        Dashboard
                      </Link>
                      <button
                        onClick={handleLogout}
                        className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-red-400 hover:bg-red-500/20 transition-colors"
                      >
                        <LogOut className="w-4 h-4" />
                        Logout
                      </button>
                    </div>
                  </div>
                )}
              </div>
            ) : (
              /* Login Button - Not Authenticated */
              <Link
                href="/login"
                className={`flex items-center gap-2 px-5 py-2.5 rounded-full transition-all duration-300 backdrop-blur-md border ${pathname === "/login"
                  ? "bg-red-600/90 border-red-500/50 shadow-[0_0_20px_rgba(220,38,38,0.4)]"
                  : "bg-white/5 border-white/10 hover:bg-white/10 hover:border-white/20 hover:shadow-[0_0_15px_rgba(255,255,255,0.1)]"
                  }`}
              >
                <User className="w-4 h-4" />
                <span className="hidden md:inline font-medium">Login</span>
              </Link>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}
