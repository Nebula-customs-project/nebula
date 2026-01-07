// setup.js - Run this with: node setup.js
const fs = require('fs');
const path = require('path');

// Create directory if it doesn't exist
function createDir(dir) {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
    console.log(`✓ Created directory: ${dir}`);
  }
}

// Write file with content
function writeFile(filePath, content) {
  fs.writeFileSync(filePath, content);
  console.log(`✓ Created file: ${filePath}`);
}

console.log('🚀 Setting up Nebula Next.js project...\n');

// Create directories
const dirs = [
  'app/cars',
  'app/configurator',
  'app/world-drive',
  'app/merchandise',
  'app/my-car',
  'components',
  'hooks'
];

dirs.forEach(dir => createDir(dir));

// ========== app/layout.js ==========
const layoutContent = `import { Inter } from 'next/font/google'
import './globals.css'
import Navigation from '@/components/Navigation'

const inter = Inter({ subsets: ['latin'] })

export const metadata = {
  title: 'Nebula - Engineering Excellence',
  description: 'Luxury automobile manufacturer',
}

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <Navigation />
        {children}
      </body>
    </html>
  )
}`;

writeFile('app/layout.js', layoutContent);

// ========== components/Navigation.js ==========
const navigationContent = `'use client'

import Link from 'next/link'
import { usePathname } from 'next/navigation'
import { Car, ShoppingCart, MapPin, User, Settings } from 'lucide-react'

export default function Navigation() {
  const pathname = usePathname()

  const navigation = [
    { href: '/', label: 'Home', icon: Car },
    { href: '/cars', label: 'Cars', icon: Car },
    { href: '/configurator', label: 'Configurator', icon: Settings },
    { href: '/world-drive', label: 'World Drive', icon: MapPin },
    { href: '/merchandise', label: 'Merchandise', icon: ShoppingCart },
    { href: '/my-car', label: 'My Nebula Car', icon: User },
  ]

  return (
    <nav className="bg-black text-white sticky top-0 z-50 shadow-lg">
      <div className="max-w-7xl mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          <Link href="/" className="flex items-center gap-2">
            <Car className="w-8 h-8 text-red-500" />
            <span className="text-2xl font-bold">NEBULA</span>
          </Link>
          <div className="flex gap-1">
            {navigation.map(item => {
              const Icon = item.icon
              const isActive = pathname === item.href
              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={\`flex items-center gap-2 px-4 py-2 rounded-lg transition \${
                    isActive ? 'bg-red-600' : 'hover:bg-gray-800'
                  }\`}
                >
                  <Icon className="w-4 h-4" />
                  <span className="hidden md:inline">{item.label}</span>
                </Link>
              )
            })}
          </div>
        </div>
      </div>
    </nav>
  )
}`;

writeFile('components/Navigation.js', navigationContent);

// ========== hooks/useMQTT.js ==========
const useMQTTContent = `'use client'

import { useState, useEffect } from 'react'

export function useMQTT(topic) {
  const [data, setData] = useState(null)
  
  useEffect(() => {
    // Simulate MQTT data updates
    const interval = setInterval(() => {
      if (topic === 'supercar/location') {
        setData({
          lat: 48.7758 + (Math.random() - 0.5) * 0.1,
          lng: 9.1829 + (Math.random() - 0.5) * 0.1,
          timestamp: Date.now()
        })
      } else if (topic === 'mycar/fuel') {
        setData(prev => Math.max(10, Math.min(100, (prev || 75) + (Math.random() - 0.5) * 2)))
      } else if (topic === 'mycar/location') {
        setData({
          lat: 48.7758 + (Math.random() - 0.5) * 0.05,
          lng: 9.1829 + (Math.random() - 0.5) * 0.05
        })
      }
    }, 3000)
    
    return () => clearInterval(interval)
  }, [topic])
  
  return data
}`;

writeFile('hooks/useMQTT.js', useMQTTContent);

