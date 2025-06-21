import type ICategory from '../../../interfaces/Category/ICategory';

export interface IProductListProps {
  categories: ICategory[];
  handleToggleModal(value:boolean, editMode:boolean): void;
}
