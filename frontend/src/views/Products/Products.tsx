import { useProductContext } from "../../context/ProductContext"

export default function Products(){
    const {products, loading, error} = useProductContext();
    console.log(products);
    console.log(loading);
    console.log(error);
    return <>
        <h1>Products!</h1>
    </>
}