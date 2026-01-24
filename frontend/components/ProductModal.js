'use client'

import { X, ShoppingCart, Heart, Star } from 'lucide-react'
import { useEffect } from 'react'

export default function ProductModal({ product, isOpen, onClose, onAddToCart, onToggleFavorite, isFavorite }) {
    // Close on escape key
    useEffect(() => {
        const handleEscape = (e) => {
            if (e.key === 'Escape') onClose()
        }
        if (isOpen) {
            document.addEventListener('keydown', handleEscape)
            document.body.style.overflow = 'hidden'
        }
        return () => {
            document.removeEventListener('keydown', handleEscape)
            document.body.style.overflow = 'unset'
        }
    }, [isOpen, onClose])

    if (!isOpen || !product) return null

    const rating = product.rating || 4
    const reviews = product.reviews || Math.floor(Math.random() * 500) + 50

    return (
        <div
            className="fixed inset-0 z-[200] flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm"
            onClick={onClose}
        >
            <div
                className="bg-gray-800 rounded-2xl shadow-2xl max-w-4xl w-full max-h-[90vh] overflow-y-auto border border-gray-700"
                onClick={(e) => e.stopPropagation()}
            >
                <div className="relative">
                    {/* Close Button */}
                    <button
                        onClick={onClose}
                        className="absolute top-4 right-4 z-10 bg-gray-900/80 hover:bg-gray-900 text-white p-2 rounded-full transition"
                    >
                        <X className="w-6 h-6" />
                    </button>

                    <div className="grid md:grid-cols-2 gap-8 p-8">
                        {/* Product Image */}
                        <div className="relative">
                            <div
                                className="aspect-square rounded-xl bg-cover bg-center"
                                style={{ backgroundImage: `url(${product.imageUrl || product.image_url || product.img})` }}
                            />
                            {product.badge && (
                                <div className={`absolute top-4 left-4 text-white text-xs font-bold px-3 py-1.5 rounded-full ${product.badge === 'Bestseller' ? 'bg-yellow-500' :
                                        product.badge === 'New' ? 'bg-green-500' :
                                            product.badge === 'Limited' ? 'bg-purple-500' :
                                                product.badge === 'Premium' ? 'bg-blue-500' : 'bg-gray-500'
                                    }`}>
                                    {product.badge}
                                </div>
                            )}
                        </div>

                        {/* Product Details */}
                        <div className="flex flex-col">
                            <div className="flex items-start justify-between mb-4">
                                <div>
                                    <h2 className="text-3xl font-bold text-white mb-2">{product.name}</h2>
                                    <p className="text-gray-400">{product.category || 'Accessories'}</p>
                                </div>
                                <button
                                    onClick={() => onToggleFavorite(product.id)}
                                    className={`p-3 rounded-full transition ${isFavorite ? 'bg-red-600 text-white' : 'bg-gray-700 text-gray-400 hover:bg-gray-600'
                                        }`}
                                >
                                    <Heart className={`w-6 h-6 ${isFavorite ? 'fill-current' : ''}`} />
                                </button>
                            </div>

                            {/* Rating */}
                            <div className="flex items-center gap-2 mb-6">
                                <div className="flex gap-1">
                                    {[...Array(5)].map((_, i) => (
                                        <Star
                                            key={i}
                                            className={`w-5 h-5 ${i < Math.floor(rating) ? 'text-yellow-400 fill-current' : 'text-gray-600'
                                                }`}
                                        />
                                    ))}
                                </div>
                                <span className="text-gray-400 text-sm">({reviews} reviews)</span>
                            </div>

                            {/* Price */}
                            <div className="mb-6">
                                <span className="text-4xl font-bold text-red-500">€{product.price.toFixed(2)}</span>
                            </div>

                            {/* Description */}
                            <div className="mb-8 flex-1">
                                <h3 className="text-lg font-semibold text-white mb-3">Description</h3>
                                <p className="text-gray-300 leading-relaxed">
                                    {product.description || 'Premium Nebula merchandise designed for automotive enthusiasts. High-quality materials and exclusive designs that showcase your passion for performance and craftsmanship.'}
                                </p>
                            </div>

                            {/* Add to Cart Button */}
                            <button
                                onClick={() => {
                                    onAddToCart(product)
                                    onClose()
                                }}
                                className="w-full bg-red-600 hover:bg-red-700 text-white font-semibold py-4 rounded-xl transition flex items-center justify-center gap-3 text-lg"
                            >
                                <ShoppingCart className="w-6 h-6" />
                                Add to Cart
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}
