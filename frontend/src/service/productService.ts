import axios from 'axios'
import type IProduct from '../interfaces/Product/IProduct';

export async function getAllProducts(){
    try{
        const result = await axios.get(`${import.meta.env.VITE_API_URL}products`);
        return result.data;
    }catch(error:unknown){
console.log(error);
    }
}

export async function getById(id: number){
    try{
        const result = await axios.get(`${import.meta.env.VITE_API_URL}products/${id}`);
        console.log(result);
        return result.data;
    }catch(error:unknown){
        console.log(error);
    }
}

export async function createProduct(product:IProduct) {
    try{
        const savedProduct = await axios.post(`${import.meta.env.VITE_API_URL}products`, product);
        console.log(savedProduct);
        return savedProduct;
    }catch(error:unknown){
        console.log(error);
    }
}

export async function setOutOfStock(id: number){
    try{
        const result = await axios.put(`${import.meta.env.VITE_API_URL}products/${id}/outofstock`);
        console.log(result);

    }catch(error:unknown){
        console.log(error);
    }
}
export async function setInStock(id:number){
    try{
        const result = await axios.put(`${import.meta.env.BASE_URL}products/${id}/instock`);
        console.log(result);

    }catch(error:unknown){
        console.log(error);
    }
}

export async function deleteProduct(id: number){
     try{
        const result = await axios.delete(`${import.meta.env.VITE_API_URL}/products/${id}`);
        console.log(result);

    }catch(error:unknown){
        console.log(error);
    }
}