// ========== app/page.js (Home) ==========
const homeContent = `import { Car, Settings, Package } from 'lucide-react'
import Link from 'next/link'

export default function Home() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-gray-900 via-gray-800 to-black text-white">
      <div className="relative h-screen flex items-center justify-center overflow-hidden">
        <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?w=1920')] bg-cover bg-center opacity-30"></div>
        <div className="relative z-10 text-center px-4">
          <h1 className="text-7xl font-bold mb-6 tracking-tight">NEBULA</h1>
          <p className="text-2xl mb-8 text-gray-300">Engineering Excellence, Defining Performance</p>
          <Link href="/cars" className="inline-block bg-red-600 hover:bg-red-700 px-8 py-4 rounded-lg text-lg font-semibold transition transform hover:scale-105">
            Explore Our Models
          </Link>
        </div>
      </div>
      
      <div className="py-20 px-4">
        <div className="max-w-6xl mx-auto grid md:grid-cols-3 gap-8">
          <div className="bg-gray-800 p-8 rounded-lg text-center hover:bg-gray-750 transition">
            <Car className="w-16 h-16 mx-auto mb-4 text-red-500" />
            <h3 className="text-xl font-bold mb-2">Innovation</h3>
            <p className="text-gray-400">Cutting-edge technology in every vehicle</p>
          </div>
          <div className="bg-gray-800 p-8 rounded-lg text-center hover:bg-gray-750 transition">
            <Settings className="w-16 h-16 mx-auto mb-4 text-red-500" />
            <h3 className="text-xl font-bold mb-2">Performance</h3>
            <p className="text-gray-400">Unmatched power and precision</p>
          </div>
          <div className="bg-gray-800 p-8 rounded-lg text-center hover:bg-gray-750 transition">
            <Package className="w-16 h-16 mx-auto mb-4 text-red-500" />
            <h3 className="text-xl font-bold mb-2">Luxury</h3>
            <p className="text-gray-400">Premium craftsmanship and comfort</p>
          </div>
        </div>
      </div>
    </div>
  )
}`;

writeFile('app/page.js', homeContent);

// ========== app/cars/page.js ==========
const carsContent = `import Link from 'next/link'

export default function CarsPage() {
  const cars = [
    { name: 'PSE Velocity', type: 'Sport', price: '€89,900', power: '450 HP', img: 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=600' },
    { name: 'PSE Elite', type: 'Luxury Sedan', price: '€125,000', power: '380 HP', img: 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=600' },
    { name: 'PSE Apex', type: 'Supercar', price: '€245,000', power: '720 HP', img: 'https://images.unsplash.com/photo-1544636331-e26879cd4d9b?w=600' },
    { name: 'PSE Urban', type: 'SUV', price: '€68,500', power: '310 HP', img: 'https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?w=600' },
    { name: 'PSE Thunder', type: 'Electric', price: '€95,000', power: '500 HP', img: 'https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=600' },
    { name: 'PSE Prestige', type: 'Luxury Coupe', price: '€175,000', power: '550 HP', img: 'https://images.unsplash.com/photo-1541443131876-44b03de101c5?w=600' }
  ]

  return (
    <div className="min-h-screen bg-gray-900 text-white py-16 px-4">
      <div className="max-w-7xl mx-auto">
        <h1 className="text-5xl font-bold mb-12 text-center">Our Collection</h1>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
          {cars.map((car, idx) => (
            <div key={idx} className="bg-gray-800 rounded-lg overflow-hidden hover:transform hover:scale-105 transition">
              <div className="h-48 bg-gray-700 bg-cover bg-center" style={{backgroundImage: \`url(\${car.img})\`}}></div>
              <div className="p-6">
                <h3 className="text-2xl font-bold mb-2">{car.name}</h3>
                <p className="text-gray-400 mb-4">{car.type}</p>
                <div className="flex justify-between items-center">
                  <span className="text-red-500 font-bold text-xl">{car.price}</span>
                  <span className="text-gray-400">{car.power}</span>
                </div>
                <Link href="/configurator" className="block w-full mt-4 bg-red-600 hover:bg-red-700 py-2 rounded transition text-center">
                  Configure
                </Link>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}`;

writeFile('app/cars/page.js', carsContent);

