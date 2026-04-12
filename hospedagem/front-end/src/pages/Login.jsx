import { Link } from "react-router-dom";

function Login() {
    return (

        <div className="max-w-md mx-auto mt-10 bg-white p-8 border border-gray-300 rounded-lg shadow-lg">
            <h2 className="text-2xl font-bold mb-2 text-center text-gray-900" >Login</h2>
            <p className="text-sm text-gray-500 mb-6 text-center">Acesso do cliente (protótipo sem autenticação)</p>

            <form className="flex flex-col gap-4">

                <input type="email" placeholder="Email" className="w-full px-3 py-2 bg-white border border-gray-300 rounded-lg" />
                <input type="Password" placeholder="Senha" className="w-full px-3 py-2 bg-white border border-gray-300 rounded-lg" />
                <button type="button" className="w-full px-4 py-2 bg-blue-600 text-white border-gray-300 rounded-lg hover:bg-blue-500">Entrar</button>

            </form>
            <Link to="/cadastro" className="text-blue-700 hover:underline mt-4 block text-center" >Não possui uma conta? Faça seu cadastro!</Link>
        </div>
    )
}

export default Login;