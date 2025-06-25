import type IProduct from "./IProduct";

export default interface IPaginatedProducts{
    products: IProduct[];
    totalElements: number;
    totalPages: number;
    page: number;
    size: number;
}