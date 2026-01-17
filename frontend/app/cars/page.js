import Link from 'next/link'

export default function CarsPage() {
  const cars = [
    { name: 'Nebula Velocity', type: 'Sport', price: '€89,900', power: '450 HP', img: 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=600' },
    { name: 'Nebula Elite', type: 'Luxury Sedan', price: '€125,000', power: '380 HP', img: 'https://images.unsplash.com/photo-1555215695-3004980ad54e?w=600' },
    { name: 'Nebula Apex', type: 'Supercar', price: '€245,000', power: '720 HP', img: 'https://images.unsplash.com/photo-1544636331-e26879cd4d9b?w=600' },
    { name: 'Nebula Urban', type: 'SUV', price: '€68,500', power: '310 HP', img: 'https://images.unsplash.com/photo-1519641471654-76ce0107ad1b?w=600' },
    { name: 'Nebula Thunder', type: 'Electric', price: '€95,000', power: '500 HP', img: 'https://images.unsplash.com/photo-1560958089-b8a1929cea89?w=600' },
    { name: 'Nebula Prestige', type: 'Luxury Coupe', price: '€175,000', power: '550 HP', img: 'https://images.unsplash.com/photo-1541443131876-44b03de101c5?w=600' }
  ]

  return (
    <div className="min-h-screen bg-gray-900 text-white py-16 px-4">
      <div className="max-w-7xl mx-auto">
        <h1 className="text-5xl font-bold mb-12 text-center">Our Collection</h1>
        <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
          {cars.map((car, idx) => (
            <div key={idx} className="bg-gray-800 rounded-lg overflow-hidden hover:transform hover:scale-105 transition">
              <div className="h-48 bg-gray-700 bg-cover bg-center" style={{backgroundImage: `url(${car.img})`}}></div>
              <div className="p-6">
                <h3 className="text-2xl font-bold mb-2">{car.name}</h3>
                <p className="text-gray-400 mb-4">{car.type}</p>
                <div className="flex justify-between items-center">
                  <span className="text-red-500 font-bold text-xl">{car.price}</span>
                  <span className="text-gray-400">{car.power}</span>
                </div>
                <Link href="/car-configurator" className="block w-full mt-4 bg-red-600 hover:bg-red-700 py-2 rounded transition text-center">
                  Configure
                </Link>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}