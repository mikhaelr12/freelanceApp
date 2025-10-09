import dayjs from 'dayjs';

export interface ICountry {
  id?: number;
  name?: string;
  iso2?: string | null;
  iso3?: string | null;
  region?: string;
  createdDate?: dayjs.Dayjs;
  lastModifiedDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  lastModifiedBy?: string | null;
  active?: boolean;
}

export const defaultValue: Readonly<ICountry> = {
  active: false,
};