// ========== app/configurator/page.js ==========
const configuratorContent = `'use client'

import { useState } from 'react'
import { Car } from 'lucide-react'

export default function ConfiguratorPage() {
  const [config, setConfig] = useState({
    color: 'red',
    rims: 'sport',
    interior: 'black'
  })

  const colors = [
    { name: 'Racing Red', value: 'red', hex: '#DC2626' },
    { name: 'Midnight Black', value: 'black', hex: '#000000' },
    { name: 'Pearl White', value: 'white', hex: '#FFFFFF' },
    { name: 'Ocean Blue', value: 'blue', hex: '#2563EB' },
    { name: 'Silver', value: 'silver', hex: '#94A3B8' }
  ]

  const rims = [
    { name: 'Sport 19"', value: 'sport', price: 'Standard' },
    { name: 'Performance 20"', value: 'performance', price: '+€2,500' },
    { name: 'Premium 21"', value: 'premium', price: '+€4,800' }
  ]

  const interiors = [
    { name: 'Black Leather', value: 'black' },
    { name: 'Beige Leather', value: 'beige' },
    { name: 'Red Sport', value: 'red' },
    { name: 'Carbon Fiber', value: 'carbon' }
  ]

  const calculateTotal = () => {
    let total = 245000
    if (config.rims === 'performance') total += 2500
    if (config.rims === 'premium') total += 4800
    return total
  }

  return (
    <div className="min-h-screen bg-gray-900 text-white py-16 px-4">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-5xl font-bold mb-12 text-center">Configure Your PSE</h1>
        
        <div className="grid md:grid-cols-2 gap-12">
          <div className="bg-gray-800 rounded-lg p-8">
            <div 
              className="aspect-video rounded-lg mb-6 flex items-center justify-center" 
              style={{backgroundColor: colors.find(c => c.value === config.color)?.hex}}
            >
              <Car className="w-32 h-32 text-white opacity-80" />
            </div>
            <div className="text-center">
              <h2 className="text-3xl font-bold mb-2">PSE Apex</h2>
              <p className="text-gray-400 mb-4">Your Configuration</p>
              <div className="bg-gray-700 p-4 rounded-lg">
                <div className="flex justify-between mb-2">
                  <span>Base Price:</span>
                  <span>€245,000</span>
                </div>
                <div className="flex justify-between mb-2">
                  <span>Rims:</span>
                  <span>{rims.find(r => r.value === config.rims)?.price}</span>
                </div>
                <div className="border-t border-gray-600 mt-2 pt-2">
                  <div className="flex justify-between font-bold text-xl">
                    <span>Total:</span>
                    <span className="text-red-500">€{calculateTotal().toLocaleString()}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="space-y-8">
            <div>
              <h3 className="text-2xl font-bold mb-4">Exterior Color</h3>
              <div className="grid grid-cols-5 gap-4">
                {colors.map(color => (
                  <button
                    key={color.value}
                    onClick={() => setConfig({...config, color: color.value})}
                    className={\`aspect-square rounded-full border-4 transition \${
                      config.color === color.value ? 'border-red-500 scale-110' : 'border-gray-600'
                    }\`}
                    style={{backgroundColor: color.hex}}
                    title={color.name}
                  />
                ))}
              </div>
            </div>

            <div>
              <h3 className="text-2xl font-bold mb-4">Rims</h3>
              <div className="space-y-3">
                {rims.map(rim => (
                  <button
                    key={rim.value}
                    onClick={() => setConfig({...config, rims: rim.value})}
                    className={\`w-full p-4 rounded-lg border-2 transition \${
                      config.rims === rim.value ? 'border-red-500 bg-gray-700' : 'border-gray-700 bg-gray-800'
                    }\`}
                  >
                    <div className="flex justify-between">
                      <span className="font-semibold">{rim.name}</span>
                      <span className="text-gray-400">{rim.price}</span>
                    </div>
                  </button>
                ))}
              </div>
            </div>

            <div>
              <h3 className="text-2xl font-bold mb-4">Interior</h3>
              <div className="grid grid-cols-2 gap-3">
                {interiors.map(interior => (
                  <button
                    key={interior.value}
                    onClick={() => setConfig({...config, interior: interior.value})}
                    className={\`p-4 rounded-lg border-2 transition \${
                      config.interior === interior.value ? 'border-red-500 bg-gray-700' : 'border-gray-700 bg-gray-800'
                    }\`}
                  >
                    {interior.name}
                  </button>
                ))}
              </div>
            </div>

            <button className="w-full bg-red-600 hover:bg-red-700 py-4 rounded-lg text-lg font-semibold transition">
              Save Configuration
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}`;

