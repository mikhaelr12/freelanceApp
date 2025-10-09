import dayjs from 'dayjs';
import { ICategory } from 'app/shared/model/category.model';

export interface ISubcategory {
  id?: number;
  name?: string;
  createdDate?: dayjs.Dayjs;
  lastModifiedDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  lastModifiedBy?: string | null;
  active?: boolean;
  category?: ICategory | null;
}

export const defaultValue: Readonly<ISubcategory> = {
  active: false,
};
