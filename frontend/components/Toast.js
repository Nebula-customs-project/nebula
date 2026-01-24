'use client'

import { useEffect, useState } from 'react'
import { X, CheckCircle, AlertCircle, Info } from 'lucide-react'

export default function Toast({ message, type = 'success', duration = 3000, onClose }) {
    const [isVisible, setIsVisible] = useState(true)
    const [isExiting, setIsExiting] = useState(false)

    useEffect(() => {
        const timer = setTimeout(() => {
            setIsExiting(true)
            setTimeout(() => {
                setIsVisible(false)
                onClose?.()
            }, 300)
        }, duration)

        return () => clearTimeout(timer)
    }, [duration, onClose])

    const handleClose = () => {
        setIsExiting(true)
        setTimeout(() => {
            setIsVisible(false)
            onClose?.()
        }, 300)
    }

    if (!isVisible) return null

    const icons = {
        success: CheckCircle,
        error: AlertCircle,
        info: Info
    }

    const colors = {
        success: 'bg-green-600',
        error: 'bg-red-600',
        info: 'bg-blue-600'
    }

    const Icon = icons[type]

    return (
        <div
            className={`fixed top-20 right-4 z-[100] transition-all duration-300 ${isExiting ? 'opacity-0 translate-x-8' : 'opacity-100 translate-x-0'
                }`}
        >
            <div className={`${colors[type]} text-white px-6 py-4 rounded-xl shadow-2xl flex items-center gap-3 min-w-[280px] max-w-md`}>
                <Icon className="w-5 h-5 flex-shrink-0" />
                <span className="flex-1 font-medium">{message}</span>
                <button
                    onClick={handleClose}
                    className="hover:bg-white/20 rounded-full p-1 transition"
                >
                    <X className="w-4 h-4" />
                </button>
            </div>
        </div>
    )
}

export function ToastContainer({ toasts, removeToast }) {
    return (
        <div className="fixed top-20 right-4 z-[100] flex flex-col gap-3">
            {toasts.map((toast) => (
                <Toast
                    key={toast.id}
                    message={toast.message}
                    type={toast.type}
                    duration={toast.duration}
                    onClose={() => removeToast(toast.id)}
                />
            ))}
        </div>
    )
}
