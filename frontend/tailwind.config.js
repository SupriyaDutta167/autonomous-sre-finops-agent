/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        page: '#0f1115',      // near-black/slate
        surface: '#181b21',   // elevated slate
        subsurface: '#22262d', // slightly lighter slate
        border: '#2e343d',    // low-contrast
        primary: '#f1f5f9',   // high contrast
        secondary: '#94a3b8', // muted
        accent: '#38bdf8',    // controlled cyan/blue
        success: '#22c55e',   // green
        warning: '#f59e0b',   // amber
        danger: '#ef4444',    // red
      },
      fontFamily: {
        sans: ['system-ui', '-apple-system', 'sans-serif'],
        mono: ['ui-monospace', 'SFMono-Regular', 'Menlo', 'monospace'],
      },
      transitionDuration: {
        '250': '250ms',
      }
    },
  },
  plugins: [],
}
