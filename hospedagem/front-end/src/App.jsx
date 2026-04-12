import { BrowserRouter, Route, Routes } from "react-router-dom"

import Cadastro from "./pages/Cadastro.jsx";
import Login from "./pages/Login.jsx";
import MainLayout from "./pages/Layouts/MainLayout.jsx";
import Hospedagem from "./pages/Hospedagem.jsx";
import Home from "./pages/Home.jsx";



function App() {
    return (
        <BrowserRouter>
            <Routes>

                {/* ROTAS COM NAVBAR */}
                <Route element={<MainLayout />}>
                    <Route path="/" element={<Home />} />
                    <Route path="/hospedagem" element={<Hospedagem />} />
                </Route>

                {/* ROTAS SEM NAVBAR */}
                <Route path="/login" element={<Login />} />
                <Route path="/cadastro" element={<Cadastro />} />

            </Routes>
        </BrowserRouter>
    );
}

export default App
