import{Link}  from 'react-router-dom';

function Cadastro() {

    return (
        <div className="max-w-md mx-auto mt-10 bg-white p-8 border border-gray-300 rounded-lg shadow-lg">
            <h2 className="text-2xl font-bold mb-6 text-center text-gray-900" >Casdastro</h2>

            <form className="flex flex-col gap-4">

                <input type="text" placeholder="Nome" className="w-full px-3 py-2 bg-white border border-gray-300 rounded-lg"/>
                <input type="email" placeholder="Email" className="w-full px-3 py-2 bg-white border border-gray-300 rounded-lg"/>
                <input type="Password" placeholder="Senha" className="w-full px-3 py-2 bg-white border border-gray-300 rounded-lg"/>
                <button type="submit" className="w-full px-4 py-2 bg-blue-600 text-white border-gray-300 rounded-lg hover:bg-blue-500">Cadastrar</button>

            </form>
            <Link to="/login" className="text-blue-700 hover:underline mt-4 block text-center" >Já tem uma conta? Faça Login</Link>
        </div>
    )
}

export default Cadastro;