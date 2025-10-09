import dayjs from 'dayjs';
import { IOffer } from 'app/shared/model/offer.model';
import { PackageTier } from 'app/shared/model/enumerations/package-tier.model';

export interface IOfferPackage {
  id?: number;
  name?: string;
  description?: string;
  price?: number;
  currency?: string;
  deliveryDays?: number;
  packageTier?: keyof typeof PackageTier;
  active?: boolean;
  createdDate?: dayjs.Dayjs;
  lastModifiedDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  lastModifiedBy?: string | null;
  offer?: IOffer | null;
}

export const defaultValue: Readonly<IOfferPackage> = {
  active: false,
};
