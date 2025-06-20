
import ProductList from "../../components/products/productList/ProductList";
import ProductStats from "../../components/products/productsStats/ProductStats";
import SearchBar from "../../components/products/SearchBar/SearchBar";
import type ICategory from "../../interfaces/Category/ICategory";

export default function Products(){
   

    const categories: ICategory[] = [
    { id: 1, name: 'Electrónica' },
    { id: 2, name: 'Limpieza' },
    { id: 3, name: 'Alimentos' },
    { id: 4, name: 'Entretenimiento' }
    ];

    return <>
        <SearchBar categories={categories}></SearchBar>
        <ProductList></ProductList>
        <ProductStats></ProductStats>
    </>
}