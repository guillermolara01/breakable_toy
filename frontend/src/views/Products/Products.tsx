import { useProductContext } from "../../context/ProductContext"
import ProductList from "../../components/products/productList/ProductList";
import ProductStats from "../../components/products/productsStats/ProductStats";
import SearchBar from "../../components/products/SearchBar/SearchBar";
import type ICategory from "../../interfaces/Category/ICategory";

export default function Products(){
    const {products, loading, error} = useProductContext();

    const categories: ICategory[] = [
    { id: 1, name: 'Electrónica' },
    { id: 2, name: 'Limpieza' },
    { id: 3, name: 'Alimentos' },
    { id: 4, name: 'Entretenimiento' }
    ];

    console.log(products);
    console.log(loading);
    console.log(error);
    return <>
        <SearchBar categories={categories}></SearchBar>
        <ProductList></ProductList>
        <ProductStats></ProductStats>
    </>
}