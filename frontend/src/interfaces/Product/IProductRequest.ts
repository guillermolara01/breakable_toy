import type ICategory from "../Category/ICategory";

export default interface IProductRequest {
  name: string;
  category: ICategory;
  unitPrice: number;
  expirationDate: string;
  stock: number;
}
