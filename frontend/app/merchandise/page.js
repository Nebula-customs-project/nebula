'use client'


import { useCallback, useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { ShoppingCart, Heart, Star, Filter } from 'lucide-react'
import { useAuth } from '@/hooks/useAuth'
import { useToast } from '@/hooks/useToast'
import { ToastContainer } from '@/components/Toast'
import { ProductGridSkeleton } from '@/components/ProductSkeleton'
import ProductModal from '@/components/ProductModal'

export default function MerchandisePage() {
  const router = useRouter();
  const { isAuthenticated } = useAuth();
  const { toasts, addToast, removeToast } = useToast()
  const [cart, setCart] = useState([])
  const [favorites, setFavorites] = useState([])
  const [selectedCategory, setSelectedCategory] = useState('All')
  const [showWishlistOnly, setShowWishlistOnly] = useState(false)
  const [cartPulse, setCartPulse] = useState(false)
  const [selectedProduct, setSelectedProduct] = useState(null)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [debugInfo, setDebugInfo] = useState(null)
  const [isCartCollapsed, setIsCartCollapsed] = useState(() => {
    if (typeof window !== 'undefined') {
      const saved = localStorage.getItem('cartCollapsed');
      return saved === 'true';
    }
    return false;
  })

  // Save cart collapsed state to localStorage whenever it changes
  useEffect(() => {
    localStorage.setItem('cartCollapsed', isCartCollapsed.toString());
  }, [isCartCollapsed]);

  const fetchProducts = useCallback(async (signal) => {
    try {
      setLoading(true)
      setError(null)
      setDebugInfo(null)
      // Use gateway for REST API requests when NEXT_PUBLIC_GATEWAY_URL is set.
      const gateway = process.env.NEXT_PUBLIC_GATEWAY_URL || 'http://localhost:8080'
      const url = `${gateway}/api/v1/merchandise/products`
      setDebugInfo({ attemptingUrl: url })
      const res = await fetch(url, {
        signal,
      })
      if (!res.ok) {
        const text = await res.text().catch(() => '')
        throw new Error(`Failed to load products (${res.status}) ${text}`)
      }
      const data = await res.json()
      setProducts(Array.isArray(data) ? data : [])
      setDebugInfo({ attemptingUrl: url, success: true, count: Array.isArray(data) ? data.length : 0 })
    } catch (err) {
      if (err && err.name === 'AbortError') return
      // Provide richer debug info for client-side troubleshooting
      const message = err && err.message ? err.message : String(err)
      setError(message)
      setDebugInfo(prev => ({ ...prev, error: message }))
      setProducts([])
    } finally {
      setLoading(false)
    }
  }, [])

  // Load cart from localStorage on mount and listen for updates
  useEffect(() => {
    const loadCart = () => {
      const savedCart = typeof window !== 'undefined' ? localStorage.getItem('cart') : null;
      setCart(savedCart ? JSON.parse(savedCart) : []);
    };
    loadCart();
    window.addEventListener('cart-updated', loadCart);
    return () => window.removeEventListener('cart-updated', loadCart);
  }, []);

  // Load favorites from localStorage on mount
  useEffect(() => {
    const saved = typeof window !== 'undefined' ? localStorage.getItem('favorites') : null;
    setFavorites(saved ? JSON.parse(saved) : []);
  }, []);

  useEffect(() => {
    const controller = new AbortController()
    fetchProducts(controller.signal)
    return () => controller.abort()
  }, [fetchProducts])

  const categories = ['All', 'Apparel', 'Accessories', 'Models', 'Lifestyle']

  let filteredProducts = selectedCategory === 'All'
    ? products
    : products.filter(p => p.category === selectedCategory)

  // Apply wishlist filter if active
  if (showWishlistOnly) {
    filteredProducts = filteredProducts.filter(p => favorites.includes(p.id))
  }

  const addToCart = (product) => {
    const cartItem = {
      productId: product.id,
      name: product.name,
      price: product.price,
      quantity: 1,
      image: product.imageUrl
    }

    const existingCart = localStorage.getItem('cart')
    const updatedCart = existingCart ? JSON.parse(existingCart) : []

    const existingItem = updatedCart.find(item => item.productId === product.id)
    if (existingItem) {
      existingItem.quantity += 1
    } else {
      updatedCart.push(cartItem)
    }

    localStorage.setItem('cart', JSON.stringify(updatedCart))
    window.dispatchEvent(new Event('cart-updated'))
    setCart(updatedCart)

    // Show toast and animate cart button
    addToast(`Added ${product.name} to cart!`, 'success')
    setCartPulse(true)
    setTimeout(() => setCartPulse(false), 600)
  }

  const updateQuantity = (productId, delta) => {
    const existingCart = localStorage.getItem('cart')
    let updatedCart = existingCart ? JSON.parse(existingCart) : []

    const item = updatedCart.find(i => i.productId === productId)
    if (item) {
      item.quantity += delta
      if (item.quantity <= 0) {
        updatedCart = updatedCart.filter(i => i.productId !== productId)
      }
    }

    localStorage.setItem('cart', JSON.stringify(updatedCart))
    window.dispatchEvent(new Event('cart-updated'))
    setCart(updatedCart)
  }

  const removeFromCart = (productId) => {
    const existingCart = localStorage.getItem('cart')
    let updatedCart = existingCart ? JSON.parse(existingCart) : []
    updatedCart = updatedCart.filter(i => i.productId !== productId)

    localStorage.setItem('cart', JSON.stringify(updatedCart))
    window.dispatchEvent(new Event('cart-updated'))
    setCart(updatedCart)
  }

  const toggleFavorite = (productId) => {
    let updated;
    const productName = products.find(p => p.id === productId)?.name || 'Product'
    if (favorites.includes(productId)) {
      updated = favorites.filter(id => id !== productId);
      addToast(`Removed ${productName} from wishlist`, 'info')
    } else {
      updated = [...favorites, productId];
      addToast(`Added ${productName} to wishlist!`, 'success')
    }
    setFavorites(updated);
    localStorage.setItem('favorites', JSON.stringify(updated));
  }

  const getBadgeColor = (badge) => {
    switch (badge) {
      case 'Bestseller': return 'bg-yellow-500'
      case 'New': return 'bg-green-500'
      case 'Limited': return 'bg-purple-500'
      case 'Premium': return 'bg-blue-500'
      default: return 'bg-gray-500'
    }
  }

  return (
    <div className="min-h-screen bg-gray-900 text-white pt-24 pb-16 px-4 sm:px-6 lg:px-8">
      <ToastContainer toasts={toasts} removeToast={removeToast} />
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8 gap-4">
          <div>
            <h1 className="text-4xl md:text-5xl font-bold mb-3">Nebula Merchandise</h1>
            <p className="text-gray-400 text-lg">Premium products for true automotive enthusiasts</p>
          </div>
          <button
            onClick={() => setShowWishlistOnly(!showWishlistOnly)}
            className={`flex items-center gap-2 px-5 py-2.5 rounded-lg transition ${showWishlistOnly ? 'bg-red-600 text-white' : 'bg-gray-800 hover:bg-gray-700'}`}
          >
            <Heart className={`w-5 h-5 ${showWishlistOnly || favorites.length > 0 ? 'fill-current' : ''}`} />
            <span className="font-semibold">{showWishlistOnly ? 'Show All' : 'Wishlist'}</span>
          </button>
        </div>

        {/* Category Filter */}
        <div className="flex items-center gap-3 mb-10 overflow-x-auto pb-2">
          <Filter className="w-5 h-5 text-gray-400 flex-shrink-0 mr-1" />
          {categories.map(category => (
            <button
              key={category}
              onClick={() => setSelectedCategory(category)}
              className={`px-6 py-2 rounded-full font-semibold transition whitespace-nowrap ${selectedCategory === category
                ? 'bg-red-600 text-white'
                : 'bg-gray-800 text-gray-400 hover:bg-gray-700'
                }`}
            >
              {category}
            </button>
          ))}
        </div>

        {/* Loading State */}
        {loading && (
          <ProductGridSkeleton count={8} />
        )}
        {error && !loading && (
          <div className="text-center py-20">
            <div className="text-red-400 text-xl font-semibold">Merchandise service is not running</div>
          </div>
        )}

        {/* Products Grid */}
        {!loading && !error && (
          <div className="grid md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {filteredProducts.map(product => {
              const rating = product.rating ? Number(product.rating) : 0
              const reviews = product.reviews ?? 0
              const priceNumber = product.price !== undefined && product.price !== null ? Number(product.price) : 0
              const priceDisplay = Number.isNaN(priceNumber) ? '0.00' : priceNumber.toFixed(2)

              return (
                <div
                  key={product.id}
                  className="bg-gray-800 rounded-xl overflow-hidden hover:transform hover:scale-105 transition-all duration-300 shadow-lg hover:shadow-2xl group cursor-pointer"
                  onClick={() => {
                    setSelectedProduct(product)
                    setIsModalOpen(true)
                  }}
                >
                  {/* Product Image */}
                  <div className="relative h-64 overflow-hidden">
                    <div
                      className="absolute inset-0 bg-cover bg-center transform group-hover:scale-110 transition-transform duration-500"
                      style={{ backgroundImage: `url(${product.imageUrl || product.img || ''})` }}
                    ></div>
                    <div className="absolute inset-0 bg-gradient-to-t from-gray-900 via-transparent to-transparent opacity-60"></div>

                    {/* Badge */}
                    {product.badge && (
                      <div className={`absolute top-4 left-4 ${getBadgeColor(product.badge)} text-white text-xs font-bold px-3 py-1 rounded-full`}>
                        {product.badge}
                      </div>
                    )}

                    {/* Favorite Button */}
                    <button
                      onClick={(e) => {
                        e.stopPropagation()
                        toggleFavorite(product.id)
                      }}
                      className={`absolute top-4 right-4 p-2 rounded-full backdrop-blur-sm transition-all ${favorites.includes(product.id)
                        ? 'bg-red-600 text-white'
                        : 'bg-white/20 text-white hover:bg-white/30'
                        }`}
                    >
                      <Heart className={`w-5 h-5 ${favorites.includes(product.id) ? 'fill-current' : ''}`} />
                    </button>

                    {/* Quick View removed as requested */}
                  </div>

                  {/* Product Info */}
                  <div className="p-5">
                    <div className="flex items-center gap-1 mb-2">
                      {[...Array(5)].map((_, i) => (
                        <Star
                          key={i}
                          className={`w-4 h-4 ${i < Math.floor(rating)
                            ? 'text-yellow-400 fill-current'
                            : 'text-gray-600'
                            }`}
                        />
                      ))}
                      <span className="text-sm text-gray-400 ml-2">({reviews})</span>
                    </div>

                    <h3 className="text-xl font-bold mb-1 line-clamp-1">{product.name}</h3>
                    <p className="text-sm text-gray-400 mb-3">{product.category || 'Accessories'}</p>

                    <div className="flex items-center justify-between">
                      <span className="text-2xl font-bold text-red-500">€{priceDisplay}</span>
                      <button
                        onClick={(e) => {
                          e.stopPropagation()
                          addToCart(product)
                        }}
                        className="bg-red-600 hover:bg-red-700 p-3 rounded-lg transition transform hover:scale-110 active:scale-95"
                      >
                        <ShoppingCart className="w-5 h-5" />
                      </button>
                    </div>
                  </div>
                </div>
              )
            })}
          </div>
        )}

        {/* Empty State */}
        {filteredProducts.length === 0 && (
          <div className="text-center py-20">
            <p className="text-2xl text-gray-400">No products found in this category</p>
          </div>
        )}

        {/* Cart Button - Always visible */}
        {(cart.length === 0 || isCartCollapsed) ? (
          <button
            onClick={() => cart.length > 0 && setIsCartCollapsed(false)}
            className={`fixed bottom-6 right-10 bg-red-600 hover:bg-red-700 p-5 rounded-full shadow-2xl transition-all hover:scale-110 z-50 ${cart.length === 0 ? 'opacity-70' : ''} ${cartPulse ? 'animate-pulse-scale' : ''}`}
          >
            <ShoppingCart className="w-7 h-7" />
            <span className="absolute -top-1 -right-1 bg-white text-red-600 text-sm font-bold w-7 h-7 rounded-full flex items-center justify-center shadow-lg">
              {cart.reduce((sum, item) => sum + item.quantity, 0)}
            </span>
          </button>
        ) : (
          <div className="fixed bottom-8 right-8 bg-gray-800/95 backdrop-blur-sm rounded-2xl shadow-2xl w-80 border border-gray-700 z-50 overflow-hidden">
            {/* Header */}
            <div className="flex justify-between items-center p-4 bg-gray-900/50 border-b border-gray-700">
              <div className="flex items-center gap-2">
                <ShoppingCart className="w-5 h-5 text-red-500" />
                <h3 className="text-lg font-bold">Cart</h3>
                <span className="bg-red-600 text-xs font-bold px-2 py-0.5 rounded-full">{cart.length}</span>
              </div>
              <button
                className="text-gray-400 hover:text-white hover:bg-gray-700 w-8 h-8 rounded-lg flex items-center justify-center transition"
                onClick={() => setIsCartCollapsed(true)}
                title="Minimize"
              >
                ×
              </button>
            </div>

            {/* Items List */}
            <div className="max-h-48 overflow-y-auto p-4 space-y-3">
              {cart.map((item) => (
                <div key={item.productId} className="flex items-center gap-2 bg-gray-700/50 rounded-lg p-2">
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-medium truncate">{item.name}</p>
                    <p className="text-red-400 font-bold text-sm">€{(item.price * item.quantity).toFixed(2)}</p>
                  </div>
                  <div className="flex items-center gap-1">
                    <button
                      className="w-6 h-6 bg-gray-600 hover:bg-gray-500 rounded text-sm font-bold flex items-center justify-center transition"
                      onClick={() => updateQuantity(item.productId, -1)}
                    >
                      −
                    </button>
                    <span className="w-6 text-center text-sm font-semibold">{item.quantity}</span>
                    <button
                      className="w-6 h-6 bg-gray-600 hover:bg-gray-500 rounded text-sm font-bold flex items-center justify-center transition"
                      onClick={() => updateQuantity(item.productId, 1)}
                    >
                      +
                    </button>
                  </div>
                </div>
              ))}
            </div>

            {/* Footer */}
            <div className="p-4 bg-gray-900/50 border-t border-gray-700">
              <div className="flex justify-between items-center mb-3">
                <span className="text-gray-400">Total ({cart.reduce((sum, item) => sum + item.quantity, 0)} items)</span>
                <span className="text-xl font-bold text-red-500">€{cart.reduce((sum, item) => sum + (item.price * item.quantity), 0).toFixed(2)}</span>
              </div>
              <button
                className="w-full bg-red-600 hover:bg-red-700 py-3 rounded-xl font-semibold transition flex items-center justify-center gap-2"
                onClick={() => router.push(isAuthenticated ? '/checkout' : '/login')}
              >
                <ShoppingCart className="w-4 h-4" />
                Checkout Now
              </button>
            </div>
          </div>
        )}

        {/* Product Modal */}
        <ProductModal
          product={selectedProduct}
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          onAddToCart={addToCart}
          onToggleFavorite={toggleFavorite}
          isFavorite={selectedProduct ? favorites.includes(selectedProduct.id) : false}
        />
      </div>
    </div>
  )
}