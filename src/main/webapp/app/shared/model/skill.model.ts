import dayjs from 'dayjs';
import { ICategory } from 'app/shared/model/category.model';
import { IProfile } from 'app/shared/model/profile.model';

export interface ISkill {
  id?: number;
  name?: string;
  createdDate?: dayjs.Dayjs;
  lastModifiedDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  lastModifiedBy?: string | null;
  active?: boolean;
  category?: ICategory | null;
  profiles?: IProfile[] | null;
}

export const defaultValue: Readonly<ISkill> = {
  active: false,
};
