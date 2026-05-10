import { room } from "../data/room.js";
import RoomCard from "../components/RoomCard.jsx";

function Hospedagem() {
    return (
        <div className="min-h-screen bg-gray-50">

            {/* HEADER */}
            <div className="max-w-7xl mx-auto px-6 pt-10 pb-6 text-center">
                <h1 className="text-3xl md:text-4xl font-bold text-gray-800">
                    Encontre sua hospedagem ideal
                </h1>

                <p className="text-gray-500 mt-2">
                    Protótipo visual para quartos, reserva e emissão de recibo
                </p>
            </div>

            {/* LISTA DE QUARTOS */}
            <div className="max-w-7xl mx-auto px-6 pb-10">

                <div className="grid gap-6
                                sm:grid-cols-1
                                md:grid-cols-2
                                lg:grid-cols-3">

                    {room.map((item) => (
                        <RoomCard key={item.id} room={item} />
                    ))}

                </div>
            </div>

            <div className="max-w-7xl mx-auto px-6 pb-6 grid gap-6 lg:grid-cols-2">
                <section className="bg-white rounded-2xl border border-gray-200 p-6 shadow-sm">
                    <h2 className="text-xl font-semibold text-gray-800">Nova Reserva/Aluguel</h2>
                    <p className="text-sm text-gray-500 mt-1 mb-4">Formulário de protótipo com campos obrigatórios</p>

                    <form className="grid gap-3 md:grid-cols-2">
                        <input className="px-3 py-2 border border-gray-300 rounded-lg" placeholder="Residência" />
                        <input className="px-3 py-2 border border-gray-300 rounded-lg" placeholder="Quarto" />
                        <input className="px-3 py-2 border border-gray-300 rounded-lg" placeholder="Cliente" />
                        <input className="px-3 py-2 border border-gray-300 rounded-lg" placeholder="Tipo (Individual/Casal)" />
                        <input className="px-3 py-2 border border-gray-300 rounded-lg" placeholder="Entrada (dd/mm/aaaa hh:mm)" />
                        <input className="px-3 py-2 border border-gray-300 rounded-lg" placeholder="Saída (dd/mm/aaaa hh:mm)" />
                        <button type="button" className="md:col-span-2 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-500">Simular Reserva</button>
                    </form>
                </section>

                <section className="bg-white rounded-2xl border border-gray-200 p-6 shadow-sm">
                    <h2 className="text-xl font-semibold text-gray-800">Recibo (Pré-visualização)</h2>
                    <p className="text-sm text-gray-500 mt-1 mb-4">Formato solicitado no enunciado</p>

                    <div className="rounded-lg bg-gray-50 border border-gray-200 p-4 text-sm text-gray-700 space-y-2">
                        <p>Data e horário de entrada: 10/05/2026 14:00</p>
                        <p>Data e horário de saída: 12/05/2026 13:30</p>
                        <p>Número de diárias: 3</p>
                        <p className="font-semibold">Total a pagar: R$ 780,00</p>
                    </div>
                </section>
            </div>
        </div>
    );
}
export default Hospedagem;