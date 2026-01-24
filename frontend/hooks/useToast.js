'use client'

import { useState, useCallback } from 'react'

let toastId = 0

export function useToast() {
    const [toasts, setToasts] = useState([])

    const addToast = useCallback((message, type = 'success', duration = 3000) => {
        const id = toastId++
        // Replace existing toasts with the new one
        setToasts([{ id, message, type, duration }])
        return id
    }, [])

    const removeToast = useCallback((id) => {
        setToasts((prev) => prev.filter((toast) => toast.id !== id))
    }, [])

    return { toasts, addToast, removeToast }
}
