import { Link } from "react-router-dom";

function Button({ text, to, variant = "primary" }) {

    // estilos do botão
    const baseStyle = `
        px-5 py-2 rounded-full text-sm font-medium 
        transition duration-200 
        flex items-center justify-center
    `;

    // variações de estilo
    const variants = {
        primary: "bg-blue-500 text-white hover:bg-blue-600 active:scale-95",
        secondary: "border border-gray-300 text-gray-700 hover:bg-gray-100 active:scale-95"
    };

    return (
        <Link to={to}>
            <button className={`${baseStyle} ${variants[variant]}`}>
                {text}
            </button>
        </Link>
    );
}

export default Button;