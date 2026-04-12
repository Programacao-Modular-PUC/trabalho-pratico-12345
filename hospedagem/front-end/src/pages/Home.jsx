function Home() {
    return (
        <div className="min-h-screen bg-gray-50">
            <div className="max-w-7xl mx-auto px-6 pt-10 pb-6">
                <h1 className="text-3xl md:text-4xl font-bold text-gray-800">
                    Sistema de Hospedagem - Protótipo
                </h1>
                <p className="text-gray-500 mt-2">
                    Entrega parcial com telas sem funcionalidade para validação da modelagem.
                </p>
            </div>

            <div className="max-w-7xl mx-auto px-6 pb-10 grid gap-6 md:grid-cols-2 lg:grid-cols-3">
                <section className="bg-white border border-gray-200 rounded-2xl p-5 shadow-sm">
                    <h2 className="text-lg font-semibold text-gray-800">Residências</h2>
                    <p className="text-sm text-gray-500 mt-2">Cadastro de endereço, contato e quartos da residência.</p>
                </section>

                <section className="bg-white border border-gray-200 rounded-2xl p-5 shadow-sm">
                    <h2 className="text-lg font-semibold text-gray-800">Quartos</h2>
                    <p className="text-sm text-gray-500 mt-2">Tipo, valor base, ar-condicionado e hidromassagem.</p>
                </section>

                <section className="bg-white border border-gray-200 rounded-2xl p-5 shadow-sm">
                    <h2 className="text-lg font-semibold text-gray-800">Clientes</h2>
                    <p className="text-sm text-gray-500 mt-2">Cadastro e autenticação com dados básicos de contato.</p>
                </section>

                <section className="bg-white border border-gray-200 rounded-2xl p-5 shadow-sm">
                    <h2 className="text-lg font-semibold text-gray-800">Reservas e Aluguéis</h2>
                    <p className="text-sm text-gray-500 mt-2">Controle de período e disponibilidade por quarto.</p>
                </section>

                <section className="bg-white border border-gray-200 rounded-2xl p-5 shadow-sm">
                    <h2 className="text-lg font-semibold text-gray-800">Recibo</h2>
                    <p className="text-sm text-gray-500 mt-2">Visualização de entrada, saída, diárias e total a pagar.</p>
                </section>

                <section className="bg-white border border-gray-200 rounded-2xl p-5 shadow-sm">
                    <h2 className="text-lg font-semibold text-gray-800">Histórico</h2>
                    <p className="text-sm text-gray-500 mt-2">Registro de hospedagens por residência.</p>
                </section>
            </div>
        </div>
    );
}

export default Home;