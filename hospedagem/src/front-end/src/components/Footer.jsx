

function Footer() {

    return (
        <footer className="w-full bg-white border-t border-gray-200 shadow-sm">

            <div className="max-w-7xl mx-auto px-6 py-4 flex items-center justify-between">

                <span className="text-sm text-gray-500">
                    © 2026 Trabalho Programação Modular.
                </span>

                <ul className="flex items-center gap-6 text-gray-600 text-sm">
                    <li>
                        <a href="#" className="hover:underline">Sobre</a>
                    </li>
                    <li>
                        <a href="#" className="hover:underline">Politica de Privacidade</a>
                    </li>
                    <li>
                        <a href="#" className="hover:underline">Contato</a>
                    </li>
                </ul>

            </div>
        </footer>

    )
}

export default Footer;