import './App.css'
import { ProductsProvider } from './context/ProductContext'
import Products from './views/Products/Products'

function App() {
  
  return (
    <>
      <ProductsProvider>
        <Products></Products>
      </ProductsProvider>
    </>
  )
}

export default App
