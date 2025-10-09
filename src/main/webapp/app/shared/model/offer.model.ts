import dayjs from 'dayjs';
import { IProfile } from 'app/shared/model/profile.model';
import { IOfferType } from 'app/shared/model/offer-type.model';
import { ITag } from 'app/shared/model/tag.model';
import { OfferStatus } from 'app/shared/model/enumerations/offer-status.model';

export interface IOffer {
  id?: number;
  name?: string;
  description?: string;
  rating?: number | null;
  status?: keyof typeof OfferStatus;
  visibility?: boolean;
  createdDate?: dayjs.Dayjs;
  lastModifiedDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  lastModifiedBy?: string | null;
  owner?: IProfile | null;
  offertype?: IOfferType | null;
  tags?: ITag[] | null;
}

export const defaultValue: Readonly<IOffer> = {
  visibility: false,
};
