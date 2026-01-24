export default function ProductSkeleton() {
    return (
        <div className="group bg-gray-800 rounded-xl overflow-hidden shadow-xl animate-pulse">
            <div className="relative h-64 bg-gray-700">
                <div className="absolute top-4 right-4 w-10 h-10 bg-gray-600 rounded-full"></div>
            </div>

            <div className="p-5 space-y-3">
                <div className="flex gap-1">
                    {[...Array(5)].map((_, i) => (
                        <div key={i} className="w-4 h-4 bg-gray-700 rounded-full"></div>
                    ))}
                </div>

                <div className="h-6 bg-gray-700 rounded w-3/4"></div>
                <div className="h-4 bg-gray-700 rounded w-1/2"></div>

                <div className="flex items-center justify-between pt-2">
                    <div className="h-8 bg-gray-700 rounded w-1/3"></div>
                    <div className="w-12 h-12 bg-gray-700 rounded-lg"></div>
                </div>
            </div>
        </div>
    )
}

export function ProductGridSkeleton({ count = 8 }) {
    return (
        <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-8">
            {[...Array(count)].map((_, i) => (
                <ProductSkeleton key={i} />
            ))}
        </div>
    )
}
