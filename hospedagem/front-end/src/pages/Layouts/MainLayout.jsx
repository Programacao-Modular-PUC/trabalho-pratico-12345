import { Outlet } from "react-router-dom"; // Importa o componente Outlet do react-router-dom para renderizar os componentes filhos
import NavBar from "../../components/NavBar";

function MainLayout() {
    return (
        <div>
            <NavBar /> {/* Renderiza a barra de navegação */}
            <Outlet /> {/* Renderiza os componentes filhos definidos nas rotas */}
        </div>
    );
}

export default MainLayout;