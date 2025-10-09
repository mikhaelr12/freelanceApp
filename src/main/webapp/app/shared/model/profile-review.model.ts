import dayjs from 'dayjs';
import { IProfile } from 'app/shared/model/profile.model';

export interface IProfileReview {
  id?: number;
  text?: string | null;
  rating?: number;
  createdDate?: dayjs.Dayjs;
  lastModifiedDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  lastModifiedBy?: string | null;
  reviewer?: IProfile | null;
  reviewee?: IProfile | null;
}

export const defaultValue: Readonly<IProfileReview> = {};
