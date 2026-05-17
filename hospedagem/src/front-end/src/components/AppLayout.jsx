import { NavLink, Outlet } from 'react-router-dom';

const links = [
  { to: '/', label: 'Painel' },
  { to: '/residencias', label: 'Residencias' },
  { to: '/quartos', label: 'Quartos' },
  { to: '/clientes', label: 'Clientes' },
  { to: '/alugueis', label: 'Alugueis' },
];

function navClass({ isActive }) {
  return [
    'rounded-md px-3 py-2 text-sm font-medium transition',
    isActive ? 'bg-slate-900 text-white' : 'text-slate-600 hover:bg-slate-100 hover:text-slate-950',
  ].join(' ');
}

export default function AppLayout() {
  return (
    <div className="min-h-screen bg-slate-50 text-slate-950">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-7xl flex-col gap-4 px-4 py-4 sm:flex-row sm:items-center sm:justify-between sm:px-6 lg:px-8">
          <div>
            <h1 className="text-xl font-semibold tracking-normal text-slate-950">Hospedagem</h1>
            <p className="text-sm text-slate-500">Gestao de residencias, quartos, clientes e alugueis</p>
          </div>

          <nav className="flex flex-wrap gap-2">
            {links.map((link) => (
              <NavLink key={link.to} to={link.to} className={navClass}>
                {link.label}
              </NavLink>
            ))}
          </nav>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-4 py-6 sm:px-6 lg:px-8">
        <Outlet />
      </main>
    </div>
  );
}
