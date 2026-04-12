🏨 Projeto Hospedagem

Sistema desenvolvido para trabalho prático da faculdade, focado em gestão de hospedagem.

Tecnologias utilizadas
React
Vite
Tailwind CSS v4
React Router

🚀 Criar o projeto pela primeira vez

Caso vá criar um projeto do zero:

npm create vite@latest my-project
cd my-project

Instalar Tailwind CSS:

npm install tailwindcss @tailwindcss/vite

Configurar o Vite (vite.config.js):

import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
plugins: [react(), tailwindcss()],
})

Importar Tailwind no CSS (src/index.css):

@import "tailwindcss";

Rodar o projeto:

npm run dev

Toda vez que clonar o projeto, você PRECISA rodar:

npm install tailwindcss @tailwindcss/vite
npm install react-router-dom
npm run dev
