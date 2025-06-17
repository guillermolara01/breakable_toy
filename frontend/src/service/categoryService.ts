import axios from 'axios';;

export  async function getCategories() {
    try{
        const response = await axios.get(`${import.meta.env.VITE_API_URL}categories`);
        console.log(`${import.meta.env.VITE_API_URL}categories`);
        const categories  = response.data;
        return categories
    }catch(error:unknown){
        console.log(error);
    }
}

export async function getCategoryById(id: number){
     try{
        const response = await axios.get(`${import.meta.env.VITE_API_URL}categories/${id}`);
        const categories  = response.data;
        return categories
    }catch(error:unknown){
        console.log(error);
    }
}