writeFile('app/configurator/page.js', configuratorContent);

// ========== app/world-drive/page.js ==========
const worldDriveContent = `'use client'

import { MapPin } from 'lucide-react'
import { useMQTT } from '@/hooks/useMQTT'

export default function WorldDrivePage() {
  const location = useMQTT('supercar/location')

  return (
    <div className="min-h-screen bg-gray-900 text-white py-16 px-4">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-5xl font-bold mb-8 text-center">PSE World Drive</h1>
        <p className="text-center text-gray-400 mb-12">Follow our Apex supercar on its global journey</p>
        
        <div className="bg-gray-800 rounded-lg p-8">
          <div className="aspect-video bg-gray-700 rounded-lg mb-6 relative overflow-hidden">
            <div className="absolute inset-0 bg-[url('https://api.mapbox.com/styles/v1/mapbox/dark-v10/static/9.1829,48.7758,11,0/800x600@2x?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw')] bg-cover bg-center"></div>
            <div className="absolute inset-0 flex items-center justify-center">
              <div className="animate-pulse">
                <MapPin className="w-12 h-12 text-red-500" />
              </div>
            </div>
          </div>
          
          <div className="grid md:grid-cols-3 gap-6">
            <div className="bg-gray-700 p-6 rounded-lg">
              <h3 className="text-lg font-semibold mb-2 text-gray-400">Current Location</h3>
              <p className="text-2xl font-bold">Stuttgart, Germany</p>
            </div>
            <div className="bg-gray-700 p-6 rounded-lg">
              <h3 className="text-lg font-semibold mb-2 text-gray-400">Coordinates</h3>
              <p className="text-xl font-mono">
                {location ? \`\${location.lat.toFixed(4)}°N, \${location.lng.toFixed(4)}°E\` : 'Loading...'}
              </p>
            </div>
            <div className="bg-gray-700 p-6 rounded-lg">
              <h3 className="text-lg font-semibold mb-2 text-gray-400">Status</h3>
              <p className="text-2xl font-bold text-green-500">● Live</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}`;

writeFile('app/world-drive/page.js', worldDriveContent);

// ========== app/merchandise/page.js ==========
const merchandiseContent = `'use client'

import { useState } from 'react'
import { ShoppingCart } from 'lucide-react'

export default function MerchandisePage() {
  const [cart, setCart] = useState([])
  
  const products = [
    { id: 1, name: 'PSE Racing Cap', price: 35, img: '🧢' },
    { id: 2, name: 'Team Jacket', price: 129, img: '🧥' },
    { id: 3, name: 'Car Model 1:18', price: 89, img: '🏎️' },
    { id: 4, name: 'Keychain', price: 25, img: '🔑' },
    { id: 5, name: 'Coffee Mug', price: 18, img: '☕' },
    { id: 6, name: 'T-Shirt', price: 45, img: '👕' }
  ]

  const addToCart = (product) => {
    setCart([...cart, product])
  }

  return (
    <div className="min-h-screen bg-gray-900 text-white py-16 px-4">
      <div className="max-w-7xl mx-auto">
        <div className="flex justify-between items-center mb-12">
          <h1 className="text-5xl font-bold">PSE Merchandise</h1>
          <div className="flex items-center gap-2 bg-red-600 px-6 py-3 rounded-lg">
            <ShoppingCart className="w-6 h-6" />
            <span className="font-bold">{cart.length}</span>
          </div>
        </div>
        
        <div className="grid md:grid-cols-3 gap-8">
          {products.map(product => (
            <div key={product.id} className="bg-gray-800 rounded-lg p-6 hover:transform hover:scale-105 transition">
              <div className="text-6xl mb-4 text-center">{product.img}</div>
              <h3 className="text-xl font-bold mb-2">{product.name}</h3>
              <div className="flex justify-between items-center mt-4">
                <span className="text-2xl font-bold text-red-500">€{product.price}</span>
                <button
                  onClick={() => addToCart(product)}
                  className="bg-red-600 hover:bg-red-700 px-4 py-2 rounded transition"
                >
                  Add to Cart
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}`;

