/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        darkBg: "#0B1329",
        darkCard: "#1C2541",
        accentMint: "#10B981",
        accentEmerald: "#059669",
        clinicalRed: "#EF4444",
        clinicalOrange: "#F97316",
        textWhite: "#F8FAFC",
        textGray: "#94A3B8",
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
