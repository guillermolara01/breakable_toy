import type ICategory from "../Category/ICategory";

export default interface IProduct{
    id: number,
    name: string,
    category: ICategory,
    unitPrice: number,
    expirationDate: string,
    stock: number
}