function RoomCard({ room }) {
    return (
        // Card principal
        <div className="bg-white rounded-2xl shadow-md overflow-hidden
                        hover:shadow-xl hover:-translate-y-1 transition duration-300">

            {/* Imagem do quarto */}
            <div className="relative">
                <img
                    src={room.image}
                    alt={room.name}
                    className="w-full h-52 object-cover"
                />

                {/* Badge de preço (fica em cima da imagem) */}
                <span className="absolute top-3 right-3 bg-white/90 backdrop-blur px-3 py-1 rounded-full text-sm font-semibold shadow">
                    {room.price}
                </span>
            </div>

            {/* Conteúdo do card */}
            <div className="p-5">

                {/* Nome do quarto */}
                <h2 className="text-lg font-semibold text-gray-800 mb-2">
                    {room.name}
                </h2>

                {/* Descrição */}
                <p className="text-sm text-gray-500 mb-4">
                    {room.description}
                </p>

                {/* Botão */}
                <button
                    className="w-full bg-blue-500 text-white py-2 rounded-xl
                               font-medium hover:bg-blue-600 active:scale-95 transition"
                >
                    Reservar
                </button>
            </div>
        </div>
    );
}
export default RoomCard;