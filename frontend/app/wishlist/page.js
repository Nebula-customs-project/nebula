"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { Heart, ShoppingCart } from "lucide-react";

export default function WishlistPage() {
  const router = useRouter();
  const [favorites, setFavorites] = useState([]);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const saved = typeof window !== "undefined" ? localStorage.getItem("favorites") : null;
    setFavorites(saved ? JSON.parse(saved) : []);
  }, []);

  useEffect(() => {
    const fetchProducts = async () => {
      setLoading(true);
      try {
        const gateway = process.env.NEXT_PUBLIC_GATEWAY_URL || "http://localhost:8080";
        const url = `${gateway}/api/v1/merchandise/products`;
        const res = await fetch(url);
        const data = await res.json();
        setProducts(Array.isArray(data) ? data : []);
      } catch {
        setProducts([]);
      } finally {
        setLoading(false);
      }
    };
    fetchProducts();
  }, []);

  const favoriteProducts = products.filter((p) => favorites.includes(p.id));

  return (
    <div className="min-h-screen bg-gray-900 text-white py-16 px-4">
      <div className="max-w-7xl mx-auto">
        <h1 className="text-4xl font-bold mb-8 text-center">My Wishlist</h1>
        {loading ? (
          <div className="text-center text-gray-400">Loading...</div>
        ) : favoriteProducts.length === 0 ? (
          <div className="text-center text-gray-400">No items in your wishlist.</div>
        ) : (
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            {favoriteProducts.map((product) => (
              <div key={product.id} className="bg-gray-800 rounded-lg overflow-hidden">
                <div className="h-48 bg-gray-700 bg-cover bg-center" style={{ backgroundImage: `url(${product.imageUrl || product.img})` }}></div>
                <div className="p-6 flex flex-col gap-2">
                  <h2 className="text-xl font-bold">{product.name}</h2>
                  <p className="text-gray-400">{product.category}</p>
                  <span className="text-lg font-bold text-red-500">€{product.price}</span>
                  <button
                    className="mt-2 bg-red-600 hover:bg-red-700 text-white py-2 rounded-lg font-semibold flex items-center justify-center gap-2"
                    onClick={() => router.push("/merchandise")}
                  >
                    <ShoppingCart className="w-5 h-5" /> Add to Cart
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
