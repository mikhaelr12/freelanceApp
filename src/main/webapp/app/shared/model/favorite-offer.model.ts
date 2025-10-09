import dayjs from 'dayjs';
import { IProfile } from 'app/shared/model/profile.model';
import { IOffer } from 'app/shared/model/offer.model';

export interface IFavoriteOffer {
  id?: number;
  createdAt?: dayjs.Dayjs;
  profile?: IProfile | null;
  offer?: IOffer | null;
}

export const defaultValue: Readonly<IFavoriteOffer> = {};
