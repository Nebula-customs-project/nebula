'use client'

export default function CreditCardPreview({ cardNumber, cardName, cardExpiry }) {
    // Format card number with spaces
    const formatCardNumber = (num) => {
        const cleaned = num.replace(/\s/g, '')
        const formatted = cleaned.match(/.{1,4}/g)?.join(' ') || '•••• •••• •••• ••••'
        return formatted.padEnd(19, '•').slice(0, 19)
    }

    // Show Mastercard logo after 4 digits
    const showLogo = (num) => {
        const cleaned = num.replace(/\s/g, '')
        return cleaned.length >= 4
    }

    const displayLogo = showLogo(cardNumber || '')

    return (
        <div className="relative w-full max-w-sm mx-auto mb-8">
            <div className="relative aspect-[1.586] rounded-2xl overflow-hidden shadow-2xl transform hover:scale-105 transition-transform duration-300">
                {/* Card Background */}
                <div className="absolute inset-0 bg-gradient-to-br from-gray-800 via-gray-900 to-black">
                    {/* Animated Gradient Overlay */}
                    <div className="absolute inset-0 bg-gradient-to-tr from-red-600/20 via-transparent to-purple-600/20 animate-gradient"></div>
                </div>

                {/* Nebula Logo/Pattern */}
                <div className="absolute top-6 left-6">
                    <div className="flex items-center gap-2">
                        <div className="w-10 h-10 rounded-full bg-gradient-to-br from-red-500 to-purple-600 flex items-center justify-center">
                            <span className="text-white font-bold text-sm">N</span>
                        </div>
                        <span className="text-white font-bold text-xl">NEBULA</span>
                    </div>
                </div>

                {/* Mastercard Logo - Top Right */}
                {displayLogo && (
                    <div className="absolute top-6 right-6">
                        <div className="w-14 h-10 flex items-center justify-center">
                            <svg viewBox="0 0 48 32" className="w-full h-auto">
                                <circle cx="16" cy="16" r="14" fill="#EB001B" />
                                <circle cx="32" cy="16" r="14" fill="#F79E1B" />
                            </svg>
                        </div>
                    </div>
                )}

                {/* Chip */}
                <div className="absolute top-20 left-6">
                    <div className="w-12 h-9 rounded bg-gradient-to-br from-yellow-300 to-yellow-600"></div>
                </div>

                {/* Card Number */}
                <div className="absolute top-36 left-6 right-6">
                    <p className="text-white text-xl md:text-2xl font-mono tracking-wider">
                        {formatCardNumber(cardNumber || '')}
                    </p>
                </div>

                {/* Card Details */}
                <div className="absolute bottom-6 left-6 right-6 flex justify-between items-end">
                    <div className="flex-1 pr-4">
                        <p className="text-gray-400 text-xs mb-1">Card Holder</p>
                        <p className="text-white text-sm md:text-base font-semibold uppercase tracking-wide truncate">
                            {cardName || 'YOUR NAME'}
                        </p>
                    </div>
                    <div>
                        <p className="text-gray-400 text-xs mb-1">Expires</p>
                        <p className="text-white text-sm md:text-base font-mono">
                            {cardExpiry || '••/••'}
                        </p>
                    </div>
                </div>
            </div>
        </div>
    )
}
