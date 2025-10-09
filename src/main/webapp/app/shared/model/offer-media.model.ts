import dayjs from 'dayjs';
import { IOffer } from 'app/shared/model/offer.model';
import { IFileObject } from 'app/shared/model/file-object.model';
import { MediaKind } from 'app/shared/model/enumerations/media-kind.model';

export interface IOfferMedia {
  id?: number;
  mediaKind?: keyof typeof MediaKind;
  isPrimary?: boolean;
  caption?: string | null;
  createdDate?: dayjs.Dayjs;
  lastModifiedDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  lastModifiedBy?: string | null;
  offer?: IOffer | null;
  file?: IFileObject | null;
}

export const defaultValue: Readonly<IOfferMedia> = {
  isPrimary: false,
};
