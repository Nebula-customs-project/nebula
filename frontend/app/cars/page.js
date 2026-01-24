"use client";
import Link from 'next/link'
import { useEffect, useState } from 'react'
import { fetchAllVehicles } from '../lib/vehicleApi'

export default function CarsPage() {
  const [cars, setCars] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    const fetchCars = async () => {
      setLoading(true)
      const result = await fetchAllVehicles()
      if (result.error) {
        setError(result.message)
        setCars([])
      } else {
        setCars(result.vehicles)
        setError(null)
      }
      setLoading(false)
    }
    fetchCars()
  }, [])

  return (
    <div className="min-h-screen bg-gray-900 text-white py-16 px-4">
      <div className="max-w-7xl mx-auto">
        <h1 className="text-5xl font-bold mb-12 text-center">Our Collection</h1>
        {loading ? (
          <div className="text-center text-gray-400">Loading vehicles...</div>
        ) : error ? (
          <div className="text-center text-red-500">{error}</div>
        ) : (
          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-8">
            {cars.map((car, idx) => (
              <div key={idx} className="bg-gray-800 rounded-lg overflow-hidden hover:transform hover:scale-105 transition">
                <div className="h-48 bg-gray-700 bg-cover bg-center" style={{ backgroundImage: `url(${car.image || car.imageUrl})` }}></div>
                <div className="p-6">
                  <h3 className="text-2xl font-bold mb-2">{car.name || car.carName}</h3>
                  <p className="text-gray-400 mb-4">{car.type || car.carType}</p>
                  <div className="flex justify-between items-center">
                    <span className="text-red-500 font-bold text-xl">{car.price ? `€${car.price}` : ''}</span>
                    <span className="text-gray-400">{car.power || car.horsepower}</span>
                  </div>
                  <Link href="/car-configurator" className="block w-full mt-4 bg-red-600 hover:bg-red-700 py-2 rounded transition text-center">
                    Configure
                  </Link>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}