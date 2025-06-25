import type ICategory from '../Category/ICategory';

export default interface IMetric {
  category: ICategory;
  quantity: number;
  value: number;
  averagePrice: number;
}
