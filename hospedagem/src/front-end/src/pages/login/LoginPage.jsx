import { Link } from 'react-router-dom';

export default function LoginPage() {
  return (
    <div className="flex min-h-screen items-center justify-center bg-slate-50 px-4">
      <div className="w-full max-w-sm rounded-md border border-slate-200 bg-white p-6 shadow-sm">
        <h1 className="text-xl font-semibold tracking-normal text-slate-950">Login</h1>
        <p className="mt-2 text-sm text-slate-500">Sessao encerrada.</p>
        <Link
          to="/"
          className="mt-5 inline-flex rounded-md bg-slate-900 px-3 py-2 text-sm font-medium text-white hover:bg-slate-800"
        >
          Voltar ao painel
        </Link>
      </div>
    </div>
  );
}
