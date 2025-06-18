import type IProduct from "./IProduct";
import type IFilterParams from "../FilteringParams/IFilterParams";

export interface IProductContext{
products: IProduct[];
  loading: boolean;
  error: string | null;
  params: IFilterParams;
  updateParams: (newParams: Partial<IFilterParams>) => void;
  refresh: () => void;
}