writeFile('app/merchandise/page.js', merchandiseContent);

// ========== app/my-car/page.js ==========
const myCarContent = `'use client'

import { Car, Fuel, Navigation, MapPin } from 'lucide-react'
import { useMQTT } from '@/hooks/useMQTT'

export default function MyCarPage() {
  const fuelLevel = useMQTT('mycar/fuel')
  const carLocation = useMQTT('mycar/location')

  return (
    <div className="min-h-screen bg-gray-900 text-white py-16 px-4">
      <div className="max-w-6xl mx-auto">
        <h1 className="text-5xl font-bold mb-12 text-center">My PSE Car</h1>
        
        <div className="grid md:grid-cols-2 gap-8">
          <div className="bg-gray-800 rounded-lg p-8">
            <h2 className="text-2xl font-bold mb-6">My PSE Velocity</h2>
            <div className="aspect-video bg-gray-700 rounded-lg mb-6 flex items-center justify-center">
              <Car className="w-32 h-32 text-red-500" />
            </div>
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-gray-400">Model</span>
                <span className="font-semibold">PSE Velocity Sport</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-gray-400">VIN</span>
                <span className="font-mono text-sm">PSE2024VEL123456</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-gray-400">Color</span>
                <span className="font-semibold">Racing Red</span>
              </div>
            </div>
          </div>

          <div className="space-y-8">
            <div className="bg-gray-800 rounded-lg p-8">
              <div className="flex items-center gap-3 mb-4">
                <Fuel className="w-6 h-6 text-red-500" />
                <h3 className="text-2xl font-bold">Fuel Level</h3>
              </div>
              <div className="relative pt-1">
                <div className="flex mb-2 items-center justify-between">
                  <div className="text-3xl font-bold">
                    {fuelLevel ? \`\${fuelLevel.toFixed(1)}%\` : 'Loading...'}
                  </div>
                  <span className="text-sm text-gray-400">Live Data</span>
                </div>
                <div className="overflow-hidden h-4 mb-4 text-xs flex rounded-full bg-gray-700">
                  <div
                    style={{ width: \`\${fuelLevel || 0}%\` }}
                    className="shadow-none flex flex-col text-center whitespace-nowrap text-white justify-center bg-gradient-to-r from-red-600 to-red-400 transition-all duration-1000"
                  ></div>
                </div>
              </div>
            </div>

            <div className="bg-gray-800 rounded-lg p-8">
              <div className="flex items-center gap-3 mb-4">
                <Navigation className="w-6 h-6 text-red-500" />
                <h3 className="text-2xl font-bold">Current Position</h3>
              </div>
              <div className="aspect-video bg-gray-700 rounded-lg mb-4 relative overflow-hidden">
                <div className="absolute inset-0 bg-[url('https://api.mapbox.com/styles/v1/mapbox/dark-v10/static/9.1829,48.7758,13,0/600x400@2x?access_token=pk.eyJ1IjoibWFwYm94IiwiYSI6ImNpejY4NXVycTA2emYycXBndHRqcmZ3N3gifQ.rJcFIG214AriISLbB6B5aw')] bg-cover bg-center"></div>
                <div className="absolute inset-0 flex items-center justify-center">
                  <MapPin className="w-8 h-8 text-red-500 animate-bounce" />
                </div>
              </div>
              <div className="text-center">
                <p className="text-sm text-gray-400 mb-1">GPS Coordinates</p>
                <p className="font-mono text-lg">
                  {carLocation ? \`\${carLocation.lat.toFixed(4)}°N, \${carLocation.lng.toFixed(4)}°E\` : 'Loading...'}
                </p>
                <p className="text-sm text-green-500 mt-2">● Live Tracking Active</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}`;

writeFile('app/my-car/page.js', myCarContent);

console.log('\n✅ Setup complete!');
console.log('\n📝 Next steps:');
console.log('1. Run: npm install lucide-react');
console.log('2. Run: npm run dev');
console.log('3. Open: http://localhost:3000');
console.log('\n🚀 Your Nebula app is ready!');