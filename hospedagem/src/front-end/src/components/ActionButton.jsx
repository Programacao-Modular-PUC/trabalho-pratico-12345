export default function ActionButton({ children, variant = 'primary', className = '', ...props }) {
  const variants = {
    primary: 'bg-slate-900 text-white hover:bg-slate-800 disabled:bg-slate-400',
    secondary: 'border border-slate-300 bg-white text-slate-700 hover:bg-slate-50 disabled:text-slate-400',
    danger: 'bg-red-700 text-white hover:bg-red-800 disabled:bg-red-300',
  };

  return (
    <button
      type="button"
      className={`rounded-md px-3 py-2 text-sm font-medium transition disabled:cursor-not-allowed ${variants[variant]} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
}
