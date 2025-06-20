import axios from 'axios'
import type IProduct from '../interfaces/Product/IProduct';
import type IFilterParams from '../interfaces/FilteringParams/IFilterParams';
import type IPaginatedProducts from '../interfaces/Product/IPaginatedProducts';


export async function getAllProducts(params: IFilterParams = {}) {
  try {
    const query = new URLSearchParams();

    if (params.name) query.append('name', params.name);
    if (params.category) query.append('category', params.category);
    if (params.available !== undefined) query.append('available', String(params.available));
    if (params.sortBy) query.append('sortBy', params.sortBy);
    if (params.direction) query.append('direction', params.direction);
    query.append('page', String(params.page ?? 0));
    query.append('size', String(params.size ?? 10));
    console.log(query.toString());
    const result = await axios.get<IPaginatedProducts>(`${import.meta.env.VITE_API_URL}products?${query.toString()}`);
    return result.data as IPaginatedProducts;
  } catch (error: unknown) {
    console.error(error);
    throw(error);
  }
}

export async function getById(id: number){
    try{
        const result = await axios.get(`${import.meta.env.VITE_API_URL}products/${id}`);
        console.log(result);
        return result.data;
    }catch(error:unknown){
        console.log(error);
        throw(error);
    }
}

export async function createProduct(product:IProduct) {
    try{
        const savedProduct = await axios.post(`${import.meta.env.VITE_API_URL}products`, product);
        console.log(savedProduct);
        return savedProduct;
    }catch(error:unknown){
        console.log(error);
        throw(error);
    }
}

export async function setOutOfStock(id: number){
    try{
        const result = await axios.put(`${import.meta.env.VITE_API_URL}products/${id}/outofstock`);
        console.log(result);

    }catch(error:unknown){
        console.log(error);
        throw(error);
    }
}
export async function setInStock(id:number){
    try{
        const result = await axios.put(`${import.meta.env.BASE_URL}products/${id}/instock`);
        console.log(result);

    }catch(error:unknown){
        console.log(error);
        throw(error);
    }
}

export async function deleteProduct(id: number){
     try{
        const result = await axios.delete(`${import.meta.env.VITE_API_URL}products/${id}`);
        console.log(result);

    }catch(error:unknown){
        console.log(error);
        throw(error);
    }
}
