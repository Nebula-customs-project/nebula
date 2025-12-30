/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './pages/**/*.{js,ts,jsx,tsx,mdx}',
    './components/**/*.{js,ts,jsx,tsx,mdx}',
    './app/**/*.{js,ts,jsx,tsx,mdx}',
  ],
  theme: {
    extend: {
      colors: {
        porsche: {
          red: '#D5001C',
          black: '#000000',
          gray: '#6B6B6B',
          silver: '#C0C0C0',
          gold: '#B8860B',
        },
        map: {
          route: '#3B82F6',
          routeGlow: 'rgba(59, 130, 246, 0.4)',
          car: '#D5001C',
          dealership: '#10B981',
          start: '#F59E0B',
        },
      },
      animation: {
        'pulse-slow': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'bounce-slow': 'bounce 2s infinite',
        'car-drive': 'carDrive 0.5s ease-in-out infinite',
      },
      keyframes: {
        carDrive: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-2px)' },
        },
      },
    },
  },
  plugins: [],
};
