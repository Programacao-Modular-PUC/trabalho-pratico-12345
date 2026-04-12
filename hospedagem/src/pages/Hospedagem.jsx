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
                    Escolha entre os melhores quartos disponíveis
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
        </div>
    );
}
export default Hospedagem;