import { Link } from "react-router-dom";
import Button from "./Button.jsx";

function NavBar() {
    return (
        <nav className="w-full bg-white border-b border-gray-200 shadow-sm">

            <div className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">

                {/* LOGO */}
                <h1 className="text-xl font-bold text-gray-800">
                    Hospedagem
                </h1>

                {/* LINKS */}
                <ul className="flex items-center gap-8 text-gray-600 font-medium">
                    <li>
                        <Link to="/" className="hover:text-black transition">
                            Visão Geral
                        </Link>
                    </li>
                    <li>
                        <Link to="/hospedagem" className="hover:text-black transition">
                            Quartos
                        </Link>
                    </li>
                    <li>
                        <Link to="/cadastro" className="hover:text-black transition">
                            Clientes
                        </Link>
                    </li>
                </ul>

                <div className="flex items-center gap-3">
                    <Button text="Login" to="/login" variant="primary" />
                    <Button text="Cadastro" to="/cadastro" variant="secondary" />
                </div>

            </div>
        </nav>
    );
}

export default NavBar;