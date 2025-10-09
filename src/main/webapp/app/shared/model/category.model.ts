import dayjs from 'dayjs';

export interface ICategory {
  id?: number;
  name?: string;
  createdDate?: dayjs.Dayjs;
  lastModifiedDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  lastModifiedBy?: string | null;
  active?: boolean;
}

export const defaultValue: Readonly<ICategory> = {
  active: false,
};
