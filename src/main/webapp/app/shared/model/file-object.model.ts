import dayjs from 'dayjs';

export interface IFileObject {
  id?: number;
  bucket?: string;
  objectKey?: string;
  contentType?: string | null;
  fileSize?: number | null;
  checksum?: string | null;
  durationSeconds?: number | null;
  createdDate?: dayjs.Dayjs;
  lastModifiedDate?: dayjs.Dayjs | null;
  createdBy?: string | null;
  lastModifiedBy?: string | null;
}

export const defaultValue: Readonly<IFileObject> = {};
