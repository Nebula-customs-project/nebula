"use client"

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import CreditCardPreview from '@/components/CreditCardPreview'

import { useAuth } from '@/hooks/useAuth'
import { apiClient } from '@/lib/api'

// PaymentForm component for Credit Card only
// PaymentForm component for Credit Card only
function PaymentForm({ onSuccess, onBack, cardholderName }) {
  const [card, setCard] = useState({ number: '', expiry: '', cvc: '' });
  const [error, setError] = useState('');

  const handleExpiryChange = (e) => {
    let value = e.target.value.replace(/[^\d]/g, ''); // Remove non-digits
    if (value.length >= 2) {
      value = value.slice(0, 2) + '/' + value.slice(2, 4);
    }
    setCard({ ...card, expiry: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    if (!/^\d{16}$/.test(card.number.replace(/\s/g, ''))) {
      setError('Card number must be 16 digits.');
      return;
    }
    // Expiry format MM/YY and validation
    if (!/^\d{2}\/\d{2}$/.test(card.expiry)) {
      setError('Expiry must be MM/YY.');
      return;
    }
    const [mm, yy] = card.expiry.split('/');
    const month = parseInt(mm, 10);
    const year = parseInt(yy, 10) + 2000;
    const now = new Date();
    const currentYear = now.getFullYear();
    const currentMonth = now.getMonth() + 1;

    if (month < 1 || month > 12) {
      setError('Expiry month must be between 01 and 12.');
      return;
    }

    // Year must be within 10 years from now
    if (year < currentYear || year > currentYear + 10) {
      setError('Invalid card.');
      return;
    }

    // Check if card is expired (past month/year)
    if (year < currentYear || (year === currentYear && month < currentMonth)) {
      setError('Card has expired.');
      return;
    }

    if (!/^\d{3,4}$/.test(card.cvc)) {
      setError('CVC must be 3 or 4 digits.');
      return;
    }
    onSuccess();
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-8 flex flex-col items-center justify-center">
      <h2 className="text-3xl font-extrabold text-gray-100 tracking-tight mb-2">Credit Card Payment</h2>

      {/* Credit Card Preview */}
      <CreditCardPreview
        cardNumber={card.number}
        cardName={cardholderName || ''}
        cardExpiry={card.expiry}
      />

      <div className="w-full max-w-xs space-y-4">
        <input type="text" placeholder="Card Number" maxLength={19} value={card.number} onChange={e => setCard({ ...card, number: e.target.value.replace(/[^\d]/g, '').replace(/(.{4})/g, '$1 ').trim() })} className="w-full px-4 py-2 border border-gray-700 bg-gray-900 text-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 placeholder-gray-400" required />
        <div className="flex gap-2">
          <input type="text" placeholder="MM/YY" maxLength={5} value={card.expiry} onChange={handleExpiryChange} className="w-1/2 px-4 py-2 border border-gray-700 bg-gray-900 text-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 placeholder-gray-400" required />
          <input type="text" placeholder="CVC" maxLength={4} value={card.cvc} onChange={e => setCard({ ...card, cvc: e.target.value.replace(/[^\d]/g, '') })} className="w-1/2 px-4 py-2 border border-gray-700 bg-gray-900 text-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 placeholder-gray-400" required />
        </div>
      </div>
      {error && <div className="text-red-400 text-sm">{error}</div>}
      <button type="submit" className="w-full max-w-xs bg-red-600 hover:bg-red-700 text-white py-3 rounded-lg font-semibold transition">Pay Now</button>
      <button type="button" onClick={onBack} className="text-gray-400 hover:text-red-400 mt-2">Back to Shipping</button>
    </form>
  );
}

export default function CheckoutPage() {
  const [step, setStep] = useState('shipping') // shipping, payment, confirmation
  const [cart, setCart] = useState([])
  const [cartLoaded, setCartLoaded] = useState(false)
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    address: '',
    city: '',
    zipCode: '',
    country: ''
  })

  // Get current user to prefill data
  const { user } = useAuth()
  const router = useRouter()

  // Load cart from localStorage on mount and on cart-updated event
  useEffect(() => {
    function syncCart() {
      const saved = typeof window !== 'undefined' ? localStorage.getItem('cart') : null
      setCart(saved ? JSON.parse(saved) : [])
      setCartLoaded(true)
    }
    syncCart()
    window.addEventListener('cart-updated', syncCart)
    return () => window.removeEventListener('cart-updated', syncCart)
  }, [])

  // Prefill user data if logged in
  useEffect(() => {
    if (user?.username) {
      apiClient.get(`/users/by-username/${user.username}`)
        .then(userData => {
          setFormData(prev => ({
            ...prev,
            firstName: userData.firstName || prev.firstName,
            lastName: userData.lastName || prev.lastName,
            email: userData.email || prev.email
          }))
        })
        .catch(err => console.error("Failed to fetch user details for checkout", err))
    }
  }, [user])

  // Redirect to merchandise if cart is empty (only after cart has loaded)
  useEffect(() => {
    if (cartLoaded && cart.length === 0 && step === 'shipping') {
      router.push('/merchandise')
    }
  }, [cart, cartLoaded, step, router])

  const totalPrice = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0)

  const handleInputChange = (e) => {
    const { name, value } = e.target
    if (name === 'zipCode') {
      // Allow only numbers
      const numericValue = value.replace(/[^0-9]/g, '')
      setFormData(prev => ({ ...prev, [name]: numericValue }))
    } else {
      setFormData(prev => ({ ...prev, [name]: value }))
    }
  }

  const handleShippingSubmit = (e) => {
    e.preventDefault()
    setStep('payment')
  }

  const handlePaymentSuccess = () => {
    setStep('confirmation')
    // Clear cart
    localStorage.removeItem('cart')
    window.dispatchEvent(new Event('cart-updated'))
  }

  if (step === 'confirmation') {
    return (
      <div className="min-h-screen bg-gray-900 flex items-center justify-center p-4">
        <div className="bg-gray-800 rounded-2xl shadow-2xl p-10 max-w-md text-center border border-gray-700 relative">
          {/* Close Button */}
          <button
            onClick={() => router.push('/')}
            className="absolute top-4 right-4 text-gray-400 hover:text-white hover:bg-gray-700 w-8 h-8 rounded-full flex items-center justify-center transition"
            title="Close"
          >
            ×
          </button>

          {/* Success Icon */}
          <div className="w-20 h-20 bg-green-500/20 rounded-full flex items-center justify-center mx-auto mb-6">
            <span className="text-5xl text-green-400">✓</span>
          </div>

          <h1 className="text-3xl font-bold text-green-400 mb-4">Order Confirmed!</h1>
          <p className="text-gray-300 mb-2">Thank you for your purchase.</p>
          <p className="text-gray-400 mb-8">Order confirmation has been sent to your email.</p>

          <button
            onClick={() => router.push('/')}
            className="w-full bg-red-600 hover:bg-red-700 text-white py-3 rounded-xl font-semibold transition"
          >
            Continue Shopping
          </button>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-900 pt-24 pb-12 px-4 sm:px-6 lg:px-8 text-gray-100">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-4xl font-bold mb-10 text-center text-gray-100">Checkout</h1>

        <div className="grid md:grid-cols-3 gap-8">
          {/* Checkout Form */}
          <div className="md:col-span-2">
            <div className="bg-gray-800 rounded-lg shadow-md p-8 border border-gray-700">
              {step === 'shipping' && (
                <form onSubmit={handleShippingSubmit} className="space-y-4">
                  <h2 className="text-2xl font-bold mb-6 text-gray-100">Shipping Information</h2>
                  <div className="grid md:grid-cols-2 gap-4">
                    <input
                      type="text"
                      name="firstName"
                      placeholder="First Name"
                      required
                      value={formData.firstName}
                      onChange={handleInputChange}
                      className="w-full px-4 py-2 border border-gray-700 bg-gray-900 text-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 placeholder-gray-400"
                    />
                    <input
                      type="text"
                      name="lastName"
                      placeholder="Last Name"
                      required
                      value={formData.lastName}
                      onChange={handleInputChange}
                      className="w-full px-4 py-2 border border-gray-700 bg-gray-900 text-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 placeholder-gray-400"
                    />
                  </div>
                  <input
                    type="email"
                    name="email"
                    placeholder="Email"
                    required
                    value={formData.email}
                    onChange={handleInputChange}
                    className="w-full px-4 py-2 border border-gray-700 bg-gray-900 text-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 placeholder-gray-400"
                  />
                  <input
                    type="text"
                    name="address"
                    placeholder="Address"
                    required
                    value={formData.address}
                    onChange={handleInputChange}
                    className="w-full px-4 py-2 border border-gray-700 bg-gray-900 text-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 placeholder-gray-400"
                  />
                  <div className="grid md:grid-cols-2 gap-4">
                    <input
                      type="text"
                      name="city"
                      placeholder="City"
                      required
                      value={formData.city}
                      onChange={handleInputChange}
                      className="w-full px-4 py-2 border border-gray-700 bg-gray-900 text-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 placeholder-gray-400"
                    />
                    <input
                      type="text"
                      name="zipCode"
                      placeholder="Zip Code"
                      required
                      maxLength={6}
                      inputMode="numeric"
                      value={formData.zipCode}
                      onChange={handleInputChange}
                      className="w-full px-4 py-2 border border-gray-700 bg-gray-900 text-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 placeholder-gray-400"
                    />
                  </div>
                  <input
                    type="text"
                    name="country"
                    placeholder="Country"
                    required
                    value={formData.country}
                    onChange={handleInputChange}
                    className="w-full px-4 py-2 border border-gray-700 bg-gray-900 text-gray-100 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 placeholder-gray-400"
                  />
                  <button
                    type="submit"
                    className="w-full bg-red-600 hover:bg-red-700 text-white py-3 rounded-lg font-semibold transition mt-6"
                  >
                    Continue to Payment
                  </button>
                </form>
              )}
              {step === 'payment' && (
                <PaymentForm
                  onSuccess={handlePaymentSuccess}
                  onBack={() => setStep('shipping')}
                  cardholderName={`${formData.firstName} ${formData.lastName}`.trim()}
                />
              )}
            </div>
          </div>
          {/* Order Summary */}
          <div className="md:col-span-1">
            <div className="bg-gray-800 rounded-lg shadow-md p-8 sticky top-24 border border-gray-700">
              <h3 className="text-xl font-bold mb-6 text-gray-100">Order Summary</h3>
              <div className="space-y-3 mb-6 max-h-64 overflow-y-auto">
                {cart.map((item, idx) => (
                  <div
                    key={item.productId}
                    className="flex items-center gap-3 bg-gray-900 rounded-lg p-2 shadow-sm border border-gray-700"
                  >
                    <div className="w-14 h-14 flex-shrink-0 rounded-lg bg-gray-700 overflow-hidden border border-gray-600">
                      {item.image ? (
                        <img
                          src={item.image}
                          alt={item.name}
                          className="w-full h-full object-cover"
                        />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center text-gray-500 text-xl">?</div>
                      )}
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="font-semibold truncate text-gray-100">{item.name}</div>
                      <div className="text-xs text-gray-400">Qty: {item.quantity}</div>
                    </div>
                    <div className="font-bold text-gray-200 ml-2">€{(item.price * item.quantity).toFixed(2)}</div>
                    <button
                      className="ml-2 text-red-500 hover:text-red-700 text-lg font-bold focus:outline-none"
                      title="Remove"
                      onClick={() => {
                        const updated = cart.filter((_, i) => i !== idx);
                        setCart(updated);
                        localStorage.setItem('cart', JSON.stringify(updated));
                        window.dispatchEvent(new Event('cart-updated'));
                      }}
                    >
                      ×
                    </button>
                  </div>
                ))}
              </div>
              <div className="border-t border-gray-700 pt-4">
                <div className="flex justify-between font-bold text-lg text-gray-100">
                  <span>Total:</span>
                  <span>€{totalPrice.toFixed(2)}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
